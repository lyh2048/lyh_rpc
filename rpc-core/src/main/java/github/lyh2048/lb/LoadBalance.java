package github.lyh2048.lb;

import github.lyh2048.extension.SPI;
import github.lyh2048.remoting.dto.RpcRequest;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddress, RpcRequest rpcRequest);
}
