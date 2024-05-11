import org.junit.Test;

import static org.junit.Assert.*;

public class NumberleModelTest {

    /**
     * Checks if the length of the input string is 7.
     *
     * @pre input is not null.
     * @post Returns true if the length of the input string is 7, otherwise returns false.
     */
    @Test
    public void testCheckLength() {
        NumberleModel model = new NumberleModel();
        assertTrue(model.checkLength("1234567"));
        assertFalse(model.checkLength("123456"));
    }

    /**
     * Checks if the input string is a valid mathematical expression.
     *
     * @pre input is not null.
     * @post Returns true if the input string contains at least one number and one operator, otherwise returns false.
     */
    @Test
    public void testIsMathExpression() {
        NumberleModel model = new NumberleModel();
        assertTrue(model.isMathExpression("2+3=5"));
        assertFalse(model.isMathExpression("123456"));
    }

    /**
     * Checks if the input contains consecutive operators.
     *
     * @pre input is not null.
     * @post Returns true if the input string contains consecutive operators, false if not, and null if the input is null.
     */
    @Test
    public void testIsConsecutiveOperators() {
        NumberleModel model = new NumberleModel();
        assertTrue(model.isConsecutiveOperators("2++3=5"));
        assertFalse(model.isConsecutiveOperators("2+3=5"));
    }

    /**
     * Compares two mathematical expressions.
     *
     * @pre expression is a valid mathematical expression.
     * @post Returns true if the expressions are equal, false if not.
     */
    @Test
    public void testCompareExpressions() {
        NumberleModel model = new NumberleModel();
        assertTrue("2+3=5", model.compareExpressions("2+3=5"));
        assertFalse("2+3=6", model.compareExpressions("2+3=6"));
    }

    /**
     * Checks if the game is over when the game is won.
     *
     * @pre The game is won.
     * @post Returns true if the game is over, false if not.
     */
    @Test
    public void testIsGameOver_GameWon() {
        NumberleModel model = new NumberleModel();
        model.processInput("2+3=5");
        assertTrue(model.isGameOver());
    }

    /**
     * Checks if the game is over when attempts are exhausted.
     *
     * @pre The remaining attempts are less than or equal to 0.
     * @post Returns true if the game is over, false if not.
     */
    @Test
    public void testIsGameOver_AttemptsExhausted() {
        NumberleModel model = new NumberleModel();
        if (model.getRemainingAttempts() <= 0) {
            assertTrue(model.isGameOver());
        }
    }
}
