/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author emarie
 */
public class UIButtons {

    private BufferedImage heartImage;
    private BufferedImage keyImage;

    private final int leftStartX = 20;
    private final int leftStartY = 20;
    private final int iconSize = 32;
    private final int spacing = 10;
    private final int rowGap = 15;

    private int heartCount = 3;
    private int keyCount = 0;

    private BufferedImage restartImage;
    private BufferedImage settingsImage;

    private final int btnSize = 32;
    private final int rightMargin = 20;
    private final int topMargin = 20;
    private final int btnSpacing = 15;

    private Rectangle restartHitbox;
    private Rectangle settingsHitbox;

    private boolean isRestartHovered = false;
    private boolean isSettingsHovered = false;
    
    public Runnable onSettingsClick;


    public UIButtons(int screenWidth) {
        loadImages();
        setupRightButtons(screenWidth);
    }

    private void loadImages() {
        try {
            heartImage = ImageIO.read(getClass().getResourceAsStream("/buttons/heartStatus.png"));
            keyImage   = ImageIO.read(getClass().getResourceAsStream("/buttons/keySymbol.png"));
            
            restartImage  = ImageIO.read(getClass().getResourceAsStream("/buttons/restartBtn.png"));
            settingsImage = ImageIO.read(getClass().getResourceAsStream("/buttons/settingsBtn.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Note: Check image paths inside /assets/ folder if icons fail to display.");
        }
    }

    public void setupRightButtons(int screenWidth) {
        int settingsX = screenWidth - rightMargin - btnSize;
        int restartX  = settingsX - btnSpacing - btnSize;

        settingsHitbox = new Rectangle(settingsX, topMargin, btnSize, btnSize);
        restartHitbox  = new Rectangle(restartX, topMargin, btnSize, btnSize);
    }

    public void draw(Graphics2D g2) {
        int currentX = leftStartX;
        int currentY = leftStartY;

        for (int i = 0; i < heartCount; i++) {
            if (heartImage != null) {
                g2.drawImage(heartImage, currentX, currentY, iconSize, iconSize, null);
            } else {
                g2.setColor(Color.RED);
                g2.fillRect(currentX, currentY, iconSize, iconSize);
            }
            currentX += iconSize + spacing;
        }

        currentX = leftStartX;
        currentY = leftStartY + iconSize + rowGap;

        for (int i = 0; i < keyCount; i++) {
            if (keyImage != null) {
                g2.drawImage(keyImage, currentX, currentY, iconSize, iconSize, null);
            } else {
                g2.setColor(Color.YELLOW);
                g2.fillRect(currentX, currentY, iconSize, iconSize);
            }
            currentX += iconSize + spacing;
        }

        drawIconButton(g2, restartImage, restartHitbox, isRestartHovered, Color.ORANGE);
        drawIconButton(g2, settingsImage, settingsHitbox, isSettingsHovered, Color.LIGHT_GRAY);
    }

    private void drawIconButton(Graphics2D g2, BufferedImage img, Rectangle bounds, boolean isHovered, Color fallbackColor) {
        if (bounds == null) return;

        if (img != null) {
            if (isHovered) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.drawImage(img, bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } else {
                g2.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height, null);
            }
        } else {
            g2.setColor(fallbackColor);
            g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (restartHitbox != null && restartHitbox.contains(e.getPoint())) {
                    System.out.println("Restart Icon Clicked!");
                } 
                else if (settingsHitbox != null && settingsHitbox.contains(e.getPoint())) {
                    System.out.println("Settings Icon Clicked!");
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                isRestartHovered  = (restartHitbox != null && restartHitbox.contains(e.getPoint()));
                isSettingsHovered = (settingsHitbox != null && settingsHitbox.contains(e.getPoint()));
            }
        };
    }

    public void setHeartCount(int count) { this.heartCount = count; }
    public void setKeyCount(int count) { this.keyCount = count; }
}