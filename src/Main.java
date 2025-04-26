public class Main {
    public static void main(String[] args) {
        PegSolitaireBitwise p = new PegSolitaireBitwise();
        p.writeSolutionsToFile("solutions");
        p.printBoard();
        p.firstMove();
        p.printBoard();
        p.printBestCaseFromCurrentBoard();




    }
}