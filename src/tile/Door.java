import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class Door {

    
    public enum Difficulty {
        EASY("Easy", 3 * 60, "Arithmetic"),
        MEDIUM("Medium", 5 * 60, "Algebra"),
        HARD("Hard", 8 * 60, "Differential Calculus");

        private final String label;
        private final int timeLimitSeconds;
        private final String topic;

        Difficulty(String label, int timeLimitSeconds, String topic) {
            this.label = label;
            this.timeLimitSeconds = timeLimitSeconds;
            this.topic = topic;
        }

        public String label() { return label; }
        public int timeLimitSeconds() { return timeLimitSeconds; }
        public String topic() { return topic; }
        public int level() { return ordinal() + 1; }

        public static Difficulty forLevel(int level) {
            return switch (level) {
                case 1 -> EASY;
                case 2 -> MEDIUM;
                default -> HARD;
            };
        }
    }

    public enum Outcome { CORRECT, WRONG, TIMEOUT }

   
    @FunctionalInterface
    public interface Listener {
        void onDoorResult(Door door, Outcome outcome, String playerAnswer, int secondsTaken);
    }

   
    public record Question(int id, Difficulty difficulty, String text, String answerText,
                           double numericAnswer, DoubleUnaryOperator reference) {

        private static final Pattern NUMBER = Pattern.compile("[+-]?\\d+(\\.\\d+)?");

        static Question numeric(int id, Difficulty d, String text, int answer) {
            return new Question(id, d, text, String.valueOf(answer), answer, null);
        }

        static Question expression(int id, String text, String answerText, DoubleUnaryOperator ref) {
            return new Question(id, Difficulty.HARD, text, answerText, Double.NaN, ref);
        }

        
        public boolean isCorrect(String rawInput) {
            String s = clean(rawInput);
            if (s.isEmpty()) {
                throw new IllegalArgumentException("Type your answer first.");
            }
            if (reference == null) {
                if (!NUMBER.matcher(s).matches()) {
                    throw new IllegalArgumentException("Enter a number, e.g. 7");
                }
                return Math.abs(Double.parseDouble(s) - numericAnswer) < 1e-9;
            }
            return ExprParser.matches(s, reference);
        }

        public String html() {
            return text
                    .replaceAll("\\^\\(([^)]*)\\)", "<sup>$1</sup>")
                    .replaceAll("\\^(\\d+)", "<sup>$1</sup>")
                    .replace("sqrt(x)", "\u221Ax");
        }

        private static String clean(String raw) {
            if (raw == null) return "";
            String s = raw.toLowerCase()
                    .replaceAll("\\s+", "")
                    .replace('\u2212', '-')   // unicode minus
                    .replace('\u00D7', '*')   // multiplication sign
                    .replace('\u00B7', '*')   // middle dot
                    .replace('\u00F7', '/')   // division sign
                    .replace("**", "^")
                    .replace('[', '(').replace(']', ')')
                    .replace('{', '(').replace('}', ')');
            return s.replaceFirst("^(?:x|y'{0,3})(?:\\([^)]*\\))?=", "");
        }
    }

   

    private static final List<Question> POOL = buildPool();

    private static List<Question> buildPool() {
        List<Question> p = new ArrayList<>();
        Difficulty e = Difficulty.EASY;
        p.add(Question.numeric(1, e, "45 + 28 - 19 = ?", 54));
        p.add(Question.numeric(2, e, "12 \u00D7 5 + 34 = ?", 94));
        p.add(Question.numeric(3, e, "84 \u00F7 6 - 7 = ?", 7));
        p.add(Question.numeric(4, e, "63 - 27 + 41 = ?", 77));
        p.add(Question.numeric(5, e, "8 \u00D7 9 - 25 = ?", 47));
        p.add(Question.numeric(6, e, "96 \u00F7 8 + 18 = ?", 30));
        p.add(Question.numeric(7, e, "50 + 16 \u00D7 3 = ?", 98));
        p.add(Question.numeric(8, e, "72 \u00F7 9 + 45 = ?", 53));
        p.add(Question.numeric(9, e, "85 - 4 \u00D7 12 = ?", 37));
        p.add(Question.numeric(10, e, "38 + 52 \u00F7 4 = ?", 51));

        Difficulty m = Difficulty.MEDIUM;
        p.add(Question.numeric(11, m, "Solve for x:  2x + 5 = 19", 7));
        p.add(Question.numeric(12, m, "Solve for x:  3x - 8 = 13", 7));
        p.add(Question.numeric(13, m, "Solve for x:  5x + 12 = 47", 7));
        p.add(Question.numeric(14, m, "Solve for x:  4x - 9 = 27", 9));
        p.add(Question.numeric(15, m, "Solve for x:  x/3 + 7 = 15", 24));
        p.add(Question.numeric(16, m, "Solve for x:  6x + 14 = 50", 6));
        p.add(Question.numeric(17, m, "Solve for x:  7x - 15 = 34", 7));
        p.add(Question.numeric(18, m, "Solve for x:  x/4 - 3 = 5", 32));
        p.add(Question.numeric(19, m, "Solve for x:  8x + 11 = 75", 8));
        p.add(Question.numeric(20, m, "Solve for x:  9x - 20 = 43", 7));

        p.add(Question.expression(21, "Find y' for y = x^4 - 5x^2 + 7x - 3",
                "4x^3 - 10x + 7",
                x -> 4 * x * x * x - 10 * x + 7));
        p.add(Question.expression(22, "Find y' for y = (3x^2 + 2x)(4x - 1)",
                "36x^2 + 10x - 2",
                x -> 36 * x * x + 10 * x - 2));
        p.add(Question.expression(23, "Find y' for y = (2x + 5) / (x^2 + 3)",
                "(-2x^2 - 10x + 6) / (x^2 + 3)^2",
                x -> (-2 * x * x - 10 * x + 6) / Math.pow(x * x + 3, 2)));
        p.add(Question.expression(24, "Find y''' for y = x^4 + 2x^3",
                "24x + 12",
                x -> 24 * x + 12));
        p.add(Question.expression(25, "Find y' for y = e^(2x)",
                "2e^(2x)",
                x -> 2 * Math.exp(2 * x)));
        p.add(Question.expression(26, "Find y' for y = ln(4x^2 + 1)",
                "8x / (4x^2 + 1)",
                x -> 8 * x / (4 * x * x + 1)));
        p.add(Question.expression(27, "Find y' for y = sin(3x^2)",
                "6x cos(3x^2)",
                x -> 6 * x * Math.cos(3 * x * x)));
        p.add(Question.expression(28, "Find y'' for y = 2x^5 - 3x^3 + 8x",
                "40x^3 - 18x",
                x -> 40 * x * x * x - 18 * x));
        p.add(Question.expression(29, "Find y' for y = e^(3x) + sqrt(x)",
                "3e^(3x) + 1/(2 sqrt(x))",
                x -> 3 * Math.exp(3 * x) + 1 / (2 * Math.sqrt(x))));
        p.add(Question.expression(30, "Find y' at x = 2 for y = x^3 - 4x + 2",
                "8",
                x -> 8));
        return Collections.unmodifiableList(p);
    }

    public static List<Question> getQuestionPool() {
        return POOL;
    }

    private static List<Question> questionsFor(Difficulty d) {
        List<Question> out = new ArrayList<>();
        for (Question q : POOL) {
            if (q.difficulty() == d) out.add(q);
        }
        return out;
    }

   
    private static final String[] LEVEL_NAMES = {"The Threshold", "Dungeon Descent", "Occluded Depths"};
    private static final AtomicBoolean QUIZ_OPEN = new AtomicBoolean(false);
    private static QuizDialog activeDialog;   // touched on the EDT only

    private final int level;
    private final int col;
    private final int row;
    private final Question question;
    private boolean open = false;
    private int timeLimitSeconds;
    private Listener listener;
    private BufferedImage closedSprite;
    private BufferedImage openSprite;

    public Door(int level, int col, int row, Question question) {
        this.level = level;
        this.col = col;
        this.row = row;
        this.question = question;
        this.timeLimitSeconds = question.difficulty().timeLimitSeconds();
    }

  
    public static List<Door> createLevelDoors(int level, Listener listener, int[]... positions) {
        List<Question> pool = questionsFor(Difficulty.forLevel(level));
        if (positions.length > pool.size()) {
            throw new IllegalArgumentException("Only " + pool.size() + " questions exist for level " + level);
        }
        Collections.shuffle(pool);
        List<Door> doors = new ArrayList<>();
        for (int i = 0; i < positions.length; i++) {
            Door d = new Door(level, positions[i][0], positions[i][1], pool.get(i));
            d.setListener(listener);
            doors.add(d);
        }
        return doors;
    }

    public static boolean allOpen(List<Door> doors) {
        for (Door d : doors) {
            if (!d.open) return false;
        }
        return true;
    }

  

    public int getLevel() { return level; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public Question getQuestion() { return question; }
    public boolean isOpen() { return open; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }

    public void setTimeLimitSeconds(int seconds) { this.timeLimitSeconds = Math.max(1, seconds); }

    public void setListener(Listener listener) { this.listener = listener; }

    public void setSprites(BufferedImage closed, BufferedImage opened) {
        this.closedSprite = closed;
        this.openSprite = opened;
    }

   

    public boolean isSolid() { return !open; }

    public Rectangle getBounds(int tileSize) {
        return new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
    }

    /** True if the player's tile is directly up/down/left/right of this door. */
    public boolean isNextTo(int playerCol, int playerRow) {
        return Math.abs(playerCol - col) + Math.abs(playerRow - row) == 1;
    }


    public void draw(Graphics2D g2, int tileSize) {
        int px = col * tileSize;
        int py = row * tileSize;
        BufferedImage img = open ? openSprite : closedSprite;
        if (img != null) {
            g2.drawImage(img, px, py, tileSize, tileSize, null);
            return;
        }
        if (open) {
            g2.setColor(new Color(20, 14, 10));
            g2.fillRect(px, py, tileSize, tileSize);
            g2.setColor(new Color(110, 70, 30));
            g2.drawRect(px + 1, py + 1, tileSize - 3, tileSize - 3);
        } else {
            g2.setColor(new Color(120, 76, 34));
            g2.fillRect(px, py, tileSize, tileSize);
            g2.setColor(new Color(70, 42, 18));
            g2.drawRect(px, py, tileSize - 1, tileSize - 1);
            g2.drawLine(px + tileSize / 2, py, px + tileSize / 2, py + tileSize);
            g2.setColor(new Color(240, 200, 90));
            int k = Math.max(4, tileSize / 6);
            g2.fillOval(px + tileSize / 2 + k, py + tileSize / 2 - k / 2, k, k);
        }
    }

    
    public static boolean isQuizOpen() {
        return QUIZ_OPEN.get();
    }

    public boolean interact(Component parent) {
        if (open || !QUIZ_OPEN.compareAndSet(false, true)) {
            return false;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                runQuiz(parent);
            } finally {
                QUIZ_OPEN.set(false);
            }
        });
        return true;
    }

   
    public static void closeActiveQuiz() {
        SwingUtilities.invokeLater(() -> {
            if (activeDialog != null) activeDialog.abort();
        });
    }

    private void runQuiz(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        QuizDialog dialog = new QuizDialog(owner, level, question, timeLimitSeconds);
        activeDialog = dialog;
        dialog.setVisible(true);         
        activeDialog = null;

        if (dialog.outcome == null) {
            return;                      
        }
        if (dialog.outcome == Outcome.CORRECT) {
            open = true;
        }
        if (listener != null) {
            String answer = dialog.playerAnswer;
            if (answer.length() > 100) answer = answer.substring(0, 100);   
            listener.onDoorResult(this, dialog.outcome, answer, dialog.secondsTaken);
        }
    }

   
    private static final class ExprParser {
        private static final String[] FUNCS = {"sqrt", "sin", "cos", "tan", "exp", "ln"};
        private static final double[] SAMPLES = {0.5, 0.9, 1.3, 1.9, 2.6, 3.4};

        private final String s;
        private final double x;
        private int pos = 0;

        private ExprParser(String s, double x) {
            this.s = s;
            this.x = x;
        }

        static boolean matches(String input, DoubleUnaryOperator reference) {
            for (double x : SAMPLES) {
                double got = new ExprParser(input, x).evaluate();   // throws on bad syntax
                double want = reference.applyAsDouble(x);
                if (!(Math.abs(got - want) <= 1e-6 * Math.max(1.0, Math.abs(want)))) {
                    return false;
                }
            }
            return true;
        }

        private double evaluate() {
            double v = parseSum();
            if (pos != s.length()) throw error();
            return v;
        }

        private IllegalArgumentException error() {
            return new IllegalArgumentException("Can't read that answer.");
        }

        private double parseSum() {
            double v = parseProduct();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '+') { pos++; v += parseProduct(); }
                else if (c == '-') { pos++; v -= parseProduct(); }
                else break;
            }
            return v;
        }

        private double parseProduct() {
            double v = parseUnary();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '*') { pos++; v *= parseUnary(); }
                else if (c == '/') { pos++; v /= parseUnary(); }
                else if (Character.isLetter(c) || c == '(') { v *= parseUnary(); }   
                else break;
            }
            return v;
        }

        private double parseUnary() {
            if (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '-') { pos++; return -parseUnary(); }
                if (c == '+') { pos++; return parseUnary(); }
            }
            return parsePower();
        }

        private double parsePower() {
            double base = parseAtom();
            if (pos < s.length() && s.charAt(pos) == '^') {
                pos++;
                return Math.pow(base, parseUnary());   
            }
            return base;
        }

        private double parseAtom() {
            if (pos >= s.length()) throw error();
            char c = s.charAt(pos);
            if (c == '(') {
                pos++;
                double v = parseSum();
                expectClose();
                return v;
            }
            if (Character.isDigit(c) || c == '.') {
                int start = pos;
                while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
                try {
                    return Double.parseDouble(s.substring(start, pos));
                } catch (NumberFormatException ex) {
                    throw error();
                }
            }
            for (String f : FUNCS) {
                int end = pos + f.length();
                if (s.startsWith(f, pos) && end < s.length() && s.charAt(end) == '(') {
                    pos = end + 1;
                    double arg = parseSum();
                    expectClose();
                    return apply(f, arg);
                }
            }
            if (c == 'x') { pos++; return x; }
            if (c == 'e') { pos++; return Math.E; }
            throw error();
        }

        private void expectClose() {
            if (pos < s.length() && s.charAt(pos) == ')') pos++;
            else throw error();
        }

        private static double apply(String f, double a) {
            return switch (f) {
                case "sqrt" -> Math.sqrt(a);
                case "sin" -> Math.sin(a);
                case "cos" -> Math.cos(a);
                case "tan" -> Math.tan(a);
                case "exp" -> Math.exp(a);
                default -> Math.log(a);   // "ln"
            };
        }
    }

   

    @SuppressWarnings("serial")
    private static final class QuizDialog extends JDialog {
        private static final Color BG = new Color(18, 14, 24);
        private static final Color GOLD = new Color(240, 200, 90);
        private static final Color TEXT = new Color(235, 225, 210);
        private static final Color RED = new Color(235, 80, 80);
        private static final Color GREEN = new Color(110, 220, 120);
        private static final int WARNING_SECONDS = 30;
        private static final int RESULT_DELAY_MS = 1000;

        private final Question question;
        private final int limitSeconds;
        private final long startNs = System.nanoTime();
        private final JLabel timerLabel = makeLabel("", 16, TEXT);
        private final JLabel feedback = makeLabel(" ", 14, RED);
        private final JTextField input = new JTextField(20);
        private final JButton submit = new JButton("OPEN DOOR");
        private final Timer ticker;
        private boolean finished = false;

        Outcome outcome = null;
        String playerAnswer = "";
        int secondsTaken = 0;

        QuizDialog(Window owner, int level, Question q, int limitSeconds) {
            super(owner, "Locked Door", Dialog.ModalityType.APPLICATION_MODAL);
            this.question = q;
            this.limitSeconds = limitSeconds;

            setUndecorated(true);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);   // once you knock, you must answer

            String levelName = LEVEL_NAMES[Math.max(0, Math.min(level, LEVEL_NAMES.length) - 1)];
            JLabel header = makeLabel("LEVEL " + level + " - " + levelName.toUpperCase(), 14, GOLD);
            JLabel mode = makeLabel("LOCKED DOOR  |  " + q.difficulty().label().toUpperCase(), 18, GOLD);
            JPanel north = new JPanel(new BorderLayout(0, 4));
            north.setOpaque(false);
            north.add(header, BorderLayout.NORTH);
            north.add(mode, BorderLayout.CENTER);

            JLabel questionLabel = makeLabel(
                    "<html><body style='width:460px;text-align:center'>" + q.html() + "</body></html>", 24, TEXT);
            questionLabel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

            JLabel hint = makeLabel(hintFor(q), 12, new Color(160, 150, 140));

            input.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            input.setHorizontalAlignment(SwingConstants.CENTER);
            input.setBackground(new Color(34, 28, 44));
            input.setForeground(TEXT);
            input.setCaretColor(GOLD);
            input.setBorder(BorderFactory.createLineBorder(GOLD, 2));

            submit.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
            submit.setFocusPainted(false);
            submit.setBackground(GOLD);
            submit.setForeground(BG);

            JPanel inputRow = new JPanel(new BorderLayout(10, 0));
            inputRow.setOpaque(false);
            inputRow.add(input, BorderLayout.CENTER);
            inputRow.add(submit, BorderLayout.EAST);

            JPanel south = new JPanel(new java.awt.GridLayout(0, 1, 0, 6));
            south.setOpaque(false);
            south.add(timerLabel);
            south.add(hint);
            south.add(inputRow);
            south.add(feedback);

            JPanel root = new JPanel(new BorderLayout(0, 6));
            root.setBackground(BG);
            root.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD, 3),
                    BorderFactory.createEmptyBorder(16, 24, 16, 24)));
            root.add(north, BorderLayout.NORTH);
            root.add(questionLabel, BorderLayout.CENTER);
            root.add(south, BorderLayout.SOUTH);
            setContentPane(root);

            submit.addActionListener(e -> onSubmit());
            input.addActionListener(e -> onSubmit());       // Enter key
            ticker = new Timer(200, e -> onTick());

            pack();
            setMinimumSize(new Dimension(560, getHeight()));
            setLocationRelativeTo(owner);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    input.requestFocusInWindow();
                }
            });
            updateTimerLabel(limitSeconds);
            ticker.start();
        }

        private static String hintFor(Question q) {
            return q.difficulty() == Difficulty.HARD
                    ? "Use ^ for powers and ( ) for functions: e^(2x), sin(3x^2), sqrt(x)"
                    : "Type your answer as a number, e.g. 7";
        }

        private static JLabel makeLabel(String text, int size, Color color) {
            JLabel l = new JLabel(text, SwingConstants.CENTER);
            l.setFont(new Font(Font.MONOSPACED, Font.BOLD, size));
            l.setForeground(color);
            return l;
        }

        private void onTick() {
            if (finished) return;
            long remainingNs = limitSeconds * 1_000_000_000L - (System.nanoTime() - startNs);
            if (remainingNs <= 0) {
                finish(Outcome.TIMEOUT, "TIME'S UP! Aurelia loses a life.", RED);
                return;
            }
            updateTimerLabel((int) ((remainingNs + 999_999_999L) / 1_000_000_000L));
        }

        private void updateTimerLabel(int secondsLeft) {
            timerLabel.setText(String.format("TIME LEFT  %02d:%02d", secondsLeft / 60, secondsLeft % 60));
            timerLabel.setForeground(secondsLeft <= WARNING_SECONDS ? RED : TEXT);
        }

        private void onSubmit() {
            if (finished) return;
            boolean correct;
            try {
                correct = question.isCorrect(input.getText());
            } catch (IllegalArgumentException ex) {
                feedback.setForeground(GOLD);
                feedback.setText(question.difficulty() == Difficulty.HARD && !input.getText().isBlank()
                        ? "Can't read that - check your ^ and ( )." : ex.getMessage());
                input.selectAll();
                input.requestFocusInWindow();
                return;
            }
            if (correct) {
                finish(Outcome.CORRECT, "CORRECT! The door creaks open...", GREEN);
            } else {
                finish(Outcome.WRONG, "WRONG! Aurelia loses a life.", RED);
            }
        }

        private void finish(Outcome result, String message, Color color) {
            finished = true;
            ticker.stop();
            outcome = result;
            playerAnswer = input.getText().trim();
            secondsTaken = (int) Math.min(limitSeconds, (System.nanoTime() - startNs) / 1_000_000_000L);
            input.setEnabled(false);
            submit.setEnabled(false);
            feedback.setForeground(color);
            feedback.setText(message);
            Timer closer = new Timer(RESULT_DELAY_MS, e -> dispose());
            closer.setRepeats(false);
            closer.start();
        }

        void abort() {
            finished = true;
            ticker.stop();
            outcome = null;
            dispose();
        }
    }
}
