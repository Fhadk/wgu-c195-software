package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import utils.Database;

public class UserDB {
    private static User currentUser;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("models");
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    // Attempt Login
    public static Boolean login(String username, String password) {
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT * FROM user WHERE userName='" + username + "' AND password='" + password + "'";
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                currentUser = new User();
                currentUser.setUsername(results.getString("userName"));
                statement.close();
                
                logger.info("User Logged in: " + currentUser.getUsername() );
                return true;
            } else {
                logger.error("User Authentication issue " );
                return false;
            }
        } catch (SQLException e) {
            logger.error("Exception: " + e.getMessage());
            return false;
        }
    }
}
