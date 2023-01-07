package top.pin90.home.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.pin90.home.common.RestResult;
import top.pin90.home.common.utils.SensitiveWordUtils;
import top.pin90.home.utils.douyin.FictionUtils;

import java.util.List;

@RestController
@RequestMapping("/zhihui/paragraph")
public class ZhiHuiSubstitutionController {
    private FictionUtils fictionUtils;

    @GetMapping("/lines/load")
    public RestResult<List<String>> loadLinesFromUrl(String url) {
        List<String> lines = fictionUtils.readlineFromUrl(url);
        return RestResult.of(RestResult.SUCCESS_CODE, null, lines);
    }

    @GetMapping("/lines/load/resolve")
    public RestResult<List<String>> loadLinesFromUrlAndResolve(String url,
                                                               @RequestParam(defaultValue = "false") Boolean saveNumberLineText) {
        List<String> lines = fictionUtils.readlineFromUrl(url);
        lines = fictionUtils.resolveLineList(lines, SensitiveWordUtils::replaceSensitiveWordToPinyin, saveNumberLineText);
        return RestResult.of(RestResult.SUCCESS_CODE, null, lines);
    }

    @GetMapping("/text/load")
    public RestResult<String> loadTextFromUrl(String url) {
        List<String> lines = fictionUtils.readlineFromUrl(url);
        String text = StringUtils.join(lines, "\n");
        return RestResult.of(RestResult.SUCCESS_CODE, null, text);
    }

    @GetMapping("/text/load/resolve")
    public RestResult<String> loadTextFromUrlAndResolve(String url,
                                                        @RequestParam(defaultValue = "false") Boolean saveNumberLineText) {
        List<String> lines = fictionUtils.readlineFromUrl(url);
        lines = fictionUtils.resolveLineList(lines, SensitiveWordUtils::replaceSensitiveWordToPinyin, saveNumberLineText);
        String text = StringUtils.join(lines, "\n");
        return RestResult.of(RestResult.SUCCESS_CODE, null, text);
    }


    @Autowired
    public void setFictionUtils(FictionUtils fictionUtils) {
        this.fictionUtils = fictionUtils;
    }
}
