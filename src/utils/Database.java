package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public class Database {
    
    private static final Logger logger = Logger.getLogger("utils");
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    
    private static final String RESOURCE_BUNDLE_PATH = "config";
    private static final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);
    private final static  String  user = bundle.getString("USERNAME") , password = bundle.getString("PASSWORD"), 
                url = bundle.getString("URL");
    private static Connection conn;
    
    /**
     * @return Connection conn
     * 
     */
    public static void connect() {
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(url, user, password);
            logger.info("Connection Established Successfully !! " );
//            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Exception: "  + e.getMessage());
        }
//        return null;
    }
    
    /**
     * 
     * 
     * @return boolean flag 
     */
    public static boolean freeResources() {
        try {
            conn.close();
            logger.info("Connection Terminated Successfully !! ");
            return true;
        } catch (SQLException e) {
             logger.error("Exception: " +  e.getMessage());
        }
        return false;
    }
    
    /**
     * 
     * @return Connection conn 
     */
    public static Connection getConnection() {
        return conn;
    }
    
}
