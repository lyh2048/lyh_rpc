package github.lyh2048.provider;

import github.lyh2048.config.RpcServiceConfig;

public interface ServiceProvider {
    void addService(RpcServiceConfig rpcServiceConfig);
    Object getService(String rpcServiceName);
    void publishService(RpcServiceConfig rpcServiceConfig);
}
