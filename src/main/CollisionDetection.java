package main;

import player.Player;
import tile.DoorKey;

public class CollisionDetection {
    GamePanel gp;

    // Hitbox is the player's tile shrunk by this many pixels on every side
    private final int inset = 8;

    public CollisionDetection(GamePanel gp) {
        this.gp = gp;
    }

    // Sets player.collisionOn = true if moving by (dx, dy) would hit a solid tile
    public void checkTile(Player player, double dx, double dy) {
        int ts = gp.tileSize;

        int leftCol   = (int) Math.floor((player.x + dx + inset) / ts);
        int rightCol  = (int) Math.floor((player.x + dx + ts - inset - 1) / ts);
        int topRow    = (int) Math.floor((player.y + dy + inset) / ts);
        int bottomRow = (int) Math.floor((player.y + dy + ts - inset - 1) / ts);

        if (isSolid(topRow, leftCol) || isSolid(topRow, rightCol)
                || isSolid(bottomRow, leftCol) || isSolid(bottomRow, rightCol)) {
            player.collisionOn = true;
        }
    }

    // Solid = any tile on a "Walls..." layer, or a closed door on the "DoorKey..." layer
// Water tile used by Level 2 (Floor2) and Level 3 (Water layer)
private static final int WATER_GID = 1751;

    public boolean isSolid(int row, int col) {
        for (int i = 0; i < gp.tileM.getLayerCount(); i++) {
            int gid = gp.tileM.getGid(i, row, col);
            if (gid == 0) continue;

            String name = gp.tileM.getLayerName(i).toLowerCase();

            // Level 1 "Walls", Level 2 "Wall2.1" + "Walls2", Level 3 "Walls3" + "Walls3_underwater"
            if (name.startsWith("wall")) return true;
            
            if (name.startsWith("object")) return true;

            // Water (Level 2 and Level 3)
            if (gid == WATER_GID) return true;

            // Closed doors
            if (name.startsWith("doorkey") && DoorKey.isDoorGid(gid)) return true;
        }
        return false;
    }
}
