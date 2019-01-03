import java.awt.*;

public class Fonts {

    public static void drawString(Graphics g, Font f, Color c, String text, int x, int y) {
        g.setColor(c);
        g.setFont(f);
        g.drawString(text, x, y);
    }

    public static void drawString(Graphics g, Font f, Color c, String text) {
        FontMetrics fm = g.getFontMetrics(f);
        int x = (Menu.WIDTH - fm.stringWidth(text)) / 2; // Horizontal center
        int y = ((Menu.HEIGHT - fm.getHeight()) / 2) + fm.getAscent(); // Vertical center
        drawString(g, f, c, text, x, y);
    }

    public static void drawString(Graphics g, Font f, Color c, String text, double x) {
        FontMetrics fm = g.getFontMetrics(f);
        int y = ((Menu.HEIGHT - fm.getHeight()) / 2) + fm.getAscent(); // Vertical center
        drawString(g, f, c, text, (int) x, y);
    }

    public static void drawString(Graphics g, Font f, Color c, String text, int y) {
        FontMetrics fm = g.getFontMetrics(f);
        int x = (Menu.WIDTH - fm.stringWidth(text)) / 2; // Horizontal center
        drawString(g, f, c, text, x, y);
    }
}
