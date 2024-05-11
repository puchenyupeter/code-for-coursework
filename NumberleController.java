import java.awt.*;
import java.util.Map;

// NumberleController.java
public class NumberleController {
    private INumberleModel model;
    private NumberleView view;

    public NumberleController(INumberleModel model) {
        this.model = model;
    }

    public void setView(NumberleView view) {
        this.view = view;
    }

    public void processInput(String input) {
        model.processInput(input);
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public boolean isGameWon() {
        return model.isGameWon();
    }

    public String getTargetWord() {
        return model.getTargetNumber();
    }

    public StringBuilder getCurrentGuess() {
        return model.getCurrentGuess();
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    public void startNewGame() {
        model.startNewGame();
    }

    public Color[] getColor() {
        String targetNumber = model.getTargetNumber();
        StringBuilder currentGuess = model.getCurrentGuess();
        return model.getColor(targetNumber, currentGuess.toString());
    }

    public Map<Character, String> getOperatorColor(String targetStr, String guessStr) {
        return model.getOperatorColor(targetStr, guessStr);
    }
}