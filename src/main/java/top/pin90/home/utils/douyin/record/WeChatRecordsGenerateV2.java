package top.pin90.home.utils.douyin.record;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import top.pin90.home.utils.douyin.record.config.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class WeChatRecordsGenerateV2 {

    private final int imgType = BufferedImage.TYPE_INT_ARGB;
    private BufferedImage img = null;
    private Graphics2D graph2D = null;
    private Font textFont = null;
    private int baseWritePoint = 0;
    private int curFileIndex = 0;
    private final List<BufferedImage> results = new LinkedList<>();

    private final List<Integer> lastBaseWritePoints = new LinkedList<>();

    private BufferedImage result;
    private final WechatRecordGenerateConfigV2 configV2;

    private final DataConfig dataConfig;

    private final ThemeConfig themeConfig;

    private final DrawConfig drawConfig;

    private OutConfig outConfig;

    private File baseOutDir = null;

    private File resultFile = null;

    private File outMidImgDir = null;

    private File sectionOutDir = null;

    private boolean debug = false;

    public WeChatRecordsGenerateV2(WechatRecordGenerateConfigV2 configV2) {
        Objects.requireNonNull(configV2);
        Objects.requireNonNull(configV2.getDataConfig());
        dataConfig = configV2.getDataConfig();
        themeConfig = configV2.getThemeConfig() == null ? new ThemeConfig() : configV2.getThemeConfig();
        drawConfig = configV2.getDrawConfig() == null ? new DrawConfig() : configV2.getDrawConfig();
        outConfig = configV2.getOutConfig() == null ? new OutConfig() : configV2.getOutConfig();
        this.configV2 = configV2;
    }

    public static void main(String[] args) throws Exception {
        WechatRecordGenerateConfigV2 config = new WechatRecordGenerateConfigV2();
        config.setDataConfig(DataConfig.twoOf(DataConfig.getExampleDataIter(), null, null));
        WeChatRecordsGenerateV2 generate = new WeChatRecordsGenerateV2(config);
//        generate.setDebug(true);
        generate.run();
    }

    public void init() {
        textFont = drawConfig.getTextFont();
        result = new BufferedImage(drawConfig.getWidth(), drawConfig.getHeight(), imgType);
        newImg();
    }

    public void run() {
        this.init();
        fillBackground();
        setStartLineAt();
        Iterator<DataConfig.DateRecord> dataIter = dataConfig.getDataIter();
        while (dataIter.hasNext()) {
            DataConfig.DateRecord next = dataIter.next();
            if (next.getSpeakerId() == DataConfig.DateRecord.ME) {
                drawMeChatLine(next.getMsg());
            } else if (next.getSpeakerId() == DataConfig.DateRecord.TIME_LINE) {
                drawTimeLine(next.getMsg());
            } else {
                drawOtherChatLine(next.getSpeakerId(), next.getMsg());
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
        graph2D.setColor(themeConfig.getBackgroundColor());
        graph2D.fillRect(0, 0, drawConfig.getWidth(), drawConfig.getHeight());
    }

    public void drawTimeLine(String time) {
        Font font = textFont.deriveFont(Font.PLAIN, (float) (drawConfig.getWidth() * 0.03));
        TextLayout textLayout = new TextLayout(time, font, graph2D.getFontRenderContext());
        double w = textLayout.getBounds().getWidth();
        float x = (float) (drawConfig.getWidth() / 2 - w / 2);
        float y = baseWritePoint + drawConfig.getMarginTopWithPreRecord() + textLayout.getAscent();
        graph2D.setColor(new Color(88, 88, 88));
        textLayout.draw(graph2D, x, y);
        baseWritePoint = (int) (y + textLayout.getDescent());
    }

    public void drawMeChatLine(String msg) {
        int baseY = baseWritePoint + drawConfig.getMarginTopWithPreRecord();
        baseWritePoint = drawMeChatBox(baseY, msg);
    }

    public void drawOtherChatLine(int speakerId, String msg) {
        int baseY = baseWritePoint + drawConfig.getMarginTopWithPreRecord();
        baseWritePoint = drawOtherChatBox(speakerId, baseY, msg);
    }

    private void drawMeAvatar(int baseY) {
        BufferedImage meAvatar = roundImg(dataConfig.getMyPersonInfo().getAvatar(), drawConfig.getAvatarSize(), (int) (drawConfig.getWidth() * 0.016));
        try {
            ImageIO.write(meAvatar, "png", new File("target/me.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graph2D.drawImage(meAvatar, drawConfig.getWidth() - drawConfig.getAvatarSize() - drawConfig.getAvatarMargin(), baseY, null);

    }

    private void drawYouAvatar(int speakerId, int baseY) {
        DataConfig.PersonInfo personInfo = dataConfig.getPersonInfo(speakerId);
        Objects.requireNonNull(personInfo, "speakerId: " + speakerId + " 不存在");
        BufferedImage youAvatar = roundImg(personInfo.getAvatar(), drawConfig.getAvatarSize(), (int) (drawConfig.getWidth() * 0.016));
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

    public int drawOtherChatBox(int speakerId, int baseY, String msg) {
        double w = caleWidth(msg);
        double textAreaWidth = w - 2 * drawConfig.getChatMsgBoxPadding();
        int x = drawConfig.getTriangleMarginLeft() + drawConfig.getTagD();
        Pair<Integer, Integer> pair = caleHeightAndNextPage(msg, x, baseY, textAreaWidth);
        baseY = pair.getKey();
        int height = pair.getRight();
        graph2D.setColor(themeConfig.getOtherBoxColor());
        drawYouChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, drawConfig.getChatBoxRadius(), drawConfig.getChatBoxRadius()));
        graph2D.setColor(themeConfig.getOtherTextColor());
        drawMsg(x, baseY, textAreaWidth, msg, true);
        drawYouAvatar(speakerId, baseY);
        return baseY + height;
    }

    public int drawMeChatBox(int baseY, String msg) {
        double w = caleWidth(msg);
        double textAreaWidth = w - 2 * drawConfig.getChatMsgBoxPadding();
        int x = (int) (drawConfig.getWidth() - (w + drawConfig.getTriangleMarginLeft() + drawConfig.getTagD()));
        Pair<Integer, Integer> pair = caleHeightAndNextPage(msg, x, baseY, textAreaWidth);
        baseY = pair.getKey();
        int height = pair.getRight();
        graph2D.setColor(themeConfig.getMeBoxColor());
        drawMeChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, drawConfig.getChatBoxRadius(), drawConfig.getChatBoxRadius()));
        graph2D.setColor(themeConfig.getMeTextColor());
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
        if (baseY + height + topAndBottomPadding - drawConfig.getMarginTopWithPreRecord() >= drawConfig.getHeight()) {
            nextPage();
            baseY = baseWritePoint;
            height = drawMsg(x, baseY, textAreaWidth, msg, false) + topAndBottomPadding;
//            height = Math.max(height, drawConfig.getAvatarSize());
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
        int x = drawConfig.getWidth() - (drawConfig.getTriangleMarginLeft() + drawConfig.getTagD());
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
            if (outConfig.isOutMidImg()) {
                makeSureOutMidImgDir();
                ImageIO.write(img, "png",
                        new File(outMidImgDir.getAbsoluteFile() + File.separator + (++curFileIndex) + ".png"));
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
        BufferedImage result = new BufferedImage(drawConfig.getWidth(), height, imgType);
        Graphics2D g = result.createGraphics();
        int y = 0;
        Color slotColor = new Color(211, 211, 211);
        for (int i = 0; i < results.size(); i++) {
            BufferedImage img = results.get(i);
            int h = lastBaseWritePoints.get(i);
            g.drawImage(img, 0, y, img.getWidth(), y + h, 0, 0, img.getWidth(), h, null);
            y += h;
            g.setColor(themeConfig.getBackgroundColor());
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
            makeSureResultFile();
            String outPath = resultFile.getAbsolutePath();
            log.info("文件将写入到 {}", outPath);
            String fileFormat = outPath.substring(outPath.lastIndexOf('.') + 1);
            ImageIO.write(result, fileFormat, resultFile);
            log.info("文件将写入完成");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void makeSureBaseOutDir() {
        if (baseOutDir != null)
            return;
        if (StringUtils.isNotBlank(outConfig.getBaseOutDir())) {
            baseOutDir = new File(outConfig.getBaseOutDir());
        } else {
            baseOutDir = new File("chatOut");
        }
    }

    private void makeSureResultFile() {
        if (resultFile != null)
            return;
        makeSureFile(outConfig::getResultFile, () -> OutConfig.DEFAULT_OUT_FILE, (file -> resultFile = file));
    }

    private void makeSureOutMidImgDir() {
        if (outMidImgDir != null)
            return;
        makeSureFile(outConfig::getOutMidImgDir, () -> OutConfig.DEFAULT_MID_IMG_OUT_DIR, (file -> outMidImgDir = file));
        //noinspection ResultOfMethodCallIgnored
        outMidImgDir.mkdirs();
    }

    private void makeSureSectionOutDir() {
        if (sectionOutDir != null)
            return;
        makeSureFile(outConfig::getSectionOutDir, () -> OutConfig.DEFAULT_SECTION_OUT_DIR, (file -> sectionOutDir = file));
        //noinspection ResultOfMethodCallIgnored
        sectionOutDir.mkdirs();
    }

    private void makeSureFile(Supplier<String> filePath, Supplier<String> defaultFilePath, Consumer<File> setFile) {
        String path = filePath.get();
        if (StringUtils.isNotBlank(path)) {
            File file = new File(path);
            if (file.isAbsolute()) {
                setFile.accept(file);
            } else {
                makeSureBaseOutDir();
                setFile.accept(new File(baseOutDir.getAbsoluteFile() + File.separator + outConfig.getResultFile()));
            }
        } else {
            makeSureBaseOutDir();
            setFile.accept(new File(baseOutDir.getAbsoluteFile() + File.separator + defaultFilePath.get()));
        }
    }

}