package top.pin90.home.utils.douyin.record;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import top.pin90.home.utils.douyin.record.config.WechatRecordGenerateConfig;

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

@Slf4j
public class WeChatRecordsGenerate {

    private final int imgType = BufferedImage.TYPE_INT_ARGB;
    private BufferedImage img = null;
    private Graphics2D graph2D = null;
    private Font textFont = null;
    private int baseWritePoint = 0;
    private int curFileIndex = 0;
    private final List<BufferedImage> results = new LinkedList<>();

    private final List<Integer> lastBaseWritePoints = new LinkedList<>();

    private BufferedImage result;

    private final WechatRecordGenerateConfig config;

    private final WechatRecordGenerateConfig.DrawConfig drawConfig;

    private boolean debug = false;

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
//        generate.setDebug(true);
        generate.run();
    }

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
        WechatRecordGenerateConfig.DataConfig dataConfig = config.getDataConfig();
        Iterator<WechatRecordGenerateConfig.DateRecord> dataIter = dataConfig.getDataIter();
        while (dataIter.hasNext()) {
            WechatRecordGenerateConfig.DateRecord next = dataIter.next();
            if (next.getSpeakerId() == WechatRecordGenerateConfig.DateRecord.YOU) {
                drawYouChatLine(next.getMsg());
            } else if (next.getSpeakerId() == WechatRecordGenerateConfig.DateRecord.ME) {
                drawMeChatLine(next.getMsg());
            } else if (next.getSpeakerId() == WechatRecordGenerateConfig.DateRecord.TIME_LINE) {
                drawTimeLine(next.getMsg());
            }
        }
        save();
        collectOne();

    }

    private void setStartLineAt() {
        if (results.isEmpty()) {
            baseWritePoint = 118 - drawConfig.getMarginTopWithPreRecord();
        } else {
            baseWritePoint = 0;
        }
    }


    public void fillBackground() {
        graph2D.setColor(config.getBackgroundConfig().getBackgroundColor());
        graph2D.fillRect(0, 0, config.getDrawConfig().getWidth(), config.getDrawConfig().getHeight());
    }

    public void drawTimeLine(String time) {
        Font font = textFont.deriveFont(Font.PLAIN, (float) (config.getDrawConfig().getWidth() * 0.03));
        TextLayout textLayout = new TextLayout(time, font, graph2D.getFontRenderContext());
        double w = textLayout.getBounds().getWidth();
        float x = (float) (config.getDrawConfig().getWidth() / 2 - w / 2);
        float y = baseWritePoint + drawConfig.getMarginTopWithPreRecord() + textLayout.getAscent();
        graph2D.setColor(new Color(88, 88, 88));
        textLayout.draw(graph2D, x, y);
        baseWritePoint = (int) (y + textLayout.getDescent());
    }

    public void drawMeChatLine(String msg) {
        int baseY = baseWritePoint + drawConfig.getMarginTopWithPreRecord();
        baseWritePoint = drawMeChatBox(baseY, msg);
    }

    public void drawYouChatLine(String msg) {
        int baseY = baseWritePoint + drawConfig.getMarginTopWithPreRecord();
        baseWritePoint = drawYouChatBox(baseY, msg);
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
        Pair<Integer, Integer> pair = caleHeightAndNextPage(msg, x, baseY, textAreaWidth);
        baseY = pair.getKey();
        int height = pair.getRight();
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
        Pair<Integer, Integer> pair = caleHeightAndNextPage(msg, x, baseY, textAreaWidth);
        baseY = pair.getKey();
        int height = pair.getRight();
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

    private Pair<Integer, Integer> caleHeightAndNextPage(String msg, int x, int baseY, double textAreaWidth) {
        int topAndBottomPadding = drawConfig.getChatMsgBoxPadding() << 1;
        int height = drawMsg(x, baseY, textAreaWidth, msg, false) + topAndBottomPadding;
//        height = Math.max(height, drawConfig.getAvatarSize());
        // 下一页逻辑
        if (baseY + height - drawConfig.getMarginTopWithPreRecord() >= drawConfig.getHeight()) {
            nextPage();
            baseY = baseWritePoint;
            height = drawMsg(x, baseY, textAreaWidth, msg, false) + topAndBottomPadding;
            height = Math.max(height, drawConfig.getAvatarSize());
        }
        return ImmutablePair.of(baseY, height);
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
        text.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON, 0, msg.length());

        AttributedCharacterIterator iterator = text.getIterator();
        FontRenderContext frc = graph2D.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);
