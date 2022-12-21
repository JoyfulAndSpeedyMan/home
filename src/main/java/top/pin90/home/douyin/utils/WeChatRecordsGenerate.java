package top.pin90.home.douyin.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeChatRecordsGenerate {

    private final int width = 1000;

    private final int height = 1500;

    private final int avatarMargin = 40;

    private final int avatarSize = 110;

    private final int triangleMarginLeft = avatarMargin + 15;

    private final int marginTopWithPreRecord = 40;

    private int baseWritePoint = 0;

    private Color backgroundColor = new Color(150, 150, 150);

    private Color youChatBoxColor = Color.BLACK;

    private Color meChatBoxColor = new Color(160, 234, 113);
    private BufferedImage img = null;

    private int imgType = 
    private Graphics2D graphics = null;


    public static void main(String[] args) throws Exception {
        WeChatRecordsGenerate generate = new WeChatRecordsGenerate();
        generate.run();
        generate.writeToFile(new File("target/chatTest.png"));
    }

    public void run() {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = img.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);

        drawBackground();

        setStartLineAt();
//        drawMeChatLine();
        drawYouChatLine();
        graphics.dispose();
    }

    private void setStartLineAt() {
        baseWritePoint = 118 - marginTopWithPreRecord;
    }

    public void drawBackground() {
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, width, height);
    }

    public void drawMeChatLine() {

    }

    public void drawYouChatLine() {
        int baseY = baseWritePoint + marginTopWithPreRecord;
        drawYouAvatar(baseY);
    }

    private void drawYouAvatar(int baseY) {
        try {
            BufferedImage src = ImageIO.read(new File("1.jpg"));
            BufferedImage bufferedImage = new BufferedImage(avatarSize, avatarSize;
            Graphics2D imgG = src.createGraphics();
            imgG.drawImage(src, 0, 0, avatarSize, avatarSize, null);
            imgG.clip(new RoundRectangle2D.Double(avatarMargin, baseY, avatarSize, avatarSize, 16, 16));
//            Shape back = graphics.getClip();
//            graphics.clip(new RoundRectangle2D.Double(avatarMargin, baseY, avatarSize, avatarSize, 16, 16));

            graphics.drawImage(src, avatarMargin, baseY, avatarSize, avatarSize, null);
//            graphics.setClip(back);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawYouChatBox(int baseY) {
        graphics.setColor(youChatBoxColor);
        int midX = baseY + avatarSize / 2;
        graphics.fillPolygon(
                new int[]{midX, midX - 14, midX + 14},
                new int[]{triangleMarginLeft, triangleMarginLeft + 14, triangleMarginLeft + 14},
                3);
    }

    public void writeToFile(File file) throws IOException {
        ImageIO.write(img, "png", file);
    }
}