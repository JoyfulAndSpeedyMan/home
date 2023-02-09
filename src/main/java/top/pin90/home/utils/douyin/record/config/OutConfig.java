package top.pin90.home.utils.douyin.record.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class OutConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 7525419414829921115L;

    public static final String DEFAULT_OUT_FILE = "result.png";

    public static final String DEFAULT_MID_IMG_OUT_DIR = "mid";

    public static final String DEFAULT_SECTION_OUT_DIR = "section";


    private String baseOutDir;

    private boolean outResult = true;

    private String resultFile;

    private boolean outMidImg = true;

    private String outMidImgDir;

    private boolean sectionOut = true;

    private String sectionOutDir;

    private List<Page> sectionList;

    @Getter
    @Setter
    @ToString
    public static class Page implements Serializable {

        @Serial
        private static final long serialVersionUID = -8268352805900710236L;

        private int start;

        private int end;
    }

}

