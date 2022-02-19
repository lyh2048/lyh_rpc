package github.lyh2048.registry;

import github.lyh2048.extension.SPI;
import github.lyh2048.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest request);
}
