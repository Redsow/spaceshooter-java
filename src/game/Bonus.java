package game;

import java.awt.*;

public class Bonus {
    private int x, y;
    private int size = 20;
    private String type;
    private int speed = 2;

    public Bonus(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void update() {
        y += speed;
    }

    public void draw(Graphics g) {
        switch (type) {
            case "health":
                g.setColor(Color.GREEN);
                g.fillRect(x, y, size, size);
                g.setColor(Color.WHITE);
                g.drawString("+", x + 5, y + 15);
                break;
            case "doubleShot":
                g.setColor(Color.CYAN);
                g.fillRect(x, y, size, size);
                g.setColor(Color.BLACK);
                g.drawString("2x", x + 2, y + 15);
                break;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }
}
