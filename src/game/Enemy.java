package game;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Enemy {
    private int x, y, width, height, health, speed;
    private int shootCooldown = 0; // счётчик для задержки выстрелов
    private Image enemyImage;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 40;
        this.health = 100;
        this.speed = 2;
        enemyImage = new ImageIcon(getClass().getResource("/images/redspaceshooter.png")).getImage();
    }

    public void update(List<EnemyBullet> enemyBullets) {
        y += speed;

        // стрельба раз в ~60 кадров
        if (shootCooldown <= 0) {
            enemyBullets.add(new EnemyBullet(x + width / 2, y + height));
            shootCooldown = 60; // задержка
        } else {
            shootCooldown--;
        }
    }

    public void draw(Graphics g) {
        g.drawImage(enemyImage, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getY() {
        return y;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
