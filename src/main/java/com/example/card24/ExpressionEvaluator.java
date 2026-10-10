package com.example.card24;

// ArrayList stores the integer tokens parsed from the expression, such as 10 and 12.
import java.util.ArrayList;
// List is used to return the numbers the player used so the controller can validate them.
import java.util.List;

/**
 * Parses arithmetic expressions using a recursive-descent approach.
 *
 * This class is responsible for understanding legal syntax, evaluating the final value,
 * and returning the exact number tokens used so the UI can compare them to the dealt cards.
 */
public final class ExpressionEvaluator {
    private final String input;
    private int position;
    private final List<Integer> numbers = new ArrayList<>();


    public record Result(double value, List<Integer> numbers) { }

    private ExpressionEvaluator(String input) {
        this.input = input;
    }

    public static Result evaluate(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Enter an expression first.");
        }
        // Bounds recursive nesting and avoids a stack overflow from huge input.
        if (input.length() > 300) {
            throw new IllegalArgumentException("Please keep the expression within 300 characters.");
        }
        ExpressionEvaluator parser = new ExpressionEvaluator(input);
        double value = parser.expression();
        parser.skipWhitespace();
        if (parser.position != input.length()) {
            throw parser.syntax("Unexpected character. Use integers, +, -, *, / and parentheses.");
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("The calculation is too large.");
        }
        return new Result(value, List.copyOf(parser.numbers));
    }


    private double expression() {
        double value = term();
        while (true) {
            if (take('+')) value += term();
            else if (take('-')) value -= term();
            else return value;
        }
    }


    private double term() {
        double value = factor();
        while (true) {
            if (take('*')) {
                value *= factor();
            } else if (take('/')) {
                double divisor = factor();

                if (divisor == 0.0) {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }
                value /= divisor;
            } else {
                return value;
            }
        }
    }

    private double factor() {
        if (take('(')) {
            double value = expression();
            if (!take(')')) throw syntax("Missing closing parenthesis.");
            return value;
        }
        skipWhitespace();
        int start = position;
        while (position < input.length()
                && input.charAt(position) >= '0' && input.charAt(position) <= '9') {
            position++;
        }
        if (start == position) throw syntax("Expected a whole number or '('.");
        try {
            int number = Integer.parseInt(input.substring(start, position));
            numbers.add(number); // Multi-digit values count as one card, not separate digits.
            return number;
        } catch (NumberFormatException exception) {
            throw syntax("That whole number is too large.");
        }
    }

    private boolean take(char expected) {
        skipWhitespace();
        if (position < input.length() && input.charAt(position) == expected) {
            position++;
            return true;
        }
        return false;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    private IllegalArgumentException syntax(String message) {
        return new IllegalArgumentException("Invalid syntax at position " + (position + 1) + ": " + message);
    }
}
