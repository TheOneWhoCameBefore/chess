package chess;

import java.time.chrono.ChronoPeriod;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Set the type of chess piece this piece is
     */
    public void setPieceType(PieceType pieceType) {
        type = pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> getKingMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
            case BISHOP -> getBishopMoves(board, myPosition);
            case KNIGHT -> getKnightMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
            case PAWN -> getPawnMoves(board, myPosition);
            default -> new ArrayList<>();
        };
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int rowModifier = -1; rowModifier <= 1; rowModifier++) {
            for (int colModifier = -1; colModifier <= 1; colModifier++) {
                if (rowModifier == 0 && colModifier == 0) {
                    continue; // Skip the current position
                }

                int newRow = row + rowModifier;
                int newCol = col + colModifier;


                if (isWithinBounds(newRow, newCol)) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece piece = board.getPiece(newPosition);

                    if (piece == null || piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check moves in the positive row direction until reaching an opponent or the edge.
        for (int rowModifier = 1; rowModifier <= 8 - row; rowModifier++) {
            int newRow = row + rowModifier;

            if (isWithinBounds(newRow, col)) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative row direction until reaching an opponent or the edge.
        for (int rowModifier = -1; rowModifier >= 1-row; rowModifier--) {
            int newRow = row + rowModifier;

            if (isWithinBounds(newRow, col)) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the positive col direction until reaching an opponent or the edge.
        for (int colModifier = 1; colModifier <= 8 - col; colModifier++) {
            int newCol = col + colModifier;

            if (isWithinBounds(newCol, col)) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative col direction until reaching an opponent or the edge.
        for (int colModifier = -1; colModifier >= 1-col; colModifier--) {
            int newCol = col + colModifier;

            if (isWithinBounds(newCol, col)) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the positive row and positive col direction until reaching an opponent or the edge.
        for (int rowModifier = 1, colModifier = 1; rowModifier <= 8 - row && colModifier <= 8 - col; rowModifier++, colModifier++) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the positive row and negative col direction until reaching an opponent or the edge.
        for (int rowModifier = 1, colModifier = -1; rowModifier <= 8 - row && colModifier >= 1 - col; rowModifier++, colModifier--) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative row and positive col direction until reaching an opponent or the edge.
        for (int rowModifier = -1, colModifier = 1; rowModifier >= 1 - row && colModifier <= 8 - col; rowModifier--, colModifier++) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative row and negative col direction until reaching an opponent or the edge.
        for (int rowModifier = -1, colModifier = -1; rowModifier >= 1 - row && colModifier >= 1 - col; rowModifier--, colModifier--) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check moves in the positive row and positive col direction until reaching an opponent or the edge.
        for (int rowModifier = 1, colModifier = 1; rowModifier <= 8 - row && colModifier <= 8 - col; rowModifier++, colModifier++) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the positive row and negative col direction until reaching an opponent or the edge.
        for (int rowModifier = 1, colModifier = -1; rowModifier <= 8 - row && colModifier >= 1 - col; rowModifier++, colModifier--) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative row and positive col direction until reaching an opponent or the edge.
        for (int rowModifier = -1, colModifier = 1; rowModifier >= 1 - row && colModifier <= 8 - col; rowModifier--, colModifier++) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative row and negative col direction until reaching an opponent or the edge.
        for (int rowModifier = -1, colModifier = -1; rowModifier >= 1 - row && colModifier >= 1 - col; rowModifier--, colModifier--) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int rowModifier = -2; rowModifier <= 2; rowModifier++) {
            for (int colModifier = -2; colModifier <= 2; colModifier++) {
                if (rowModifier == 0 || colModifier == 0 || Math.abs(rowModifier) == Math.abs(colModifier)) {
                    continue;
                }

                int newRow = row + rowModifier;
                int newCol = col + colModifier;

                if (isWithinBounds(newRow, newCol)) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece piece = board.getPiece(newPosition);

                    if (piece == null || piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check moves in the positive row direction until reaching an opponent or the edge.
        for (int rowModifier = 1; rowModifier <= 8 - row; rowModifier++) {
            int newRow = row + rowModifier;

            if (isWithinBounds(newRow, col)) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative row direction until reaching an opponent or the edge.
        for (int rowModifier = -1; rowModifier >= 1-row; rowModifier--) {
            int newRow = row + rowModifier;

            if (isWithinBounds(newRow, col)) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the positive col direction until reaching an opponent or the edge.
        for (int colModifier = 1; colModifier <= 8 - col; colModifier++) {
            int newCol = col + colModifier;

            if (isWithinBounds(newCol, col)) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // Check moves in the negative col direction until reaching an opponent or the edge.
        for (int colModifier = -1; colModifier >= 1-col; colModifier--) {
            int newCol = col + colModifier;

            if (isWithinBounds(newCol, col)) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                } else { // Encountering any piece
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int range;

        if (board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                range = 2;
            } else {
                range = 1;
            }
        } else {
            if (row == 7) {
                range = -2;
            } else {
                range = -1;
            }
        }

        for (int rowModifier = 0; Math.abs(rowModifier) != Math.abs(range) + 1; rowModifier += (range > 0) ? 1 : -1) {
            if (rowModifier == 0) {
                continue;
            }
            int newRow = row + rowModifier;

            for (int colModifier = -1; colModifier <= 1; colModifier++) {
                int newCol = col + colModifier;

                if (colModifier == 0) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece piece = board.getPiece(newPosition);

                    if (piece == null) {
                        if (newRow == 1 || newRow == 8) {
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(myPosition, newPosition));
                        }
                    } else {
                        if (Math.abs(range) == 2 && Math.abs(rowModifier) != 2) {
                            range -= (range > 0) ? 1 : -1;
                        }
                    }
                } else {
                    if (Math.abs(rowModifier) == 1) {
                        if (isWithinBounds(newRow, newCol)) {
                            ChessPosition newPosition = new ChessPosition(newRow, newCol);
                            ChessPiece piece = board.getPiece(newPosition);

                            if (piece != null && piece.getTeamColor() != this.getTeamColor()) {
                                if (newRow == 1 || newRow == 8) {
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, newPosition));
                                }
                            }
                        }
                    }
                }
            }
        }


        return moves;
    }
}
