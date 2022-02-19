package github.lyh2048.registry.zookeeper;

import github.lyh2048.registry.ServiceRegistry;
import github.lyh2048.registry.zookeeper.utils.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

@Slf4j
public class ZookeeperRegisterImpl implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework client = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(client, servicePath);
    }
}
