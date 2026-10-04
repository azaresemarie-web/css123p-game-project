
package main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import player.Player;
import tile.TileManager;
import tile.DoorKey;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;

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
    public KeyHandler keyH = new KeyHandler();
    public TileManager tileM = new TileManager(this);
    public CollisionDetection cChecker = new CollisionDetection(this);
    public DoorKey doorKey = new DoorKey(this);
    Player player = new Player(this, keyH);
    public int hearts = 3;
    volatile boolean resetRequested = false;
    Camera camera = new Camera(this);
    UIButtons uiButtons;

    volatile boolean nearDoor = false;    
    volatile boolean puzzleOpen = false;

    // 2. ENGINE SYSTEM
    int FPS = 60;
    Thread gameThread;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyH);
        uiButtons = new UIButtons(screenWidth);
        MouseAdapter mouseHandler = uiButtons.getMouseListener();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        
        
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
    
     // Called when the player picks up a key
    public void playKeyPickup() {
        stopSE();
        playSE(4);    // ring.wav
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

    private final int[][] spawnTiles = { {3, 3}, {15, 2}, {1, 15} };

    // Level 1: right edge | Level 2: left edge | Level 3: top opening
    private final int[][] exitZones = { {28, 3, 29, 4}, {0, 14, 1, 17}, {23, 0, 25, 1} };

    // If the player's center is inside the exit zone, go exit
    private void checkLevelExit() {
        int level = tileM.getCurrentLevel();
        int col = (int) ((player.x + tileSize / 2) / tileSize);
        int row = (int) ((player.y + tileSize / 2) / tileSize);
        int[] z = exitZones[level];

        if (col >= z[0] && col <= z[2] && row >= z[1] && row <= z[3]) {
            if (level + 1 < tileM.getLevelCount()) {
                loadLevel(level + 1);
            } else {
            }
        }
    }
    
    // Called by MathDialog on a wrong answer or when time expires
    public void loseHeart() {
        hearts--;
        if (hearts <= 0) {
            resetRequested = true;   // update() restarts the level safely
        }
    }

    public void loadLevel(int level) {
        tileM.loadLevel(level);
        hearts = 3;
        doorKey.keysCollected = 0;
        player.x = spawnTiles[level][0] * tileSize;
        player.y = spawnTiles[level][1] * tileSize;
        nearDoor = false;
    }
    
public void update() {
    // Freeze the game while the Math Panel is open
    if (puzzleOpen) return;
    if (resetRequested) {
        resetRequested = false;
        loadLevel(tileM.getCurrentLevel());
    }
    if (keyH.levelRequest >= 0) {
        loadLevel(keyH.levelRequest);
        keyH.levelRequest = -1;
    }

    player.update();
    camera.update();
    uiButtons.setKeyCount(doorKey.keysCollected);
    uiButtons.setHeartCount(hearts);
    doorKey.attemptKeyPickup(player);

    // Is the player standing next to a door?
    int[] door = doorKey.findNearbyDoor(player);
    nearDoor = (door != null);

    // Pressed E next to a door -> open the Math Panel
    if (nearDoor && keyH.ePressed) {
        keyH.resetKeys();
        puzzleOpen = true;
        nearDoor = false;

        SwingUtilities.invokeLater(() -> {
            doorKey.interact(player, door);   // blocks until the dialog closes
            keyH.resetKeys();
            puzzleOpen = false;
            requestFocusInWindow();
        });
    }
}

private void drawInteractPrompt(Graphics2D g2) {
    String text = "E to Enter";
    g2.setFont(new Font("Arial", Font.BOLD, 16));
    FontMetrics fm = g2.getFontMetrics();

    int boxW = fm.stringWidth(text) + 20;
    int boxH = 28;

    // Centered above the player's head (the sprite is drawn 1 tile above player.y)
    int boxX = (int) player.x + tileSize / 2 - boxW / 2;
    int boxY = (int) player.y - tileSize - boxH - 4;

    // Keep it on screen
    boxX = Math.max(0, Math.min(boxX, screenWidth - boxW));
    boxY = Math.max(0, boxY);

    g2.setColor(new Color(0, 0, 0, 190));
    g2.fillRoundRect(boxX, boxY, boxW, boxH, 10, 10);
    g2.setColor(Color.WHITE);
    g2.drawRoundRect(boxX, boxY, boxW, boxH, 10, 10);
    g2.drawString(text, boxX + 10, boxY + 19);
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        camera.apply(g2);

        // 1. Draw the TileManager map (floors, decorative cracks, walls, water) first
        if (tileM != null) {
            tileM.draw(g2);
        }

        // 2. Draw doors or other background environmental objects here (Fil's section)
        
        // 3. Draw the player on top (Ems's section)
        if (player != null) {
            player.draw(g2);
        }
        if (nearDoor && !puzzleOpen) {
            drawInteractPrompt(g2);
        }

        camera.reset(g2);
        camera.drawLighting(g2);
        uiButtons.draw(g2);
        g2.dispose();
    }
    
    public TileManager getTileM() {
        return tileM;
    }
}
