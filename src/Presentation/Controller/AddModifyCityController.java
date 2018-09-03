package Presentation.Controller;

import Model.City;
import Presentation.InputValidator;
import Presentation.UI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class AddModifyCityController implements Initializable, DialogController<City> {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Presentation.Strings");
    @FXML private Label lblTitle;
    @FXML private Label lblCountry;
    @FXML private Label lblName;
    @FXML private TextField txtName;
    @FXML private TextField txtCountry;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    private boolean save;
    private City city;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblCountry.setText(STRINGS.getString("country"));
        lblName.setText(STRINGS.getString("city"));
        btnSave.setText(STRINGS.getString("save"));
        btnCancel.setText(STRINGS.getString("cancel"));
        txtName.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= City.MAX_NAME_LEN ? change : null));
        txtName.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtName, isNameValid(), 
            STRINGS.getString("error_invalid_city"), STRINGS.getString("error_invalid_city_body"))));
        save = false;
    }
    
    @Override
    public void postInit(City city) {
        this.city = city;
        txtCountry.setText(city.getCountry().getName());
        if(city.getId() == 0) {
            // Add City
            lblTitle.setText(STRINGS.getString("add_city"));
        } else {
            // Edit City
            lblTitle.setText(STRINGS.getString("edit_city"));
            txtName.setText(city.getName());
        }
        checkCanSave();
    }
        
    @Override
    public City process() {
        String name = txtName.getText().trim();
        
        if (save && city.getId() == 0) {
            // New City
            return new City(name, city.getCountry());
        } else if (save) {
            // Existing City
            city.setName(name);
            return city;
        } else {
            // Action canceled
            return null;
        }
    }
    
    @Override
    public void checkCanSave() {
        boolean canSave = isNameValid();
        btnSave.setDisable(!canSave);
    }
    
    @Override
    public void handleSave() {
        save = true;
        closeWindow();
    }
    
    @Override
    public void handleCancel() {
        save = false;
        closeWindow();
    }
    
    private void closeWindow() {
        ((Stage) lblTitle.getScene().getWindow()).close();
    }
       
    private boolean isNameValid() {
        return !txtName.getText().isEmpty();
    }
}
