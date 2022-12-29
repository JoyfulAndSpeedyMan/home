package top.pin90.home.douyin.utils;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class WeChatRecordsGenerate {

    private final int width = 1000;

    private final int height = 1500;

    private final int avatarMargin = 40;

    private final int avatarSize = 110;

    private final int triangleMarginLeft = avatarMargin + avatarSize + 15;

    private final int marginTopWithPreRecord = 40;

    private final int chatMsgBoxPadding = 23;

    private final int chatMsgBoxLRPadding = chatMsgBoxPadding;
    private int baseWritePoint = 0;


    private Color backgroundColor = new Color(150, 150, 150);

    private Color youChatBoxColor = Color.white;

    private Color meChatBoxColor = new Color(160, 234, 113);

    private Color textColor = Color.BLACK;

    private BufferedImage img = null;

    private int imgType = BufferedImage.TYPE_INT_ARGB;
    private Graphics2D graph2D = null;

    private Shape clip = null;

    private BufferedImage youAvatar;

    private BufferedImage meAvatar;
    private Font textFont = null;


    public static void main(String[] args) throws Exception {
        WeChatRecordsGenerate generate = new WeChatRecordsGenerate();
        generate.run();
        generate.writeToFile(new File("target/chatTest.png"));
    }

    @SneakyThrows
    public void init() {
        img = new BufferedImage(width, height, imgType);
        graph2D = img.createGraphics();
        graph2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        clip = new Rectangle2D.Double(0, 0, width, height);
        graph2D.setClip(clip);

        youAvatar = ImageIO.read(new File("you.jpg"));
        meAvatar = ImageIO.read(new File("me.jpg"));
//        InputStream stream = WeChatRecordsGenerate.class.getResourceAsStream("/SourceHanSansSC-Normal-2.otf");
//        Font font;
//        try {
//            font = Font.createFont(Font.TRUETYPE_FONT, stream);
//        } catch (FontFormatException | IOException e) {
//            throw new RuntimeException(e);
//        }
//        double v = width * 0.68 / 13.0;
//        textFont = font.deriveFont(Font.PLAIN, 40);
        textFont = new Font("微软雅黑", Font.PLAIN, 45);

        graph2D.setFont(textFont);
//        .getLineMetrics()
    }

    public void run() {
        this.init();
        drawBackground();
        setStartLineAt();


        drawYouChatLine();
        drawMeChatLine();
        graph2D.dispose();
    }

    private void setStartLineAt() {
        baseWritePoint = 118 - marginTopWithPreRecord;
    }

    public void drawBackground() {
        graph2D.setColor(backgroundColor);
        graph2D.fillRect(0, 0, width, height);
    }

    public void drawMeChatLine() {
        int baseY = baseWritePoint;
        drawMeAvatar(baseY);
        int bottom = drawMeChatBox(baseY, "后来呢?");
        baseWritePoint = bottom + marginTopWithPreRecord;
    }

    public void drawYouChatLine() {
        int baseY = baseWritePoint;
        drawYouAvatar(baseY);

        int bottom = drawYouChatBox(baseY, "有一天，班级群有个人发消息aa说：“不要出声！有人进了宿舍！”");
        drawYouChatTag(baseY);
        baseWritePoint = bottom + marginTopWithPreRecord;
    }

    private void drawMeAvatar(int baseY) {
        BufferedImage meAvatar = roundImg(this.meAvatar, avatarSize, 16);
        try {
            ImageIO.write(meAvatar, "png", new File("target/me.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graph2D.drawImage(meAvatar, width - avatarSize - avatarMargin, baseY, null);
    }

    private void drawYouAvatar(int baseY) {
        BufferedImage youAvatar = roundImg(this.youAvatar, avatarSize, 16);
        try {
            ImageIO.write(youAvatar, "png", new File("target/you.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graph2D.drawImage(youAvatar, avatarMargin, baseY, null);
    }

    private BufferedImage roundImg(BufferedImage srcImg, int size, int radius) {
        BufferedImage targetImg = new BufferedImage(size, size, imgType);
        Graphics2D ig = targetImg.createGraphics();
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig.setComposite(AlphaComposite.Src);
        ig.setColor(Color.WHITE);
        ig.fill(new RoundRectangle2D.Double(0, 0, size, size, radius, radius));
        ig.setComposite(AlphaComposite.SrcAtop);
        ig.drawImage(srcImg, 0, 0, size, size, null);
        ig.dispose();
        return targetImg;
    }

    public int drawYouChatBox(int baseY, String msg) {
        double w = caleWidth(msg);
        double textAreaWidth = w - 2 * chatMsgBoxPadding;
        int x = triangleMarginLeft + 14;
        int height = drawMsg(x, baseY, textAreaWidth, msg, false) + 20;
        height = Math.max(height, avatarSize);
        graph2D.setColor(youChatBoxColor);
        drawYouChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, 16, 16));
        drawMsg(x, baseY, textAreaWidth, msg, true);
        return baseY + height;
    }

    public int drawMeChatBox(int baseY, String msg) {
        double w = caleWidth(msg);
        double textAreaWidth = w - 2 * chatMsgBoxPadding;
        int x = (int) (width - (w + triangleMarginLeft + 14));
        int height = drawMsg(x, baseY, textAreaWidth, msg, false) + 20;
        height = Math.max(height, avatarSize);
        graph2D.setColor(meChatBoxColor);
        drawMeChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, 16, 16));
        drawMsg(x, baseY, textAreaWidth, msg, true);
        return baseY + height;
    }

    private double caleWidth(String msg){
        double maxWidth = width * 0.7;
        FontMetrics fontMetrics = graph2D.getFontMetrics();
        Rectangle2D stringBounds = fontMetrics.getStringBounds(msg, graph2D);
        return Math.min(stringBounds.getWidth() + 2 * chatMsgBoxPadding, maxWidth - 2 * chatMsgBoxPadding);
    }


    private void drawYouChatTag(int baseY) {
        int midY = baseY + avatarSize / 2;
        int x = triangleMarginLeft + 14;
        graph2D.fillPolygon(
                new int[]{triangleMarginLeft, x, x},
                new int[]{midY, midY - 14, midY + 14},
                3);
    }

    private void drawMeChatTag(int baseY) {
        int midY = baseY + avatarSize / 2;
        int x = width - (triangleMarginLeft + 14);
        graph2D.fillPolygon(
                new int[]{x + 14, x, x},
                new int[]{midY, midY - 14, midY + 14},
                3);
    }


    public int drawMsg(int boxX, int boxY, double w, String msg, boolean draw) {
        graph2D.setColor(textColor);
        AttributedString text = new AttributedString(msg);
        text.addAttribute(TextAttribute.FONT, textFont, 0, msg.length());
        AttributedCharacterIterator iterator = text.getIterator();
        FontRenderContext frc = graph2D.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);
        float wrappingWidth = (float) w;
        int x = boxX + chatMsgBoxPadding;
        int y = boxY + chatMsgBoxPadding;
        boolean midLine = false;
        while (measurer.getPosition() < msg.length()) {
            TextLayout layout = measurer.nextLayout(wrappingWidth);
            Rectangle2D bounds = layout.getBounds();
            double lineSpacing;
            if (midLine) {
                lineSpacing = bounds.getHeight() * 0.2;
            } else {
                lineSpacing = 0;
                midLine = true;
            }
            y += layout.getAscent() + lineSpacing;
            if (draw) {
                layout.draw(graph2D, x, y);
            }
            y += layout.getDescent() + layout.getLeading();
        }
        return y - boxY;
    }

    public void writeToFile(File file) throws IOException {
        ImageIO.write(img, "png", file);
    }
}