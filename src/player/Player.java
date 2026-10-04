/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package player;

import main.GamePanel;
import main.KeyHandler;
/**
 *
 * @author emarie
 */
public class Player {
    
    GamePanel gp;
    KeyHandler keyH;
    
    public Player(GamePanel gp, KeyHandler keyH) {
       
        this.gp = gp;
        this.keyH = keyH;
        
    }
    
}
