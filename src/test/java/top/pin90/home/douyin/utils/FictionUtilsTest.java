package top.pin90.home.douyin.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.home.common.utils.SensitiveWordUtils;
import top.pin90.home.common.utils.file.ClasspathUtils;
import top.pin90.home.utils.douyin.FictionUtils;
import top.pin90.home.utils.douyin.record.WeChatRecordsGenerate;
import top.pin90.home.utils.douyin.record.config.WechatRecordGenerateConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static top.pin90.home.utils.douyin.record.config.WechatRecordGenerateConfig.DateRecord.*;

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
        lines.forEach(line -> {
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

    @Test
    public void generateChatRecordImgByUrl() {
        String url = "https://www.zhihu.com/market/paid_column/1553436710532493313/section/1553440073525415936?is_share_data=true&vp_share_title=0";
        int limit = 300;
        List<String> lines = fictionUtils.readlineFromUrl(url, limit);
        log.info("文章抓取完成, 共{}条， limit {}，开始处理文本", lines.size(), limit);
        List<String> resultList = fictionUtils.resolveLineList(lines,
                SensitiveWordUtils::replaceSensitiveWordToPinyin, false, limit);
        log.info("文本处理完成, 共{}条，limit {}，开始自定义文本", resultList.size(), limit);

        List<WechatRecordGenerateConfig.DateRecord> allData = new ArrayList<>();
        allData.add(new WechatRecordGenerateConfig.DateRecord(ME, "你们学校发生过什么惊悚的事情"));
        allData.add(new WechatRecordGenerateConfig.DateRecord(TIME_LINE, "2023年 12月31日 23:56"));
        for (String msg : resultList) {
            allData.add(new WechatRecordGenerateConfig.DateRecord(YOU, msg));
        }

        log.info("自定义文本完成, 共{}条，开始生成图片", allData.size());

        WechatRecordGenerateConfig config = WechatRecordGenerateConfig.darkDefaultConfig();
        config.getDataConfig().setDataIter(allData.iterator());
        WeChatRecordsGenerate generate = new WeChatRecordsGenerate(config);
        generate.run();
        log.info("图片生成完成！");
    }
}
