package top.pin90.home.douyin.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.home.common.manager.http.RestTemplateManager;
import top.pin90.home.common.manager.http.WebClientManager;
import top.pin90.home.common.utils.SensitiveWordUtils;
import top.pin90.home.common.utils.file.ClasspathUtils;
import top.pin90.home.utils.douyin.FictionUtils;
import top.pin90.home.utils.douyin.record.WeChatRecordsGenerateV2;
import top.pin90.home.utils.douyin.record.config.DataConfig;
import top.pin90.home.utils.douyin.record.config.WechatRecordGenerateConfigV2;
import top.pin90.home.utils.douyin.record.config.theme.ThemeConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static top.pin90.home.utils.douyin.record.config.WechatRecordGenerateConfig.DateRecord.*;

@Slf4j
public class FictionUtilsTestV2 {

    private String inTextPath;

    private FictionUtils fictionUtils;

    @BeforeEach
    public void before() throws IOException {
        File file = ClasspathUtils.getFile("/douyin/utils/fiction/in.txt");
        inTextPath = file.getAbsolutePath();
        fictionUtils = new FictionUtils(RestTemplateManager.getInstance(), WebClientManager.getInstance());
    }

    @Test
    public void getParagraphFromUrl() {
        String url = "https://www.zhihu.com/market/paid_column/1553436710532493313/section/1553440073525415936?is_share_data=true&vp_share_title=0";
        List<String> lines = fictionUtils.readlineFromUrl(url);
        lines.forEach(log::info);
    }

    @Test
    public void getParagraphFromUrlAndResolve() {
        String url = "https://www.zhihu.com/market/paid_column/1553436710532493313/section/1553440073525415936?is_share_data=true&vp_share_title=0";
        List<String> lines = fictionUtils.readlineFromUrl(url);
        List<String> resultList = fictionUtils.resolveLineList(lines, SensitiveWordUtils::replaceSensitiveWordToPinyin, false);
        resultList.forEach(log::info);
    }


