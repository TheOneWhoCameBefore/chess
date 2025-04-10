package ui;

import chess.ChessGame;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 chess. Type help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public ChessClient getClient() {
        return client;
    }

    public void printPrompt() {
        if (client.state == State.SIGNEDIN) {
            System.out.print("\n[LOGGED IN]>>> ");
        } else if (client.state == State.SIGNEDOUT) {
            System.out.print("\n[LOGGED OUT]>>> ");
        } else if (client.state == State.INGAME) {
            System.out.print("\n[IN GAME]>>> ");
        }
    }

    public void notify(String message) {
        System.out.println("\n" + SET_TEXT_COLOR_GREEN + message + RESET_TEXT_COLOR);
        printPrompt();
    }

    public void loadGame(ChessGame game, String role) {
        System.out.println("\n" + new PrintGame(game).printBoard(
                Objects.equals(role, "black") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE, null));
        printPrompt();
    }

    public void error(String message) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + message + RESET_TEXT_COLOR);
        printPrompt();
    }
}