package github.lyh2048.remoting.transport.client;

import github.lyh2048.enums.CompressTypeEnum;
import github.lyh2048.enums.SerializationTypeEnum;
import github.lyh2048.extension.ExtensionLoader;
import github.lyh2048.factory.SingletonFactory;
import github.lyh2048.registry.ServiceDiscovery;
import github.lyh2048.remoting.constants.RpcConstants;
import github.lyh2048.remoting.dto.RpcMessage;
import github.lyh2048.remoting.dto.RpcRequest;
import github.lyh2048.remoting.dto.RpcResponse;
import github.lyh2048.remoting.transport.RpcRequestTransport;
import github.lyh2048.remoting.transport.codec.RpcMessageDecoder;
import github.lyh2048.remoting.transport.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;


    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new RpcMessageEncoder());
                        ch.pipeline().addLast(new RpcMessageDecoder());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zookeeper");
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }


    @SneakyThrows
    public Channel doConnect(InetSocketAddress address) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(address).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected {} successfully", address.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalArgumentException();
            }
        });
        return completableFuture.get();
    }

    public Channel getChannel(InetSocketAddress address) {
        Channel channel = channelProvider.get(address);
        if (channel == null) {
            channel = doConnect(address);
            channelProvider.set(address, channel);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    @Override
    public Object sendRpcRequest(RpcRequest request) {
        CompletableFuture<RpcResponse<Object>> result = new CompletableFuture<>();
        InetSocketAddress address = serviceDiscovery.lookupService(request);
        Channel channel = getChannel(address);
        if (channel.isActive()) {
            unprocessedRequests.put(request.getRequestId(), result);
            RpcMessage rpcMessage = RpcMessage.builder().data(request)
                    .codec(SerializationTypeEnum.PROTOSTUFF.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: {}", rpcMessage);
                } else {
                    future.channel().close();
                    result.completeExceptionally(future.cause());
                    log.error("Send failed", future.cause());
                }
            });
        } else {
            throw new IllegalArgumentException();
        }
        return result;
    }
}
