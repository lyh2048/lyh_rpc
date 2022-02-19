package github.lyh2048.controller;

import github.lyh2048.Hello;
import github.lyh2048.HelloService;
import github.lyh2048.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HelloController {

    @RpcReference(version = "test1", group = "test1")
    private HelloService helloService;

    public void test() {
        String hello = helloService.hello(new Hello("666", "999"));
        log.info("result: " + hello);
    }
}
