package com.example.card24;

// ArrayList stores the deck before shuffling and dealing.
import java.util.ArrayList;
// Collections.shuffle randomly rearranges the deck.
import java.util.Collections;
// HashMap tracks how many times each card value appears in the current hand.
import java.util.HashMap;
// List is used for the dealt hand and the list of numbers parsed from the expression.
import java.util.List;
// Map lets the controller compare expected values versus values actually used.
import java.util.Map;

// @FXML connects fields and methods to the FXML file so JavaFX can inject the UI elements.
import javafx.fxml.FXML;
// Alert displays dialogs for success, warning, and error states.
import javafx.scene.control.Alert;
// TextField holds the player's expression input.
import javafx.scene.control.TextField;
// Image loads the card PNG files from the classpath resources folder.
import javafx.scene.image.Image;
// ImageView displays the image inside the card view.
import javafx.scene.image.ImageView;
// HBox is the container that arranges the four cards horizontally.
import javafx.scene.layout.HBox;
// StackPane wraps each card image so the visual card can be styled as a single unit.
import javafx.scene.layout.StackPane;

/**
 * Controls the UI, random card dealing, input validation, and result alerts.
 */
public class Card24Controller {
    private static final double TOLERANCE = 0.000001;
    @FXML private HBox cardsBox;
    @FXML private TextField expressionField;
    private List<Card> cards = List.of();

    @FXML
    private void initialize() {
        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        // Shuffle a full deck and deal without replacement. Ranks may repeat.
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (int value = 1; value <= 13; value++) {
                deck.add(new Card(value, suit));
            }
        }
        Collections.shuffle(deck);
        cards = List.copyOf(deck.subList(0, 4));
        cardsBox.getChildren().clear();
        for (Card card : cards) cardsBox.getChildren().add(createCardView(card));
        expressionField.clear();
        expressionField.requestFocus();
    }

    private StackPane createCardView(Card card) {
        // Classpath resources travel with the project; no Downloads path is needed.
        String resourcePath = "cards/" + card.imageFileName();
        var resource = Card24Application.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing card image: " + resourcePath);
        }
        Image image = new Image(resource.toExternalForm());
        if (image.isError()) {
            throw new IllegalStateException("Unable to load card image: " + resourcePath,
                    image.getException());
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(143);
        imageView.setFitHeight(203);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        StackPane view = new StackPane(imageView);
        view.getStyleClass().add("playing-card");
        view.setAccessibleText(card.rank() + " of " + card.suit().name() + ", value " + card.value());
        return view;
    }

    @FXML
    private void handleVerify() {
        try {
            // The parser rejects empty/malformed input and records every number token.
            ExpressionEvaluator.Result result = ExpressionEvaluator.evaluate(expressionField.getText());
            List<Integer> expected = cards.stream().map(Card::value).toList();
            validateCardValues(expected, result.numbers());
            if (Math.abs(result.value() - 24.0) < TOLERANCE) {
                showAlert(Alert.AlertType.INFORMATION, "Correct solution!",
                        "You used all four cards exactly once and made 24.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Not quite 24",
                        "Your expression evaluates to " + result.value() + ". Try again.");
            }
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Check your expression", exception.getMessage());
        }
    }

    /** Compare counts, so two displayed 3s require exactly two input 3s. */
    static void validateCardValues(List<Integer> expected, List<Integer> used) {
        Map<Integer, Integer> remaining = new HashMap<>();
        for (int value : expected) remaining.merge(value, 1, Integer::sum);
        for (int value : used) {
            if (!remaining.containsKey(value)) {
                throw new IllegalArgumentException("Value " + value + " is not on the displayed cards.");
            }
            if (remaining.get(value) == 0) {
                throw new IllegalArgumentException("Card value " + value + " was used too many times.");
            }
            remaining.put(value, remaining.get(value) - 1);
        }
        List<String> missing = new ArrayList<>();
        for (int value = 1; value <= 13; value++) {
            int count = remaining.getOrDefault(value, 0);
            if (count > 0) missing.add(value + " (" + count + " missing)");
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Use all four cards exactly once. Missing: "
                    + String.join(", ", missing) + ".");
        }
    }

    private void showAlert(Alert.AlertType type, String heading, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(expressionField.getScene().getWindow());
        alert.setTitle("Card 24 Game");
        alert.setHeaderText(heading);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
