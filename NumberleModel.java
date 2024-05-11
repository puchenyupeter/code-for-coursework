import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private List<String> equationList;
    private boolean randomEquationMode  = true;
    private String fixedEquation = "2*3+2=8";

    public NumberleModel() {
        equationList = new ArrayList<>();
        loadEquationsFromFile("equations.txt");
    }

    private void loadEquationsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                equationList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String selectRandomEquation() {
        Random rand = new Random();
        int index = rand.nextInt(equationList.size());
        return equationList.get(index);
    }


    /**
     * Initializes the model.
     *
     * @pre None
     * @post The model is initialized with a target number.
     */
    @Override
    public void initialize() {
        if ( !randomEquationMode ) {
            targetNumber = fixedEquation;
        } else {
            targetNumber = selectRandomEquation();
        }

        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;
        currentGuess = new StringBuilder("       ");
        setChanged();
        notifyObservers();

    }


    /**
     * Processes the user input and updates the game state.
     *
     * @param input The user's input.
     * @pre input is not null.
     * @post The game state is updated based on the input.
     *       The remaining attempts are decreased by one.
     *       If the input matches the target number, gameWon is set to true.
     *       If remainingAttempts becomes less than 0, gameWon is set to false.
     * @return True if the input is processed successfully, false otherwise.
     */
    @Override
    public boolean processInput(String input) {
        remainingAttempts--;
        currentGuess = new StringBuilder(input);
        assert remainingAttempts >= 0 : "Remaining attempts must be non-negative";
        assert input != null : "Input must not be null";
        if (input.equals(targetNumber)) {
            gameWon = true;
        }
        setChanged();
        notifyObservers();
        printLatestInput(); // Print latest input data
        return true;
    }

    /**
     * Toggles the random equation mode.
     *
     * @pre None
     * @post The random equation mode is toggled.
     */
    public void toggleRandomEquationMode() {
        randomEquationMode = !randomEquationMode;
        if (randomEquationMode ||  !randomEquationMode) {
            initialize();
        }
    }

    public boolean isRandomEquationMode() {
        return randomEquationMode;
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    /**
     * Checks if the game is won by comparing the target number with the current guess.
     *
     * @return true if the current guess matches the target number, otherwise returns false.
     * @pre targetNumber and currentGuess are not null.
     * @post Returns true if the current guess matches the target number, otherwise returns false.
     */
    @Override
    public boolean isGameWon() {
        assert targetNumber != null;
        assert currentGuess != null;
        if(targetNumber.equals(currentGuess.toString()))
            gameWon = true;
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    /**
     * Checks if the input string has the correct length.
     *
     * @param input The input string to check.
     * @return True if the length of the input string is 7, otherwise returns false.
     * @pre input is not null.
     * @post The length of the input string is checked.
     */
    @Override
    public boolean checkLength(String input) {
        assert input != null : "Input must not be null";
        if (input.length() != 7) {
            return false;
        }else
            return true;
    }

    /**
     * Checks if the input string is a valid mathematical expression.
     *
     * @param input The input string to check.
     * @return True if the input string contains at least one number and one operator, otherwise returns false.
     * @pre input is not null.
     * @post The input string is checked for validity as a mathematical expression.
     */
    @Override
    public boolean isMathExpression(String input) {
        assert input != null : "Input must not be null";
        boolean containsNumber = false;
        boolean containsOperator = false;


        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                containsNumber = true;
            } else if (isOperator(c)) {
                containsOperator = true;
            }
        }

        return containsNumber && containsOperator;
    }


    /**
     * Checks if the input contains consecutive operators and provides feedback if detected.
     *
     * @param input The input string to be checked.
     * @return True if the input string contains consecutive operators, false if not, and null if the input is null.
     * @pre input is not null.
     * @post The input string is checked for consecutive operators.
     */
    @Override
    public boolean isConsecutiveOperators(String input) {
        assert input != null : "Input must not be null";

        char prevChar = input.charAt(0);
        for (int i = 1; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (isOperator(prevChar) && isOperator(currentChar)) {
                return true;
            }
            prevChar = currentChar;
        }

        return false;
    }


    /**
     * Checks if the character is an operator.
     *
     * @param c The character to check.
     * @return True if the character is an operator (+, -, *, /, =), false otherwise.
     * @pre None
     * @post Returns true if the character is an operator, false otherwise.
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=';
    }

    /**
     * Compares the results of two mathematical expressions.
     *
     * @param expression The mathematical expression to compare.
     * @return True if the results of both sides of the expression are equal, false otherwise.
     * @pre expression is not null.
     * @post Returns true if the results of both sides of the expression are equal, false otherwise.
     */
    public boolean compareExpressions(String expression) {
        expression = expression.replaceAll("\\s", "");
        String[] parts = expression.split("=");
        int leftResult = calculateExpression(parts[0]);
        int rightResult = calculateExpression(parts[1]);

        return leftResult == rightResult;
    }

    /**
     * Calculates the result of the given mathematical expression.
     *
     * @param expression The mathematical expression to calculate.
     * @return The result of the expression.
     * @pre expression is a valid mathematical expression.
     * @post The result of the expression is returned.
     */
    private int calculateExpression(String expression) {
        Stack<Integer> operandStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c)) {
                StringBuilder numBuilder = new StringBuilder();
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    numBuilder.append(expression.charAt(i));
                    i++;
                }
                i--;
                operandStack.push(Integer.parseInt(numBuilder.toString()));
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(c)) {
                    performOperation(operandStack, operatorStack);
                }
                operatorStack.push(c);
            } else if (c == '(') {
                operatorStack.push(c);
            } else if (c == ')') {
                while (operatorStack.peek() != '(') {
                    performOperation(operandStack, operatorStack);
                }
                operatorStack.pop(); // Discard '('
            }
        }

        while (!operatorStack.isEmpty()) {
            performOperation(operandStack, operatorStack);
        }

        return operandStack.pop();
    }

    /**
     * Determines the precedence of an operator.
     *
     * @param operator The operator to determine precedence for.
     * @return The precedence level of the operator.
     * @pre None
     * @post The precedence level of the operator is returned.
     */
    private int precedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Performs the specified operation on the operand stack.
     *
     * @param operandStack  The stack containing operands.
     * @param operatorStack The stack containing operators.
     * @pre Both operandStack and operatorStack are not empty.
     * @post The specified operation is performed on the operand stack.
     */
    private void performOperation(Stack<Integer> operandStack, Stack<Character> operatorStack) {
        // Check if both operandStack and operatorStack are not empty before popping elements
        if (!operandStack.isEmpty() && !operatorStack.isEmpty()) {
            char operator = operatorStack.pop();

            // Check if operandStack has at least two elements before popping operands
            if (operandStack.size() >= 2) {
                int operand2 = operandStack.pop();
                int operand1 = operandStack.pop();
                switch (operator) {
                    case '+':
                        operandStack.push(operand1 + operand2);
                        break;
                    case '-':
                        operandStack.push(operand1 - operand2);
                        break;
                    case '*':
                        operandStack.push(operand1 * operand2);
                        break;
                    case '/':
                        operandStack.push(operand1 / operand2);
                        break;
                }
            }
        } else {
            // Handle the case when either operandStack or operatorStack is empty
            System.err.println("Error: Operand stack or operator stack is empty.");
        }
    }


    /**
     * Gets the color representation for each character in the guess.
     *
     * @param targetNumber The target number.
     * @param currentGuess The user's current guess.
     * @return A color array representing the guess.
     * @pre targetNumber and currentGuess are not null.
     * @post A color array representing the guess is returned.
     */

    public Color[] getColor(String targetNumber, String currentGuess) {
        assert targetNumber != null : "Target number must not be null";
        assert currentGuess != null : "Current guess must not be null";
        Color[] colorArray = new Color[currentGuess.length()];

        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);
            char targetChar = targetNumber.charAt(i);
            Color color = null;

            if (c == targetChar) {
                color = Color.GREEN;
            } else if (targetNumber.indexOf(c) == -1) {
                color = Color.GRAY;
            } else {
                int correctCount = 0;
                int guessCount = 0;
                int haveCount = 0;

                for (int j = 0; j < targetNumber.length(); j++) {
                    if (targetNumber.charAt(j) == c) {
                        correctCount++;
                    }
                    if (currentGuess.charAt(j) == c) {
                        guessCount++;
                    }
                    if (targetNumber.charAt(j) == c && currentGuess.charAt(j) == c) {
                        haveCount++;
                    }
                }

                if (guessCount > correctCount) {
                    if (correctCount > haveCount && i <= currentGuess.indexOf(c)) {
                        color = Color.ORANGE;
                    } else {
                        color = Color.GRAY;
                    }
                } else {
                    color = Color.ORANGE;
                }
            }
            colorArray[i] = color;
        }
        return colorArray;
    }

    /**
     * Gets the color representation for each operator in the guess.
     *
     * @param targetStr The target string.
     * @param guessStr The user's guess string.
     * @return A map containing the color representation for each operator in the guess.
     * @pre targetStr and guessStr are not null.
     * @post A map containing the color representation for each operator in the guess is returned.
     */

    public Map<Character, String> getOperatorColor(String targetStr, String guessStr) {
        assert targetStr != null : "Target string must not be null";
        assert guessStr != null : "Guess string must not be null";
        Map<Character, String> colorMap = new HashMap<>();
        for (int i = 0; i < guessStr.length(); i++) {
            char c = guessStr.charAt(i);
            char targetChar = targetStr.charAt(i);
            if (c == targetChar) {
                colorMap.put(c, "GREEN");
            } else if (targetStr.indexOf(c) == -1) {
                colorMap.put(c, "GRAY");
            } else {
                int correct = 0;
                int guess = 0;
                int have = 0;
                for (int j = 0; j < targetStr.length(); j++) {
                    if (targetStr.charAt(j) == c) {
                        correct++;
                    }
                    if (guessStr.charAt(j) == c) { guess++;
                    }
                    if (targetStr.charAt(j) == c && guessStr.charAt(j) == c) {
                        have++;
                    }
                }
                if (guess > correct) {
                    if (correct > have && i <= guessStr.indexOf(c)) {
                        colorMap.put(c, "ORANGE");
                    } else {
                        colorMap.put(c, "GRAY");
                    }
                } else {
                    colorMap.put(c, "ORANGE");
                }
            }
        }
        return colorMap;
    }





    private void printLatestInput() {
        System.out.print("Latest Input: ");
        for (char c : currentGuess.toString().toCharArray()) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

}
