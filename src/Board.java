public class Board {
    int board = 63;
    int boardSize;
    int valid_peg = 63;
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
        System.out.println("Enter the position (0-5) of the peg to remove:");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int pos = scanner.nextInt();
        if (pos >= 0 && pos <= 5) {
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
    }
    // New method to check if a move is valid
}
