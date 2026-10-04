/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Nalla
 */
public class Camera {

    private final GamePanel gp;

    // Zoom factor: 2.2x gives a close-up, focused dungeon view
    public double zoom = 2.2;

    // Camera viewport coordinates
    public double x;
    public double y;

    private BufferedImage darknessFilter;
    private AffineTransform originalTransform;

    public Camera(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        if (gp.player == null) return;

        // Center point of the player
        double playerCenterX = gp.player.x + (gp.tileSize / 2.0);
        double playerCenterY = gp.player.y + (gp.tileSize / 2.0);

        // Calculate offset so the player stays in the middle of the screen
        x = playerCenterX - ((gp.screenWidth / 2.0) / zoom);
        y = playerCenterY - ((gp.screenHeight / 2.0) / zoom);

        // Clamp camera so it does not pan out of the 30x20 dungeon bounds
        double maxCameraX = gp.screenWidth - (gp.screenWidth / zoom);
        double maxCameraY = gp.screenHeight - (gp.screenHeight / zoom);

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > maxCameraX) x = maxCameraX;
        if (y > maxCameraY) y = maxCameraY;
    }

    /**
     * Zooms the map and entities. Call BEFORE drawing tiles and player.
     */
    public void apply(Graphics2D g2) {
        originalTransform = g2.getTransform();
        g2.scale(zoom, zoom);
        g2.translate(-x, -y);
    }

    /**
     * Restores screen coordinates for UI/lighting.
     */
    public void reset(Graphics2D g2) {
        if (originalTransform != null) {
            g2.setTransform(originalTransform);
        }
    }

    /**
     * Renders a soft torch vignette: bright center, fading softly into a dark 
     * but visible dungeon background (not pitch black).
     */
    public void drawLighting(Graphics2D g2) {
        if (gp.player == null) return;

        // Maintain an off-screen ARGB buffer matching window dimensions
        if (darknessFilter == null 
                || darknessFilter.getWidth() != gp.screenWidth 
                || darknessFilter.getHeight() != gp.screenHeight) {
            darknessFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D gDark = darknessFilter.createGraphics();

        // 1. Fill entire screen with ambient dungeon darkness (approx 78% opacity)
        // This keeps the surroundings visible rather than solid black
        gDark.setComposite(AlphaComposite.Src);
        gDark.setColor(new Color(10, 10, 15, 200));
        gDark.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Calculate the player's on-screen pixel position under the camera zoom
        float screenPlayerX = (float) ((gp.player.x + (gp.tileSize / 2.0) - x) * zoom);
        float screenPlayerY = (float) ((gp.player.y + (gp.tileSize / 2.0) - y) * zoom);

        Point2D center = new Point2D.Float(screenPlayerX, screenPlayerY);
        float radius = 260.0f; // Torchlight reach radius

        // 3. Clear out the torchlight using DST_OUT composite for a smooth gradient
        gDark.setComposite(AlphaComposite.DstOut);

        float[] dist = {0.0f, 0.45f, 0.85f, 1.0f};
        Color[] colors = {
            new Color(0, 0, 0, 255),  // 100% cleared out in the center (fully bright)
            new Color(0, 0, 0, 220),  // Bright torch core
            new Color(0, 0, 0, 80),   // Soft fading transition ring
            new Color(0, 0, 0, 0)     // Edge transitions to the ambient room fog
        };

        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        gDark.setPaint(p);
        gDark.fillOval((int)(screenPlayerX - radius), (int)(screenPlayerY - radius), (int)(radius * 2), (int)(radius * 2));
        gDark.dispose();

        // 4. Draw the soft lighting mask over the screen
        g2.drawImage(darknessFilter, 0, 0, null);
    }
}
