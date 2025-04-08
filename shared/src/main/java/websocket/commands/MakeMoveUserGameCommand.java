package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveUserGameCommand extends UserGameCommand {
    private final ChessMove move;

    public MakeMoveUserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() { return move; }
}
