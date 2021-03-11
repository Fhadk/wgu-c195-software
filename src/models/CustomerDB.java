package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import utils.Database;

public class CustomerDB {
    
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final Logger logger = Logger.getLogger("models");
    
    public static Customer getCustomer(int id) {
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT * FROM customer WHERE customerId='" + id + "'";
            
            logger.debug(query);
            
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                Customer customer = new Customer();
                customer.setCustomerName(results.getString("customerName"));
                statement.close();
                return customer;
            }
        } catch (SQLException e) {
            logger.error("Exception: "  + e.getMessage());
        }
        return null;
    }
    
    // Returns all Customers in Database
    public static ObservableList<Customer> getAllCustomers() {
        allCustomers.clear();
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT customer.customerId, customer.customerName, address.address,"
                    + " address.phone, address.postalCode, city.city"
                    + " FROM customer "
                    + " INNER JOIN address ON customer.addressId = address.addressId "
                    + " INNER JOIN city ON address.cityId = city.cityId";
            
            logger.debug(query);
            
            ResultSet results = statement.executeQuery(query);
            while(results.next()) {
                Customer customer = new Customer(
                    results.getInt("customerId"), 
                    results.getString("customerName"), 
                    results.getString("address"),
                    results.getString("city"),
                    results.getString("phone"),
                    results.getString("postalCode"));
                allCustomers.add(customer);
            }
            statement.close();
            return allCustomers;
        } catch (SQLException e) {
            logger.error("Exception: "  + e.getMessage());
            return null;
        }
    }
    
    
    public static int getAutoIncrementedID(){
        int id = 0;
        try{
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT MAX(customerId) ID from customer";
            
            ResultSet results = statement.executeQuery(query);
            while(results.next()){ 
                    id = results.getInt("ID");
            }   
            statement.close();
            
         } catch (SQLException e) {
            logger.error("Exception: "  + e.getMessage());
        }
        return id;
    }
    
    
    // Saves new Customer to Database
    public static boolean saveCustomer(String name, String address, int cityId, String zip, String phone) {
        try {
            Statement statement = Database.getConnection().createStatement();
            int addressId = getAutoIncrementedID()+1;
            
            logger.debug(addressId);
            
            String query = "INSERT INTO address ( address, phone, postalcode, cityId, addressId,"
                    + " address2, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES('" + address + "','" + phone + "', '" + zip + "'," 
                    + cityId +" ," + addressId +" , '', '" + LocalDate.now() + "',"+ " '', '" + LocalDate.now() + "', '' )"; 
                    
            String queryCustomer = "INSERT INTO customer (customerId, customerName, addressId, active,"
                    + " createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (" + addressId + ",'" + name + "'," + addressId +"," + 1 +" ,'" 
                    + LocalDate.now() + "',"+ " '', '" + LocalDate.now() + "', '' )";
            
            logger.debug(query);
            logger.debug(queryCustomer);
            
            if((statement.executeUpdate(query)) == 1 && (statement.executeUpdate(queryCustomer)) == 1)
                return true;
        } catch (SQLException e) {
            logger.error("Exception: "  + e.getMessage());
        }
        return false;
    }
    
    // Update existing Customer in Database
    public static boolean updateCustomer(int id, String name, String address, int cityId, String zip, String phone) {
        try {
            Statement statement = Database.getConnection().createStatement();
            String queryOne = "UPDATE address SET address='" + address + "', cityId=" + cityId + ", postalCode='" + zip + "', phone='" + phone + "' "
                + "WHERE addressId=" + id;
            int updateOne = statement.executeUpdate(queryOne);
            if(updateOne == 1) {
                String queryTwo = "UPDATE customer SET customerName='" + name + "', addressId=" + id + " WHERE customerId=" + id;
                int updateTwo = statement.executeUpdate(queryTwo);
                if(updateTwo == 1) {
                    return true;
                }
            }
        } catch(SQLException e) {
            logger.error("Exception: "  + e.getMessage());
        }
        return false;
    }
    
    // Delete Customer from Database
    public static boolean deleteCustomer(int id) {
        try {
            Statement statement = Database.getConnection().createStatement();
            String queryOne = "DELETE FROM address WHERE addressId=" + id;
            int updateOne = statement.executeUpdate(queryOne);
            if(updateOne == 1) {
                String queryTwo = "DELETE FROM customer WHERE customerId=" + id;
                int updateTwo = statement.executeUpdate(queryTwo);
                if(updateTwo == 1) {
                    return true;
                }
            }
        } catch(SQLException e) {
            logger.error("Exception: "  + e.getMessage());
        }
        return false;
    }
}
