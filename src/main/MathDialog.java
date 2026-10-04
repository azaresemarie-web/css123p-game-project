package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import player.Player;
import tile.DoorKey;

public class MathDialog extends JDialog {
    private int timeRemaining;
    private Timer countdownTimer;
    private MathLogic.Question currentQuestion;
    private final GamePanel gp;

    public MathDialog(JFrame parentFrame, GamePanel gp, DoorKey doorKey, Player player, int targetRow, int targetCol, int currentLevel) {
        super(parentFrame, "Door Lock Puzzle", true);
        this.gp = gp;

        MathLogic logic = new MathLogic();
        currentQuestion = logic.getRandomQuestion(currentLevel);
        timeRemaining = currentQuestion.timeLimitSeconds;

        setSize(400, 250);
        setLocationRelativeTo(parentFrame);
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel timerLabel = new JLabel("Time Remaining: " + timeRemaining + "s", SwingConstants.CENTER);
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));

<<<<<<< HEAD
        // --- Custom Visible Exit ("X") Button ---
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Krungthep", Font.BOLD, 20));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(180, 40, 40));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createLineBorder(new Color(90, 20, 20), 2));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setBounds(DIALOG_WIDTH - 90, 45, 40, 40); // Shifted inside inner area
        closeButton.addActionListener(e -> closeDialog());
        mainPanel.add(closeButton);

        // --- Timer Label (Pushed inside cream parchment area) ---
        timerLabel = new JLabel("Time: " + timeRemaining + "s", SwingConstants.CENTER);
        timerLabel.setForeground(new Color(180, 40, 40));
        timerLabel.setFont(new Font("Krungthep", Font.BOLD, 22));
        timerLabel.setBounds(100, 55, DIALOG_WIDTH - 200, 30);
        mainPanel.add(timerLabel);

        // --- Question Label ---
        questionLabel = new JLabel(currentQuestion.text, SwingConstants.CENTER);
        questionLabel.setForeground(new Color(60, 30, 15));
        questionLabel.setFont(new Font("Krungthep", Font.BOLD, 28));
        questionLabel.setBounds(80, 105, DIALOG_WIDTH - 160, 40);
        mainPanel.add(questionLabel);

        // --- Answer Input Text Field ---
        answerField = new JTextField();
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setFont(new Font("Krungthep", Font.BOLD, 24));
        answerField.setBounds((DIALOG_WIDTH - 300) / 2, 165, 300, 50);
        answerField.setBorder(BorderFactory.createLineBorder(new Color(120, 60, 20), 3));
        mainPanel.add(answerField);

        // --- In-Panel Feedback / Error Message ---
        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Krungthep", Font.BOLD, 16));
        feedbackLabel.setForeground(new Color(200, 30, 30));
        feedbackLabel.setBounds(60, 230, DIALOG_WIDTH - 120, 30);
        mainPanel.add(feedbackLabel);
=======
        JLabel questionLabel = new JLabel(currentQuestion.text, SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField answerField = new JTextField();
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton submitButton = new JButton("Submit Answer");
>>>>>>> 43dd43701b13b4ea0b517c3661d4a872f45fe4df

        add(timerLabel);
        add(questionLabel);
        add(answerField);
        add(submitButton);

        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("Time Remaining: " + timeRemaining + "s");

                // Tick sound during last 5 seconds
                if (timeRemaining <= 5 && timeRemaining > 0) {
                    gp.playTimerTick();
                }

                if (timeRemaining <= 0) {
                    processFailure(player);
                }
            }
        });
        countdownTimer.start();

        submitButton.addActionListener(e -> {
            String input = answerField.getText().replaceAll("\\s+", "").toLowerCase();
            String correct = currentQuestion.answer.replaceAll("\\s+", "").toLowerCase();

            if (input.equals(correct)) {
                countdownTimer.stop();
                gp.playRightAnswer(); // Play success chime
                doorKey.unlockDoor(targetRow, targetCol);
                dispose();
            } else {
                processFailure(player);
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void processFailure(Player player) {
        countdownTimer.stop();
        gp.playWrongAnswer(); // Play buzzer
        gp.loseHeart();
        // If player has health logic, reduce life here (e.g., player.life--)
        JOptionPane.showMessageDialog(this, "Incorrect or Time Expired! Aurelia lost 1 life.", "Failed", JOptionPane.ERROR_MESSAGE);
        dispose();
    }
}
