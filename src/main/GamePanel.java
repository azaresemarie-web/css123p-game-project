
package main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import player.Player;
import tile.TileManager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Nalla
 */
public class GamePanel extends JPanel implements Runnable {

    // 1. TILE & SCREEN SETTINGS (16x16 scaled 2x = 32x32 px per tile)
    final int originalTileSize = 16;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale; // 32x32 pixels

    // Emarie's 30x20 tile requirement
    public final int maxScreenCol = 30;
    public final int maxScreenRow = 20;
    public final int screenWidth = tileSize * maxScreenCol;   // 960 px
    public final int screenHeight = tileSize * maxScreenRow;  // 640 px

    // Engine Systems & Managers
    KeyHandler keyH = new KeyHandler();
    TileManager tileM = new TileManager(this);
    Player player = new Player(this, keyH);

    // 2. ENGINE SYSTEM
    int FPS = 60;
    Thread gameThread;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyH);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        // Update player position and animations
        player.update();
        
        // If your TileManager has animated water tiles, update them here if needed:
        // tileM.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1. Draw the TileManager map (floors, decorative cracks, walls, water) first
        if (tileM != null) {
            tileM.draw(g2);
        }

        // 2. Draw doors or other background environmental objects here (Fil's section)
        
        // 3. Draw the player on top (Ems's section)
        if (player != null) {
            player.draw(g2);
        }

        g2.dispose();
    }
}