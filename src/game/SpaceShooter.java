package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.Timer;



public class SpaceShooter extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private boolean gameRunning = true;
    private int level = 1;
    private int score = 0;
    private int gameSpeed = 1;
    private Image background;

    private JButton retryButton;
    private JButton exitButton;
    private JButton achievementsButton;

    private Player player;
    private ArrayList<Bullet> playerBullets;
    private ArrayList<Enemy> enemies;
    private java.util.List<EnemyBullet> enemyBullets = new ArrayList<>();
    private Set<Integer> pressedKeys;

    private int shootCooldown = 0;
    private int doubleShotTimer = 0;

    // ==== Логика босса ====
    private Boss boss = null;
    private int nextBossScore = 2000;   // первый порог появления босса
    private int nextBossLevel = 1;      // первый уровень босса

    private enum GameState {
        RUNNING,
        GAME_OVER,
        MENU_ACHIEVEMENTS
    }

    private GameState gameState = GameState.RUNNING;
    private int lastScore = 0;

    private ArrayList<Bonus> bonuses = new ArrayList<>();

    public SpaceShooter() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);

        player = new Player(20, 20);
        playerBullets = new ArrayList<>();
        enemies = new ArrayList<>();
        pressedKeys = new HashSet<>();

        background = new ImageIcon(getClass().getResource("/images/background.png")).getImage();


        timer = new Timer(16, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        initMenuButtons();
    }

    public void restartGame() {
        level = 1;
        score = 0;
        gameSpeed = 1;
        player.reset();
        playerBullets.clear();
        enemies.clear();
        enemyBullets.clear();
        pressedKeys.clear();
        shootCooldown = 0;

        boss = null;
        nextBossScore = 2000;
        nextBossLevel = 1;

        gameState = GameState.RUNNING;
        toggleMenu(false);
    }

    private void toggleMenu(boolean show) {
        retryButton.setVisible(show);
        exitButton.setVisible(show);
        achievementsButton.setVisible(show);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            updateGame();
        }
        repaint();
    }

    private void initMenuButtons() {
        setLayout(null);

        retryButton = new JButton("Retry");
        retryButton.setBounds(300, 250, 200, 40);
        retryButton.addActionListener(e -> restartGame());
        retryButton.setVisible(false);
        add(retryButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(300, 300, 200, 40);
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setVisible(false);
        add(exitButton);

        achievementsButton = new JButton("Achievements");
        achievementsButton.setBounds(300, 350, 200, 40);
        achievementsButton.addActionListener(e -> {
            gameState = GameState.MENU_ACHIEVEMENTS;
            repaint();
        });
        achievementsButton.setVisible(false);
        add(achievementsButton);
    }

    private void updateGame() {
        if (gameState != GameState.RUNNING) return;

        // === управление игроком ===
        int speed = 8;
        if (pressedKeys.contains(KeyEvent.VK_A)) player.move(-speed, 0);
        if (pressedKeys.contains(KeyEvent.VK_D)) player.move(speed, 0);
        if (pressedKeys.contains(KeyEvent.VK_W)) player.move(0, -speed);
        if (pressedKeys.contains(KeyEvent.VK_S)) player.move(0, speed);

        double bulletSpeed = 12;
        if (shootCooldown > 0) shootCooldown--;
        if (pressedKeys.contains(KeyEvent.VK_SPACE) && shootCooldown == 0) {
            if (doubleShotTimer > 0) {
                playerBullets.add(new Bullet(player.getX() + 5, player.getY(), bulletSpeed));
                playerBullets.add(new Bullet(player.getX() + 20, player.getY(), bulletSpeed));
            } else {
                playerBullets.add(new Bullet(player.getX() + 13, player.getY(), bulletSpeed));
            }
            shootCooldown = 15;
        }

        // === обновление врагов ===
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy en = it.next();
            en.update(enemyBullets);
            if (en.getY() > getHeight() || !en.isAlive()) {
                it.remove();
            }
        }

        if (Math.random() < 0.02 * gameSpeed) {
            enemies.add(new Enemy((int)(Math.random() * (getWidth()-50)), 0));
        }

        // === бонусы ===
        if (Math.random() < 0.005) {
            String type = Math.random() < 0.5 ? "health" : "doubleShot";
            bonuses.add(new Bonus((int)(Math.random() * (getWidth()-30)), 0, type));
        }

        Iterator<Bonus> bi = bonuses.iterator();
        while (bi.hasNext()) {
            Bonus bonus = bi.next();
            bonus.update();
            if (bonus.getBounds().intersects(player.getBounds())) {
                if ("health".equals(bonus.getType())) {
                    player.restoreHealth();
                } else if ("doubleShot".equals(bonus.getType())) {
                    doubleShotTimer = 600;
                }
                bi.remove();
            } else if (bonus.getY() > getHeight()) {
                bi.remove();
            }
        }

        if (doubleShotTimer > 0) doubleShotTimer--;

        // === пули врагов ===
        Iterator<EnemyBullet> ebIt = enemyBullets.iterator();
        while (ebIt.hasNext()) {
            EnemyBullet eb = ebIt.next();
            eb.update();
            if (eb.getBounds().intersects(player.getBounds())) {
                player.takeDamage(20);
                ebIt.remove();
                continue;
            }
            if (eb.getY() > getHeight()) ebIt.remove();
        }

        // === пули игрока ===
        playerBullets.removeIf(b -> { b.update(); return b.getY() < 0; });

        // попадание пуль по врагам
        Iterator<Bullet> pbi = playerBullets.iterator();
        while (pbi.hasNext()) {
            Bullet b = pbi.next();
            Iterator<Enemy> ei = enemies.iterator();
            boolean removedBullet = false;
            while (ei.hasNext()) {
                Enemy en = ei.next();
                if (b.getBounds().intersects(en.getBounds())) {
                    score += 100;
                    ei.remove();
                    pbi.remove();
                    removedBullet = true;
                    break;
                }
            }
            if (removedBullet) continue;
        }

        // столкновение игрока с врагом
        Iterator<Enemy> ei2 = enemies.iterator();
        while (ei2.hasNext()) {
            Enemy en = ei2.next();
            if (player.getBounds().intersects(en.getBounds())) {
                player.takeDamage(50);
                ei2.remove();
            }
        }

        // === ЛОГИКА БОССА ===
        if (boss == null && score >= nextBossScore) {
            boss = new Boss(nextBossLevel, (getWidth() - 120) / 2, 50);
            nextBossLevel++;
            nextBossScore += 2000;
        }

        if (boss != null && boss.isAlive()) {
            boss.update(getWidth());

            // попадание пуль игрока в босса
            Iterator<Bullet> pbIt = playerBullets.iterator();
            while (pbIt.hasNext()) {
                Bullet b = pbIt.next();
                if (b.getBounds().intersects(boss.getBounds())) {
                    boss.takeDamage(100);
                    pbIt.remove();
                }
            }

            // попадание пуль босса в игрока
            Iterator<Bullet> bossBulletsIt = boss.getBullets().iterator();
            while (bossBulletsIt.hasNext()) {
                Bullet be = bossBulletsIt.next();
                if (be.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(20);
                    bossBulletsIt.remove();
                }
            }

            if (!boss.isAlive()) {
                boss = null;
            }
        }

        if (player.getHealth() <= 0 && gameState == GameState.RUNNING) {
            lastScore = score;
            gameState = GameState.GAME_OVER;
            toggleMenu(true);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // сначала фон
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // игрок и объекты
        player.draw(g);
        for (Bullet b : playerBullets) b.draw(g);
        for (Enemy e : enemies) e.draw(g);
        for (EnemyBullet bullet : enemyBullets) bullet.draw(g);

        if (boss != null && boss.isAlive()) boss.draw(g);
        for (Bonus b : bonuses) b.draw(g);

        // UI
        g.setColor(Color.WHITE);
        g.drawString("HP: " + player.getHealth(), 10, 20);
        g.drawString("Level: " + level, 10, 40);
        g.drawString("Score: " + score, 10, 60);

        if (gameState == GameState.GAME_OVER) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Times New Roman", Font.BOLD, 36));
            g.drawString("GAME OVER", getWidth() / 2 - 100, getHeight() / 2 - 100);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Final Score: " + lastScore, getWidth() / 2 - 70, getHeight() / 2 - 60);
        }

        if (gameState == GameState.MENU_ACHIEVEMENTS) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Achievements", getWidth() / 2 - 100, 100);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Best Score: " + lastScore, getWidth() / 2 - 80, 150);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
