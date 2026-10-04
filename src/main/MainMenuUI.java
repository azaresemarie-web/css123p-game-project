package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainMenuUI {

    private BufferedImage logoImage;
    private BufferedImage playImage;
    private BufferedImage controlsImage;
    private BufferedImage settingsImage;

    private final int logoWidth = 320;
    private final int logoHeight = 120;
    private final int btnWidth = 200;
    private final int btnHeight = 50;
    private final int spacing = 15;

    private Rectangle logoBounds;
    private Rectangle playHitbox;
    private Rectangle controlsHitbox;
    private Rectangle settingsHitbox;

    private boolean isPlayHovered = false;
    private boolean isControlsHovered = false;
    private boolean isSettingsHovered = false;

    public Runnable onPlayClick;
    public Runnable onControlsClick;
    public Runnable onSettingsClick;

    public MainMenuUI(int screenWidth, int screenHeight) {
        loadImages();
        setupLayout(screenWidth, screenHeight);
    }

    private void loadImages() {
        try {
            logoImage = ImageIO.read(getClass().getResourceAsStream("/buttons/logo.png"));
            playImage = ImageIO.read(getClass().getResourceAsStream("/buttons/playBtn.png"));
            controlsImage = ImageIO.read(getClass().getResourceAsStream("/buttons/controlsBtn.png"));
            settingsImage = ImageIO.read(getClass().getResourceAsStream("/buttons/mainSettingsBtn.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Note: Check image paths inside /buttons/ folder if menu icons fail to display.");
        }
    }

    public void setupLayout(int screenWidth, int screenHeight) {
        int centerX = (screenWidth - btnWidth) / 2;
        int logoX = (screenWidth - logoWidth) / 2;

        int totalHeight = logoHeight + spacing + (btnHeight * 3) + (spacing * 2);
        int startY = (screenHeight - totalHeight) / 2;

        logoBounds = new Rectangle(logoX, startY, logoWidth, logoHeight);

        int currentY = startY + logoHeight + spacing;
        playHitbox = new Rectangle(centerX, currentY, btnWidth, btnHeight);

        currentY += btnHeight + spacing;
        controlsHitbox = new Rectangle(centerX, currentY, btnWidth, btnHeight);

        currentY += btnHeight + spacing;
        settingsHitbox = new Rectangle(centerX, currentY, btnWidth, btnHeight);
    }

    public void draw(Graphics2D g2) {
        if (logoBounds != null) {
            if (logoImage != null) {
                g2.drawImage(logoImage, logoBounds.x, logoBounds.y, logoBounds.width, logoBounds.height, null);
            } else {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                int textX = logoBounds.x + (logoBounds.width - fm.stringWidth("FORMULA HERO")) / 2;
                g2.drawString("FORMULA HERO", textX, logoBounds.y + 70);
            }
        }

        drawIconButton(g2, playImage, playHitbox, isPlayHovered, "PLAY", new Color(46, 204, 113));
        drawIconButton(g2, controlsImage, controlsHitbox, isControlsHovered, "CONTROLS", new Color(52, 152, 219));
        drawIconButton(g2, settingsImage, settingsHitbox, isSettingsHovered, "SETTINGS", new Color(155, 89, 182));
    }

    private void drawIconButton(Graphics2D g2, BufferedImage img, Rectangle bounds, boolean isHovered, String fallbackText, Color fallbackColor) {
        if (bounds == null) {
            return;
        }

        if (img != null) {
            if (isHovered) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                g2.drawImage(img, bounds.x - 3, bounds.y - 2, bounds.width + 6, bounds.height + 4, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } else {
                g2.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height, null);
            }
        } else {
            g2.setColor(isHovered ? fallbackColor.brighter() : fallbackColor);
            g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fm = g2.getFontMetrics();
            int textX = bounds.x + (bounds.width - fm.stringWidth(fallbackText)) / 2;
            int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(fallbackText, textX, textY);
        }
    }

    public MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (playHitbox != null && playHitbox.contains(e.getPoint())) {
                    if (onPlayClick != null) {
                        onPlayClick.run();
                    }
                } else if (controlsHitbox != null && controlsHitbox.contains(e.getPoint())) {
                    if (onControlsClick != null) {
                        onControlsClick.run();
                    }
                } else if (settingsHitbox != null && settingsHitbox.contains(e.getPoint())) {
                    if (onSettingsClick != null) {
                        onSettingsClick.run();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                isPlayHovered = (playHitbox != null && playHitbox.contains(e.getPoint()));
                isControlsHovered = (controlsHitbox != null && controlsHitbox.contains(e.getPoint()));
                isSettingsHovered = (settingsHitbox != null && settingsHitbox.contains(e.getPoint()));
            }
        };
    }
}
