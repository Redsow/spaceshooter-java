package game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Boss {
    private int x, y, width = 120, height = 60;
    private int dx; // скорость движения по X
    private int hp;
    private int maxHp;
    private int level;
    private int shootCooldown = 0;
    private int shootInterval;
    private ArrayList<Bullet> bullets;
    private Image bossImage;

    public Boss(int level, int startX, int startY) {
        this.level = level;
        this.x = startX;
        this.y = startY;

        bossImage = new ImageIcon(getClass().getResource("/images/boss.png")).getImage();

        this.maxHp = 1000 + (level - 1) * 1000; // ✅ max HP растет с уровнем
        this.hp = maxHp;

        this.dx = 2 + level; // скорость движения растет
        this.shootInterval = 50 - level * 5; // интервал стрельбы уменьшается
        if (shootInterval < 10) shootInterval = 10; // ограничение
        this.bullets = new ArrayList<>();
    }

    public void update(int panelWidth) {
        // движение по X
        x += dx;
        if (x <= 0 || x + width >= panelWidth) dx = -dx;

        // стрельба
        if (shootCooldown > 0) shootCooldown--;
        if (shootCooldown == 0) {
            double bulletSpeed = 4 + level * 0.5;
            bullets.add(new Bullet(x + 10, y + height, bulletSpeed));
            bullets.add(new Bullet(x + width / 2, y + height, bulletSpeed));
            bullets.add(new Bullet(x + width - 10, y + height, bulletSpeed));
            shootCooldown = shootInterval;
        }

        // обновление пуль
        bullets.removeIf(b -> {
            b.updateDown();
            return b.getY() > 600;
        });
    }

    public void draw(Graphics g) {
        // сам босс
        g.drawImage(bossImage, x, y, null);

        // рамка полоски здоровья
        g.setColor(Color.WHITE);
        g.drawRect(x, y - 10, width, 6);

        // заполнение полоски (в пределах рамки)
        g.setColor(Color.GREEN);
        double hpPercent = (double) hp / maxHp;
        if (hpPercent < 0) hpPercent = 0;
        int barWidth = (int) Math.round(width * hpPercent);
        g.fillRect(x, y - 10, barWidth, 6);

        // пули
        for (Bullet b : bullets) {
            b.draw(g);
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int getHp() {
        return hp;
    }

    public int getLevel() {
        return level;
    }
}
