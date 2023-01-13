package top.pin90.home.utils.douyin.record.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@ToString
public class DataConfig implements Serializable {

    public final static int TIME_LINE = 0;

    public final static int ME = 1;

    public final static int YOU = 2;
    public static final Image MY_AVATAR;

    public static final Image YOU_AVATAR;

    public static final Map<Integer, PersonInfo> ONE_TO_ONE_PERSON_INFO;

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
            ONE_TO_ONE_PERSON_INFO = Map.of(
                    ME, new PersonInfo(ME, MY_AVATAR),
                    YOU, new PersonInfo(YOU, YOU_AVATAR)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final long serialVersionUID = -4328012538503416160L;

    private Iterator<WechatRecordGenerateConfig.DateRecord> dataIter;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class PersonInfo {

        private int id;

        private Image avatar;
    }
}
