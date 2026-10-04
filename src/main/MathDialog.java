package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import player.Player;
import tile.DoorKey;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MathDialog extends JDialog {
    private int timeRemaining;
    private Timer countdownTimer;
    private MathLogic.Question currentQuestion;
    private final GamePanel gp;

    // Assets
    private BufferedImage bgImage;
    private ImageIcon submitIcon;
    private ImageIcon resetIcon;

    // UI Components
    private JLabel timerLabel;
    private JLabel questionLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;

    // Expanded Window Dimensions
    private static final int DIALOG_WIDTH = 650;
    private static final int DIALOG_HEIGHT = 450;

    public MathDialog(JFrame parentFrame, GamePanel gp, DoorKey doorKey, Player player, int targetRow, int targetCol, int currentLevel) {
        super(parentFrame, "Door Lock Puzzle", true);
        this.gp = gp;

        // 1. Load Math Question
        MathLogic logic = new MathLogic();
        currentQuestion = logic.getRandomQuestion(currentLevel);
        timeRemaining = currentQuestion.timeLimitSeconds;

        // 2. Load Button Assets with Proportional Scaling
        loadAssets(150, 55);

        // 3. Configure Dialog Window
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparent around rounded pixel border
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(parentFrame);

        // 4. Main Panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(new Color(60, 30, 15));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);

        // --- Custom Visible Exit ("X") Button ---
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
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
        timerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        timerLabel.setBounds(100, 55, DIALOG_WIDTH - 200, 30);
        mainPanel.add(timerLabel);

        // --- Question Label ---
        questionLabel = new JLabel(currentQuestion.text, SwingConstants.CENTER);
        questionLabel.setForeground(new Color(60, 30, 15));
        questionLabel.setFont(new Font("Arial", Font.BOLD, 28));
        questionLabel.setBounds(80, 105, DIALOG_WIDTH - 160, 40);
        mainPanel.add(questionLabel);

        // --- Answer Input Text Field ---
        answerField = new JTextField();
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setFont(new Font("Arial", Font.BOLD, 24));
        answerField.setBounds((DIALOG_WIDTH - 300) / 2, 165, 300, 50);
        answerField.setBorder(BorderFactory.createLineBorder(new Color(120, 60, 20), 3));
        mainPanel.add(answerField);

        // --- In-Panel Feedback / Error Message ---
        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        feedbackLabel.setForeground(new Color(200, 30, 30));
        feedbackLabel.setBounds(60, 230, DIALOG_WIDTH - 120, 30);
        mainPanel.add(feedbackLabel);

        // --- SUBMIT Button ---
        JButton submitButton = new JButton();
        if (submitIcon != null) {
            submitButton.setIcon(submitIcon);
        } else {
            submitButton.setText("SUBMIT");
        }
        styleImageButton(submitButton);
        submitButton.setBounds((DIALOG_WIDTH / 2) - 165, 285, 150, 55);
        mainPanel.add(submitButton);

        // --- RESET Button ---
        JButton resetButton = new JButton();
        if (resetIcon != null) {
            resetButton.setIcon(resetIcon);
        } else {
            resetButton.setText("RESET");
        }
        styleImageButton(resetButton);
        resetButton.setBounds((DIALOG_WIDTH / 2) + 15, 285, 150, 55);
        mainPanel.add(resetButton);

        add(mainPanel);

        // 5. Timer Logic
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("Time: " + timeRemaining + "s");

                if (timeRemaining <= 5 && timeRemaining > 0) {
                    gp.playTimerTick();
                }

                if (timeRemaining <= 0) {
                    processFailure(player, "Time Expired! Aurelia lost 1 life.");
                }
            }
        });
        countdownTimer.start();

        // 6. Event Handlers
        submitButton.addActionListener(e -> checkAnswer(doorKey, player, targetRow, targetCol));
        answerField.addActionListener(e -> checkAnswer(doorKey, player, targetRow, targetCol));

        resetButton.addActionListener(e -> {
            answerField.setText("");
            answerField.requestFocus();
        });

        // Close when pressing ESC
        answerField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeDialog();
                }
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void checkAnswer(DoorKey doorKey, Player player, int targetRow, int targetCol) {
        String input = answerField.getText().replaceAll("\\s+", "").toLowerCase();
        String correct = currentQuestion.answer.replaceAll("\\s+", "").toLowerCase();

        if (input.equals(correct)) {
            countdownTimer.stop();
            gp.playRightAnswer();
            doorKey.unlockDoor(targetRow, targetCol);
            dispose();
        } else {
            processFailure(player, "Incorrect answer! Aurelia lost 1 life.");
        }
    }

    private void loadAssets(int btnWidth, int btnHeight) {
        try {
            bgImage = ImageIO.read(getClass().getResourceAsStream("/buttons/mathPanel.png"));

            BufferedImage submitImg = ImageIO.read(getClass().getResourceAsStream("/buttons/submitBtn.png"));
            if (submitImg != null) {
                Image scaled = submitImg.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
                submitIcon = new ImageIcon(scaled);
            }

            BufferedImage resetImg = ImageIO.read(getClass().getResourceAsStream("/buttons/resetBtn.png"));
            if (resetImg != null) {
                Image scaled = resetImg.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
                resetIcon = new ImageIcon(scaled);
            }

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Note: Check asset paths inside /buttons/ directory.");
        }
    }

    private void styleImageButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void processFailure(Player player, String errorMessage) {
        countdownTimer.stop();
        gp.playWrongAnswer();

        feedbackLabel.setText(errorMessage);
        answerField.setEnabled(false);

        Timer delayTimer = new Timer(1500, e -> dispose());
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void closeDialog() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        dispose();
    }
}