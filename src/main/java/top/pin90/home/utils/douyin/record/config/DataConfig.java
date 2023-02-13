package top.pin90.home.utils.douyin.record.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@ToString
public class DataConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -4328012538503416160L;

    private Iterator<DateRecord> dataIter;

    private Map<Integer, PersonInfo> personInfoMap;

    private DataConfig(Iterator<DateRecord> dataIter, Map<Integer, PersonInfo> personInfoMap) {
        this.dataIter = dataIter;
        this.personInfoMap = personInfoMap;
    }

    public static DataConfig twoOf(Iterator<DateRecord> dataIter, Image myAvatar, Image youAvatar) {
        Objects.requireNonNull(dataIter);
        Map<Integer, PersonInfo> personInfoMap = new HashMap<>(2);
        personInfoMap.compute(ME, (k, v) ->
                myAvatar != null ? new PersonInfo(k, myAvatar) : MY_PERSON_INFO
        );
        personInfoMap.compute(YOU, (k, v) ->
                youAvatar != null ? new PersonInfo(k, youAvatar) : YOU_PERSON_INFO
        );
        return new DataConfig(dataIter, personInfoMap);
    }


    public PersonInfo getMyPersonInfo() {
        return personInfoMap.get(ME);
    }

    public PersonInfo getPersonInfo(int id){
        return personInfoMap.get(id);
    }


    @Getter
    @Setter
    public static class PersonInfo {

        private int id;

        private Image avatar;

        public PersonInfo(int id, Image avatar) {
            Objects.requireNonNull(avatar);
            this.id = id;
            this.avatar = avatar;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @SuperBuilder
    @ToString
    public static class DateRecord {

        /**
         * 发言者id
         */
        private int speakerId = -1;

        private String msg;
    }

    public final static int TIME_LINE = 0;

    public final static int ME = 1;

    public final static int YOU = 2;


    public static final Image MY_AVATAR;

    public static final Image YOU_AVATAR;

    public static final PersonInfo MY_PERSON_INFO;

    public static final PersonInfo YOU_PERSON_INFO;

    private static Iterator<DateRecord> EXAMPLE_DATA_ITER = null;

    public static Iterator<DateRecord> getExampleDataIter() {
        return EXAMPLE_DATA_ITER;
    }

    static {
        try {
            MY_AVATAR = ImageIO.read(
                    Objects.requireNonNull(
                            WechatRecordGenerateConfig.class.getResourceAsStream("/wechat/record/avatar/me.jpg")
                    )
            );
            YOU_AVATAR = ImageIO.read(
                    Objects.requireNonNull(
                            WechatRecordGenerateConfig.class.getResourceAsStream("/wechat/record/avatar/you.jpg")
                    )
            );
            MY_PERSON_INFO = new PersonInfo(ME, MY_AVATAR);
            YOU_PERSON_INFO = new PersonInfo(YOU, YOU_AVATAR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DateRecord me1 = new DateRecord(ME, "你好");
        DateRecord timeLine = new DateRecord(TIME_LINE, "10月31日 00:57");
        DateRecord y1 = new DateRecord(YOU, "你好");
        DateRecord y2 = new DateRecord(YOU, "查寝的人会用各种方式让你开门");
        DateRecord y3 = new DateRecord(YOU, "不要开灯不要开窗不要拉开窗帘不要开灯不要开窗不要拉开窗帘");
        DateRecord me2 = new DateRecord(ME, "卫生间伐uebfueyw瑟瑟发抖发·17");
        EXAMPLE_DATA_ITER = List.of(me1, timeLine, y1, y2, y3, me2).iterator();
    }
}
