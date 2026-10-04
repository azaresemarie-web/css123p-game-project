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

/**
 *
 * @author emarie
 */
public class Player {
    
    public int x, y, speed;
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
        speed = 2;
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
        if (keyH.upPressed == true || keyH.downPressed == true || 
            keyH.leftPressed == true || keyH.rightPressed == true) {
            
            if (keyH.upPressed == true) {
                direction = "up";
                y -= speed;         
            } else if (keyH.downPressed == true) {
                direction = "down";
                y += speed; 
            } else if (keyH.leftPressed == true) {
                direction = "left";
                x -= speed;
            } else if (keyH.rightPressed == true) {
                direction = "right";
                x += speed; 
            }
            
            spriteCounter++;
            if (spriteCounter > 12) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        
        switch (direction) {
            case "up":
                if (spriteNum == 1) { image = up1; }
                if (spriteNum == 2) { image = up2; }
                break;
            case "down":
                if (spriteNum == 1) { image = down1; }
                if (spriteNum == 2) { image = down2; }
                break;
            case "left":
                if (spriteNum == 1) { image = left1; }
                if (spriteNum == 2) { image = left2; }
                break;
            case "right":
                if (spriteNum == 1) { image = right1; }
                if (spriteNum == 2) { image = right2; }
                break;
        }
        
        if (image != null) {
            // Adjust this multiplier to taste (e.g., 1.5 or 2 for a larger hero character)
            int drawSize = (int)(gp.tileSize * 2); 
            
            // Center the scaled character nicely on the tile coordinates
            int drawX = x - (drawSize - gp.tileSize) / 2;
            int drawY = y - (drawSize - gp.tileSize); 
            
            g2.drawImage(image, drawX, drawY, drawSize, drawSize, null);
        } else {
            g2.setColor(Color.white);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }
    }
}