package top.pin90.home.common.utils.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RestTemplateManager {

    private static RestTemplate INSTANCE;

    static {
        reload();
    }

    public static void reload() {
        INSTANCE = newInstance();
    }


    public static RestTemplate getInstance() {
        return INSTANCE;
    }

    private static RestTemplate newInstance() {
        return new RestTemplate();
    }
}
