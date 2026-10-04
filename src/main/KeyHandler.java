/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author emarie
 */
public class KeyHandler implements KeyListener{

    public boolean upPressed, downPressed, rightPressed, leftPressed;
    public boolean ePressed;
    public int levelRequest = -1;  
    
    
    public void resetKeys() {
        upPressed = downPressed = leftPressed = rightPressed = false;
        ePressed = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_E) ePressed = true;
        
        if (code == KeyEvent.VK_1) levelRequest = 0;
        if (code == KeyEvent.VK_2) levelRequest = 1;
        if (code == KeyEvent.VK_3) levelRequest = 2;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_E) ePressed = false;
        
    }
    
}
