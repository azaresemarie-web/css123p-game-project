package main;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Nalla
 */
import player.Player;
import tile.DoorKey;

public class CollisionDetection {
    GamePanel gp;

    private final int inset = 8;

    public CollisionDetection(GamePanel gp) {
        this.gp = gp;
    }

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

    private static final int WATER_GID = 1751;

    public boolean isSolid(int row, int col) {
        for (int i = 0; i < gp.tileM.getLayerCount(); i++) {
            int gid = gp.tileM.getGid(i, row, col);
            if (gid == 0) continue;

            String name = gp.tileM.getLayerName(i).toLowerCase();

            if (name.startsWith("wall")) return true;
            
            if (name.startsWith("object")) return true;

            if (gid == WATER_GID) return true;

            if (name.startsWith("doorkey") && DoorKey.isDoorGid(gid)) return true;
        }
        return false;
    }
}