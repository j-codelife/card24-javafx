package com.example.card24;

// Locale helps format names consistently when converting suit names to lowercase.
// The project uses lowercase suit names when building image resource paths such as
// "queen_of_spades.png".
import java.util.Locale;

/**
 * Represents one playing card in the game.
 *
 * Each card has a numeric value from 1 to 13 and a suit. The suit determines the
 * color and the image asset name, while the value is the number used by the expression parser.
 */
public record Card(int value, Suit suit) {
    public enum Suit {
        HEARTS("♥", true), DIAMONDS("♦", true),
        CLUBS("♣", false), SPADES("♠", false);

        private final String symbol;
        private final boolean red;

        Suit(String symbol, boolean red) {
            this.symbol = symbol;
            this.red = red;
        }

        public String symbol() { return symbol; }
        public boolean isRed() { return red; }
    }

    public Card {
        if (value < 1 || value > 13 || suit == null) {
            throw new IllegalArgumentException("A card needs a value of 1–13 and a suit.");
        }
    }

    /** Maps the numeric value and suit to the supplied resource filename. */
    public String imageFileName() {
        String fileRank = switch (value) {
            case 1 -> "ace";
            case 11 -> "jack";
            case 12 -> "queen";
            case 13 -> "king";
            default -> Integer.toString(value);
        };
        return fileRank + "_of_" + suit.name().toLowerCase(Locale.ROOT) + ".png";
    }

    // Only the display changes for face cards; value remains 1, 11, 12 or 13.
    public String rank() {
        return switch (value) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> Integer.toString(value);
        };
    }
}
