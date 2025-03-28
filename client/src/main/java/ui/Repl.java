package ui;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 chess. Type Help to get started. ♕");

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

    private void printPrompt() {
        if (client.state == State.SIGNEDIN) {
            System.out.print("\n[LOGGED IN]>>> ");
        } else if (client.state == State.SIGNEDOUT) {
            System.out.print("\n[LOGGED OUT]>>> ");
        } else if (client.state == State.INGAME) {
            System.out.print("\n[IN GAME]>>> ");
        }
    }

}