package views;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.UserDB;
import org.apache.log4j.Logger;

public class LoginController implements Initializable {
    
    @FXML
    private TextField usernameTxt;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private PasswordField passwordTxt;
    
    @FXML
    private AnchorPane errorPane;
    
    @FXML
    private Label usernameLabel;
    
    @FXML
    private Label password_Label;
    
    @FXML
    private Label passwordLabel;
    
    @FXML
    private Label mainMessage;
    
    @FXML 
    private Label language;
    
    @FXML
    private Button loginButton;
    
    private String errorHeader;
    private String errorTitle;
    private String errorText;
    
    private static final Logger logger = Logger.getLogger("views");
    
    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();
        boolean validUser = UserDB.login(username, password);
        if(validUser) {
            ((Node) (event.getSource())).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(errorTitle);
            alert.setHeaderText(errorHeader);
            alert.setContentText(errorText);
            alert.showAndWait();
        }
    }
    
    @FXML
    public void changeLanguageFrench(ActionEvent event){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("languages/login_fr", locale);
        titleLabel.setText(rb.getString("title"));
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        mainMessage.setText(rb.getString("message"));
        language.setText(rb.getString("language"));
        errorHeader = rb.getString("errorheader");
        errorTitle = rb.getString("errortitle");
        errorText = rb.getString("errortext");
    }
    
     @FXML
    public void changeLanguageEnglish(ActionEvent event){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("languages/login_en", locale);
        titleLabel.setText(rb.getString("title"));
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        mainMessage.setText(rb.getString("message"));
        language.setText(rb.getString("language"));
        errorHeader = rb.getString("errorheader");
        errorTitle = rb.getString("errortitle");
        errorText = rb.getString("errortext");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Locale locale = Locale.getDefault();
        rb = ResourceBundle.getBundle("languages/login", locale);
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        mainMessage.setText(rb.getString("message"));
        language.setText(rb.getString("language"));
        errorHeader = rb.getString("errorheader");
        errorTitle = rb.getString("errortitle");
        errorText = rb.getString("errortext");
    }    
    
}
