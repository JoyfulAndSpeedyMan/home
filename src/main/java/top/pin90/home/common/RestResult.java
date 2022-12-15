package top.pin90.home.common;

import lombok.*;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class RestResult<T> implements Serializable {


    public static String SUCCESS_CODE = "000000";

    public static String FAIL_CODE = "000001";
    private String code;
    private String msg;
    private T data;

    private RestResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public static <T> RestResult<T> of(String code, String msg, T data) {
        return new RestResult<>(code, msg, data);
    }

    public static <T> Mono<RestResult<T>> monoOf(String code, String msg, T data) {
        return Mono.just(of(code, msg, data));
    }

    public static <T> Mono<RestResult<T>> successMono(String msg, T data) {
        return Mono.just(of(SUCCESS_CODE, msg, data));
    }

    public static <T> Mono<RestResult<T>> successMono(T data) {
        return Mono.just(of(SUCCESS_CODE, null, data));
    }
}
