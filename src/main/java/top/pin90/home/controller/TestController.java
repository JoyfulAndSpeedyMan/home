package top.pin90.home.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/t")
    public String t(){
        Thread thread = Thread.currentThread();
        log.info("呵呵哈哈哈 {}", thread);
        return thread.toString();
    }
}
