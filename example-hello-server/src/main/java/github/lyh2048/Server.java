package github.lyh2048;

import github.lyh2048.annotation.RpcScan;
import github.lyh2048.config.RpcServiceConfig;
import github.lyh2048.impl.HelloServiceImpl2;
import github.lyh2048.remoting.transport.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"github.lyh2048"})
public class Server {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Server.class);
        NettyRpcServer server = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2").version("test2").service(helloService2).build();
        server.registerService(rpcServiceConfig);
        server.start();
    }
}
