package top.pin90.home.utils.douyin.record.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class DrawConfig implements Serializable {

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

    private int slot;

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
//            Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
//            map.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
//            textFont = textFont.deriveFont(map);
        chatMsgBoxPadding = (int) (avatarSize * 0.26);
        tagD = (int) (width * 0.014);
        chatBoxMaxWidth = width * 0.7;
        chatBoxRadius = width * 0.02;
        avatarMargin = (int) (avatarSize * 0.3478);
        triangleMarginLeft = (int) (avatarMargin + avatarSize + width * 0.015);
        marginTopWithPreRecord = (int) (avatarSize * 0.3);
        slot = 0;
    }
}
