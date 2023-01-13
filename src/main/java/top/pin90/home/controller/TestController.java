package top.pin90.home.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
public class TestController {

    private RestTemplate restTemplate;

    private AtomicLong count = new AtomicLong(0);

    @GetMapping("/t")
    public String t() {
        Thread thread = Thread.currentThread();
        log.info("呵呵哈哈哈 {}", thread);
        return thread.toString();
    }

    @GetMapping("/currentTime")
    public String currentTime(int s) throws InterruptedException {
        Thread.sleep(s);
        return System.currentTimeMillis() + "";
    }

    @GetMapping("/testHttp")
    public ResponseEntity<String> testHttp(){
        String uri = "https://www.zhihu.com/market/paid_column/1553436710532493313/section/1553440073525415936?is_share_data=true&vp_share_title=0";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(uri, String.class);
        return forEntity;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
