package top.pin90.home.douyin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.pin90.home.common.RestResult;

@RestController
@RequestMapping("/zhihui")
public class ZhiHuiSubstitution {

    public RestResult<String> escapedParagraph(String paragraph){
        String result = null;
//        paragraph.replace()

        return RestResult.of(RestResult.SUCCESS_CODE, null, result);
    }

}
