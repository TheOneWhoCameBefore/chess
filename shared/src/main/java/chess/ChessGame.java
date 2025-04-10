package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private Boolean inProgress = true;
    private ChessBoard gameBoard;
    private TeamColor teamTurn;

    public ChessGame() {
        this.gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    public Boolean isFinished() {
        return inProgress;
    }

    public void setFinished() {
        inProgress = false;
    }
    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }


    /**
     * Gets the positions of the specified team's pieces
     *
     * @return the chessboard
     */
    public Collection<ChessPosition> getTeamPositions(TeamColor team) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == team) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves =  new ArrayList<>();
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        TeamColor teamColor = piece.getTeamColor();

        for (ChessMove move : piece.pieceMoves(gameBoard, startPosition)) {
            ChessBoard testBoard = new ChessBoard(gameBoard);
            testBoard.makeMove(move);

            if (!testBoard.isInCheck(teamColor)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()) != null &&
            teamTurn == gameBoard.getPiece(move.getStartPosition()).getTeamColor() &&
            validMoves(move.getStartPosition()).contains(move)) {
            gameBoard.makeMove(move);
            if (teamTurn == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        } else {
            throw new InvalidMoveException("Invalid move.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return gameBoard.isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> teamMoves = new ArrayList<>();
        for (ChessPosition position : getTeamPositions(teamColor)) {
            teamMoves.addAll(validMoves(position));
        }
        return  isInCheck(teamColor) && teamMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> teamMoves = new ArrayList<>();
        for (ChessPosition position : getTeamPositions(teamColor)) {
            teamMoves.addAll(validMoves(position));
        }
        return !isInCheck(teamColor) && teamMoves.isEmpty();
    }


}
