package websocket.commands;

public class ConnectUserGameCommand extends UserGameCommand {
    public ConnectUserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
