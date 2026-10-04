package tile;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import main.GamePanel;
import main.MathDialog;
import player.Player;

public class DoorKey {
    GamePanel gp;
    public int keysCollected = 0;

    // GIDs from level1.tmx (Tiled). Update these if other levels use different tiles.
    private static final int KEY_GID = 5693;
    private static final int[] DOOR_GIDS = {812, 813, 829, 830, 846, 847};

    // Set to true if the player must hold a key before the Math Panel opens
    private static final boolean REQUIRE_KEY = false;

    // How many pixels away from a door still counts as "near"
    private static final int REACH = 10;
    private static final int HITBOX_INSET = 8; // same as CollisionDetection

    public DoorKey(GamePanel gp) {
        this.gp = gp;
    }

    public static boolean isDoorGid(int gid) {
        for (int d : DOOR_GIDS) {
            if (gid == d) return true;
        }
        return false;
    }

    // Finds the "DoorKey" layer by name instead of hard-coding index 1
    private int getDoorLayer() {
        for (int i = 0; i < gp.tileM.getLayerCount(); i++) {
            if (gp.tileM.getLayerName(i).toLowerCase().startsWith("doorkey")) return i;
        }
        return -1;
    }

    public void attemptKeyPickup(Player player) {
        int layer = getDoorLayer();
        if (layer < 0) return;

        int col = (int) ((player.x + gp.tileSize / 2) / gp.tileSize);
        int row = (int) ((player.y + gp.tileSize / 2) / gp.tileSize);

        if (gp.tileM.getGid(layer, row, col) == KEY_GID) {
            keysCollected++;
            gp.tileM.setGid(layer, row, col, 0);
            System.out.println("Key Picked Up! Total: " + keysCollected);
            gp.playKeyPickup();          
        }
    }

    // Returns {row, col} of a door tile touching/near the player, or null if none
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

    // Opens the Math Panel for the given door
    public void interact(Player player, int[] door) {
        if (REQUIRE_KEY && keysCollected <= 0) {
            System.out.println("Locked. You need a key!");
            return;
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(gp);
        MathDialog puzzleWindow = new MathDialog(parentFrame, gp, this, player,
        door[0], door[1], gp.tileM.getCurrentLevel());
        puzzleWindow.setVisible(true); // modal: returns after the dialog closes
    }

    // Called by MathDialog on a correct answer. Removes the WHOLE door (it is 2x3 tiles).
    public void unlockDoor(int targetRow, int targetCol) {
        int layer = getDoorLayer();
        if (layer >= 0) removeDoorTiles(layer, targetRow, targetCol);
        if (keysCollected > 0) keysCollected--;
        System.out.println("Door unlocked.");
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
