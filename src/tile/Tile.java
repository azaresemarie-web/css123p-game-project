/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tile;

import java.awt.image.BufferedImage;

/**
 *
 * @author emarie
 */
public class Tile {
    BufferedImage image;
    private boolean collision = false;
    
    public Tile(){
        
    }
    public Tile(BufferedImage image, boolean collision){
        this.image = image;
        this.collision = collision;
    }

    public BufferedImage getImage(){
        return image;
    }
    public void setImage(BufferedImage image){
        this.image = image;
    }
    public boolean hasCollision(){
        return collision;
    }
    public void setCollision(boolean collsion){
        this.collision = collsion;
    }
}
