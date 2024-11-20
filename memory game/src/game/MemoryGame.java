package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryGame {
    private JFrame frame;
    private List<MemoryCard> cards;
    private JButton[][] cardButtons;
    private int gridSize = 3;
    private int gridColumns = 4;
    private int totalMatches;
    private int attempts;
    private int triesLeft;
    private JLabel attemptLabel;
    private MemoryCard firstCard = null;
    private JButton firstButton = null;
    private boolean processingMatch = false;

    public MemoryGame() {
        initializeCards();
        initializeGame();
    }

    private void initializeGame() {
        frame = new JFrame("Memory Game");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Test Your Memory");
        titlePanel.add(titleLabel);
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> resetGame());
        titlePanel.add(newGameButton);
        frame.add(titlePanel, BorderLayout.NORTH);

        JPanel attemptPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attemptPanel.add(new JLabel("Attempts:"));
        attemptLabel = new JLabel("0/10");
        attemptPanel.add(attemptLabel);
        frame.add(attemptPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(gridSize, gridColumns));
        cardButtons = new JButton[gridSize][gridColumns];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridColumns; j++) {
                JButton button = new JButton();
                button.setIcon(new ImageIcon("default.png"));
                button.addMouseListener(new CardClickListener(i, j));
                cardButtons[i][j] = button;
                buttonPanel.add(button);
            }
        }
        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
        runGame();
    }

    private void runGame() {
        Collections.shuffle(cards);
        displayCards();
    }

    private void initializeCards() {
        cards = new ArrayList<>();
        int totalCards = gridSize * gridColumns;

        // Create card pairs
        for (int i = 1; i <= totalCards / 2; i++) {
            String filenamePng = i + ".png";
            String filenameJpg = i + ".jpg";
            ImageIcon image = null;

            // Check if .png exists
            if (new java.io.File(filenamePng).exists()) {
                image = new ImageIcon(filenamePng);
                System.out.println("Loaded image: " + filenamePng);
            } 
            // Otherwise, check for .jpg
            else if (new java.io.File(filenameJpg).exists()) {
                image = new ImageIcon(filenameJpg);
                System.out.println("Loaded image: " + filenameJpg);
            } else {
                System.err.println("Error: No image file found for card " + i + " (expected .png or .jpg)");
                continue; // Skip this card if neither file is found
            }

            image.setDescription(image.getDescription() == null ? (image.toString()) : image.getDescription());

            // Add the card twice for matching
            cards.add(new MemoryCard(image));
            cards.add(new MemoryCard(image));
        }

        // Shuffle the list to randomize card placement
        Collections.shuffle(cards);

        totalMatches = totalCards / 2;
        attempts = 0;
        triesLeft = 10;
    }



    private void displayCards() {
        System.out.println("Displaying cards...");
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridColumns; j++) {
                cardButtons[i][j].setIcon(new ImageIcon("default.png"));
            }
        }
    }

    private void resetGame() {
        System.out.println("Resetting game...");
        initializeCards();
        displayCards();
        attempts = 0;
        triesLeft = 10;
        firstCard = null;
        firstButton = null;
        updateAttemptLabel();
    }

    private void updateAttemptLabel() {
        System.out.println("Updating attempt label: " + attempts + "/10");
        attemptLabel.setText(attempts + "/10");
    }

    private class CardClickListener extends MouseAdapter {
        private int row;
        private int col;

        public CardClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("Card clicked at Row: " + row + ", Col: " + col);
            System.out.println("Processing Match: " + processingMatch);

            if (processingMatch) return;

            MemoryCard card = cards.get(row * gridColumns + col);
            JButton button = cardButtons[row][col];

            System.out.println("Card Matched Status: " + card.isMatched());
            System.out.println("Card Image: " + card.getImage().getDescription());

            if (card.isMatched() || !button.getIcon().toString().equals("default.png")) return;

            System.out.println("Setting button icon to: " + card.getImage().getDescription());
            button.setIcon(card.getImage());
            System.out.println("Button icon set successfully.");

            if (firstCard == null) {
                firstCard = card;
                firstButton = button;
            } else {
                processingMatch = true;
                attempts++;
                updateAttemptLabel();

                if (firstCard.equals(card)) {
                    System.out.println("Cards matched!");
                    card.setMatched(true);
                    firstCard.setMatched(true);
                    firstCard = null;
                    firstButton = null;
                    totalMatches--;

                    if (totalMatches == 0) {
                        JOptionPane.showMessageDialog(frame, "You Win!");
                        resetGame();
                    }
                    processingMatch = false;
                } else {
                    System.out.println("Cards did not match. Hiding cards...");
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(() -> {
                                button.setIcon(new ImageIcon("default.png"));
                                firstButton.setIcon(new ImageIcon("default.png"));
                                firstCard = null;
                                firstButton = null;
                                processingMatch = false;
                            });
                        }
                    }, 1000);

                    triesLeft--;
                    if (triesLeft == 0) {
                        JOptionPane.showMessageDialog(frame, "Game Over!");
                        resetGame();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGame::new);
    }
}
