package alyoshenka;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

/**
 * Генерирует assets/logo.png и assets/icon.png для браузера Алешенька.
 */
public final class LogoGenerator {

    private static final Color FLAG_BLUE = new Color(0x0039A6);
    private static final Color FLAG_RED = new Color(0xD52B1E);

    private static final String DIR = "assets";
    private static final Font FONT = new Font("Segoe UI", Font.BOLD, 96);

    private LogoGenerator() {}

    public static void main(String[] args) throws Exception {
        File dir = new File(DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Не удалось создать папку " + DIR);
        }
        ImageIO.write(buildLogo(), "png", new File(dir, "logo.png"));
        ImageIO.write(buildIcon(), "png", new File(dir, "icon.png"));
        buildToolbarIcons(dir);
        System.out.println("Assets written to " + dir.getAbsolutePath());
    }

    private static void buildToolbarIcons(File dir) throws Exception {
        Color navy = new Color(0x0E3D7A);
        Color green = new Color(0x1E7A3E);
        Color red = new Color(0xC5262E);

        // Назад: хвост + треугольник влево
        save(dir, "icon_back.png", g -> {
            g.setColor(navy);
            g.fillRect(46, 16, 12, 32);
            g.fillPolygon(new int[]{10, 46, 46}, new int[]{32, 14, 50}, 3);
        });

        // Вперёд: зеркально
        save(dir, "icon_forward.png", g -> {
            g.setColor(navy);
            g.fillRect(6, 16, 12, 32);
            g.fillPolygon(new int[]{54, 18, 18}, new int[]{32, 14, 50}, 3);
        });

        // Домой: крыша + корпус
        save(dir, "icon_home.png", g -> {
            g.setColor(navy);
            g.fillPolygon(new int[]{32, 6, 58}, new int[]{9, 28, 28}, 3);
            g.fillRect(16, 28, 32, 26);
        });

        // Стоп: красный крест
        save(dir, "icon_stop.png", g -> {
            g.setColor(red);
            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(14, 14, 50, 50);
            g.drawLine(50, 14, 14, 50);
        });

        // Обновить: зелёное кольцо со стрелкой
        save(dir, "icon_refresh.png", g -> {
            g.setColor(green);
            g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawOval(11, 11, 42, 42);
            g.fillPolygon(new int[]{46, 54, 49}, new int[]{13, 12, 22}, 3);
        });

        // Закрыть (крестик): серый
        save(dir, "icon_close.png", g -> {
            g.setColor(new Color(0x666666));
            g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(20, 20, 44, 44);
            g.drawLine(44, 20, 20, 44);
        });
    }

    private static void save(File dir, String name, Consumer<Graphics2D> draw) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw.accept(g);
        g.dispose();
        ImageIO.write(img, "png", new File(dir, name));
    }

    private static BufferedImage buildLogo() {
        String word = "АЛЕШЕНЬКА";
        Graphics2D measure = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        measure.setFont(FONT);
        FontMetrics fm = measure.getFontMetrics(FONT);
        int tracking = 6;
        int totalW = 0;
        int[] cw = new int[word.length()];
        for (int i = 0; i < word.length(); i++) {
            cw[i] = fm.charWidth(word.charAt(i));
            totalW += cw[i] + tracking;
        }
        totalW -= tracking;
        measure.dispose();

        int w = totalW + 120;
        int h = 200 + 40;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int baseline = 140;
        FontRenderContext frc = g.getFontRenderContext();
        float textW = (float) FONT.getStringBounds(word, frc).getWidth();
        int x = (int) ((w - textW) / 2);

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            Color color;
            switch (i) {
                case 0: color = Color.WHITE; break;
                case 1: color = FLAG_BLUE; break;
                case 2: color = FLAG_RED; break;
                default: color = new Color(0xEDF1F8); break;
            }
            // тень, чтобы читалось на тёмном фоне
            g.setColor(new Color(0, 0, 0, 110));
            g.setFont(FONT);
            g.drawString(String.valueOf(ch), x + 3, baseline + 4);

            g.setColor(color);
            g.drawString(String.valueOf(ch), x, baseline);
            x += cw[i] + tracking;
        }

        // триколорная полоска под словом
        int barY = 205;
        int barW = totalW;
        int barX = (w - barW) / 2;
        int strip = barW / 3;
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Float(barX, barY, strip, 10, 5, 5));
        g.setColor(FLAG_BLUE);
        g.fill(new RoundRectangle2D.Float(barX + strip, barY, strip, 10, 0, 0));
        g.setColor(FLAG_RED);
        g.fill(new RoundRectangle2D.Float(barX + 2 * strip, barY, strip, 10, 5, 5));
        g.dispose();
        return img;
    }

    private static BufferedImage buildIcon() {
        int size = 256;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        RoundRectangle2D badge = new RoundRectangle2D.Float(10, 10, size - 20, size - 20, 46, 46);
        g.setPaint(new GradientPaint(0, 10, new Color(0x17408F), 0, size - 10, new Color(0x0A1E45)));
        g.fill(badge);

        // блик сверху
        g.setPaint(new GradientPaint(0, 10, new Color(255, 255, 255, 90), 0, size / 2, new Color(255, 255, 255, 0)));
        g.fill(new RoundRectangle2D.Float(10, 10, size - 20, (size - 20) / 2, 46, 46));

        // внешняя окантовка
        g.setColor(new Color(0xD8E4FF));
        g.setStroke(new BasicStroke(5));
        g.draw(badge);

        // буква А
        Font big = new Font("Segoe UI", Font.BOLD, 176);
        g.setFont(big);
        FontMetrics fm = g.getFontMetrics(big);
        String a = "А";
        int tx = (size - fm.stringWidth(a)) / 2;
        int ty = baselineForCenter(g, fm, 128);
        g.setColor(new Color(0, 0, 0, 90));
        g.drawString(a, tx + 3, ty + 4);
        g.setColor(Color.WHITE);
        g.drawString(a, tx, ty);

        // триколорная плашка поверх низа буквы А
        int barW = 168;
        int barH = 22;
        int barX = (size - barW) / 2;
        int barY = size - 62;
        int strip = barW / 3;
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Float(barX, barY, strip, barH, 8, 8));
        g.setColor(FLAG_BLUE);
        g.fill(new RoundRectangle2D.Float(barX + strip, barY, strip, barH, 0, 0));
        g.setColor(FLAG_RED);
        g.fill(new RoundRectangle2D.Float(barX + 2 * strip, barY, strip, barH, 8, 8));

        g.dispose();
        return img;
    }

    private static int baselineForCenter(Graphics2D g, FontMetrics fm, int centerY) {
        return centerY - (fm.getAscent() + fm.getDescent()) / 2 + fm.getAscent();
    }
}