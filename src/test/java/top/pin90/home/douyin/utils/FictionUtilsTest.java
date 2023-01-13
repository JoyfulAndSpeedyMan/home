package top.pin90.home.douyin.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.home.common.manager.http.RestTemplateManager;
import top.pin90.home.common.utils.SensitiveWordUtils;
import top.pin90.home.common.utils.file.ClasspathUtils;
import top.pin90.home.utils.douyin.FictionUtils;
import top.pin90.home.utils.douyin.record.WeChatRecordsGenerate;
import top.pin90.home.utils.douyin.record.config.WechatRecordGenerateConfig;

import javax.imageio.ImageIO;
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
        fictionUtils = new FictionUtils(RestTemplateManager.getInstance());
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
    public void generateChatRecordImgByUrl() throws IOException {
        String url = "https://www.zhihu.com/market/paid_column/1353427390241054720/section/1353427779145400320";
        int limit = 300;
        List<String> lines = fictionUtils.readlineFromUrl(url, limit);
        log.info("文章抓取完成, 共{}条， limit {}，开始处理文本", lines.size(), limit);
        List<String> resultList = fictionUtils.resolveLineList(lines,
                SensitiveWordUtils::replaceSensitiveWordToPinyin, false, limit);
        log.info("文本处理完成, 共{}条，limit {}，开始自定义文本", resultList.size(), limit);



        WechatRecordGenerateConfig defaultConfig = WechatRecordGenerateConfig.defaultConfig();

        List<WechatRecordGenerateConfig.DateRecord> allData = new ArrayList<>();
        allData.add(new WechatRecordGenerateConfig.DateRecord(ME, "你有什么难忘的事情吗"));
        allData.add(new WechatRecordGenerateConfig.DateRecord(TIME_LINE, "1月7日 13:36"));
        for (String msg : resultList) {
            allData.add(new WechatRecordGenerateConfig.DateRecord(YOU, msg));
        }
        log.info("自定义文本完成, 共{}条，开始生成图片", allData.size());
        defaultConfig.getDataConfig().setDataIter(allData.iterator());

        String baseDir = "D:\\workspace\\文件\\知乎小说\\情书";
        String youAvatar = "you.jpg";
        String chatRecord = "聊天记录.png";
        WechatRecordGenerateConfig.ChatConfig youChatConfig = defaultConfig.getYouChatConfig();
        youChatConfig.setAvatar(ImageIO.read(new File(baseDir + File.separator + youAvatar)));

        WechatRecordGenerateConfig.OutConfig outConfig = defaultConfig.getOutConfig();
        outConfig.setOutFile(baseDir + File.separator + chatRecord);
        outConfig.setOutAllImgDir(baseDir + File.separator + "allImgs");
        outConfig.setOutputMiddleImg(true);
        WeChatRecordsGenerate generate = new WeChatRecordsGenerate(defaultConfig);
        generate.run();
        log.info("图片生成完成！");
    }
}
