import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Font;

public class DrawPanel extends JPanel implements MouseListener {
    private ArrayList<Card> deck;
    private ArrayList<Card> hand;
    private ArrayList<Card> selected;
    private Rectangle button;
    private String message;

    public DrawPanel() {
        button = new Rectangle(147, 250, 160, 26);
        this.addMouseListener(this);

        deck = Card.buildDeck();
        hand = new ArrayList<>();
        selected = new ArrayList<>();
        message = "";

        for (int i = 0; i < 9; i++) {
            hand.add(drawCard());
        }
    }

    private Card drawCard() {
        if (deck.size() == 0) {
            return null;
        }
        int r = (int) (Math.random() * deck.size());
        return deck.remove(r);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = 120;
        int y = 10;

        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            if (c.getHighlight()) {
                g.drawRect(x, y, c.getImage().getWidth(), c.getImage().getHeight());
            }

            c.setRectangleLocation(x, y);
            g.drawImage(c.getImage(), x, y, null);

            x = x + c.getImage().getWidth() + 10;

            if (i == 2 || i == 5) {
                x = 120;
                y = y + c.getImage().getWidth() + 10;
            }
        }
        g.setFont(new Font("Courier New", Font.BOLD, 20));
        g.drawString("GET NEW CARDS", 150, 270);
        g.drawRect((int)button.getX(), (int)button.getY(), (int)button.getWidth(), (int)button.getHeight());

        g.drawString("Cards left in deck: " + deck.size(), 100, 325);
        g.drawString(message, 100, 355);
    }

    private boolean isValidElevenPair(ArrayList<Card> cards) {
        if (cards.size() != 2) {
            return false;
        }
        return cards.get(0).getNumericValue() + cards.get(1).getNumericValue() == 11;
    }

    private boolean isValidFaceTrio(ArrayList<Card> cards) {
        if (cards.size() != 3) {
            return false;
        }

        boolean hasJ = false;
        boolean hasQ = false;
        boolean hasK = false;
        for (Card c : cards) {
            if (c.getValue().equals("J")) {
                hasJ = true;
            }
            if (c.getValue().equals("Q")) {
                hasQ = true;
            }
            if (c.getValue().equals("K")) {
                hasK = true;
            }
        }
        return hasJ && hasQ && hasK;
    }

    private void removeSelectedCards() {
        for (int i = 0; i < hand.size(); i++) {
            if (selected.contains(hand.get(i))) {
                Card newCard = drawCard();
                if (newCard != null) {
                    hand.set(i, newCard);
                } else {
                    hand.set(i, null);
                }
            }
        }
        selected.clear();
    }

    private void checkWinOrLose() {
        ArrayList<Card> check = new ArrayList<>();
        for (Card c : hand) {
            if (c != null) check.add(c);
        }

        boolean hasMove = false;

        for (int i = 0; i < check.size(); i++) {
            for (int j = i + 1; j < check.size(); j++) {
                if (check.get(i).getNumericValue() + check.get(j).getNumericValue() == 11) {
                    hasMove = true;
                }
            }
        }

        boolean j = false;
        boolean q = false;
        boolean k = false;

        for (Card c : check) {
            if (c.getValue().equals("J")) {
                j = true;
            }
            if (c.getValue().equals("Q")) {
                q = true;
            }
            if (c.getValue().equals("K")) {
                k = true;
            }
        }

        if (j && q && k) {
            hasMove = true;
        }

        if (deck.size() == 0 && allNull(hand)) {
            message = "You win!";
        } else if (!hasMove) {
            message = "No valid moves! You lose.";
        } else {
            message = "";
        }
    }

    private boolean allNull(ArrayList<Card> cards) {
        for (Card c : cards) {
            if (c != null) {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        deck = Card.buildDeck();
        hand.clear();
        for (int i = 0; i < 9; i++) {
            hand.add(drawCard());
        }
        selected.clear();
        message = "";
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        Point clicked = e.getPoint();

        if (e.getButton() == 1 || e.getButton() == 3) {
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                if (c != null && c.getCardBox().contains(clicked)) {
                    if (selected.contains(c)) {
                        selected.remove(c);
                        c.flipHighlight();
                    } else {
                        selected.add(c);
                        c.flipHighlight();
                    }

                    if (selected.size() == 2 && isValidElevenPair(selected)) {
                        removeSelectedCards();
                    } else if (selected.size() == 3 && isValidFaceTrio(selected)) {
                        removeSelectedCards();
                    }
                    break;
                }
            }

            if (button.contains(clicked)) {
                resetGame();
            }

            checkWinOrLose();
            repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }
}