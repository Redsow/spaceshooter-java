package game;

import java.awt.*;

public class Bullet {
    private int x, y, width = 4, height = 10;
    private double speed = 12;

    public Bullet(int x, int y, double speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void update() {
        y -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getY() { return y; }

    public void setY(int newY) {
        this.y = newY;
    }

    public void updateDown() {
        y += speed; // скорость вниз
    }

}
