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

    public double zoom = 2.2;

    public double x;
    public double y;

    private BufferedImage darknessFilter;
    private AffineTransform originalTransform;

    public Camera(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        if (gp.player == null) return;

        double playerCenterX = gp.player.x + (gp.tileSize / 2.0);
        double playerCenterY = gp.player.y + (gp.tileSize / 2.0);

        x = playerCenterX - ((gp.screenWidth / 2.0) / zoom);
        y = playerCenterY - ((gp.screenHeight / 2.0) / zoom);

        double maxCameraX = gp.screenWidth - (gp.screenWidth / zoom);
        double maxCameraY = gp.screenHeight - (gp.screenHeight / zoom);

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > maxCameraX) x = maxCameraX;
        if (y > maxCameraY) y = maxCameraY;
    }

    public void apply(Graphics2D g2) {
        originalTransform = g2.getTransform();
        g2.scale(zoom, zoom);
        g2.translate(-x, -y);
    }

    public void reset(Graphics2D g2) {
        if (originalTransform != null) {
            g2.setTransform(originalTransform);
        }
    }

    public void drawLighting(Graphics2D g2) {
        if (gp.player == null) return;

        if (darknessFilter == null 
                || darknessFilter.getWidth() != gp.screenWidth 
                || darknessFilter.getHeight() != gp.screenHeight) {
            darknessFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D gDark = darknessFilter.createGraphics();

        gDark.setComposite(AlphaComposite.Src);
        gDark.setColor(new Color(10, 10, 15, 200));
        gDark.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        float screenPlayerX = (float) ((gp.player.x + (gp.tileSize / 2.0) - x) * zoom);
        float screenPlayerY = (float) ((gp.player.y + (gp.tileSize / 2.0) - y) * zoom);

        Point2D center = new Point2D.Float(screenPlayerX, screenPlayerY);
        float radius = 260.0f;

        gDark.setComposite(AlphaComposite.DstOut);

        float[] dist = {0.0f, 0.45f, 0.85f, 1.0f};
        Color[] colors = {
            new Color(0, 0, 0, 255),
            new Color(0, 0, 0, 220),
            new Color(0, 0, 0, 80),
            new Color(0, 0, 0, 0)
        };

        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        gDark.setPaint(p);
        gDark.fillOval((int)(screenPlayerX - radius), (int)(screenPlayerY - radius), (int)(radius * 2), (int)(radius * 2));
        gDark.dispose();

        g2.drawImage(darknessFilter, 0, 0, null);
    }
}