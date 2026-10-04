package tile;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import main.GamePanel;
import main.MathDialog;
import player.Player;

public class DoorKey {
    GamePanel gp;
    public int keysCollected = 0;

    // Detection flag to draw the floating prompt
    public boolean playerNearDoor = false;
    private int[] currentNearbyDoor = null;
    
    private static final int KEY_GID = 5693;
    private static final int[] DOOR_GIDS = {812, 813, 829, 830, 846, 847};
    private static final boolean REQUIRE_KEY = false;
    private static final int REACH = 10;
    private static final int HITBOX_INSET = 8;

    public DoorKey(GamePanel gp) {
        this.gp = gp;
    }

    public static boolean isDoorGid(int gid) {
        for (int d : DOOR_GIDS) {
            if (gid == d) return true;
        }
        return false;
    }

    private int getDoorLayer() {
        for (int i = 0; i < gp.tileM.getLayerCount(); i++) {
            if (gp.tileM.getLayerName(i).toLowerCase().startsWith("doorkey")) return i;
        }
        return -1;
    }

    public void update(Player player) {
        attemptKeyPickup(player);
        
        currentNearbyDoor = findNearbyDoor(player);
        playerNearDoor = (currentNearbyDoor != null);
        
        if (playerNearDoor && gp.keyH.ePressed) {
            gp.keyH.ePressed = false; // Reset key
            interact(player, currentNearbyDoor);
        }
    }

    public void attemptKeyPickup(Player player) {
        int layer = getDoorLayer();
        if (layer < 0) return;

        int col = (int) ((player.x + gp.tileSize / 2) / gp.tileSize);
        int row = (int) ((player.y + gp.tileSize / 2) / gp.tileSize);

        if (gp.tileM.getGid(layer, row, col) == KEY_GID) {
            keysCollected++;
            gp.tileM.setGid(layer, row, col, 0);
            gp.playRightAnswer();
        }
    }

    public int[] findNearbyDoor(Player player) {
        int layer = getDoorLayer();
        if (layer < 0) return null;

        int ts = gp.tileSize;
        int left   = (int) Math.floor((player.x + HITBOX_INSET - REACH) / ts);
        int right  = (int) Math.floor((player.x + ts - HITBOX_INSET - 1 + REACH) / ts);
        int top    = (int) Math.floor((player.y + HITBOX_INSET - REACH) / ts);
        int bottom = (int) Math.floor((player.y + ts - HITBOX_INSET - 1 + REACH) / ts);

        for (int row = top; row <= bottom; row++) {
            for (int col = left; col <= right; col++) {
                if (isDoorGid(gp.tileM.getGid(layer, row, col))) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public void interact(Player player, int[] door) {
        if (REQUIRE_KEY && keysCollected <= 0) {
            gp.playWrongAnswer();
            return;
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(gp);
        MathDialog puzzleWindow = new MathDialog(parentFrame, gp, this, player,
                door[0], door[1], gp.tileM.getCurrentLevel());
        puzzleWindow.setVisible(true);
    }

    public void draw(Graphics2D g2) {
        if (playerNearDoor && currentNearbyDoor != null) {
            int doorX = currentNearbyDoor[1] * gp.tileSize;
            int doorY = currentNearbyDoor[0] * gp.tileSize;

            String prompt = "E to Enter";
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int promptX = doorX + (gp.tileSize / 2) - (fm.stringWidth(prompt) / 2);
            int promptY = doorY - 8;

            // Black outline / shadow
            g2.setColor(Color.BLACK);
            g2.drawString(prompt, promptX + 1, promptY + 1);

            // Retro golden yellow text
            g2.setColor(new Color(255, 245, 170));
            g2.drawString(prompt, promptX, promptY);
        }
    }
    
    public void unlockDoor(int targetRow, int targetCol) {
        int layer = getDoorLayer();
        if (layer >= 0) removeDoorTiles(layer, targetRow, targetCol);
        if (keysCollected > 0) keysCollected--;
    }

    private void removeDoorTiles(int layer, int row, int col) {
        if (!isDoorGid(gp.tileM.getGid(layer, row, col))) return;
        gp.tileM.setGid(layer, row, col, 0);
        removeDoorTiles(layer, row - 1, col);
        removeDoorTiles(layer, row + 1, col);
        removeDoorTiles(layer, row, col - 1);
        removeDoorTiles(layer, row, col + 1);
    }
}
