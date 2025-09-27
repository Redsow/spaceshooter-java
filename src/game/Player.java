package game;

import javax.swing.*;
import java.awt.*;

public class Player {
    private int x, y, width = 30, height = 40;
    private int health = 100;
    private final int maxHealth = 100;
    private Image shipImage;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        shipImage = new ImageIcon(getClass().getResource("/images/spacesshooter.png")).getImage();

        // Размер игрока равен размеру картинки
        width = shipImage.getWidth(null);
        height = shipImage.getHeight(null);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;

        // Ограничение по X
        if (x < 0) x = 0;
        if (x + width > 800) x = 800 - width;

        // Ограничение по Y
        if (y < 0) y = 0;
        if (y + height > 600) y = 600 - height;
    }

    public void reset() {
        health = maxHealth;
        x = 400;
        y = 500;
    }

    public void restoreHealth() {
        this.health = maxHealth; // где maxHealth, например, 100
    }



    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }



    public void draw(Graphics g) {
        g.drawImage(shipImage, x, y, null);
    }

    public int getHealth() { return health; }
    public int getX() { return x; }
    public int getY() { return y; }
}
