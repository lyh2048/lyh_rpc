package github.lyh2048.lb;

import github.lyh2048.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance{
    @Override
    protected String doSelect(List<String> serviceAddress, RpcRequest request) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
