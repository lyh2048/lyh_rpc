package github.lyh2048.impl;

import github.lyh2048.Hello;
import github.lyh2048.HelloService;
import github.lyh2048.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RpcService(group = "test1", version = "test1")
public class HelloServiceImpl1 implements HelloService {
    static {
        log.info("HelloServiceImpl1被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl1收到: {}", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl1返回: {}", result);
        return result;
    }
}
