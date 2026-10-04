package main;

import java.util.Random;

public class MathLogic {
    
    public static class Question {
        public String text;
        public String answer;
        public int timeLimitSeconds;

        public Question(String text, String answer, int timeLimitSeconds) {
            this.text = text;
            this.answer = answer;
            this.timeLimitSeconds = timeLimitSeconds;
        }
    }

    private Question[] easyPool = {
        new Question("45 + 28 - 19 = ?", "54", 180),
        new Question("12 * 5 + 34 = ?", "94", 180),
        new Question("84 / 6 - 7 = ?", "7", 180),
        new Question("63 - 27 + 41 = ?", "77", 180),
        new Question("8 * 9 - 25 = ?", "47", 180)
    };

    private Question[] mediumPool = {
        new Question("2x + 5 = 19. Find X.", "7", 300),
        new Question("3x - 8 = 13. Find X.", "7", 300),
        new Question("5x + 12 = 47. Find X.", "7", 300),
        new Question("4x - 9 = 27. Find X.", "9", 300),
        new Question("x / 3 + 7 = 15. Find X.", "24", 300)
    };

    private Question[] hardPool = {
        new Question("Find y' for y = x^4 - 5x^2 + 7x - 3", "4x^3-10x+7", 480),
        new Question("Find y' for y = (3x^2 + 2x)(4x - 1)", "36x^2+10x-2", 480),
        new Question("Find y''' for y = x^4 + 2x^3", "24x+12", 480),
        new Question("Find y' for y = e^(2x)", "2e^(2x)", 480),
        new Question("Find y' at x=2 for y = x^3 - 4x + 2", "8", 480)
    };

    public Question getRandomQuestion(int currentLevel) {
        Random rand = new Random();
        int index = rand.nextInt(5);
        
        return switch (currentLevel) {
            case 0 -> easyPool[index];     
            case 1 -> mediumPool[index];   
            default -> hardPool[index];    
        };
    }
}