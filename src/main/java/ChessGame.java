public class ChessGame {

    private String name;

    private String lastTurn;

    private Move lastMove;

    private boolean waiting;

    public ChessGame(String name) {
        this.name = name;
        this.lastTurn = "black";
        this.waiting = true;
    }

    public void update(Move lastMove, String lastTurn) {
        this.lastMove = lastMove;
        this.lastTurn = lastTurn;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public String getName() {
        return name;
    }

    public String getLastTurn() {
        return lastTurn;
    }

    public Move getLastMove() {
        return lastMove;
    }

}
