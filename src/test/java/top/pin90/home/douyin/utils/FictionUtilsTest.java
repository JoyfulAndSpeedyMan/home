package top.pin90.home.douyin.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.home.common.utils.SensitiveWordUtils;
import top.pin90.home.common.utils.file.ClasspathUtils;
import top.pin90.home.utils.douyin.FictionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class FictionUtilsTest {

    private String inTextPath;

    private FictionUtils fictionUtils;

    @BeforeEach
    public void before() throws IOException {
        File file = ClasspathUtils.getFile("/douyin/utils/fiction/in.txt");
        inTextPath = file.getAbsolutePath();
        fictionUtils = new FictionUtils();
    }

    @Test
    public void getParagraphFromUrl() throws IOException {
        String url = "https://www.zhihu.com/market/paid_column/1553436710532493313/section/1553440073525415936?is_share_data=true&vp_share_title=0";
        List<String> lines = fictionUtils.readlineFromUrl(url);
        lines.forEach(line ->{
            log.info(line);
        });
    }

    @Test
    public void getParagraphFromUrlAndResolve() throws IOException {
        String url = "https://www.zhihu.com/market/paid_column/1553436710532493313/section/1553440073525415936?is_share_data=true&vp_share_title=0";
        List<String> lines = fictionUtils.readlineFromUrl(url);
        List<String> resultList = fictionUtils.resolveLineList(lines, SensitiveWordUtils::replaceSensitiveWordToPinyin, false);
        resultList.forEach(log::info);
    }


}
