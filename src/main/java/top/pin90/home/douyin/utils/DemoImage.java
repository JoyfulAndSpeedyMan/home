package top.pin90.home.douyin.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class DemoImage {
    public static void main(String[] args) throws IOException {
        BufferedImage img = new BufferedImage(600, 1000, BufferedImage.TYPE_INT_ARGB);

        Graphics g1 = img.getGraphics();
        g1.setColor(Color.ORANGE);
        g1.drawOval(50, 50, 100, 100);
        g1.dispose();
        ImageIO.write(img,"png",new File("target/g1.png"));

        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);


        g2.setColor(Color.RED);
        // g2.setStroke(new BasicStroke(5));
        // g2.drawOval(100, 100, 50, 50); //空心圆

        // 画点
        // g2.drawLine(150, 150, 150,150);
        Random rand = new Random();
        for (int i = 0; i <= 10; i++) {
            //g2.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255),rand.nextInt(50)+50));
            g2.setColor(new Color(255,0,0,50)); //#ff0000
            int x = rand.nextInt(600);
            int y = rand.nextInt(400);
            int sw = rand.nextInt(100);
            int sh = rand.nextInt(100);
            g2.fillOval(x, y, sw,sh);//实心圆
        }

        g2.dispose();
        ImageIO.write(img, "png", new File("target/g2.png"));
    }
}
