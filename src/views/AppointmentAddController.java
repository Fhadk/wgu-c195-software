package views;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.AppointmentDB;

/**
 * FXML Controller class
 *
 */
public class AppointmentAddController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField customerName;
    
    @FXML
    private ComboBox contact;
    
    @FXML
    private TextField location;
    
    @FXML 
    private DatePicker date;
    
    @FXML
    private ComboBox time;
    
    @FXML
    private ComboBox type;
    
    private final ObservableList<String> contacts = FXCollections.observableArrayList("Philip", "Cricus", "Ragnar");
    
    private final ObservableList<String> times = FXCollections.observableArrayList("9:00:00 HOURS","10:00:00 HOURS","11:00:00 HOURS","12:00:00 HOURS","13:00:00 HOURS","14:00:00 HOURS","15:00:00 HOURS","16:00:00 HOURS");
    
    private final ObservableList<String> types = FXCollections.observableArrayList(
    "Pre-Sale : Introduction","InvestPlan : Action Plan","ROI : Stock Market","Consultation : Tax Preparation");
    
    private ObservableList<String> errors = FXCollections.observableArrayList();
    
    public boolean handleAddAppointment(int id) {
        errors.clear();
        int aptContact = contact.getSelectionModel().getSelectedIndex();
        int aptType = type.getSelectionModel().getSelectedIndex();
        int aptTime = time.getSelectionModel().getSelectedIndex();
        LocalDate ld = date.getValue();
        if(!validateContact(aptContact)||!validateType(aptType)||!validateTime(aptTime)||!validateDate(ld)) {
            return false;
        }
        if(AppointmentDB.overlappingAppointment(-1, location.getText(), ld.toString(), times.get(aptTime))) {
            errors.add("Overlapping Appointments");
            return false;
        }
        if(AppointmentDB.saveAppointment(id, types.get(aptType), contacts.get(aptContact), location.getText(), ld.toString(), times.get(aptTime))) {
            return true;
        } else {
            errors.add("Database Error");
            return false;
        }
    }
    
    public boolean validateContact(int aptContact) {
        if(aptContact == -1) {
            errors.add("A Contact must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public boolean validateType(int aptType) {
        if(aptType == -1) {
            errors.add("An Appointment Type must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public boolean validateTime(int aptTime) {
        if(aptTime == -1) {
            errors.add("An Appointment Time must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public boolean validateDate(LocalDate aptDate) {
        if(aptDate == null) {
            errors.add("An Appointment Date must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public String displayErrors(){
        String s = "";
        if(errors.size() > 0) {
            for(String err : errors) {
                s = s.concat(err);
            }
            return s;
        } else {
            s = "Database Error";
            return s;
        }
    }
    
    @FXML
    public void handleLocation() {
        String c = contact.getSelectionModel().getSelectedItem().toString();
        if(c.equals("Philip")) {
            location.setText("New York");
        } else if(c.equals("Cricus")) {
            location.setText("London");
        } else if(c.equals("Ragnar")) {
            location.setText("Phoenix");
        }
    }
    
    public void populateCustomerName(String name) {
        customerName.setText(name);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contact.setItems(contacts);
        time.setItems(times);
        type.setItems(types);
        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(
                    empty || 
                    date.getDayOfWeek() == DayOfWeek.SATURDAY || 
                    date.getDayOfWeek() == DayOfWeek.SUNDAY ||
                    date.isBefore(LocalDate.now()));
                if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY || date.isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #ffc4c4;");
                }
            }
        });
    }    
    
}
