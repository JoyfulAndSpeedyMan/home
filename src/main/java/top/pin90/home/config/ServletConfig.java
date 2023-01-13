package top.pin90.home.config;

import ch.qos.logback.classic.ViewStatusMessagesServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
public class ServletConfig {

    //配置 StatViewServlet
    @Profile("!production")
    @Bean
    public ServletRegistrationBean<ViewStatusMessagesServlet> logbackViewStatusMessagesServlet() {
        ServletRegistrationBean<ViewStatusMessagesServlet> registration =
                new ServletRegistrationBean<>(new ViewStatusMessagesServlet());
        registration.addUrlMappings("/logback");
        registration.setLoadOnStartup(0);
        return registration;
    }
}
