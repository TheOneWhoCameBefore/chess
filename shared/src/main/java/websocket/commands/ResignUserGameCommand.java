package websocket.commands;

public class ResignUserGameCommand extends UserGameCommand {
    public ResignUserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
