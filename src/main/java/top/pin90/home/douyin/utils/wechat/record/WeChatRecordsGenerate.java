package top.pin90.home.douyin.utils.wechat.record;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import top.pin90.home.douyin.utils.wechat.record.config.WechatRecordGenerateConfig;

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
import java.util.*;
import java.util.List;

public class WeChatRecordsGenerate {

    private int imgType = BufferedImage.TYPE_INT_ARGB;
    private BufferedImage img = null;
    private Graphics2D graph2D = null;
    private Font textFont = null;

    private File outDir;

    private int baseWritePoint = 0;

    private int curFileIndex = 0;
    private List<BufferedImage> results = new LinkedList<>();

    private BufferedImage result;

    private WechatRecordGenerateConfig config;

    private WechatRecordGenerateConfig.DrawConfig drawConfig;

    public WeChatRecordsGenerate(WechatRecordGenerateConfig config) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(config.getDataConfig());
        Objects.requireNonNull(config.getBackgroundConfig());
        Objects.requireNonNull(config.getMeChatConfig());
        Objects.requireNonNull(config.getYouChatConfig());
        Objects.requireNonNull(config.getOutConfig());
        Objects.requireNonNull(config.getDrawConfig());
        drawConfig = config.getDrawConfig();
        this.config = config;
    }

    public static void main(String[] args) throws Exception {
        WechatRecordGenerateConfig config = WechatRecordGenerateConfig.darkDefaultConfig();
        WeChatRecordsGenerate generate = new WeChatRecordsGenerate(config);
        generate.run();
    }

    @SneakyThrows
    public void init() {
        WechatRecordGenerateConfig.DrawConfig drawConfig = config.getDrawConfig();


        textFont = config.getDrawConfig().getTextFont();
        if (StringUtils.isNotBlank(config.getOutConfig().getOutAllImgDir())) {
            File file = new File(config.getOutConfig().getOutAllImgDir());
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        result = new BufferedImage(drawConfig.getWidth(), config.getDrawConfig().getHeight(), imgType);
        newImg();
    }

    public void run() {
        this.init();
        fillBackground();
        setStartLineAt();

        drawMeChatLine("你听说过什么恐怖故事？");
        drawTimeLine();
//        while (dataIter.hasNext()) {
//            drawYouChatLine(dataIter.next());
//        }
        save();
        collectOne();

    }

    private void setStartLineAt() {
        baseWritePoint = 118 - drawConfig.getMarginTopWithPreRecord();
    }


    public void fillBackground() {
        graph2D.setColor(config.getBackgroundConfig().getBackgroundColor());
        graph2D.fillRect(0, 0, config.getDrawConfig().getWidth(), config.getDrawConfig().getHeight());
    }

    public void drawTimeLine() {
        Font font = textFont.deriveFont(Font.PLAIN, (float) (config.getDrawConfig().getWidth() * 0.03));
        TextLayout textLayout = new TextLayout(config.getDataConfig().getTime(), font, graph2D.getFontRenderContext());
        double w = textLayout.getBounds().getWidth();
        float x = (float) (config.getDrawConfig().getWidth() / 2 - w / 2);
        float y = baseWritePoint + drawConfig.getMarginTopWithPreRecord() + textLayout.getAscent();
        graph2D.setColor(new Color(141, 140, 150));
        textLayout.draw(graph2D, x, y);
        baseWritePoint = (int) (y + textLayout.getDescent());
    }

    public void drawMeChatLine(String msg) {
        int baseY = baseWritePoint + drawConfig.getMarginTopWithPreRecord();
        int bottom = drawMeChatBox(baseY, msg);
        baseWritePoint = bottom;
    }

    public void drawYouChatLine(String msg) {
        int baseY = baseWritePoint + drawConfig.getMarginTopWithPreRecord();
        int bottom = drawYouChatBox(baseY, msg);
        baseWritePoint = bottom;
    }

    private void drawMeAvatar(int baseY) {
        BufferedImage meAvatar = roundImg(config.getMeChatConfig().getAvatar(), drawConfig.getAvatarSize(), (int) (config.getDrawConfig().getWidth() * 0.016));
        try {
            ImageIO.write(meAvatar, "png", new File("target/me.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graph2D.drawImage(meAvatar, config.getDrawConfig().getWidth() - drawConfig.getAvatarSize() - drawConfig.getAvatarMargin(), baseY, null);

    }

    private void drawYouAvatar(int baseY) {
        BufferedImage youAvatar = roundImg(config.getYouChatConfig().getAvatar(), drawConfig.getAvatarSize(), (int) (config.getDrawConfig().getWidth() * 0.016));
        try {
            ImageIO.write(youAvatar, "png", new File("target/you.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graph2D.drawImage(youAvatar, drawConfig.getAvatarMargin(), baseY, null);
    }

    private BufferedImage roundImg(Image srcImg, int size, int radius) {
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
        double textAreaWidth = w - 2 * drawConfig.getChatMsgBoxPadding();
        int x = drawConfig.getTriangleMarginLeft() + drawConfig.getTagD();
        int height = drawMsg(x, baseY, textAreaWidth, msg, false) + drawConfig.getChatMsgBoxPadding();
        height = Math.max(height, drawConfig.getAvatarSize());
        if (baseY + height - config.getDrawConfig().getWidth() * 0.02 >= config.getDrawConfig().getHeight()) {
            nextPage();
            baseY = baseWritePoint;
            height = drawMsg(x, baseY, textAreaWidth, msg, false) + drawConfig.getChatMsgBoxPadding();
            height = Math.max(height, drawConfig.getAvatarSize());
        }
        graph2D.setColor(config.getYouChatConfig().getBoxColor());
        drawYouChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, drawConfig.getChatBoxRadius(), drawConfig.getChatBoxRadius()));
        graph2D.setColor(config.getYouChatConfig().getTextColor());
        drawMsg(x, baseY, textAreaWidth, msg, true);
        drawYouAvatar(baseY);
        return baseY + height;
    }

    public int drawMeChatBox(int baseY, String msg) {
        double w = caleWidth(msg);
        double textAreaWidth = w - 2 * drawConfig.getChatMsgBoxPadding();
        int x = (int) (config.getDrawConfig().getWidth() - (w + drawConfig.getTriangleMarginLeft() + drawConfig.getTagD()));
        int height = drawMsg(x, baseY, textAreaWidth, msg, false) + drawConfig.getChatMsgBoxPadding();
        height = Math.max(height, drawConfig.getAvatarSize());
        graph2D.setColor(config.getMeChatConfig().getBoxColor());
        drawMeChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, drawConfig.getChatBoxRadius(), drawConfig.getChatBoxRadius()));
        graph2D.setColor(config.getMeChatConfig().getTextColor());
        drawMsg(x, baseY, textAreaWidth, msg, true);
        drawMeAvatar(baseY);
        return baseY + height;
    }

    private double caleWidth(String msg) {
        FontMetrics fontMetrics = graph2D.getFontMetrics();
        Rectangle2D stringBounds = fontMetrics.getStringBounds(msg, graph2D);
        return Math.min(stringBounds.getWidth() + 2 * drawConfig.getChatMsgBoxPadding(), drawConfig.getChatBoxMaxWidth() - 2 * drawConfig.getChatMsgBoxPadding());

    }

    private void drawYouChatTag(int baseY) {
        int midY = baseY + drawConfig.getAvatarSize() / 2;
        int x = drawConfig.getTriangleMarginLeft() + drawConfig.getTagD();
        graph2D.fillPolygon(
                new int[]{drawConfig.getTriangleMarginLeft(), x, x},
                new int[]{midY, midY - drawConfig.getTagD(), midY + drawConfig.getTagD()},
                3);
    }

    private void drawMeChatTag(int baseY) {
        int midY = baseY + drawConfig.getAvatarSize() / 2;
        int x = config.getDrawConfig().getWidth() - (drawConfig.getTriangleMarginLeft() + drawConfig.getTagD());
        graph2D.fillPolygon(
                new int[]{x + 14, x, x},
                new int[]{midY, midY - drawConfig.getTagD(), midY + drawConfig.getTagD()},
                3);
    }


    public int drawMsg(int boxX, int boxY, double w, String msg, boolean draw) {
        AttributedString text = new AttributedString(msg);
        text.addAttribute(TextAttribute.FONT, textFont, 0, msg.length());
        AttributedCharacterIterator iterator = text.getIterator();
        FontRenderContext frc = graph2D.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);
        float wrappingWidth = (float) w;
        int x = boxX + drawConfig.getChatMsgBoxPadding();
        int y = boxY + drawConfig.getChatMsgBoxPadding();
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

    public void nextPage() {
        save();
        newImg();
    }

    public void newImg() {
        img = new BufferedImage(config.getDrawConfig().getWidth(), config.getDrawConfig().getHeight(), imgType);
        graph2D = img.createGraphics();
        graph2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        graph2D.setFont(textFont);

        setStartLineAt();
        fillBackground();
    }

    public void save() {
        try {
            results.add(img);
            graph2D.dispose();
//            ImageIO.write(img, "png", new File(outDir.getAbsoluteFile() + File.separator + (++curFileIndex) + ".png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void collectOne() {
        int height = 0;
        int slot = (int) (config.getDrawConfig().getWidth() * 0.002);
        for (BufferedImage bufferedImage : results) {
            height += bufferedImage.getHeight();
            height += slot;
        }
        height -= slot;
        BufferedImage result = new BufferedImage(config.getDrawConfig().getWidth(), height, imgType);
        Graphics2D g = result.createGraphics();
        g.setColor(new Color(211, 211, 211));
        int y = 0;
        for (int i = 0; i < results.size(); i++) {
            BufferedImage img = results.get(i);
            g.drawImage(img, 0, y, img.getWidth(), img.getHeight(), null);
            if (i != results.size() - 1) {
                y += img.getHeight();
                g.fillRect(0, y, img.getWidth(), slot);
                y += slot;
            }
        }
        this.result = result;
        try {
            String classpath = this.getClass().getResource("/").getFile();
            if (StringUtils.isNotBlank(config.getOutConfig().getOutFile())) {
//                File file = new File(classpath + File.separator + config.getOutConfig().getOutFile());
                File file = new File(config.getOutConfig().getOutFile());
                ImageIO.write(result, config.getOutConfig().getFileFormat(), file);
            } else {
                if (StringUtils.isBlank(config.getOutConfig().getOutAllImgDir())) {
                    throw new RuntimeException("没有输出位置");
                }
                ImageIO.write(result, "png",
                        new File(classpath + File.separator + config.getOutConfig().getOutAllImgDir() + File.separator + "result.png"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}