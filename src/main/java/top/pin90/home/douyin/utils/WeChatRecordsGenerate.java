package top.pin90.home.douyin.utils;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

public class WeChatRecordsGenerate {

    private final int width = 600;

    private final int height = 1500;

    private final int tagD = (int) (width * 0.014);

    private final double chatBoxMaxWidth = width * 0.7;
    private final double chatBoxRadius = width * 0.02;

    private final int avatarSize = (int) (width * 0.115);

    private final int avatarMargin = (int) (avatarSize * 0.3478);

    private final int triangleMarginLeft = (int) (avatarMargin + avatarSize + width * 0.015);

    private final int marginTopWithPreRecord = (int) (width * 0.04);

    private final int chatMsgBoxPadding = (int) (width * 0.023);

    private int baseWritePoint = 0;


    private Color backgroundColor = new Color(150, 150, 150);

    private Color youChatBoxColor = Color.BLACK;

    private Color meChatBoxColor = new Color(160, 234, 113);

    private Color youTextColor = Color.WHITE;

    private Color meTextColor = Color.BLACK;

    private BufferedImage img = null;

    private int imgType = BufferedImage.TYPE_INT_ARGB;
    private Graphics2D graph2D = null;

    private boolean curImgHasMsg = false;

    private Shape clip = null;

    private BufferedImage youAvatar;
    private BufferedImage meAvatar;
    private Font textFont = null;

    private final Iterator<String> dataIter;

    private List<BufferedImage> results = new LinkedList<>();

    private BufferedImage result;
    private File outDir;

    private int curFileIndex = 0;

    public WeChatRecordsGenerate(Iterator<String> dataIter, File outDir) {
        this.dataIter = dataIter;
        this.outDir = outDir;
    }

    public static void main(String[] args) throws Exception {

        FileInputStream in = new FileInputStream("小说文本.txt");
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\n");
        List<String> list = new LinkedList<>();
        int i = 0;
        while (scanner.hasNext()) {
            String s = scanner.next();
            if (StringUtils.isNotBlank(s)) {
//                System.out.println(s);
                list.add(s);
            }
            i++;
//            if (i >= 4) {
//                break;
//            }
        }
        Iterator<String> iter = list.iterator();
        WeChatRecordsGenerate generate = new WeChatRecordsGenerate(iter, new File("target/chatTest"));
        generate.run();
    }

    @SneakyThrows
    public void init() {
        newImg();
        youAvatar = ImageIO.read(new File("you.jpg"));
        meAvatar = ImageIO.read(new File("me.jpg"));
        outDir.mkdirs();
        result = new BufferedImage(width, height, imgType);
    }

    public void run() {
        this.init();
        drawBackground();
        setStartLineAt();

        drawMeChatLine("你听说过什么恐怖故事？aaa");
        while (dataIter.hasNext()) {
            drawYouChatLine(dataIter.next());
        }
        save();
        collectOne();

    }

    private void setStartLineAt() {
        baseWritePoint = 118 - marginTopWithPreRecord;
    }

    public void drawBackground() {
        graph2D.setColor(backgroundColor);
        graph2D.fillRect(0, 0, width, height);
    }

    public void drawMeChatLine(String msg) {
        int baseY = baseWritePoint;
        int bottom = drawMeChatBox(baseY, msg);
        baseWritePoint = bottom + marginTopWithPreRecord;
    }

    public void drawYouChatLine(String msg) {
        int baseY = baseWritePoint;
        int bottom = drawYouChatBox(baseY, msg);
        baseWritePoint = bottom + marginTopWithPreRecord;
    }

