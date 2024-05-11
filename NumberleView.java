import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Observer;

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JButton randomEquationButton = new JButton("Random equation");
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField inputTextField = new JTextField(3);
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");
    private final JButton showTargetButton = new JButton("Show Target Equation");
    // Changed from JTable to JTextField[][] for MatrixField
    private final JTextField[][] MatrixField = new JTextField[6][7];
    private JPanel specialButtonsPanel;
    private JPanel numberButtonsPanel;

    // Track current row index
    private int currentRowIndex = 0;
    private final int MAX_ATTEMPTS = 6;



    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel) this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel) this.model, null);

    }



    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600); // Adjusted frame height for the grid and keyboard
        frame.setLayout(new BorderLayout());

        // Create main grid panel
        JPanel mainGridPanel = new JPanel(new GridLayout(6, 7)); // 6 rows, 7 columns

        // Populate grid with text fields
        for (int row = 0; row < MatrixField.length; row++) {
            for (int col = 0; col < MatrixField[row].length; col++) {
                MatrixField[row][col] = new JTextField(3);
                MatrixField[row][col].setHorizontalAlignment(JTextField.CENTER);
                mainGridPanel.add(MatrixField[row][col]);
            }
        }

        frame.add(mainGridPanel, BorderLayout.CENTER);
        // Create main keyboard panel
        JPanel mainKeyboardPanel = new JPanel(new BorderLayout());
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        showTargetButton.setMargin(new Insets(0, 0, 0, 0));
        showTargetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Target Equation: " + controller.getTargetWord(), "Target Equation", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        randomEquationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleRandomEquationMode();
            }
        });


        // Add the showTargetButton to the checkBoxPanel
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        checkBoxPanel.add(showTargetButton);
        checkBoxPanel.add(randomEquationButton);
        // Create a panel for the attempts label
        JPanel attemptsLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        attemptsLabel.setHorizontalAlignment(JLabel.CENTER);
        attemptsLabelPanel.add(attemptsLabel);

        // Add some empty space around the attempts label
        attemptsLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Create a panel to hold both the checkbox panel and the attempts label panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(checkBoxPanel, BorderLayout.NORTH);
        northPanel.add(attemptsLabelPanel, BorderLayout.CENTER);

        // Add the north panel to the frame's NORTH region
        frame.add(northPanel, BorderLayout.NORTH);

        // Create panel for number buttons
         numberButtonsPanel = new JPanel(new GridLayout(1, 10)); // 1 row, 10 columns
        // Add buttons representing numbers 0-9
        for (int i = 0; i < 10; i++) {
            addButton(numberButtonsPanel, Integer.toString(i));
        }
        mainKeyboardPanel.add(numberButtonsPanel, BorderLayout.NORTH);

        // Create panel for special buttons
        specialButtonsPanel = new JPanel(new GridLayout(1, 7)); // 1 row, 7 columns
        String[] specialButtons = {"+", "-", "*", "/", "=", "Backspace", "Enter"};
        for (String buttonText : specialButtons) {
            addButton(specialButtonsPanel, buttonText);
        }
        // Add special buttons panel to the south of the main keyboard panel
        mainKeyboardPanel.add(specialButtonsPanel, BorderLayout.SOUTH);

        // Add main keyboard panel to the frame
        frame.add(mainKeyboardPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void toggleRandomEquationMode() {
        model.toggleRandomEquationMode();
        if (model.isRandomEquationMode()) {
            randomEquationButton.setText("Random equation");
        } else {
            randomEquationButton.setText("Fix equation");
        }
        System.out.println("Equation Mode: " + (model.isRandomEquationMode() ? "Random" : "Fixed"));
    }


    // Method to add a button to the keyboard panel
    private void addButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(50, 50)); // Set button size
        if (text.equals("Backspace")) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = currentRowIndex;
                    for (int col = MatrixField[row].length - 1; col >= 0; col--) {
                        if (MatrixField[row][col].getText() != null && !MatrixField[row][col].getText().isEmpty()) {
                            MatrixField[row][col].setText(null);
                            return;
                        }
                    }
                }
            });
        }
        else if (text.equals("Enter")) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    processInput();
                }
            });
        } else {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addNumberToGrid(text);
                }
            });
        }
        panel.add(button);
    }

    private void processInput() {
        StringBuilder input = new StringBuilder();
        for (int col = 0; col < MatrixField[currentRowIndex].length; col++) {
            String value = MatrixField[currentRowIndex][col].getText();
            if (value != null && !value.isEmpty()) {
                input.append(value);
            }
        }
        randomEquationButton.setEnabled(!checkContainsNumberAndOperator(input.toString()));

        if (input.length() == 0) {
            randomEquationButton.setEnabled(true);
            JOptionPane.showMessageDialog(null, "Please enter a guess.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!model.checkLength(input.toString())) {
            JOptionPane.showMessageDialog(null, "Your guess must contain exactly 7 characters.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!model.isMathExpression(input.toString())) {
            JOptionPane.showMessageDialog(null, "Your guess must contain both numbers and operators and = .", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (model.isConsecutiveOperators(input.toString())) {
            JOptionPane.showMessageDialog(null, "Your guess contain consecutive operators.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!model.compareExpressions(input.toString())) {
            JOptionPane.showMessageDialog(null, "The left side does not match the right side.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call controller's method to process input
        controller.processInput(input.toString());
        if (input.length() == MatrixField[currentRowIndex].length) { // Check if current row is full
            currentRowIndex++; // Move to next row if current row is full
            if (currentRowIndex >= MatrixField.length) { // Check if all rows are filled
                currentRowIndex = 0; // Reset currentRowIndex if all rows are filled
            }
        }
    }

    private boolean checkContainsNumberAndOperator(String input) {

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c) || isOperator(c)) {
                return true;
            }
        }
        return false;
    }


    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=';
    }

    // Method to add number or special character to grid
    private void addNumberToGrid(String input) {
        if (currentRowIndex < MatrixField.length) {
            for (int col = 0; col < MatrixField[currentRowIndex].length; col++) {
                String value = MatrixField[currentRowIndex][col].getText();
                if (value == null || value.isEmpty()) {
                    MatrixField[currentRowIndex][col].setText(input);
                    break;
                }
            }
        }
    }

    private void gameAgain(String status) {
        String targetWord = controller.getTargetWord();
        int result = JOptionPane.showConfirmDialog(frame, status + "! The answer is " + targetWord + "\n Do you want to play again?", status, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            controller.startNewGame();
            SwingUtilities.invokeLater(() -> {
                // Reset UI components
                resetUI();
                randomEquationButton.setEnabled(true);
            });

        } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            frame.dispose();
            System.exit(0);
        }
    }

    private void resetUI() {
        // Clear all text input fields
        for (JTextField[] row : MatrixField) {
            for (JTextField field : row) {
                field.setText("");
                field.setBackground(Color.white); // Reset background color

            }
        }
        // Reset current input status
        currentRowIndex = 0;
        // Update attempts label
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        // Reset button background colors
        resetButtonBackgroundColors(numberButtonsPanel);
        resetButtonBackgroundColors(specialButtonsPanel);
    }

    // Method to reset button background colors
    private void resetButtonBackgroundColors(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setBackground(UIManager.getColor("Button.background"));
            }
        }
    }


    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        // Update grid table colors based on model's color method
        updateGridColors();
        updateOperatorKeyboardColors();
        if (controller.isGameWon()) {
            gameAgain("Success");
        } else if (controller.isGameOver()) {
            gameAgain( "Fail");
        }
    }

    private void updateGridColors() {
        Color[] colors = model.getColor(controller.getTargetWord(), controller.getCurrentGuess().toString());

        int currentRowIndex = this.MAX_ATTEMPTS - controller.getRemainingAttempts() - 1;
        if (currentRowIndex >= 0 && colors != null) {
            for (int col = 0; col < MatrixField[currentRowIndex].length; col++) {
                if (colors[col] != null) {
                    Color backgroundColor = colors[col];
                    if (backgroundColor.equals(Color.GREEN)) {
                        backgroundColor = new Color(121, 139, 25);
                    }

                    MatrixField[currentRowIndex][col].setBackground(backgroundColor);
                }
            }
        }
    }

    private void updateKeyboardColors(JPanel panel) {
        String targetStr = controller.getTargetWord();
        String guessStr = controller.getCurrentGuess().toString();
        Map<Character, String> operatorColorMap = controller.getOperatorColor(targetStr, guessStr);

        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                char c = button.getText().charAt(0);
                String color = operatorColorMap.get(c);
                if (color != null) {
                    switch (color) {
                        case "GREEN":
                            button.setBackground(new Color(121, 139, 25));
                            break;
                        case "ORANGE":
                            button.setBackground(Color.ORANGE);
                            break;
                        case "GRAY":
                            button.setBackground(Color.LIGHT_GRAY);
                            break;
                        default:
                            button.setBackground(UIManager.getColor("Button.background"));
                    }
                }
            }
        }
    }


    private void updateOperatorKeyboardColors() {
        updateKeyboardColors(numberButtonsPanel);
        updateKeyboardColors(specialButtonsPanel);
    }

}
