
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

    // ! Audio Subsystems (Separate instances so SFX never interrupts background music)
    public SoundEffects music = new SoundEffects();
    public SoundEffects se = new SoundEffects();
    
    // Engine Systems & Managers
    KeyHandler keyH = new KeyHandler();
    TileManager tileM = new TileManager(this);
    Player player = new Player(this, keyH);
    Camera camera = new Camera(this);

    // 2. ENGINE SYSTEM
    int FPS = 60;
    Thread gameThread;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyH);
        
        //bg theme on loop
        playMusic(0);
    }
    
    // ==========================================
    // AUDIO CONTROLLER METHODS
    // ==========================================
    
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }
    
    public void stopSE() {
        se.stop();
    }
    
    // Called when an answer is submitted as CORRECT
    public void playRightAnswer() {
        stopSE();     // Cut off any timer tick sound first
        playSE(1);    // Play right.wav chime
    }

    // Called when an answer is WRONG or when time expires
    public void playWrongAnswer() {
        stopSE();     // Cut off any timer tick sound first
        playSE(2);    // Play wrong.wav buzzer
    }

    // Called every second during a question countdown
    public void playTimerTick() {
        playSE(3);    // Play timer.wav
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
        long timer = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
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
       if (player != null) {
            player.update();
        }
        
        // If your TileManager has animated water tiles, update them here if needed:
        // tileM.update();
        
        //CAMERA LOCK
        if (camera != null) {
            camera.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //camera zoom & centering
        if (camera != null) {
            camera.apply(g2);
        }
        
        // 1. Draw the TileManager map (floors, decorative cracks, walls, water) first
        if (tileM != null) {
            tileM.draw(g2);
        }

        // 2. Draw doors or other background environmental objects here (Fil's section)
        
        // 3. Draw the player on top (Ems's section)
        if (player != null) {
            player.draw(g2);
        }
        
        //reset camera
        if (camera != null) {
            camera.reset(g2);
        }
        
        if (camera != null) {
            camera.drawLighting(g2);
        }
        
        g2.dispose();
    }
}
