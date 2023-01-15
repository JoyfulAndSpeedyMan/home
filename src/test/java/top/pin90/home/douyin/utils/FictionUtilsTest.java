package top.pin90.home.douyin.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.home.common.manager.http.RestTemplateManager;
import top.pin90.home.common.manager.http.WebClientManager;
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
        fictionUtils = new FictionUtils(RestTemplateManager.getInstance(), WebClientManager.getInstance());
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
        String url = "https://www.zhihu.com/market/paid_column/1573680063303835648/section/1580926136275644416";
        Integer start = null;
        Integer limit = 250;
        String cookie = "_zap=7f2fbc2c-758c-40f0-8b6d-e76d476459bb; d_c0=\"AJDfhl_6_xOPTkd-qdL68yJkL9TbxGt3akA=|1636375908\"; _9755xjdesxxd_=32; YD00517437729195%3AWM_TID=NXm4A1w4KehERQBBQRLVccjnK5b11ZRq; __snaker__id=qK8MLkZKWFnUX9LV; q_c1=6b58d5d965cb43ee9e63f2ffa7a17cc3|1670297697000|1670297697000; _xsrf=3sf9tXrs5B588oeFeH6eURStFrygmmTN; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1673060926,1673268591,1673697327; captcha_session_v2=2|1:0|10:1673697326|18:captcha_session_v2|88:elp1THlTMEVWamdHaUNsTzljdmU5ZDVBUnVuazlocU1MMVIwMXFKVDNkVU52a090LzR2eDZRQTB5bUpUSEtjbw==|6c6ad1cd66777569313a9239e0f4591cb37baacee71dd6120f091a78bdce0d54; arialoadData=false; gdxidpyhxdE=4uLD4Bm1SJn3rcDcd0hxED6Lc8k33L%5CwXvrS2Bq%2FJEybKOE1IHZpVrTqby20dPygS2r%5CkVJCAbUPLv1a%2FxaMldUnV00rrd%2ByLxKurXTR5rgEDadlaVlgqQLq9KW3OrWuouYeKPcR3EefKVYwuSu%2FQYQQjGUYiQJ58xRAWcE1bYRWI%2BHV%3A1673698228832; YD00517437729195%3AWM_NI=ibKe22ZeWyLyLJiXUPbtiJ5nOqCE4sLHVrCVOMobhNyGCiA0zQv72YlkYsHDPxECjqWhuWJBy1KJS00fXU0wXE7U8MiEukAb4P7twaGCMlD22973%2FDMJhhojxmmhFSiMTkM%3D; YD00517437729195%3AWM_NIKE=9ca17ae2e6ffcda170e2e6ee8fbb348f8db698e66faceb8ea3c54e929b8b83c546f8b18eb9d166878fab86c12af0fea7c3b92aabbe8586b13a9abb999acf739096a7b9d85ca2aca7abb8668daf8da6b77990989a93c25e8d93a183eb46bb998d8ac43cba8fbad8b153b0b59794cb5094a88ba4e97b95b8bbb8c94abbef85abd23cf4ba98b6b37d9c9bf9b6bb47ab9bbb8cc46af890f995f66b859d838dc73af2e7f9d4ae508f96a889ee62bcb3a389c559ab939c8cd037e2a3; z_c0=2|1:0|10:1673697343|4:z_c0|92:Mi4xbldHS0JRQUFBQUFBa04tR1hfcl9FeVlBQUFCZ0FsVk5QLWF2WkFDaE5NMWFsZUdsMEpNNzZSdjMtZllIX25Ydmp3|9c3a852628af69930ccc58526194c7b1daa7adfd1ab160576a1e61e0ceec7f53; KLBRSID=0a401b23e8a71b70de2f4b37f5b4e379|1673700437|1673700116";
//        String cookie = null;
        List<String> lines = fictionUtils.readlineFromUrl(url, cookie);
        log.info("文章抓取完成, 共{}条 ，开始处理文本", lines.size());
        List<String> resultList = fictionUtils.resolveLineList(lines,
                SensitiveWordUtils::replaceSensitiveWordToPinyin, start, limit);
        log.info("文本处理完成, 共{}条，start {} , limit  {}，开始自定义文本", resultList.size(), start, limit);


        WechatRecordGenerateConfig defaultConfig = WechatRecordGenerateConfig.defaultConfig();

        List<WechatRecordGenerateConfig.DateRecord> allData = new ArrayList<>();
        allData.add(new WechatRecordGenerateConfig.DateRecord(ME, "你见过熊孩子吗"));
        allData.add(new WechatRecordGenerateConfig.DateRecord(TIME_LINE, "1月8日 17:27"));
        for (String msg : resultList) {
            allData.add(new WechatRecordGenerateConfig.DateRecord(YOU, msg));
        }
        log.info("自定义文本完成, 共{}条，开始生成图片", allData.size());
        defaultConfig.getDataConfig().setDataIter(allData.iterator());

        String baseDir = "D:\\workspace\\文件\\知乎小说\\掌掴熊孩子";
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
