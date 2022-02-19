package github.lyh2048.registry.zookeeper;

import github.lyh2048.enums.RpcErrorMessageEnum;
import github.lyh2048.exception.RpcException;
import github.lyh2048.extension.ExtensionLoader;
import github.lyh2048.lb.LoadBalance;
import github.lyh2048.registry.ServiceDiscovery;
import github.lyh2048.registry.zookeeper.utils.CuratorUtils;
import github.lyh2048.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZookeeperDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZookeeperDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }
    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String rpcServiceName = request.getRpcServiceName();
        CuratorFramework client = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(client, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, request);
        log.info("Successfully found the service address: {}", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
