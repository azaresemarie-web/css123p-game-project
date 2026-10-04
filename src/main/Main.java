package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author emarie
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("Formula Hero");

            GamePanel gamePanel = new GamePanel();

            window.add(gamePanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            // Start the game loop thread (GamePanel will display MainMenuUI first)
            gamePanel.startGameThread();
        });
    }
}