    private void drawMeAvatar(int baseY) {
        BufferedImage meAvatar = roundImg(this.meAvatar, avatarSize, (int) (width * 0.016));
        try {
            ImageIO.write(meAvatar, "png", new File("target/me.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graph2D.drawImage(meAvatar, width - avatarSize - avatarMargin, baseY, null);
    }

    private void drawYouAvatar(int baseY) {
        BufferedImage youAvatar = roundImg(this.youAvatar, avatarSize, (int) (width * 0.016));
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
        int x = triangleMarginLeft + tagD;
        int height = drawMsg(x, baseY, textAreaWidth, msg, false) + chatMsgBoxPadding;
        height = Math.max(height, avatarSize);
        if (baseY + height - width * 0.02 >= this.height) {
            nextPage();
            baseY = baseWritePoint;
            height = drawMsg(x, baseY, textAreaWidth, msg, false) + chatMsgBoxPadding;
            height = Math.max(height, avatarSize);
        }
        graph2D.setColor(youChatBoxColor);
        drawYouChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, chatBoxRadius, chatBoxRadius));
        graph2D.setColor(youTextColor);
        drawMsg(x, baseY, textAreaWidth, msg, true);
        drawYouAvatar(baseY);
        return baseY + height;
    }

    public int drawMeChatBox(int baseY, String msg) {
        double w = caleWidth(msg);
        double textAreaWidth = w - 2 * chatMsgBoxPadding;
        int x = (int) (width - (w + triangleMarginLeft + tagD));
        int height = drawMsg(x, baseY, textAreaWidth, msg, false) + chatMsgBoxPadding;
        height = Math.max(height, avatarSize);
        graph2D.setColor(meChatBoxColor);
        drawMeChatTag(baseY);
        graph2D.fill(new RoundRectangle2D.Double(x, baseY, w, height, chatBoxRadius, chatBoxRadius));
        graph2D.setColor(meTextColor);
        drawMsg(x, baseY, textAreaWidth, msg, true);
        drawMeAvatar(baseY);
        return baseY + height;
    }

    private double caleWidth(String msg) {
        FontMetrics fontMetrics = graph2D.getFontMetrics();
        Rectangle2D stringBounds = fontMetrics.getStringBounds(msg, graph2D);
        return Math.min(stringBounds.getWidth() + 2 * chatMsgBoxPadding, chatBoxMaxWidth - 2 * chatMsgBoxPadding);
    }

    private void drawYouChatTag(int baseY) {
        int midY = baseY + avatarSize / 2;
        int x = triangleMarginLeft + tagD;
        graph2D.fillPolygon(
                new int[]{triangleMarginLeft, x, x},
                new int[]{midY, midY - tagD, midY + tagD},
                3);
    }

    private void drawMeChatTag(int baseY) {
        int midY = baseY + avatarSize / 2;
        int x = width - (triangleMarginLeft + tagD);
        graph2D.fillPolygon(
                new int[]{x + 14, x, x},
                new int[]{midY, midY - tagD, midY + tagD},
                3);
    }


    public int drawMsg(int boxX, int boxY, double w, String msg, boolean draw) {
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

    public void nextPage() {
        save();
        newImg();
    }

    public void newImg() {
        img = new BufferedImage(width, height, imgType);
        graph2D = img.createGraphics();
        graph2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        clip = new Rectangle2D.Double(0, 0, width, height);
        graph2D.setClip(clip);
        if (textFont == null) {
            try {
                InputStream in = this.getClass().getResourceAsStream("/SourceHanSansSC-Normal-2.otf");
                Font font = Font.createFont(Font.TRUETYPE_FONT, in);
                textFont = font.deriveFont(Font.PLAIN, (float) (width * 0.045));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//        textFont = new Font("微软雅黑", Font.PLAIN, 45);
        }
        graph2D.setFont(textFont);

        setStartLineAt();
        drawBackground();
    }

    public void save() {
        try {
            BufferedImage newPic = new BufferedImage(width, height,
                    BufferedImage.TYPE_3BYTE_BGR);

//            float[] data = { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.125f, 0.125f,
//                    0.0625f, 0.125f, 0.0625f };
            float[] data =
                    {-1.0f, -1.0f, -1.0f, -1.0f, 10.0f, -1.0f, -1.0f, -1.0f, -1.0f};
            Kernel kernel = new Kernel(3, 3, data);
            ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            co.filter(img, newPic);

            results.add(img);
            graph2D.dispose();
//            ImageIO.write(img, "png", new File(outDir.getAbsoluteFile() + File.separator + (++curFileIndex) + ".png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void collectOne() {
        int height = 0;
        int slot = (int) (width * 0.005);
        for (BufferedImage bufferedImage : results) {
            height += bufferedImage.getHeight();
            height += slot;
        }
        height -= slot;
        BufferedImage result = new BufferedImage(width, height, imgType);
        Graphics2D g = result.createGraphics();
        int y = 0;
        for (int i = 0; i < results.size(); i++) {
            BufferedImage img = results.get(i);
            g.drawImage(img, 0, y, img.getWidth(), img.getHeight(), null);
            if (i != results.size() - 1) {
                y += img.getHeight();
                y += slot;
            }
        }
        this.result = result;
        try {
            ImageIO.write(result, "png", new File(outDir.getAbsoluteFile() + File.separator + "result.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}