package chess;

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
    private final ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * Constructor for deep copy
     * @param original the original piece to copy
     */
    public ChessPiece(ChessPiece original) {
        this.pieceColor = original.getTeamColor();
        this.type = original.getPieceType();
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
        };
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private Collection<ChessMove> getDirectionMoves(int rowDirection,
                                                    int colDirection,
                                                    int distance,
                                                    boolean canMoveToEmpty,
                                                    boolean canCapture,
                                                    boolean pawnPromotion,
                                                    ChessBoard board,
                                                    ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int rowModifier = rowDirection, colModifier = colDirection;
             rowModifier * rowDirection <= distance && colModifier * colDirection <= distance;
             rowModifier += rowDirection, colModifier += colDirection) {
            int newRow = row + rowModifier;
            int newCol = col + colModifier;

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null && canMoveToEmpty) {
                        if (pawnPromotion) {
                            moves.addAll(pawnPromotionMoves(myPosition, newPosition));
                        } else {
                            moves.add(new ChessMove(myPosition, newPosition));
                        }
                } else if (piece != null && piece.getTeamColor() != this.getTeamColor() && canCapture) {
                        if (pawnPromotion) {
                            moves.addAll(pawnPromotionMoves(myPosition, newPosition));
                        } else {
                            moves.add(new ChessMove(myPosition, newPosition));
                        }
                } if (piece != null) {
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> getHorizontalVerticalMoves(int distance, ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = getDirectionMoves(1, 0, distance, true, true, false, board, myPosition);
        moves.addAll(getDirectionMoves(-1, 0, distance, true, true, false, board, myPosition));
        moves.addAll(getDirectionMoves(0, 1, distance, true, true, false, board, myPosition));
        moves.addAll(getDirectionMoves(0, -1, distance, true, true, false, board, myPosition));
        return moves;
    }

    private Collection<ChessMove> getDiagonalMoves(int distance, ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = getDirectionMoves(1, 1, distance, true, true, false, board, myPosition);
        moves.addAll(getDirectionMoves(1, -1, distance, true, true, false, board, myPosition));
        moves.addAll(getDirectionMoves(-1, 1, distance, true, true, false, board, myPosition));
        moves.addAll(getDirectionMoves(-1, -1, distance, true, true, false, board, myPosition));
        return moves;
    }

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = getHorizontalVerticalMoves(1, board, myPosition);
        moves.addAll(getDiagonalMoves(1, board, myPosition));
        return moves;
    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = getHorizontalVerticalMoves(8, board, myPosition);
        moves.addAll(getDiagonalMoves(8, board, myPosition));
        return moves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        return getDiagonalMoves(8, board, myPosition);
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
        return getHorizontalVerticalMoves(8, board, myPosition);
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();

        if (board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                moves.addAll(getDirectionMoves(1, 0, 2, true, false, false, board, myPosition));
                moves.addAll(getDirectionMoves(1, 1, 1, false, true, false, board, myPosition));
                moves.addAll(getDirectionMoves(1, -1, 1, false, true, false, board, myPosition));
            } else if (row == 7) {
                moves.addAll(getDirectionMoves(1, 0, 1,true, false, true, board, myPosition));
                moves.addAll(getDirectionMoves(1, 1, 1, false, true, true, board, myPosition));
                moves.addAll(getDirectionMoves(1, -1, 1,false, true, true, board, myPosition));
            } else {
                moves.addAll(getDirectionMoves(1, 0, 1,true, false, false, board, myPosition));
                moves.addAll(getDirectionMoves(1, 1, 1, false, true, false, board, myPosition));
                moves.addAll(getDirectionMoves(1, -1, 1,false, true, false, board, myPosition));
            }
        } else {
            if (row == 7) {
                moves.addAll(getDirectionMoves(-1, 0, 2, true, false, false, board, myPosition));
                moves.addAll(getDirectionMoves(-1, 1, 1, false, true, false, board, myPosition));
                moves.addAll(getDirectionMoves(-1, -1, 1, false, true, false, board, myPosition));
            } else if (row == 2) {
                moves.addAll(getDirectionMoves(-1, 0, 1, true, false, true, board, myPosition));
                moves.addAll(getDirectionMoves(-1, 1, 1, false, true, true, board, myPosition));
                moves.addAll(getDirectionMoves(-1, -1, 1, false, true, true, board, myPosition));
            } else {
                moves.addAll(getDirectionMoves(-1, 0, 1, true, false, false, board, myPosition));
                moves.addAll(getDirectionMoves(-1, 1, 1, false, true, false, board, myPosition));
                moves.addAll(getDirectionMoves(-1, -1, 1, false, true, false, board, myPosition));
            }
        }

        return moves;
    }

    private Collection<ChessMove> pawnPromotionMoves(ChessPosition myPosition, ChessPosition newPosition) {
        return List.of(new ChessMove[]{
                new ChessMove(myPosition, newPosition, PieceType.QUEEN),
                new ChessMove(myPosition, newPosition, PieceType.BISHOP),
                new ChessMove(myPosition, newPosition, PieceType.ROOK),
                new ChessMove(myPosition, newPosition, PieceType.KNIGHT)
        });
    }
 }
