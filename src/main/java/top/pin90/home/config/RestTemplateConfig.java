package top.pin90.home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import top.pin90.home.common.utils.http.RestTemplateManager;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(){
        return RestTemplateManager.getInstance();
    }

}
