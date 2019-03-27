package counter;

import java.sql.*;

public class DatabaseCounter implements Counter {

    public DatabaseCounter(Connection connIn) {
        this.conn = connIn;
    }

    private final String GET_USER_SQL = "SELECT 1 FROM USERS WHERE NAME = ?";
    private final String ADD_USER_SQL = "INSERT INTO USERS (NAME,GREET_COUNT) VALUES (?,?)";
    private final String GET_USER_COUNT_SQL = "SELECT GREET_COUNT FROM USERS WHERE NAME = ?";
    private final String REMOVE_ALL_USERS_SQL = "DELETE FROM USERS";
    private final String UPDATE_COUNT_SQL = "UPDATE USERS SET GREET_COUNT = ? WHERE NAME = ?";
    private final String REMOVE_USER_SQL = "DELETE FROM USERS WHERE NAME = ?";
    private  final String SELECT_COUNT_SQL = "SELECT COUNT(*) FROM USERS";
    private Connection conn;


    public boolean countUser(String userName) throws SQLException {
        if (userInDatabase(userName)) {
            return updateCount(userName);
        } else {
            return addUser(userName);
        }
    }

    public int userGreetCount(String userName) throws SQLException {
        PreparedStatement getUserCountStmt = conn.prepareStatement(GET_USER_COUNT_SQL);
        getUserCountStmt.setString(1, userName);
        ResultSet resultSet = getUserCountStmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("GREET_COUNT");
        }
        return 0;
    }

    public int totalGreetCount() throws SQLException {
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_COUNT_SQL)) {
             resultSet.next();
             return  resultSet.getInt(1);
        }
    }

    public boolean clearUserCount(String userName) throws SQLException {
        PreparedStatement removeUserStmt = conn.prepareStatement(REMOVE_USER_SQL);
        removeUserStmt.setString(1, userName);
        if (removeUserStmt.executeUpdate() > 0) {
            return true;
        }
        return false;
    }

    public boolean clearAllUserCounts() throws SQLException {
        PreparedStatement addUserStmt = conn.prepareStatement(REMOVE_ALL_USERS_SQL);
        if (addUserStmt.executeUpdate() > 0) {
            return true;
        }
        return false;
    }


    private boolean userInDatabase(String userName) throws SQLException {
        PreparedStatement getUserStmt = conn.prepareStatement(GET_USER_SQL);
        getUserStmt.setString(1, userName);
        ResultSet resultSet = getUserStmt.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    private boolean addUser(String userName) throws SQLException {

        PreparedStatement addUserStmt = conn.prepareStatement(ADD_USER_SQL);
        addUserStmt.setString(1, userName);
        addUserStmt.setInt(2, 1);
        if (addUserStmt.executeUpdate() > 0) {
            addUserStmt.close();
            return true;
        }
        return false;
    }

    private boolean updateCount(String userName) throws SQLException {

        int currentGreetCount = userGreetCount(userName);
        PreparedStatement updateCountStmt = conn.prepareStatement(UPDATE_COUNT_SQL);
        updateCountStmt.setInt(1, ++currentGreetCount);
        updateCountStmt.setString(2, userName);

        if (updateCountStmt.executeUpdate() > 0) {
            return true;
        }
        return false;
    }

}



