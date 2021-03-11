package views;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import models.Appointment;
import models.AppointmentDB;
import models.Customer;
import models.CustomerDB;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 */
public class MainController implements Initializable {
    private static final Logger logger = Logger.getLogger("views");
    private static final String RESOURCE_BUNDLE_PATH = "config";
    private static final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);
    private final static  String  utils = bundle.getString("FILE_PATH_UTILS") , models = bundle.getString("FILE_PATH_MODELS"), 
                views = bundle.getString("FILE_PATH_VIEWS");
    private static boolean FLAG = true ;
    private String text;
    

    /**
     * Initializes the controller class.
     */
    @FXML
    public void handleCustomerButton() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("CustomerMain.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.error("Exception: " + e.getMessage());
        }
    }
    
    @FXML
    public void handleAppointmentButton() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("AppointmentMain.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.error("Exception: " + e.getMessage());
        }
    }
    
    @FXML
    public void handleReportsButton() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("ReportsMain.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
           logger.error("Exception: " + e.getMessage());
        }
    }
    
    @FXML
    public void modelsLogs() {
        if(Files.isReadable(Paths.get(models))== true){
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(models));
                } catch (IOException e) {
                    logger.error("Exception: " + e.getMessage());
                }
            }
        }
    }
    
    
    @FXML
    public void utilsLogs() {
        if(Files.isReadable(Paths.get(utils))== true){
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(utils));
                } catch (IOException e) {
                    logger.error("Exception: " + e.getMessage());
                }
            }
        }
    }
    
    @FXML
    public void viewsLogs() {
        if(Files.isReadable(Paths.get(views))== true){
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(views));
                } catch (IOException e) {
                    logger.error("Exception: " + e.getMessage());
                }
            }
        }
    }
    
    
    private void showingAlert(String location) {
    	try {
            while(FLAG){
                logger.debug(">>>>>> Going to check for 15MinAppointment of "+ location );
                Appointment appointment = AppointmentDB.appointmentIn15Min(location);
                if(appointment != null) {
                    logger.debug(">>>>>> Got Appointment Data of "+ location);
                     Customer customer = CustomerDB.getCustomer(appointment.getAptCustId());
                     text = String.format("You have a %s appointment with %s at %s",
                         appointment.getAptDescription(), 
                         customer.getCustomerName(),
                         appointment.get15Time());
                     
                 logger.debug(">>>>>> " + text + " " + location);
                 
                 JFrame j= new JFrame();
                 JOptionPane.showMessageDialog(j, text, "Alert", JOptionPane.INFORMATION_MESSAGE);
                 }
                Thread.sleep(300000);
            }               
        } catch (InterruptedException ex) {
           logger.error("Exception: " + ex.getMessage()); 
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	Runnable t = () -> {
        	showingAlert("London");
        };
        
        Runnable t2 = () -> {
        	showingAlert("New York");
        };
        
        Runnable t3 = () -> {
        	showingAlert("Phoenix");
        };     
       
       Thread london =  new Thread(t);
       london.setDaemon(true);
       london.start();
       
       Thread newYork =  new Thread(t2);
       newYork.setDaemon(true);
       newYork.start();
       
       Thread phoenix =  new Thread(t3);
       phoenix.setDaemon(true);
       phoenix.start();
    }    
    
}
