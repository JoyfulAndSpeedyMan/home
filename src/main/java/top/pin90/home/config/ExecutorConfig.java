package top.pin90.home.config;

import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@Configuration
public class ExecutorConfig {

    @Bean
    public ThreadFactory virtualThreadFactory() {
        return Thread
                .ofVirtual()
                .name("Virtual Thread-", 0)
                .factory();
    }

    @Bean
    public ExecutorService virtualExecutorService(ThreadFactory virtualExecutorService) {
        return Executors.newThreadPerTaskExecutor(virtualExecutorService);
    }

    @Bean(name = {APPLICATION_TASK_EXECUTOR_BEAN_NAME,
            AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME})
    public AsyncTaskExecutor asyncTaskExecutor(ExecutorService virtualExecutorService) {
        return new TaskExecutorAdapter(virtualExecutorService);
    }

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer(
            ExecutorService virtualExecutorService) {
        return protocolHandler -> {
            protocolHandler.setExecutor(virtualExecutorService);
        };
    }
}
