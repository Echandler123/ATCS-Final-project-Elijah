import java.util.*;

public class PegSolitaireBitwise {
    int board;
    private Map<Integer, Integer> scoreMemo;     // Memoization for best-case outcomes from board states
    private Map<Integer, Integer> allSolutions;  // Placeholder for additional solution tracking
    private Move[][] ALL_MOVES;                  // Precomputed valid moves from each peg

    public PegSolitaireBitwise() {
        board = (1 << 15) - 1; // All 15 pegs set to "on" (bitwise 1)
        scoreMemo = new HashMap<>();
        allSolutions = new HashMap<>();
        ALL_MOVES = new Move[15][];
        initAllMoves(); // Initializes the valid moves from each peg
        loadSolutionsFromFile("src/solutions"); // Loads precomputed board evaluations
    }

    private void initAllMoves() {
        // Initialize temporary move list for each peg
        List<Move>[] temp = new List[15];
        for (int i = 0; i < 15; i++) {
            temp[i] = new ArrayList<>();
        }

        // Define all 36 valid moves as raw triples (from, over, to)
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

        // Populate move lists for each starting peg
        for (int[] m : rawMoves) {
            Move move = new Move(m[0], m[1], m[2]);
            temp[m[0]].add(move);
        }

        // Convert lists to arrays for performance
        for (int i = 0; i < 15; i++) {
            ALL_MOVES[i] = temp[i].toArray(new Move[0]);
        }
    }

    public void firstMove() {
        // Prompts player to remove an initial peg
        System.out.println("Enter the number (1–15) of the peg to remove:");
        Scanner scanner = new Scanner(System.in);
        int pos = scanner.nextInt() - 1;
        if (pos >= 0 && pos <= 14) {
            board &= ~(1 << pos); // Clear bit at selected position
        } else {
            System.out.println("Invalid number. Please enter a number between 1 and 15.");
        }
    }

    public void printBoard() {
        // Outputs the current board state in a pyramid format
        System.out.printf("      %2s\n", pegChar(0));
        System.out.printf("     %2s %2s\n", pegChar(1), pegChar(2));
        System.out.printf("   %2s %2s %2s\n", pegChar(3), pegChar(4), pegChar(5));
        System.out.printf("  %2s %2s %2s %2s\n", pegChar(6), pegChar(7), pegChar(8), pegChar(9));
        System.out.printf(" %2s %2s %2s %2s %2s\n", pegChar(10), pegChar(11), pegChar(12), pegChar(13), pegChar(14));
    }

    private String pegChar(int pos) {
        // Returns peg number if peg is present, "0" if not
        return ((board & (1 << pos)) != 0) ? String.valueOf(pos + 1) : "0";
    }

    public void printBestCaseFromCurrentBoard() {
        // Displays the best-case end state (minimum pegs left)
        int best = scoreMemo.getOrDefault(board, -1);
        if (best == -1) {
            System.out.println("Board state was not part of precomputed solutions.");
        } else {
            System.out.println("Best possible endgame from current board: " + best + " peg(s) remaining");
        }
    }

    public int applyMove(int b, Move m) {
        // Attempts to apply a move to a given board state
        // Returns updated board state, or -1 if move is invalid
        if (((b >> m.from) & 1) == 1 && ((b >> m.over) & 1) == 1 && ((b >> m.to) & 1) == 0) {
            b &= ~(1 << m.from); // Remove 'from'
            b &= ~(1 << m.over); // Remove 'over'
            b |= (1 << m.to);    // Add 'to'
            return b;
        }
        return -1;
    }

    List<Move> getValidMoves(int b) {
        // Returns list of all valid moves for the current board state
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
        // Writes scoreMemo map to file in "board,score" format
        try (java.io.PrintWriter out = new java.io.PrintWriter(filename)) {
            for (Map.Entry<Integer, Integer> entry : scoreMemo.entrySet()) {
                out.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println("Solutions written to " + filename);
        } catch (Exception e) {
            System.err.println("Failed to write file: " + e.getMessage());
        }
    }

    public void loadSolutionsFromFile(String filename) {
        // Loads scoreMemo from file into memory
        scoreMemo.clear();
        try (java.util.Scanner in = new java.util.Scanner(new java.io.File(filename))) {
            while (in.hasNextLine()) {
                String[] parts = in.nextLine().split(",");
                int board = Integer.parseInt(parts[0]);
                int score = Integer.parseInt(parts[1]);
                scoreMemo.put(board, score);
            }
            System.out.println("Solutions loaded from " + filename);
        } catch (Exception e) {
            System.err.println("Failed to load file: " + e.getMessage());
        }
    }

    public void move() {
        // Accepts a move from user and applies it if valid
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter move as three peg numbers (from over to), each between 1 and 15:");
        try {
            int from = scanner.nextInt() - 1;
            int over = scanner.nextInt() - 1;
            int to = scanner.nextInt() - 1;

            Move attempted = new Move(from, over, to);
            int newBoard = applyMove(board, attempted);

            if (newBoard != -1) {
                board = newBoard;
                System.out.println("Move applied.");
            } else {
                System.out.println("Invalid move. Make sure the 'from' and 'over' pegs exist, and 'to' is empty.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter three integers.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    public void playGame() {
        // Main game loop: user plays until no moves are available
        Scanner scanner = new Scanner(System.in);
        printBoard();
        firstMove(); // Remove initial peg
        printBoard();
        printBestCaseFromCurrentBoard(); // Show best possible outcome

        while (true) {
            List<Move> validMoves = getValidMoves(board);
            if (validMoves.isEmpty()) {
                System.out.println("No more valid moves.");
                System.out.println("Final peg count: " + Integer.bitCount(board));
                break;
            }

            System.out.println("Enter your move as three peg numbers (from over to), each between 1 and 15, or type 'q' to quit:");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("q")) {
                System.out.println("Game exited by user.");
                break;
            }

            String[] parts = line.split("\\s+");
            if (parts.length != 3) {
                System.out.println("Please enter exactly three numbers.");
                continue;
            }

            try {
                int from = Integer.parseInt(parts[0]) - 1;
                int over = Integer.parseInt(parts[1]) - 1;
                int to = Integer.parseInt(parts[2]) - 1;

                Move move = new Move(from, over, to);
                int newBoard = applyMove(board, move);
                if (newBoard != -1) {
                    board = newBoard;
                    System.out.println("Move applied.");
                    printBoard();
                    printBestCaseFromCurrentBoard();
                } else {
                    System.out.println("Invalid move. Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric values.");
            }
        }

        printBestCaseFromCurrentBoard();
    }

    public int getBoard() {
        // Returns current board bitmask
        return board;
    }

    public void removeFirstPeg(int pos) {
        // Removes peg at a specific position (0–14)
        if (pos >= 0 && pos <= 14) {
            board &= ~(1 << pos);
        }
    }

    public boolean tryMove(int from, int over, int to) {
        // Tries a move and returns success/failure
        Move move = new Move(from, over, to);
        int newBoard = applyMove(board, move);
        if (newBoard != -1) {
            board = newBoard;
            return true;
        }
        return false;
    }

    public String getBestCaseText() {
        // Returns summary of best-case for current board
        int best = scoreMemo.getOrDefault(board, -1);
        if (best == -1) {
            return "No best case data.";
        }
        return "Best possible: " + best + " peg(s) left";
    }
}