//        measurer.
        float wrappingWidth = (float) w;
        int x = boxX + drawConfig.getChatMsgBoxPadding();
        int y = boxY + drawConfig.getChatMsgBoxPadding();
        int textBaseY = y;
        boolean midLine = false;

        int cn = 0;
        while (measurer.getPosition() < msg.length()) {
            cn = measurer.getPosition() - cn;
            TextLayout layout = measurer.nextLayout(wrappingWidth);
            Rectangle2D bounds = layout.getBounds();
            double lineSpacing;
            if (midLine) {
                lineSpacing = bounds.getHeight() * 0.2;
            } else {
                lineSpacing = 0;
                midLine = true;
            }
            // 行距
            y += lineSpacing;
            // 计算基线的位置
            y += layout.getAscent();
            if (draw) {
                layout.draw(graph2D, x, y);

                if (debug) {
                    int ty = (int) (y - (layout.getAscent()));
                    Color bc = graph2D.getColor();
                    Rectangle pixelBounds = layout.getPixelBounds(null, x, y);
                    graph2D.setColor(Color.yellow);
                    graph2D.drawRect(x, ty, (int) pixelBounds.getWidth() + 3, (int) (pixelBounds.getHeight() + layout.getDescent()));
                    graph2D.setColor(bc);
                }
            }
            y += layout.getLeading();
            y += layout.getDescent();
        }
        return y - textBaseY;
    }

    public void nextPage() {
        save();
        newImg();
    }

    public void newImg() {
        img = new BufferedImage(drawConfig.getWidth(), drawConfig.getHeight(), imgType);
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
            lastBaseWritePoints.add(baseWritePoint);
            if (config.getOutConfig().isOutputMiddleImg()) {
                ImageIO.write(img, "png", new File(config.getOutConfig().getOutAllImgDir() + File.separator + (++curFileIndex) + ".png"));
            }
            graph2D.dispose();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void collectOne() {
        log.info("图片生成完成，开始整合图片");
        int height = 0;
        int slot = drawConfig.getSlot();
        for (Integer lastBaseWritePoint : lastBaseWritePoints) {
            height += lastBaseWritePoint + drawConfig.getMarginTopWithPreRecord();
            height += slot;
        }
        height -= slot;
        BufferedImage result = new BufferedImage(config.getDrawConfig().getWidth(), height, imgType);
        Graphics2D g = result.createGraphics();
        int y = 0;
        Color slotColor = new Color(211, 211, 211);
        for (int i = 0; i < results.size(); i++) {
            BufferedImage img = results.get(i);
            int h = lastBaseWritePoints.get(i);
            g.drawImage(img, 0, y, img.getWidth(), y + h, 0, 0, img.getWidth(), h, null);
            y += h;
            g.setColor(config.getBackgroundConfig().getBackgroundColor());
            g.fillRect(0, y, img.getWidth(), drawConfig.getMarginTopWithPreRecord());
            y += drawConfig.getMarginTopWithPreRecord();
            if (i != results.size() - 1 && slot > 0) {
                g.setColor(slotColor);
                g.fillRect(0, y, img.getWidth(), slot);
                y += slot;
            }
        }
        this.result = result;
        log.info("图片整合完成，准备写入文件");
        try {
            if (StringUtils.isNotBlank(config.getOutConfig().getOutFile())) {
//                File file = new File(classpath + File.separator + config.getOutConfig().getOutFile());
                File file = new File(config.getOutConfig().getOutFile());
                log.info("文件将写入到 {}", file.getAbsolutePath());
                ImageIO.write(result, config.getOutConfig().getFileFormat(), file);
                log.info("文件将写入完成");
            } else {
                if (StringUtils.isBlank(config.getOutConfig().getOutAllImgDir())) {
                    throw new RuntimeException("没有输出位置");
                }
                File file = new File(config.getOutConfig().getOutAllImgDir() + File.separator + "result.png");
                ImageIO.write(result, "png", file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}