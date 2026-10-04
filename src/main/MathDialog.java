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

        JLabel questionLabel = new JLabel(currentQuestion.text, SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField answerField = new JTextField();
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton submitButton = new JButton("Submit Answer");

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
        // If player has health logic, reduce life here (e.g., player.life--)
        JOptionPane.showMessageDialog(this, "Incorrect or Time Expired! Aurelia lost 1 life.", "Failed", JOptionPane.ERROR_MESSAGE);
        dispose();
    }
}
