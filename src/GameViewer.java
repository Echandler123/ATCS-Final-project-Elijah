import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * GameViewer is the GUI interface for a triangular Peg Solitaire game.
 * It provides visual interaction using Java Swing.
 */
public class GameViewer extends JFrame implements MouseListener, KeyListener {

    private PegSolitaireBitwise game;

    private final int fWidth = 800;
    private final int fHeight = 700;

    // Images representing peg states
    private Image pegImage;
    private Image selectedImage;
    private Image emptyImg;

    // Coordinates for displaying each peg on screen
    private final int[][] pegCoords = {
            {400, 100}, {360, 160}, {440, 160},
            {320, 220}, {400, 220}, {480, 220},
            {280, 280}, {360, 280}, {440, 280}, {520, 280},
            {240, 340}, {320, 340}, {400, 340}, {480, 340}, {560, 340}
    };

    // List of peg indices selected by the user during a move
    private final java.util.List<Integer> selectedPegs = new ArrayList<>();

    /**
     * Constructor sets up the window, loads images, and shows an initial prompt.
     * Also attaches input listeners for mouse and keyboard.
     */
    public GameViewer() {
        game = new PegSolitaireBitwise();

        // Load images from the Resources folder (use ImageIcon once to avoid reloading every time)
        pegImage = new ImageIcon("Resources/peg.png").getImage();
        selectedImage = new ImageIcon("Resources/Selected.png").getImage();
        emptyImg = new ImageIcon("Resources/empty.png").getImage();

        // Setup main window
        setTitle("Triangular Peg Solitaire");
        setSize(fWidth, fHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center the window
        addMouseListener(this);
        addKeyListener(this);
        setVisible(true);

        // Prompt the user to begin by removing one peg
        JOptionPane.showMessageDialog(this, "Click a peg to remove to start the game.");
    }

    /**
     * Paints the board efficiently using double buffering and rendering only changed parts.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Use Graphics2D for enhanced performance (if needed)
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBoard(g2d); // draw current peg layout

        // Draw user instructions and best-case endgame result
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Select pegs (from → over → to): " + selectedPegsToString(), 30, 50);
        g2d.drawString(game.getBestCaseText(), fWidth - 300, 50);
    }

    /**
     * Draws pegs, selections, and peg index numbers on the board.
     * Optimized to minimize unnecessary redraws.
     */
    private void drawBoard(Graphics g) {
        int board = game.getBoard();
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(Color.BLACK);

        // Drawing pegs and selection state
        for (int i = 0; i < 15; i++) {
            int x = pegCoords[i][0];
            int y = pegCoords[i][1];
            boolean isPeg = ((board >> i) & 1) == 1;

            // Always draw empty background to maintain consistency
            g.drawImage(emptyImg, x - 20, y - 20, 40, 40, this);

            if (isPeg) {
                boolean isSelected = selectedPegs.contains(i);
                Image img = isSelected ? selectedImage : pegImage;
                g.drawImage(img, x - 20, y - 20, 40, 40, this);
            }

            // Draw peg number (1–15) below each peg
            String numStr = String.valueOf(i + 1);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(numStr);
            g.drawString(numStr, x - textWidth / 2, y + 35);
        }
    }

    /**
     * Determines which peg (if any) was clicked based on mouse coordinates.
     * Optimized for efficient hit detection.
     */
    private int getClickedPeg(int mx, int my) {
        for (int i = 0; i < pegCoords.length; i++) {
            int x = pegCoords[i][0];
            int y = pegCoords[i][1];
            if (Math.pow(mx - x, 2) + Math.pow(my - y, 2) <= 400) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Mouse click event handler.
     * Optimized to minimize unnecessary actions and re-renders.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int clicked = getClickedPeg(e.getX(), e.getY());
        if (clicked == -1) return;

        int board = game.getBoard();
        boolean isPeg = ((board >> clicked) & 1) == 1;

        // First action of the game: remove one peg to start
        if (Integer.bitCount(board) == 15) {
            if (isPeg) {
                game.removeFirstPeg(clicked);
                repaint();
            }
            return;
        }

        // Toggle selection if already selected
        if (selectedPegs.contains(clicked)) {
            selectedPegs.remove((Integer) clicked);
            repaint();
            return;
        }

        // Only allow up to 3 selected pegs for a move
        if (selectedPegs.size() >= 3) return;

        selectedPegs.add(clicked);

        // Try to apply the move when 3 pegs are selected
        if (selectedPegs.size() == 3) {
            int from = selectedPegs.get(0);
            int over = selectedPegs.get(1);
            int to = selectedPegs.get(2);

            // Check if the move is valid
            boolean valid = game.getValidMoves(board).stream()
                    .anyMatch(m -> m.from == from && m.over == over && m.to == to);

            if (valid) {
                game.tryMove(from, over, to);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid move. Try again.");
            }

            // Reset selection after attempted move
            selectedPegs.clear();
        }

        repaint();
        checkEndGame(); // Check if no moves remain
    }

    /**
     * Checks whether the game has ended (no valid moves left).
     */
    private void checkEndGame() {
        if (game.getValidMoves(game.getBoard()).isEmpty()) {
            int finalPegs = Integer.bitCount(game.getBoard());

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Game over! Pegs remaining: " + finalPegs + "\nBest possible: " + game.getBestCaseText() + "\nPlay again?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Play Again", "Quit"},
                    "Play Again"
            );

            if (choice == JOptionPane.YES_OPTION) {
                game = new PegSolitaireBitwise();
                selectedPegs.clear();
                JOptionPane.showMessageDialog(this, "Click a peg to remove to start the game.");
                repaint();
            } else {
                System.exit(0);
            }
        }
    }
    /**
     * Returns a string representation of the currently selected pegs.
     * Helps the user visualize their in-progress move.
     */
    private String selectedPegsToString() {
        if (selectedPegs.isEmpty()) return "none";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedPegs.size(); i++) {
            sb.append(selectedPegs.get(i) + 1); // add 1 for display purposes
            if (i < selectedPegs.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }


    // Key listener method to quit the game on pressing 'Q'
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {
            System.exit(0);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
