package dataaccess;

public class MemoryDataAccess {
    public final AuthDAO authDAO = new MemoryAuthDAO();
    public final GameDAO gameDAO = new MemoryGameDAO();
    public final UserDAO userDAO = new MemoryUserDAO();
}
