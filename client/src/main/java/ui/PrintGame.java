package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;

public class PrintGame {
    private final ChessGame game;
    private AlternatingShade currentShade ;

    public PrintGame(ChessGame game) {
        this.game = game;
        currentShade = AlternatingShade.DARK;
    }

    public enum AlternatingShade {
        LIGHT,
        DARK
    }

    public String printBoard(ChessGame.TeamColor perspective) {
        StringBuilder stringBoard = new StringBuilder();
        String[] ranks = {FULL_1, FULL_2, FULL_3, FULL_4, FULL_5, FULL_6, FULL_7, FULL_8};
        String[] columns = {FULL_A, FULL_B, FULL_C, FULL_D, FULL_E, FULL_F, FULL_G, FULL_H};

        int start = 8;
        int end = 1;
        int incrementBy = -1;
        if (perspective == ChessGame.TeamColor.BLACK) {
            start = 1;
            end = 8;
            incrementBy = 1;
            reverseArray(ranks);
            reverseArray(columns);
        }

        stringBoard.append(SET_TEXT_COLOR_BLACK).append(getNextGrey()).append(EMPTY);
        for (String column : columns) {
            stringBoard.append(getNextGrey()).append(column);
        }
        stringBoard.append(getNextGrey()).append(EMPTY).append(getNextGrey()).append(RESET_BG_COLOR).append("\n");
        for (int row = start; (incrementBy * end) - (incrementBy * row) >= 0; row += incrementBy) {
            String rankIndex = ranks[(incrementBy * row) - (incrementBy * start)];
            stringBoard.append(getNextGrey()).append(SET_TEXT_COLOR_BLACK).append(rankIndex);
            for (int col = end; (incrementBy * col) - (incrementBy * start) >= 0; col -= incrementBy) {
                stringBoard.append(getNextBoardColor());
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(position);
                if (piece != null) {
                    stringBoard.append(getSymbol(piece));
                } else {
                    stringBoard.append(EMPTY);
                }
            }
            stringBoard.append(getNextGrey()).append(SET_TEXT_COLOR_BLACK).append(rankIndex).append(getNextGrey()).append(RESET_BG_COLOR).append("\n");
        }
        stringBoard.append(getNextGrey()).append(EMPTY);
        for (String column : columns) {
            stringBoard.append(getNextGrey()).append(column);
        }
        stringBoard.append(getNextGrey()).append(EMPTY).append(RESET_BG_COLOR).append(RESET_TEXT_COLOR);

        return stringBoard.toString();
    }

    private String getSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> piece.getTeamColor() == WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> piece.getTeamColor() == WHITE ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT -> piece.getTeamColor() == WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> piece.getTeamColor() == WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
    }

    private String getNextGrey() {
        if (currentShade == AlternatingShade.LIGHT) {
            currentShade = AlternatingShade.DARK;
            return SET_BG_COLOR_LIGHT_GREY;
        } else {
            currentShade = AlternatingShade.LIGHT;
            return SET_BG_COLOR_DARK_GREY;
        }
    }

    private String getNextBoardColor() {
        if (currentShade == AlternatingShade.LIGHT) {
            currentShade = AlternatingShade.DARK;
            return SET_BG_COLOR_LIGHT_BROWN;
        } else {
            currentShade = AlternatingShade.LIGHT;
            return SET_BG_COLOR_DARK_BROWN;
        }
    }

    public static void reverseArray(String[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            String temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }
}
