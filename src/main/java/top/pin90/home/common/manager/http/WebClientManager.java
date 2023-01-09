package top.pin90.home.common.manager.http;

import org.springframework.web.reactive.function.client.WebClient;

public class WebClientManager {

    private static WebClient INSTANCE;

    static {
        reload();
    }

    public static void reload() {
        INSTANCE = newInstance();
    }


    public static WebClient getInstance() {
        return INSTANCE;
    }

    private static WebClient newInstance() {
        return WebClient.create();
    }

}
