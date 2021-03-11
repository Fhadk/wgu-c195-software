package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import utils.Database;


public class AppointmentDB {
    
    private static final Logger logger = Logger.getLogger("models");
     
    public static ObservableList<Appointment> getMonthlyAppointments (int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now().plusMonths(1);
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT * FROM appointment WHERE customerId = '" + id + "' AND " + 
                "start >= '" + begin + "' AND start <= '" + end + "'"; 
            
            logger.debug(query);
            
            ResultSet results = statement.executeQuery(query);
            while(results.next()) {
                appointment = new Appointment(results.getInt("appointmentId"), results.getInt("customerId"), results.getString("start"),
                    results.getString("end"), results.getString("title"), results.getString("description"),
                    results.getString("location"), results.getString("contact"));
                appointments.add(appointment);
            }
            statement.close();
            return appointments;
        } catch (SQLException e) {
            logger.error("Exception: " +  e.getMessage());
            return null;
        }
    }
    
    public static ObservableList<Appointment> getWeeklyAppoinments(int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now().plusWeeks(1);
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT * FROM appointment WHERE customerId = '" + id + "' AND " + 
                "start >= '" + begin + "' AND start <= '" + end + "'";
            
             logger.debug(query);
             
            ResultSet results = statement.executeQuery(query);
            while(results.next()) {
                appointment = new Appointment(results.getInt("appointmentId"), results.getInt("customerId"), results.getString("start"),
                    results.getString("end"), results.getString("title"), results.getString("description"),
                    results.getString("location"), results.getString("contact"));
                appointments.add(appointment);
            }
            statement.close();
            return appointments;
        } catch (SQLException e) {
            logger.error("Exception: " +  e.getMessage());
            return null;
        }
    }
    
    public static Appointment appointmentIn15Min(String location) {
        Appointment appointment;
        LocalDate ld = LocalDate.now();
	 	LocalTime lt = LocalTime.now();	 	
        long timeDifference =  AppointmentDB.TimeZoneConvertor(location);
		
		if(timeDifference < 0){
			lt = lt.plusHours(timeDifference);
			
		}else {
			lt = lt.minusHours(timeDifference);
		}
				
        LocalDateTime ldt2 = LocalDateTime.of(ld,lt);
        Timestamp ts = Timestamp.valueOf(ldt2);
        Timestamp ts2 = Timestamp.valueOf(ldt2.plusMinutes(15));
        String user = UserDB.getCurrentUser().getUsername();
        
       try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT * FROM appointment WHERE start BETWEEN '" + ts + "' AND '" + ts2 + "'";
         
             logger.debug(query);
             
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                appointment = new Appointment(results.getInt("appointmentId"), results.getInt("customerId"), results.getString("start"),
                    results.getString("end"), results.getString("title"), results.getString("description"),
                    results.getString("location"), results.getString("contact"));
                logger.debug("Getting Location: " + results.getString("start") +" "+results.getString("end"));
                return appointment;
            }
        } catch (SQLException e) {
            logger.error("Exception: " +  e.getMessage());
        }
        return null;
    }
    
    public static long TimeZoneConvertor(String location) {
    	LocalDate ldAppointment;
	 	LocalTime ltAppointment;
        LocalDate ld = LocalDate.now();
	 	LocalTime lt = LocalTime.now();	 	
        ZoneId zid;
        
        if(location.equalsIgnoreCase("New York")) {
            zid = ZoneId.of("America/New_York");
            ldAppointment = LocalDate.now(ZoneId.of("America/New_York"));
    	 	ltAppointment = LocalTime.now(ZoneId.of("America/New_York"));
        } else if(location.equalsIgnoreCase("Phoenix")) {
            zid = ZoneId.of("America/Phoenix");
            ldAppointment = LocalDate.now(ZoneId.of("America/Phoenix"));
    	 	ltAppointment = LocalTime.now(ZoneId.of("America/Phoenix"));
        } else {
            zid = ZoneId.of("Europe/London");
            ldAppointment = LocalDate.now(ZoneId.of("Europe/London"));
    	 	ltAppointment = LocalTime.now(ZoneId.of("Europe/London"));
        }
      
        LocalDateTime fromDateTime = LocalDateTime.of(ldAppointment,ltAppointment);
		LocalDateTime toDateTime = LocalDateTime.of(ld,lt);
		return Duration.between(toDateTime, fromDateTime).toHours();
    }
    
    
    public static boolean saveAppointment(int id, String type, String contact, String location, String date, String time) {
        String title = type.split(":")[0];
        String description = type.split(":")[1];
        String tsStart = createTimeStamp(date, time, location, true);
        String tsEnd = createTimeStamp(date, time, location, false);
        int ID = 0;
        try {
            Statement statement = Database.getConnection().createStatement();
            
            String query = "SELECT MAX(appointmentId) ID FROM appointment";
            
            logger.debug(query);
            
            ResultSet rs = statement.executeQuery(query);
            
            while(rs.next()){
                ID = Integer.parseInt(rs.getString("ID")) + 1;
                logger.debug(ID);
            }
            
            query = "INSERT INTO appointment SET appointmentId = '" + ID + "', customerId='" + id + "', title='" + title + "', description='" +
                description + "', contact='" + contact + "', location='" + location + "', start='" + tsStart + "', end='" + 
                tsEnd + "', url='', createDate=NOW(), createdBy='', lastUpdate=NOW(), lastUpdateBy=''";
            
              logger.debug(query);
            
            int update = statement.executeUpdate(query);
            if(update == 1) {
                return true;
            }
        } catch (SQLException e) {
            logger.error("Exception: " +  e.getMessage());
        }
        return false;
    }
    
    public static boolean updateAppointment(int id, String type, String contact, String location, String date, String time) {
        String title = type.split(":")[0];
        String description = type.split(":")[1];
        String tsStart = createTimeStamp(date, time, location, true);
        String tsEnd = createTimeStamp(date, time, location, false);
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "UPDATE appointment SET title='" + title + "', description='" + description + "', contact='" +
                contact + "', location='" + location + "', start='" + tsStart + "', end='" + tsEnd + "' WHERE " +
                "appointmentId='" + id + "'";
            
            logger.debug(query);
            
            int update = statement.executeUpdate(query);
            if(update == 1) {
                return true;
            }
        } catch (SQLException e) {
            logger.error("Exception: " +  e.getMessage());
        }
        return false;
    }
    
    public static boolean overlappingAppointment(int id, String location, String date, String time) {
        String start = createTimeStamp(date, time, location, true);
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT * FROM appointment WHERE start = '" + start + "' AND location = '" + location + "'";
            
            logger.debug(query);
            
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                if(results.getInt("appointmentId") == id) {
                    statement.close();
                    return false;
                }
                statement.close();
                return true;
            } else {
                statement.close();
                return false;
            }
        } catch (SQLException e) {
           logger.error("Exception: " +  e.getMessage());
            return true;
        }
    }
    
    public static boolean deleteAppointment(int id) {
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "DELETE FROM appointment WHERE appointmentId = " + id;
            
            logger.debug(query);
            
            int update = statement.executeUpdate(query);
            if(update == 1) {
                return true;
            }
        } catch (SQLException e) {
            logger.error("Exception: " +  e.getMessage());
        }
        return false;
    }
    
    public static String createTimeStamp(String date, String time, String location, boolean startMode) {
        String h = time.split(":")[0];
        int rawH = Integer.parseInt(h);
        if(!startMode) {
            rawH += 1;
        }
        String rawD = String.format("%s %02d:%s", date, rawH, "00");
        return rawD;
    }
    
}
