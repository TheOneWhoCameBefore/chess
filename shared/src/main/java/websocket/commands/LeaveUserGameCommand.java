package websocket.commands;

public class LeaveUserGameCommand extends UserGameCommand {
    public LeaveUserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
