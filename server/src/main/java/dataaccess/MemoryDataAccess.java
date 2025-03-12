package dataaccess;

public class MemoryDataAccess {
    public final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    public final MemoryGameDAO gameDAO = new MemoryGameDAO();
    public final MemoryUserDAO userDAO = new MemoryUserDAO();
}
