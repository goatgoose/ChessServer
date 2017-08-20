import java.io.StringWriter;
import java.util.ArrayList;

import com.oracle.javafx.jmx.json.*;
import org.json.*;
import static spark.Spark.*;

public class ChessServer {

    private static ArrayList<ChessGame> chessGames = new ArrayList<>();

    public static void main(String[] args) {

        post("/createGame", (req, res) -> {
            JSONObject request = new JSONObject(req.body());
            String gameName = request.getString("game");

            if (getChessGame(gameName) == null) {
                chessGames.add(new ChessGame(gameName));
                System.out.println("Game created: " + gameName);
                return "success";
            } else {
                getChessGame(gameName).setWaiting(false);
                System.out.println("Game already created: " + gameName);
                return "exists";
            }
        });

        post("/deleteGame", (req, res) -> {
            JSONObject request = new JSONObject(req.body());
            String gameName = request.getString("game");

            chessGames.remove(getChessGame(gameName));

            System.out.println("Game deleted: " + gameName);

            return "success";
        });

        post("/isWaiting", (req, res) -> {
            JSONObject request = new JSONObject(req.body());
            String gameName = request.getString("game");

            ChessGame chessGame = getChessGame(gameName);

            JSONObject response = new JSONObject();
            response.put("isWaiting", chessGame.isWaiting());

            // https://www.tutorialspoint.com/json/json_java_example.htm
            StringWriter out = new StringWriter();
            response.write(out);

            return out.toString();
        });

        post("/movePiece", (req, res) -> {
            JSONObject request = new JSONObject(req.body());
            String gameName = request.getString("game");
            String turn = request.getString("player");

            JSONObject fromObj = request.getJSONObject("from");
            Coordinate from = new Coordinate(fromObj.getInt("x"), fromObj.getInt("y"));
            JSONObject toObj = request.getJSONObject("to");
            Coordinate to = new Coordinate(toObj.getInt("x"), toObj.getInt("y"));

            ChessGame game = getChessGame(gameName);
            game.update(new Move(from, to), turn);

            System.out.println("Piece moved from " + from.getX() + ", " + from.getY() + " to " + to.getX() + ", " + to.getY());

            return "success";
        });

        post("/getLastTurn", (req, res) -> {
            JSONObject request = new JSONObject(req.body());
            String gameName = request.getString("game");

            ChessGame game = getChessGame(gameName);

            JSONObject response = new JSONObject();
            response.put("turn", game.getLastTurn());

            System.out.println("Got last turn: " + game.getLastTurn());

            StringWriter out = new StringWriter();
            response.write(out);

            return out.toString();
        });

        post("/getLastMove", (req, res) -> {
            JSONObject request = new JSONObject(req.body());
            String gameName = request.getString("game");

            ChessGame game = getChessGame(gameName);
            Move lastMove = game.getLastMove();

            JSONObject response = new JSONObject();

            JSONObject from = new JSONObject();
            from.put("x", lastMove.getFrom().getX());
            from.put("y", lastMove.getFrom().getY());

            JSONObject to = new JSONObject();
            to.put("x", lastMove.getTo().getX());
            to.put("y", lastMove.getTo().getY());

            response.put("from", from);
            response.put("to", to);

            System.out.println("Got last move: " + lastMove.getFrom().getX() + ", " + lastMove.getFrom().getY() + " to " + lastMove.getTo().getX() + ", " + lastMove.getTo().getY());

            StringWriter out = new StringWriter();
            response.write(out);

            return out.toString();
        });

        get("/availableServers", (req, res) -> {
            ArrayList<ChessGame> waitingGames = getWaitingGames();

            JSONArray jsonWaitingGames = new JSONArray();
            for (int i = 0; i < waitingGames.size(); i++) {
                ChessGame game = waitingGames.get(i);
                jsonWaitingGames.put(i, game.getName());
            }

            JSONObject response = new JSONObject();
            response.put("servers", jsonWaitingGames);

            StringWriter out = new StringWriter();
            response.write(out);

            System.out.println("Got avalible servers");

            return out.toString();
        });
    }

    private static ChessGame getChessGame(String name) {
        for (ChessGame chessGame : chessGames) {
            if (chessGame.getName().equals(name)) {
                return chessGame;
            }
        }
        return null;
    }

    private static ArrayList<ChessGame> getWaitingGames() {
        ArrayList<ChessGame> waitingGames = new ArrayList<>();
        for (ChessGame chessGame : chessGames) {
            if (chessGame.isWaiting()) {
                waitingGames.add(chessGame);
            }
        }
        return waitingGames;
    }

}
