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
        return WebClient.builder()
                .codecs(item->item.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

}
