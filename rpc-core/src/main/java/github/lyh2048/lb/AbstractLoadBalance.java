package github.lyh2048.lb;

import github.lyh2048.remoting.dto.RpcRequest;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance{
    @Override
    public String selectServiceAddress(List<String> serviceAddress, RpcRequest rpcRequest) {
        if (serviceAddress == null || serviceAddress.size() == 0) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }
        return doSelect(serviceAddress, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceAddress, RpcRequest request);
}
