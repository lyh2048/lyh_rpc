package github.lyh2048.remoting.handler;

import github.lyh2048.exception.RpcException;
import github.lyh2048.factory.SingletonFactory;
import github.lyh2048.provider.ServiceProvider;
import github.lyh2048.provider.ZookeeperServiceProviderImpl;
import github.lyh2048.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ZookeeperServiceProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParameters());
            log.info("service: {} successfully invoke method: {}", request.getInterfaceName(), request.getMethodName());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