    @Test
    public void readlineAnswerFromUrlAndResolve(){
        String url = "https://www.zhihu.com/question/59637484/answer/2818550322";
        List<String> lines = fictionUtils.readlineAnswerFromUrl(url, null, "SESSIONID=qH02stXFJzdTBuZTLDn9KeQ8RiukJh2jhSh3ArO0Nzj; JOID=WlgUBkLmrO-w8cigQesV-vPg5m1bzY7In9PjgmbEh82X3uqLY_xWBd74waVJh0FyLKlZtg1K8Yjmq8yRsYkCA58=; osd=UVAdB0ntpOax-sOoSOoe8fvp52ZQxYfJlNjri2fPjMWe3-GAa_VXDtXwyKRCjEl7LaJSvgRL-oPuos2auoELApQ=; _zap=a0f06d38-e031-4864-8926-a7e584afa592; d_c0=\"ACBeL0kuwxOPTuqm9Mg2GRU9vtdZhfRdT0I=|1632295876\"; _9755xjdesxxd_=32; YD00517437729195%3AWM_TID=ishQkqtGuX5AQRFFRBN%2B8QMQCub0NZP4; q_c1=7d5136da81d144cf928b28bb36184b81|1649903249000|1649903249000; q_c1=7d5136da81d144cf928b28bb36184b81|1666174742000|1649903249000; __utma=51854390.262921897.1666174743.1666174743.1666174743.1; __utmz=51854390.1666174743.1.1.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/question/519618285/answer/2622268185; __utmv=51854390.100--|2=registration_date=20170727=1^3=entry_date=20170727=1; __snaker__id=9qK089XSLhTsNiOR; YD00517437729195%3AWM_NI=jChqG8LSQBhN3BBafaNjDIH1xsrHfzjtGgLT4vmdcxIGUfVhEyCWqxVVcnLDAug7VVx%2B1H7tqSf6Z%2F4jYmJbXj1%2F%2FaJ%2FyA6LnC2qt7BPx%2BZLE2sG2g1h%2FHGM%2BLPPVgxTMkE%3D; YD00517437729195%3AWM_NIKE=9ca17ae2e6ffcda170e2e6ee87b6728b95a2a7cd49ac928bb2d45e929e8aadc169afecf88cc97b8189ad82b12af0fea7c3b92aaa9da892c634908d9fa3eb46f19e8e82e56b8eb68caacb3bbcadbea9e572aaac8988cf60f8aaa385e2748b999b87d56487a78db8f474968aad8fcf5083aba9d8d55ebcf5ffa9b680b38daad7d925f8b6978ec96a9cb6ffd0ae6b8af1b7a2b548ae9dfe92f73d8290b8ccd94887eebda8b16df3b8e1a9c647e994a0b1d56dbc8c838cc837e2a3; _xsrf=202a8392-e970-4be7-9626-bfd63a18d7b0; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1675159015,1675304742,1675309188,1675820459; arialoadData=false; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1675820816; z_c0=2|1:0|10:1675820817|4:z_c0|80:MS4xbldHS0JRQUFBQUFtQUFBQVlBSlZUZGtkd21RZGlYQmhxdnUweWU4aTdxcVdCOXBvQmptWDlBPT0=|d7a6eb2e6e18be379b9aaaefded967ce31f7a5cda5a252458d7741de84ee1adf; KLBRSID=b5ffb4aa1a842930a6f64d0a8f93e9bf|1675821737|1675820450");
        List<String> resultList = fictionUtils.resolveLineList(lines, SensitiveWordUtils::replaceSensitiveWordToPinyin, false);
        resultList.forEach(log::info);
    }
    @Test
    public void generateChatRecordImgByUrl() throws IOException {
        String url = "https://www.zhihu.com/question/270622531/answer/2850758779";
        Integer start = null;
        Integer limit = 300;
        String cookie = "SESSIONID=8VwrGDazFmVqthRcJ0AxczdTT8NTgDNqBeLyqu2xpFe; JOID=UlAWBEhJe_kxA0ypU07A5ngaaGZDYlPeFiNngXRpUNEWJGyCe1vSHVUIT6ZT0yJclQixbQMfTuvfxOaLTR6PwSQ=; osd=V18cCkNMdPM_CEmmWUDL43cQZm1GbVnQHSZoi3piVd4cKmeHdFHcFlAHRahY1i1WmwO0YgkRRe7QzuiASBGFzy8=; _zap=a0f06d38-e031-4864-8926-a7e584afa592; d_c0=\"ACBeL0kuwxOPTuqm9Mg2GRU9vtdZhfRdT0I=|1632295876\"; _9755xjdesxxd_=32; YD00517437729195:WM_TID=ishQkqtGuX5AQRFFRBN+8QMQCub0NZP4; q_c1=7d5136da81d144cf928b28bb36184b81|1649903249000|1649903249000; q_c1=7d5136da81d144cf928b28bb36184b81|1666174742000|1649903249000; __utma=51854390.262921897.1666174743.1666174743.1666174743.1; __utmz=51854390.1666174743.1.1.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/question/519618285/answer/2622268185; __utmv=51854390.100--|2=registration_date=20170727=1^3=entry_date=20170727=1; __snaker__id=9qK089XSLhTsNiOR; YD00517437729195:WM_NI=jChqG8LSQBhN3BBafaNjDIH1xsrHfzjtGgLT4vmdcxIGUfVhEyCWqxVVcnLDAug7VVx+1H7tqSf6Z/4jYmJbXj1//aJ/yA6LnC2qt7BPx+ZLE2sG2g1h/HGM+LPPVgxTMkE=; YD00517437729195:WM_NIKE=9ca17ae2e6ffcda170e2e6ee87b6728b95a2a7cd49ac928bb2d45e929e8aadc169afecf88cc97b8189ad82b12af0fea7c3b92aaa9da892c634908d9fa3eb46f19e8e82e56b8eb68caacb3bbcadbea9e572aaac8988cf60f8aaa385e2748b999b87d56487a78db8f474968aad8fcf5083aba9d8d55ebcf5ffa9b680b38daad7d925f8b6978ec96a9cb6ffd0ae6b8af1b7a2b548ae9dfe92f73d8290b8ccd94887eebda8b16df3b8e1a9c647e994a0b1d56dbc8c838cc837e2a3; z_c0=2|1:0|10:1675820817|4:z_c0|80:MS4xbldHS0JRQUFBQUFtQUFBQVlBSlZUZGtkd21RZGlYQmhxdnUweWU4aTdxcVdCOXBvQmptWDlBPT0=|d7a6eb2e6e18be379b9aaaefded967ce31f7a5cda5a252458d7741de84ee1adf; _xsrf=54cf2043-c772-4dbe-a417-e67d9376a398; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1675304742,1675309188,1675820459,1675842336; arialoadData=false; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1675929100; KLBRSID=d1f07ca9b929274b65d830a00cbd719a|1675929105|1675924696";
//        String cookie = null;
        List<String> lines = fictionUtils.readlineAnswerFromUrl(url, cookie);
        log.info("文章抓取完成, 共{}条 ，开始处理文本", lines.size());
        List<String> resultList = fictionUtils.resolveLineList(lines,
                SensitiveWordUtils::replaceSensitiveWordToPinyin, start, limit);
        log.info("文本处理完成, 共{}条，start {} , limit  {}，开始自定义文本", resultList.size(), start, limit);

        List<DataConfig.DateRecord> allData = new ArrayList<>();
        allData.add(new DataConfig.DateRecord(ME, "男朋友有哪些令人窒息的操作"));
        allData.add(new DataConfig.DateRecord(TIME_LINE, "2月8日 10:23"));
        for (String msg : resultList) {
            allData.add(new DataConfig.DateRecord(YOU, msg));
        }
        log.info("自定义文本完成, 共{}条，开始生成图片", allData.size());
        WechatRecordGenerateConfigV2 config = new WechatRecordGenerateConfigV2();
        config.setThemeConfig(ThemeConfig.DEFAULT_CONFIG);
        config.setDataConfig(DataConfig.twoOf(allData.iterator(), null, null));
        WeChatRecordsGenerateV2 generate = new WeChatRecordsGenerateV2(config);
        generate.run();
        log.info("图片生成完成！");
    }
}
