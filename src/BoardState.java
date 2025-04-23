import java.util.ArrayList;

public class BoardState {
    int board;
    ArrayList<Move> moves = new ArrayList<>();
    int bestScore = Integer.MAX_VALUE;
}
class Move {
    int from, over, to;
    int resultingBoard;

}
