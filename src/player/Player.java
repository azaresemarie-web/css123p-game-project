/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;
import main.CollisionDetection;

/**
 *
 * @author emarie
 */
public class Player {
    
    public boolean collisionOn = false;

    // Position & Speed
    public double x, y;
    public double speed;
    
    GamePanel gp;
    KeyHandler keyH;

    // Animation & Sprite fields
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 2.0; // Normalized speed for 60 FPS update loop
        direction = "down";
    }

    public void getPlayerImage() {
        try {
            BufferedImage spriteSheet = ImageIO.read(getClass().getResourceAsStream("/complete_map/player.png"));

            if (spriteSheet != null) {
                int width = 32;
                int height = 32;

                // Row 0: Down (columns 0 and 1)
                down1 = spriteSheet.getSubimage(0 * width, 0 * height, width, height);
                down2 = spriteSheet.getSubimage(1 * width, 0 * height, width, height);

                // Row 1: Left
                left1 = spriteSheet.getSubimage(0 * width, 1 * height, width, height);
                left2 = spriteSheet.getSubimage(1 * width, 1 * height, width, height);

                // Row 2: Right
                right1 = spriteSheet.getSubimage(0 * width, 2 * height, width, height);
                right2 = spriteSheet.getSubimage(1 * width, 2 * height, width, height);

                // Row 3: Up
                up1 = spriteSheet.getSubimage(0 * width, 3 * height, width, height);
                up2 = spriteSheet.getSubimage(1 * width, 3 * height, width, height);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        boolean isMoving = false;
        
        // Calculate velocity vectors to ensure smooth diagonal and cardinal movement
        double dx = 0;
        double dy = 0;

        if (keyH.upPressed) {
            direction = "up";
            dy -= speed;
            isMoving = true;
        }
        if (keyH.downPressed) {
            direction = "down";
            dy += speed;
            isMoving = true;
        }
        if (keyH.leftPressed) {
            direction = "left";
            dx -= speed;
            isMoving = true;
        }
        if (keyH.rightPressed) {
            direction = "right";
            dx += speed;
            isMoving = true;
        }

        // Normalize speed when moving diagonally to prevent moving faster sideways
        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        // ==========================================
        // COLLISION CHECKING
        // ==========================================
        if (isMoving && gp.cChecker != null) {
            // Check horizontal collision (X)
            if (dx != 0) {
                collisionOn = false;
                gp.cChecker.checkTile(this, dx, 0);
                if (collisionOn) {
                    dx = 0; // Block X movement
                }
            }

            // Check vertical collision (Y)
            if (dy != 0) {
                collisionOn = false;
                gp.cChecker.checkTile(this, 0, dy);
                if (collisionOn) {
                    dy = 0; // Block Y movement
                }
            }
        }
        
        // Apply calculated movement
        x += dx;
        y += dy;

        // --- MAP BOUNDARY CONSTRAINTS ---
        double minX = 0;
        double minY = 0;
        double maxX = gp.screenWidth - gp.tileSize;
        double maxY = gp.screenHeight - gp.tileSize;

        if (x < minX) x = minX;
        if (x > maxX) x = maxX;
        if (y < minY) y = minY;
        if (y > maxY) y = maxY;

        // Animate sprite only when actively moving
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) { // Smooth cycle every 10 ticks
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1; // Reset to static posture when idle
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up" -> image = (spriteNum == 1) ? up1 : up2;
            case "down" -> image = (spriteNum == 1) ? down1 : down2;
            case "left" -> image = (spriteNum == 1) ? left1 : left2;
            case "right" -> image = (spriteNum == 1) ? right1 : right2;
        }

        int drawX = (int) Math.round(x);
        int drawY = (int) Math.round(y);

        if (image != null) {
            int drawSize = gp.tileSize * 2;
            
            int screenX = drawX - (drawSize - gp.tileSize) / 2;
            int screenY = drawY - (drawSize - gp.tileSize);

            g2.drawImage(image, screenX, screenY, drawSize, drawSize, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(drawX, drawY, gp.tileSize, gp.tileSize);
        }
    }
}
