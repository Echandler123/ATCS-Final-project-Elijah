
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameViewer extends JFrame implements MouseListener, KeyListener {

    private PegSolitaireBitwise game;
    private final int fWidth = 800;
    private final int fHeight = 700;
    private Image pegImage;
    private Image selectedImage;
    private Image emptyImg;
    private final int[][] pegCoords = {
            {400, 100},        // 0
            {360, 160}, {440, 160},             // 1–2
            {320, 220}, {400, 220}, {480, 220}, // 3–5
            {280, 280}, {360, 280}, {440, 280}, {520, 280}, // 6–9
            {240, 340}, {320, 340}, {400, 340}, {480, 340}, {560, 340} // 10–14
    };

    private final java.util.List<Integer> selectedPegs = new ArrayList<>();

    public GameViewer() {
        game = new PegSolitaireBitwise();
        pegImage = new ImageIcon("Resources/peg.png").getImage();
        selectedImage = new ImageIcon("Resources/Selected.png").getImage();
        emptyImg = new ImageIcon("Resources/empty.png").getImage();


        setTitle("Triangular Peg Solitaire");
        setSize(fWidth, fHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addMouseListener(this);
        addKeyListener(this);
        setVisible(true);

        // Initial message
        JOptionPane.showMessageDialog(this, "Click a peg to remove to start the game.");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawBoard(g);

        // Instructions - top left
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Select pegs (from → over → to): " + selectedPegsToString(), 30, 50);

        // Best case - top right
        g.drawString(game.getBestCaseText(), fWidth - 300, 50);
    }

    private void drawBoard(Graphics g) {
        int board = game.getBoard();
        for (int i = 0; i < 15; i++) {
            int x = pegCoords[i][0];
            int y = pegCoords[i][1];
            boolean isPeg = ((board >> i) & 1) == 1;

            // Draw empty image for all valid positions
            g.drawImage(emptyImg, x - 20, y - 20, 40, 40, this);

            if (isPeg) {
                boolean isSelected = selectedPegs.contains(i);
                Image img = isSelected ? selectedImage : pegImage;
                g.drawImage(img, x - 20, y - 20, 40, 40, this);
            }
        }
    }


    private String selectedPegsToString() {
        if (selectedPegs.isEmpty()) return "none";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedPegs.size(); i++) {
            sb.append(selectedPegs.get(i) + 1);
            if (i < selectedPegs.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }
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
    @Override
    public void mouseClicked(MouseEvent e) {
        int clicked = getClickedPeg(e.getX(), e.getY());
        if (clicked == -1) return;

        int board = game.getBoard();
        boolean isPeg = ((board >> clicked) & 1) == 1;

        // First peg to remove
        if (Integer.bitCount(board) == 15) {
            if (isPeg) {
                game.removeFirstPeg(clicked);
                repaint();
            }
            return;
        }

        // Deselect if already selected
        if (selectedPegs.contains(clicked)) {
            selectedPegs.remove((Integer) clicked);
            repaint();
            return;
        }

        // Prevent selecting more than 3
        if (selectedPegs.size() >= 3) return;

        // Add selected peg/hole
        selectedPegs.add(clicked);

        // Once 3 selections made, validate them as a move
        if (selectedPegs.size() == 3) {
            int from = selectedPegs.get(0);
            int over = selectedPegs.get(1);
            int to = selectedPegs.get(2);

            // Check if this exact move exists in valid moves for current board
            boolean valid = game.getValidMoves(game.getBoard()).stream()
                    .anyMatch(m -> m.from == from && m.over == over && m.to == to);


            if (valid) {
                game.tryMove(from, over, to);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid move. Try again.");
            }
            selectedPegs.clear();
        }

        repaint();
        checkEndGame();
    }
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
                // Restart the game
                game = new PegSolitaireBitwise();
                selectedPegs.clear();
                JOptionPane.showMessageDialog(this, "Click a peg to remove to start the game.");
                repaint();
            } else {
                System.exit(0);
            }
        }
    }






    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {
            System.exit(0);
        }
    }

    // Unused
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

}
