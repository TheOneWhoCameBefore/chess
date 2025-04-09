package chess;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    /**
     * Constructor with all parameters
     * @param startPosition
     * @param endPosition
     * @param promotionPiece
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * Overloaded constructor with promotionPiece defaulted to nulll
     * @param startPosition
     * @param endPosition
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this(startPosition, endPosition, null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(
                getStartPosition(),
                chessMove.getStartPosition()) && Objects.equals(getEndPosition(),
                chessMove.getEndPosition()) && getPromotionPiece() == chessMove.getPromotionPiece();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartPosition(), getEndPosition(), getPromotionPiece());
    }

    public String toAlgebraicNotation(ChessGame game) {
        return "" + (char) ('a' + startPosition.getColumn() - 1) + startPosition.getRow() + (char) ('a' + endPosition.getColumn() - 1) + endPosition.getRow();
    }

    public ChessMove fromAlgebraicNotation(String notation) {
        int startColumn = notation.charAt(0) - 'a' + 1;
        int startRow = Character.getNumericValue((notation.charAt(1)));
        ChessPosition startPosition = new ChessPosition(startRow, startColumn);

        int endColumn = notation.charAt(2) - 'a' + 1;
        int endRow = Character.getNumericValue(notation.charAt(3));
        ChessPosition endPosition = new ChessPosition(endRow, endColumn);

        return new ChessMove(startPosition, endPosition);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
