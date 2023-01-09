package top.pin90.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class HomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeApplication.class, args);
        String block = WebClient.create()
                .get()
                .uri("https://www.zhihu.com/market/paid_column/1553436710532493313/section/1553440073525415936?is_share_data=true&vp_share_title=0")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
