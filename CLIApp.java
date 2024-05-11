import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;

public class CLIApp {
    private static final int MAX_ATTEMPTS = 6;
    private NumberleModel model;
    private List<String> previousGuesses;

    public CLIApp() {
        model = new NumberleModel();
        model.initialize();
        previousGuesses = new ArrayList<>();
    }

    public static void main(String[] args) {
        CLIApp cliApp = new CLIApp();
        cliApp.startGame();
    }

    private void startGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Numberle!");
        boolean playAgain = true;
        while (playAgain) {
            model.startNewGame();
            playRound(scanner);
            System.out.println("Do you want to play again? (yes/no): ");
            String choice = scanner.nextLine().trim();
            if (!choice.equalsIgnoreCase("yes")) {
                playAgain = false;
            }

        }
        scanner.close();
        System.out.println("Thanks for playing Numberle!");
    }

    private void playRound(Scanner scanner) {
        while (!model.isGameOver()) {
            System.out.println("Attempts remaining: " + model.getRemainingAttempts());
            if (!previousGuesses.isEmpty()) {
                System.out.println("Previous guesses:");
                for (String guess : previousGuesses) {
                    System.out.println(colorizeString(guess, model.getColor(model.getTargetNumber(), guess)));
                }
            }
            System.out.println("Please guess a 7-digit math expression: ");
            String guess = scanner.nextLine().replaceAll("\\s+", ""); // Remove all whitespace characters
            if (!isValidInput(guess)) {
                System.out.println("Invalid input. Please enter a valid expression.");
                continue;
            }
            if (!model.isMathExpression(guess)) {
                System.out.println("Invalid expression. Please enter a valid mathematical expression.");
                continue;
            }
            if (!model.checkLength(guess)) {
                System.out.println("Invalid expression length. Please enter a 7-digit expression.");
                continue;
            }
            if (!model.compareExpressions(guess)) {
                System.out.println("Invalid expression. The expression is not correct.");
                continue;
            }
            model.processInput(guess);
            previousGuesses.add(guess);
            printGuessFeedback(guess);
        }
        if (!model.isGameWon()) {
            System.out.println("Game over. You've used all your attempts. The correct number was: " + model.getTargetNumber());
        }
        previousGuesses.clear(); // Clear previous guesses for next round
    }

    private String colorizeString(String str, Color[] colors) {
        StringBuilder formattedString = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            formattedString.append(colorizeChar(str.charAt(i), colors[i])).append(" ");
        }
        return formattedString.toString();
    }


    private boolean isValidInput(String input) {
        return input.matches("[0-9()+\\-*/=]{7}");
    }

    private void printGuessFeedback(String guess) {
        Color[] colors = model.getColor(model.getTargetNumber(), guess);
        StringBuilder formattedGuess = new StringBuilder("Your Guess: ");
        for (int i = 0; i < guess.length(); i++) {
            formattedGuess.append(colorizeChar(guess.charAt(i), colors[i])).append(" ");
        }
        System.out.println(formattedGuess);
        if (model.isGameWon()) {
            System.out.println("Congratulations! You guessed the correct number: " + model.getTargetNumber());
        } else {
            System.out.println("Incorrect guess. Keep trying!");
        }
        System.out.println("============================================");
    }

    private String colorizeChar(char c, Color color) {
        if (color == Color.GREEN) {
            return "\u001B[92m" + c + "\u001B[0m"; // Green color
        } else if (color == Color.ORANGE) {
            return "\u001B[91m" + c + "\u001B[0m"; // Orange color
        } else if (color == Color.GRAY) {
            return "\u001B[90m" + c + "\u001B[0m"; // Gray color
        } else {
            return String.valueOf(c); // Default to original character
        }
    }
}
