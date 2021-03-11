package views;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.util.Collections;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import utils.Database;

/**
 * FXML Controller class
 */
public class ReportsMainController implements Initializable {

    /**
     * Initializes the controller class.
     * @param event
     */
    private XYChart.Series<String,Float> series;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("views");
    
    @FXML
    private PieChart pieChart;
    
    @FXML
    private TextArea textArea;
    
    @FXML
    private TextArea textArea2;
    
    @FXML
    public void handleBackButton(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    @FXML
    public void handlePieChart() throws SQLException {
        
        pieChart.setData(getChartData());
        pieChart.setTitle(" Customer Appointment ");
        pieChart.setLegendSide(Side.BOTTOM);
    }
    
    @FXML
    public void handleAppointment() throws SQLException {
        
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT appointment.contact, appointment.description, customer.customerName, start, end " +
                    "FROM appointment JOIN customer ON customer.customerId = appointment.customerId " +
                    "GROUP BY appointment.contact, MONTH(start), start";
            ResultSet results = statement.executeQuery(query);
            StringBuilder reportTwoText = new StringBuilder();
            reportTwoText.append(String.format("%1$-25s %2$-25s %3$-25s %4$-25s %5$s \n", 
                    "Consultant", "Appointment", "Customer", "Start", "End"));
            reportTwoText.append(String.join("", Collections.nCopies(110, "-")));
            reportTwoText.append("\n");
            while(results.next()) {
                reportTwoText.append(String.format("%1$-25s %2$-25s %3$-25s %4$-25s %5$s \n", 
                    results.getString("contact"), results.getString("description"), results.getString("customerName"),
                    results.getString("start"), results.getString("end")));
            }
            statement.close();
            textArea2.setText(reportTwoText.toString());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    
    
    @FXML
    public void handleTotal() throws SQLException {
        
        try {
            Statement statement = Database.getConnection().createStatement();
            String query = "SELECT description, MONTHNAME(start) as 'Month', COUNT(*) as 'Total' FROM appointment GROUP BY description, MONTH(start)";
            ResultSet results = statement.executeQuery(query);
            StringBuilder reportOneText = new StringBuilder();
            reportOneText.append(String.format("%1$-55s %2$-55s %3$s \n", "Month", "Appointment Type", "Total"));
            reportOneText.append(String.join("", Collections.nCopies(110, "-")));
            reportOneText.append("\n");
            while(results.next()) {
                reportOneText.append(String.format("%1$-55s %2$-60s %3$d \n", 
                    results.getString("Month"), results.getString("description"), results.getInt("Total")));
            }
            statement.close();
            textArea.setText(reportOneText.toString());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    
    
    
    
    private ObservableList<Data> getChartData()throws SQLException {  
        ObservableList<Data> list = FXCollections.observableArrayList();      
        Statement st = Database.getConnection().createStatement();

        String qry = "SELECT customer.customerName as Name, COUNT(*) as Total FROM customer JOIN appointment " +
                    "ON customer.customerId = appointment.customerId GROUP BY customerName";

        logger.debug(qry);

        ResultSet rs=st.executeQuery(qry);

            while(rs.next()){
                list.add(new PieChart.Data(rs.getString("Name"),Float.parseFloat(rs.getString("Total"))));
            }
        return list; 
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    
    
}
