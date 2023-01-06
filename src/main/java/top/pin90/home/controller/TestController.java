package top.pin90.home.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
public class TestController {

    private AtomicLong count = new AtomicLong(0);

    @GetMapping("/t")
    public String t() {
        Thread thread = Thread.currentThread();
        log.info("呵呵哈哈哈 {}", thread);
        return thread.toString();
    }

    @GetMapping("/currentTime")
    public String currentTime(int s) {
        if(s > 0) {
            try {
                Thread.sleep(s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return System.currentTimeMillis() + "";
    }
}
