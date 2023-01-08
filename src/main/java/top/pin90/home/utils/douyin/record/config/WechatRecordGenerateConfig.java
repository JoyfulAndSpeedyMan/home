package top.pin90.home.utils.douyin.record.config;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.*;

@Data
public class WechatRecordGenerateConfig implements Serializable {

    private static final long serialVersionUID = -3440548284519506482L;

    private DataConfig dataConfig;

    private BackgroundConfig backgroundConfig;

    private ChatConfig meChatConfig;

    private ChatConfig youChatConfig;

    private OutConfig outConfig;

    private DrawConfig drawConfig;


    public static DataConfig exampleData() {
        DateRecord me = new DateRecord(DateRecord.ME_OID, "你好");
        DateRecord timeLine = new DateRecord(DateRecord.TIME_LINE, "10月31日 00:57");
        DateRecord y1 = new DateRecord(DateRecord.YOU_OID, "你好");
        DateRecord y2 = new DateRecord(DateRecord.YOU_OID, "查寝的人会用各种方式让你开门");
        DateRecord y3 = new DateRecord(DateRecord.YOU_OID, "不要开灯不要开窗不要拉开窗帘不要开灯不要开窗不要拉开窗帘");
        return DataConfig.builder()
                .dataIter(Arrays.asList(me, timeLine, y1, y2, y3).iterator())
                .build();
    }

    public static WechatRecordGenerateConfig darkDefaultConfig() {
        return darkDefaultConfig(exampleData());
    }

    public static WechatRecordGenerateConfig darkDefaultConfig(DataConfig dataConfig) {
        Objects.requireNonNull(dataConfig, "数据配置不能为空");
        Objects.requireNonNull(dataConfig.dataIter, "数据配置不能为空");
        WechatRecordGenerateConfig config = new WechatRecordGenerateConfig();
        config.setDataConfig(dataConfig);
        config.setOutConfig(new OutConfig("target/result.png"));
        config.setBackgroundConfig(new BackgroundConfig());
        config.setMeChatConfig(new ChatConfig(
                ChatConfig.MY_AVATAR,
                new Color(89, 178, 105),
                new Color(6, 18, 10))
        );
        config.setYouChatConfig(new ChatConfig(ChatConfig.YOU_AVATAR,
                new Color(44, 44, 44),
                new Color(213, 213, 213))
        );
        config.setDrawConfig(new DrawConfig(1000, 1500));
        return config;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @SuperBuilder
    @ToString
    public static class DataConfig implements Serializable {

        private static final long serialVersionUID = -4328012538503416160L;

        private Iterator<DateRecord> dataIter;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @SuperBuilder
    @ToString
    public static class DateRecord {

        public final static int TIME_LINE = 0;

        public final static int ME_OID = 1;

        public final static int YOU_OID = 2;


        /**
         * 发言者id
         */
        private int oid = -1;

        private String msg;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    @ToString
    public static class BackgroundConfig implements Serializable {

        private static final long serialVersionUID = -5992999806215169535L;

        private Color backgroundColor = new Color(17, 17, 17);

    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    public static class ChatConfig implements Serializable {

        private static final long serialVersionUID = -6045960918485655822L;

        public static final Image MY_AVATAR;
        public static final Image YOU_AVATAR;

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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Image avatar;

        private Color boxColor = Color.BLACK;

        private Color textColor = Color.WHITE;

        public ChatConfig(Image avatar) {
            this.avatar = avatar;
        }

        public ChatConfig(Image avatar, Color boxColor) {
            this.avatar = avatar;
            this.boxColor = boxColor;
        }

        public ChatConfig(Image avatar, Color boxColor, Color textColor) {
            this.avatar = avatar;
            this.boxColor = boxColor;
            this.textColor = textColor;
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @SuperBuilder
    @ToString
    public static class OutConfig implements Serializable {

        private static final long serialVersionUID = -3650362894969563213L;

        private String outAllImgDir;

        private String outFile;

        private String fileFormat;

        public OutConfig(String outFile) {
            this.outFile = outFile;
            this.fileFormat = outFile.substring(outFile.lastIndexOf('.') + 1);
        }
    }

    @Getter
    @Setter
    @ToString
    public static class DrawConfig implements Serializable {

        private static final long serialVersionUID = 738985094273281928L;

        private int width;
        private int height;
        private int tagD;
        private double chatBoxMaxWidth;
        private double chatBoxRadius;
        private int avatarSize;
        private int avatarMargin;
        private int triangleMarginLeft;
        private int marginTopWithPreRecord;
        private int chatMsgBoxPadding;
        private Font textFont = null;


        public DrawConfig() {
            width = 600;
            height = 1500;
            calc();
        }

        public DrawConfig(int width, int height) {
            this.height = height;
            this.width = width;
            calc();
        }

        public void calc() {
//            avatarSize = (int) (width * 0.115);
//            textFont = new Font("微软雅黑", Font.PLAIN, (int) (width * 0.047));

            avatarSize = (int) (115);
            textFont = new Font("微软雅黑", Font.PLAIN, (int) (avatarSize * 0.4));
            chatMsgBoxPadding = (int) (avatarSize * 0.26);
            tagD = (int) (width * 0.014);
            chatBoxMaxWidth = width * 0.7;
            chatBoxRadius = width * 0.02;
            avatarMargin = (int) (avatarSize * 0.3478);
            triangleMarginLeft = (int) (avatarMargin + avatarSize + width * 0.015);
            marginTopWithPreRecord = (int) (width * 0.04);

        }
    }

}
