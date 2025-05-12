import java.util.*;

public class PegSolitaireBitwise {
    int board;
    private Map<Integer, Integer> scoreMemo;
    private Map<Integer, Integer> allSolutions;
    private Move[][] ALL_MOVES;
    public PegSolitaireBitwise() {
        board = (1 << 15) - 1; // All pegs on
        scoreMemo = new HashMap<>();
        allSolutions = new HashMap<>();
        ALL_MOVES = new Move[15][];
        initAllMoves(); // fills ALL_MOVES[][]
        loadSolutionsFromFile("src/solutions");
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
        System.out.println("Enter the number (1–15) of the peg to remove:");
        Scanner scanner = new Scanner(System.in);
        int pos = scanner.nextInt() - 1;
        if (pos >= 0 && pos <= 14) {
            board &= ~(1 << pos);
        } else {
            System.out.println("Invalid number. Please enter a number between 1 and 15.");
        }

    }
    public void printBoard() {
        System.out.printf("      %2s\n", pegChar(0));
        System.out.printf("     %2s %2s\n", pegChar(1), pegChar(2));
        System.out.printf("   %2s %2s %2s\n", pegChar(3), pegChar(4), pegChar(5));
        System.out.printf("  %2s %2s %2s %2s\n", pegChar(6), pegChar(7), pegChar(8), pegChar(9));
        System.out.printf(" %2s %2s %2s %2s %2s\n", pegChar(10), pegChar(11), pegChar(12), pegChar(13), pegChar(14));
    }
    private String pegChar(int pos) {
        return ((board & (1 << pos)) != 0) ? String.valueOf(pos + 1) : "0";
    }


    public void printBestCaseFromCurrentBoard() {
        int best = scoreMemo.getOrDefault(board, -1);
        if (best == -1) {
            System.out.println("Board state was not part of precomputed solutions.");
        } else {
            System.out.println("Best possible endgame from current board: " + best + " peg(s) remaining");
        }
    }



    public int applyMove(int b, Move m) {
        if (((b >> m.from) & 1) == 1 && ((b >> m.over) & 1) == 1 && ((b >> m.to) & 1) == 0) {
            b &= ~(1 << m.from);
            b &= ~(1 << m.over);
            b |= (1 << m.to);
            return b;
        }
        return -1;
    }

    List<Move> getValidMoves(int b) {
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
    public void move() {
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
            scanner.nextLine(); // clear invalid input
        }
    }
    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        printBoard();
        firstMove(); // Ask for the initial peg to remove
        printBoard();
        printBestCaseFromCurrentBoard();

        while (true) {
            List<Move> validMoves = getValidMoves(board);
            if (validMoves.isEmpty()) {
                System.out.println("No more valid moves.");
                System.out.println("Final peg count: " + Integer.bitCount(board));
                break;
            }

            System.out.println("Enter your move as three peg numbers (from over to), each between 1 and 15, or type 'q' to quit:");;
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
                    printBestCaseFromCurrentBoard(); // Show best case after each move
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
        return board;
    }
    public void removeFirstPeg(int pos) {
        if (pos >= 0 && pos <= 14) {
            board &= ~(1 << pos);
        }
    }
    public boolean tryMove(int from, int over, int to) {
        Move move = new Move(from, over, to);
        int newBoard = applyMove(board, move);
        if (newBoard != -1) {
            board = newBoard;
            return true;
        }
        return false;
    }
    public String getBestCaseText() {
        int best = scoreMemo.getOrDefault(board, -1);
        if (best == -1) {
            return "No best case data.";
        }
        return "Best possible: " + best + " peg(s) left";
    }


}
