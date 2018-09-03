package Presentation.Controller;

import Model.Country;
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

public class AddModifyCountryController implements Initializable, DialogController<Country> {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Presentation.Strings");
    @FXML private Label lblTitle;
    @FXML private Label lblName;
    @FXML private TextField txtName;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    private boolean save;
    private Country country;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblName.setText(STRINGS.getString("name"));
        btnSave.setText(STRINGS.getString("save"));
        btnCancel.setText(STRINGS.getString("cancel"));
        txtName.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= Country.MAX_NAME_LEN ? change : null));
        txtName.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtName, isNameValid(),
                STRINGS.getString("error_invalid_country"), STRINGS.getString("error_invalid_country_body"))));
        save = false;
    } 
    
    @Override
    public void postInit(Country country) {
        this.country = country;
        if(country.getId() == 0) {
            // Add Country
            lblTitle.setText(STRINGS.getString("add_country"));
        } else {
            // Edit Country
            lblTitle.setText(STRINGS.getString("edit_country"));
            txtName.setText(country.getName());
        }
        checkCanSave();
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
    
    @Override
    public Country process() {
        String name = txtName.getText().trim();
        
        if (save && country.getId() == 0) {
            // New country
            return new Country(name);
        } else if (save) {
            // Existing country
            country.setName(name);
            return country;
        } else {
            // Action canceled
            return null;
        }
    }
        
    private void closeWindow() {
        ((Stage) lblTitle.getScene().getWindow()).close();
    }
    
    private boolean isNameValid() {
        return !txtName.getText().isEmpty();
    }
}
