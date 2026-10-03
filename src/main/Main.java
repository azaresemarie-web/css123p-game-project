
import javax.swing.JFrame;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author emarie
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("2D Game Project");

        // Create Chloe's Game Engine Panel
        //GamePanel gamePanel = new GamePanel(); !!!!!!!!!!!!!!!!!!!!!!!!!!!
        //window.add(gamePanel);                !!!!!!!!!!!!!!!!!!!!!!!!!!!
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Start the continuous 60 FPS loop
        //gamePanel.startGameThread();          !!!!!!!!!!!!!!!!!!!!!!!!!!!      
    }
}
