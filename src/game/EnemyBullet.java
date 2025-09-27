package game;

import java.awt.*;
import java.util.List;

public class EnemyBullet {
    private int x, y, speed, width, height;

    public EnemyBullet(int x, int y) {
        this.x = x;
        this.y = y;
        this.speed = 8; // увеличил скорость (раньше было меньше)
        this.width = 4;
        this.height = 10;
    }

    public void update() {
        y += speed; // летит вниз
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getY() {
        return y;
    }
}
