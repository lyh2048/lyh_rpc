package github.lyh2048.remoting.transport;

import github.lyh2048.extension.SPI;
import github.lyh2048.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    /**
     * 向服务提供者发送rpc请求并获取结果
     * @param request rpc请求对象
     * @return 结果
     */
    Object sendRpcRequest(RpcRequest request);
}
