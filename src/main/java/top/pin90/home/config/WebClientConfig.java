package top.pin90.home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import top.pin90.home.common.manager.http.RestTemplateManager;
import top.pin90.home.common.manager.http.WebClientManager;

@Configuration
public class WebClientConfig {

    @Bean
    public RestTemplate restTemplate(){
        return RestTemplateManager.getInstance();
    }

    @Bean
    public WebClient webClient(){
        return WebClientManager.getInstance();
    }
}
