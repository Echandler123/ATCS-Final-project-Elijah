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
        selectedImage = new ImageIcon("Resources/Selected.jpg").getImage();

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

    @Override
    public void mouseClicked(MouseEvent e) {
        int clicked = getClickedPeg(e.getX(), e.getY());
        if (clicked == -1) return;

        // First peg to remove
        if (Integer.bitCount(game.getBoard()) == 15) {
            game.removeFirstPeg(clicked);
            repaint();
            return;
        }

        // If 3 pegs already selected, ignore further input until reset
        if (selectedPegs.size() >= 3) return;

        // Deselect if already selected
        if (selectedPegs.contains(clicked)) {
            selectedPegs.remove((Integer) clicked);
            repaint();
            return;
        }

        // Determine selection role (from, over, to)
        int bit = (game.getBoard() >> clicked) & 1;

        if (selectedPegs.size() < 2) {
            // First and second pegs (from and over) must be present
            if (bit == 0) return;
            selectedPegs.add(clicked);
        } else if (selectedPegs.size() == 2) {
            // Third peg (to) must be empty
            if (bit == 1) return;
            selectedPegs.add(clicked);

            // Now try the move
            boolean success = game.tryMove(selectedPegs.get(0), selectedPegs.get(1), selectedPegs.get(2));
            selectedPegs.clear();
            if (!success) {
                JOptionPane.showMessageDialog(this, "Invalid move. Try again.");
            }
        }

        repaint();
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
