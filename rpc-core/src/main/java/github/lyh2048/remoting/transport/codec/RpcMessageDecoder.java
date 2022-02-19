package github.lyh2048.remoting.transport.codec;

import github.lyh2048.compress.Compress;
import github.lyh2048.enums.CompressTypeEnum;
import github.lyh2048.enums.SerializationTypeEnum;
import github.lyh2048.extension.ExtensionLoader;
import github.lyh2048.remoting.constants.RpcConstants;
import github.lyh2048.remoting.dto.RpcMessage;
import github.lyh2048.remoting.dto.RpcRequest;
import github.lyh2048.remoting.dto.RpcResponse;
import github.lyh2048.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 */

@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(
            int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip
    ) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("解码错误！", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .compress(compressType)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bytes = new byte[bodyLength];
            in.readBytes(bytes);
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bytes = compress.decompress(bytes);
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: {}", codecName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tempValue = serializer.deserialize(bytes, RpcRequest.class);
                rpcMessage.setData(tempValue);
            } else {
                RpcResponse tempValue = serializer.deserialize(bytes, RpcResponse.class);
                rpcMessage.setData(tempValue);
            }
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("版本错误: " + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] temp = new byte[len];
        in.readBytes(temp);
        for (int i = 0; i < len; i++) {
            if (temp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("魔数错误: " + Arrays.toString(temp));
            }
        }
    }
}
