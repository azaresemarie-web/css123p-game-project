/* package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "FormulaHeroLeaderboard";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL + DB_NAME, USER, PASSWORD);
    }

    public static void initializeDatabase() {

        String[] setupQueries = {
            "CREATE DATABASE IF NOT EXISTS " + DB_NAME,
            "USE " + DB_NAME,
            
            "CREATE TABLE IF NOT EXISTS LEVEL (" +
            "level_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "level_number INT NOT NULL, " +
            "level_name VARCHAR(100) NOT NULL, " +
            "description VARCHAR(255))",

            "CREATE TABLE IF NOT EXISTS GAME_SESSION (" +
            "session_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "player_name VARCHAR(100) NOT NULL, " +
            "level_id INT NOT NULL, " +
            "start_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "end_time DATETIME NULL, " +
            "lives_remaining INT NOT NULL, " +
            "session_status ENUM('IN_PROGRESS', 'COMPLETED', 'FAILED') DEFAULT 'IN_PROGRESS', " +
            "FOREIGN KEY (level_id) REFERENCES LEVEL(level_id) ON DELETE CASCADE)",

            "CREATE TABLE IF NOT EXISTS QUESTION (" +
            "question_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "level_id INT NOT NULL, " +
            "difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL, " +
            "question_text VARCHAR(255) NOT NULL, " +
            "correct_answer VARCHAR(100) NOT NULL, " +
            "topic_category VARCHAR(100), " +
            "time_limit_seconds INT NOT NULL, " +
            "FOREIGN KEY (level_id) REFERENCES LEVEL(level_id) ON DELETE CASCADE)",

            "CREATE TABLE IF NOT EXISTS QUESTION_ATTEMPT (" +
            "attempt_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "session_id INT NOT NULL, " +
            "question_id INT NOT NULL, " +
            "player_answer VARCHAR(100), " +
            "is_correct BOOLEAN NOT NULL, " +
            "time_taken_seconds INT NOT NULL, " +
            "attempt_order INT NOT NULL, " +
            "FOREIGN KEY (session_id) REFERENCES GAME_SESSION(session_id) ON DELETE CASCADE, " +
            "FOREIGN KEY (question_id) REFERENCES QUESTION(question_id) ON DELETE CASCADE)",

            "CREATE TABLE IF NOT EXISTS LEADERBOARD (" +
            "leaderboard_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "player_name VARCHAR(100) NOT NULL, " +
            "total_completion_time_seconds INT NOT NULL, " +
            "level_reached INT NOT NULL, " +
            "date_achieved DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "rank_position INT DEFAULT 0)"
        };

        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            for (String query : setupQueries) {
                stmt.executeUpdate(query);
            }
            System.out.println("Database and tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database initialization failed. Is XAMPP MySQL running?");
            e.printStackTrace();
        }
    }
}
*/