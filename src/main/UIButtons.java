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

    // --- Left Side UI (Hearts & Keys) ---
    private BufferedImage heartImage;
    private BufferedImage keyImage;

    private final int leftStartX = 20;
    private final int leftStartY = 20;
    private final int iconSize = 32;
    private final int spacing = 10;
    private final int rowGap = 15;

    private int heartCount = 3;
    private int keyCount = 3;

    // --- Right Side UI (Custom Icon Buttons) ---
    private BufferedImage restartImage;
    private BufferedImage settingsImage;

    private final int btnSize = 32;       // Pixel size for restart/settings icons
    private final int rightMargin = 20;
    private final int topMargin = 20;
    private final int btnSpacing = 15;

    // Hitboxes for click detection
    private Rectangle restartHitbox;
    private Rectangle settingsHitbox;

    // Hover flags for visual feedback
    private boolean isRestartHovered = false;
    private boolean isSettingsHovered = false;

    public UIButtons(int screenWidth) {
        loadImages();
        setupRightButtons(screenWidth);
    }

    private void loadImages() {
        try {
            // Load heart and key icons
            heartImage = ImageIO.read(getClass().getResourceAsStream("/buttons/heartStatus.png"));
            keyImage   = ImageIO.read(getClass().getResourceAsStream("/buttons/keySymbol.png"));
            
            // Load custom button PNG assets
            restartImage  = ImageIO.read(getClass().getResourceAsStream("/buttons/restartBtn.png"));
            settingsImage = ImageIO.read(getClass().getResourceAsStream("/buttons/settingsBtn.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Note: Check image paths inside /assets/ folder if icons fail to display.");
        }
    }

    /**
     * Positions click hitboxes for the top-right icons based on screen width.
     */
    public void setupRightButtons(int screenWidth) {
        int settingsX = screenWidth - rightMargin - btnSize;          // Rightmost icon (Settings)
        int restartX  = settingsX - btnSpacing - btnSize;             // Left of Settings (Restart)

        settingsHitbox = new Rectangle(settingsX, topMargin, btnSize, btnSize);
        restartHitbox  = new Rectangle(restartX, topMargin, btnSize, btnSize);
    }

    /**
     * Renders all HUD icons to the screen.
     */
    public void draw(Graphics2D g2) {
        // -------------------------------------------------------------
        // 1. DRAW TOP-LEFT: HEARTS & KEYS
        // -------------------------------------------------------------
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

        // -------------------------------------------------------------
        // 2. DRAW TOP-RIGHT: RESTART & SETTINGS ICON BUTTONS
        // -------------------------------------------------------------
        drawIconButton(g2, restartImage, restartHitbox, isRestartHovered, Color.ORANGE);
        drawIconButton(g2, settingsImage, settingsHitbox, isSettingsHovered, Color.LIGHT_GRAY);
    }

    /**
     * Helper to draw individual custom PNG buttons with a hover highlight effect.
     */
    private void drawIconButton(Graphics2D g2, BufferedImage img, Rectangle bounds, boolean isHovered, Color fallbackColor) {
        if (bounds == null) return;

        if (img != null) {
            if (isHovered) {
                // Slightly dim/highlight icon when mouse hovers over it
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.drawImage(img, bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, null); // Enlarge slightly
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Reset opacity
            } else {
                g2.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height, null);
            }
        } else {
            // Fallback square if PNG file is not found
            g2.setColor(fallbackColor);
            g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    // -------------------------------------------------------------
    // 3. MOUSE INTERACTION LISTENER
    // -------------------------------------------------------------
    public MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (restartHitbox != null && restartHitbox.contains(e.getPoint())) {
                    System.out.println("Restart Icon Clicked!");
                    // TODO: Execute game restart logic here
                } 
                else if (settingsHitbox != null && settingsHitbox.contains(e.getPoint())) {
                    System.out.println("Settings Icon Clicked!");
                    // TODO: Execute settings menu logic here
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                isRestartHovered  = (restartHitbox != null && restartHitbox.contains(e.getPoint()));
                isSettingsHovered = (settingsHitbox != null && settingsHitbox.contains(e.getPoint()));
            }
        };
    }

    // --- Getters & Setters ---
    public void setHeartCount(int count) { this.heartCount = count; }
    public void setKeyCount(int count) { this.keyCount = count; }
}