import java.util.*;

public class PegSolitaireBitwise {

    private int board;
    private Map<Integer, Integer> scoreMemo;
    private Map<Integer, Integer> allSolutions;
    private Move[][] ALL_MOVES;

    public PegSolitaireBitwise() {
        board = (1 << 15) - 1; // All pegs on
        scoreMemo = new HashMap<>();
        allSolutions = new HashMap<>();
        ALL_MOVES = new Move[15][];
        initAllMoves(); // fills ALL_MOVES[][]
        loadSolutionsFromFile("solutions");
    }

    private void initAllMoves() {
        List<Move>[] temp = new List[15];
        for (int i = 0; i < 15; i++) {
            temp[i] = new ArrayList<>();
        }

        // Define all 36 valid moves (both directions)
        int[][] rawMoves = {
                {0, 1, 3}, {3, 1, 0}, {0, 2, 5}, {5, 2, 0},
                {1, 3, 6}, {6, 3, 1}, {1, 4, 8}, {8, 4, 1},
                {2, 4, 7}, {7, 4, 2}, {2, 5, 9}, {9, 5, 2},
                {3, 4, 5}, {5, 4, 3}, {3, 6, 10}, {10, 6, 3},
                {4, 7, 11}, {11, 7, 4}, {5, 8, 12}, {12, 8, 5},
                {6, 7, 8}, {8, 7, 6}, {7, 8, 9}, {9, 8, 7},
                {10, 11, 12}, {12, 11, 10}, {11, 12, 13}, {13, 12, 11},
                {12, 13, 14}, {14, 13, 12},
                {3, 7, 12}, {12, 7, 3}, {4, 8, 13}, {13, 8, 4},
                {5, 9, 14}, {14, 9, 5}
        };

        for (int[] m : rawMoves) {
            Move move = new Move(m[0], m[1], m[2]);
            temp[m[0]].add(move);
        }

        for (int i = 0; i < 15; i++) {
            ALL_MOVES[i] = temp[i].toArray(new Move[0]);
        }
    }

    public void firstMove() {
        System.out.println("Enter the position (0–14) of the peg to remove:");
        Scanner scanner = new Scanner(System.in);
        int pos = scanner.nextInt();
        if (pos >= 0 && pos <= 14) {
            board &= ~(1 << pos);
        } else {
            System.out.println("Invalid position. Please enter a position between 0 and 14.");
        }
    }

    public void printBoard() {
        System.out.println("   " + pegChar(0));
        System.out.println("  " + pegChar(1) + " " + pegChar(2));
        System.out.println(" " + pegChar(3) + " " + pegChar(4) + " " + pegChar(5));
        System.out.println(pegChar(6) + " " + pegChar(7) + " " + pegChar(8) + " " + pegChar(9));
        System.out.println(pegChar(10) + " " + pegChar(11) + " " + pegChar(12) + " " + pegChar(13) + " " + pegChar(14));
    }

    private String pegChar(int pos) {
        return ((board & (1 << pos)) != 0) ? "1" : "0";
    }

    public void printBestCaseFromCurrentBoard() {
        int best = scoreMemo.getOrDefault(board, -1);
        if (best == -1) {
            System.out.println("Board state was not part of precomputed solutions.");
        } else {
            System.out.println("Best possible endgame from current board: " + best + " peg(s) remaining");
        }
    }

    private void computeAllSolutionsFromEveryStart() {
        scoreMemo.clear();
        for (int missing = 0; missing < 15; missing++) {
            int startBoard = (1 << 15) - 1;
            startBoard &= ~(1 << missing);
            evaluateAndCacheAll(startBoard);
        }
    }

    private int evaluateAndCacheAll(int board) {
        if (scoreMemo.containsKey(board)) {
            return scoreMemo.get(board);
        }

        List<Move> validMoves = getValidMoves(board);
        if (validMoves.isEmpty()) {
            int pegCount = Integer.bitCount(board);
            scoreMemo.put(board, pegCount);
            return pegCount;
        }

        int best = Integer.MAX_VALUE;
        for (Move m : validMoves) {
            int next = applyMove(board, m);
            if (next != -1) {
                int result = evaluateAndCacheAll(next);
                best = Math.min(best, result);
            }
        }

        scoreMemo.put(board, best);
        return best;
    }

    private int applyMove(int b, Move m) {
        if (((b >> m.from) & 1) == 1 && ((b >> m.over) & 1) == 1 && ((b >> m.to) & 1) == 0) {
            b &= ~(1 << m.from);
            b &= ~(1 << m.over);
            b |= (1 << m.to);
            return b;
        }
        return -1;
    }

    private List<Move> getValidMoves(int b) {
        List<Move> moves = new ArrayList<>();
        for (int from = 0; from < 15; from++) {
            for (Move m : ALL_MOVES[from]) {
                if (((b >> m.from) & 1) == 1 && ((b >> m.over) & 1) == 1 && ((b >> m.to) & 1) == 0) {
                    moves.add(m);
                }
            }
        }
        return moves;
    }
    public void writeSolutionsToFile(String filename) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(filename)) {
            for (Map.Entry<Integer, Integer> entry : scoreMemo.entrySet()) {
                int board = entry.getKey();
                int score = entry.getValue();
                out.println(board + "," + score);
            }
            System.out.println("Solutions written to " + filename);
        } catch (Exception e) {
            System.err.println("Failed to write file: " + e.getMessage());
        }
    }
    public void loadSolutionsFromFile(String filename) {
        scoreMemo.clear();
        try (java.util.Scanner in = new java.util.Scanner(new java.io.File(filename))) {
            while (in.hasNextLine()) {
                String line = in.nextLine();
                String[] parts = line.split(",");
                int board = Integer.parseInt(parts[0]);
                int score = Integer.parseInt(parts[1]);
                scoreMemo.put(board, score);
            }
            System.out.println("Solutions loaded from " + filename);
        } catch (Exception e) {
            System.err.println("Failed to load file: " + e.getMessage());
        }
    }
}
