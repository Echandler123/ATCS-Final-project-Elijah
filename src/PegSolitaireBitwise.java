import java.util.*;

public class PegSolitaireBitwise {
    static final int TOTAL_HOLES = 15;
    private int board = (1 << TOTAL_HOLES) - 1; // full board initially
    static final List<Move> ALL_MOVES = new ArrayList<>();
    Map<Integer, Integer> scoreMemo = new HashMap<>();

    public PegSolitaireBitwise() {
        int[][] rawMoves = {
                {0, 1, 3}, {0, 2, 5}, {1, 3, 6}, {1, 4, 8}, {2, 4, 7}, {2, 5, 9},
                {3, 1, 0}, {3, 4, 5}, {3, 6, 10}, {3, 7, 12}, {4, 7, 11}, {4, 8, 13},
                {5, 2, 0}, {5, 4, 3}, {5, 8, 12}, {5, 9, 14},
                {6, 3, 1}, {6, 7, 8}, {7, 4, 2}, {7, 8, 9},
                {8, 4, 1}, {8, 7, 6}, {9, 5, 2}, {9, 8, 7},
                {10, 6, 3}, {10, 11, 12}, {11, 7, 4}, {11, 12, 13},
                {12, 7, 3}, {12, 8, 5}, {12, 11, 10}, {12, 13, 14},
                {13, 8, 4}, {13, 12, 11}, {14, 9, 5}, {14, 13, 12}
        };
        for (int[] m : rawMoves) {
            ALL_MOVES.add(new Move(m[0], m[1], m[2]));
        }
    }

    public void firstMove() {
        System.out.println("Enter the position (0–14) of the peg to remove:");
        Scanner scanner = new Scanner(System.in);
        int pos = scanner.nextInt();
        if (pos >= 0 && pos < TOTAL_HOLES) {
            board &= ~(1 << pos);
            System.out.println("Removed peg at position " + pos);
        } else {
            System.out.println("Invalid position. Please enter a number between 0 and 14.");
        }
    }
    int evaluate(int board) {
        if (scoreMemo.containsKey(board)) return scoreMemo.get(board);

        int best = Integer.MAX_VALUE;
        boolean moved = false;

        for (Move move : ALL_MOVES) {
            int newBoard = applyMove(board, move);
            if (newBoard != -1) {
                moved = true;
                int result = evaluate(newBoard);
                best = Math.min(best, result);
            }
        }

        int finalScore = moved ? best : Integer.bitCount(board);
        scoreMemo.put(board, finalScore);
        return finalScore;
    }
    public void printBoard() {
        String[] bits = new String[TOTAL_HOLES];
        for (int i = 0; i < TOTAL_HOLES; i++) {
            bits[i] = (board & (1 << i)) != 0 ? "1" : "0";
        }
        System.out.println("   " + bits[0]);
        System.out.println("  " + bits[1] + " " + bits[2]);
        System.out.println(" " + bits[3] + " " + bits[4] + " " + bits[5]);
        System.out.println(bits[6] + " " + bits[7] + " " + bits[8] + " " + bits[9]);
        System.out.println(bits[10] + " " + bits[11] + " " + bits[12] + " " + bits[13] + " " + bits[14]);
    }

    private int applyMove(int b, Move move) {
        int from = move.getFrom();
        int over = move.getOver();
        int to = move.getTo();

        int fromBit = 1 << from;
        int overBit = 1 << over;
        int toBit = 1 << to;

        if ((b & fromBit) != 0 && (b & overBit) != 0 && (b & toBit) == 0) {
            return (b & ~fromBit & ~overBit) | toBit;
        }
        return -1; // invalid move
    }
    public void printBestCaseFromCurrentBoard() {
        int best = evaluate(board);
        System.out.println("Best possible endgame from current board: " + best + " peg(s) remaining");
    }


}
