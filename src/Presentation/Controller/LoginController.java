package Presentation.Controller;

import Data.Login;
import Data.Logger;
import Data.UserNotFoundException;
import Model.User;
import Presentation.UI;
import heroldcalendar.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class LoginController implements Initializable {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Presentation.Strings");
    @FXML StackPane loading;
    @FXML ImageView imgLoading;
    @FXML TextField txtUser;
    @FXML PasswordField txtPass;
    @FXML Label lblLoginTitle;
    @FXML Label lblUser;
    @FXML Label lblPass;
    @FXML Button btnLogin;
    @FXML Button btnCancel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblLoginTitle.setText(STRINGS.getString("login_title"));
        lblUser.setText(STRINGS.getString("label_username"));
        lblPass.setText(STRINGS.getString("label_password"));
        btnLogin.setText(STRINGS.getString("login"));
        btnCancel.setText(STRINGS.getString("cancel"));
        imgLoading.visibleProperty().bind(loading.focusedProperty());
    }    
    
    @FXML
    private void handleLogin() {
        String username = txtUser.getText();
        String password = txtPass.getText();
        UI.fadeOut(loading);
        PerformLogin login = new PerformLogin(username, password);
        Main.execute(login);
    }
    
    @FXML
    private void handleCancel() {
        Main.getPrimaryStage().close();
    }
    
    private class PerformLogin extends Task<Void> {
        private final String username;
        private final String password;
        
        public PerformLogin(String username, String password){
            this.username = username;
            this.password = password;
        }
        
        @Override
        public Void call() {
            try (Login login = new Login(username, password)) {
                User user = login.login(); // throws UserNotFoundException if user/pass doesn't match
                Logger.loginSuccessful(user.getName());
                Platform.runLater(() -> UI.loadMainStage(user));
            } catch (UserNotFoundException e) {
                Logger.loginFailed(username);
                Platform.runLater(() -> {
                    UI.showError(STRINGS.getString("error_login_failed_header"), STRINGS.getString("error_login_failed_body"))
                        .filter(response -> response == ButtonType.OK)
                        .ifPresent(response -> txtUser.requestFocus());
                    UI.fadeIn(loading);
                });
            }
            return null;
        }
    };
}
