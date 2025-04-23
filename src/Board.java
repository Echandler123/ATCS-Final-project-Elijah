public class Board {
    int board = 1023;
    int boardSize;
    int valid_peg = 1023;
    int checker;

    public Board() {
        printBoard();
        firstMove();
    }
    public Board(int boardSize) {
        this.boardSize = boardSize;
    }
    // Gets user input for first move
    public void firstMove() {
        System.out.println("Enter the position (0-9) of the peg to remove:");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int pos = scanner.nextInt();
        if (pos >= 0 && pos <= 9) {
            board &= ~(1 << pos);
        }
        else {
            System.out.println("Invalid position. Please enter a position between 0 and 5.");
        }
    }
    public void printBoard() {
        System.out.println("   " + ((board & (1 << 0)) != 0 ? "1" : "0"));
        System.out.println("  " + ((board & (1 << 1)) != 0 ? "1" : "0") + " " +
                ((board & (1 << 2)) != 0 ? "1" : "0"));
        System.out.println(" " + ((board & (1 << 3)) != 0 ? "1" : "0") + " " +
                ((board & (1 << 4)) != 0 ? "1" : "0") + " " +
                ((board & (1 << 5)) != 0 ? "1" : "0"));
        System.out.println("" + ((board & (1 << 6)) != 0 ? "1" : "0") + " " +
                ((board & (1 << 7)) != 0 ? "1" : "0") + " " +
                ((board & (1 << 8)) != 0 ? "1" : "0") + " " +
                ((board & (1 << 9)) != 0 ? "1" : "0"));
    }
    // New method to check if a there is a peg in the spot that is chosen
    public boolean validChoice(int move) {
        if ((board & move) != 0) {
            board = board ^ move;
            return true;
        }
        return false;
    }
    public void Jump(int move,int to) {
        int over =

        if (validChoice(move) && validChoice(over) && (!validChoice(to))) {
            board ^= move;
            board ^= over;

            // Set to
            board |= to;

            System.out.println("Jump performed.");
        }
        else {
            System.out.println("Invalid jump.");
        }
    }
}
