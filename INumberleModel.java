import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.Map;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;
    void initialize();
    boolean processInput(String input);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
    boolean  compareExpressions(String input);
    boolean checkLength(String input);
    void toggleRandomEquationMode();
    boolean isRandomEquationMode();
    boolean isConsecutiveOperators(String input);
    boolean isMathExpression(String input);
    Color[] getColor(String targetNumber, String currentGuess);
    Map<Character, String> getOperatorColor(String targetStr, String guessStr);
}
