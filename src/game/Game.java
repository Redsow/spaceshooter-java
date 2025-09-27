package game;

import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shooter");
        SpaceShooter game = new SpaceShooter();


        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
