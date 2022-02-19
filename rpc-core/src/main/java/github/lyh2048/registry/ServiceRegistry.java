package github.lyh2048.registry;

import github.lyh2048.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
