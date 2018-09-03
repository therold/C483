package Presentation.Controller;

import Data.CityData;
import Data.CountryData;
import Data.CustomerData;
import Model.City;
import Model.Country;
import Model.Customer;
import Presentation.InputValidator;
import Presentation.UI;
import heroldcalendar.Main;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AddModifyCustomerController implements Initializable {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Presentation.Strings");
    private static final ObservableList<Customer> CUSTOMERS = FXCollections.observableArrayList();
    private static final ObservableList<Country> COUNTRIES = FXCollections.observableArrayList();
    private static final ObservableList<City> CITIES = FXCollections.observableArrayList();
    private static final ObservableList<City> ACTIVECITIES = FXCollections.observableArrayList();
    private static enum MODE { EDITING, VIEWING };
    @FXML private Label lblTitle;
    @FXML private Label lblName;
    @FXML private Label lblAddress;
    @FXML private Label lblAddress2;
    @FXML private Label lblCountry;
    @FXML private Label lblCity;
    @FXML private Label lblPostalCode;
    @FXML private Label lblPhone;
    @FXML private TextField txtName;
    @FXML private TextField txtAddress;
    @FXML private TextField txtAddress2;
    @FXML private TextField txtCountry;
    @FXML private TextField txtCity;
    @FXML private TextField txtPostalCode;
    @FXML private TextField txtPhone;
    @FXML private Button btnAddCountry;
    @FXML private Button btnEditCountry;
    @FXML private Button btnDeleteCountry;
    @FXML private Button btnAddCity;
    @FXML private Button btnEditCity;
    @FXML private Button btnDeleteCity;
    @FXML private Button btnSave;
    @FXML private Button btnModify;
    @FXML private Button btnDelete;
    @FXML private Button btnCancel;
    @FXML private Button btnAddCustomer;
    @FXML private ComboBox<Country> cboCountry;
    @FXML private ComboBox<City> cboCity;
    @FXML private ListView<Customer> lvCustomer;
    @FXML private StackPane loading;
    private Customer customer;
    private Country selectedCountry;
    private City selectedCity;
    private MODE mode = MODE.VIEWING;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblTitle.setText(STRINGS.getString("manage_customer"));
        lblName.setText(STRINGS.getString("name"));
        lblAddress.setText(STRINGS.getString("address"));
        lblAddress2.setText(STRINGS.getString("address"));
        lblCountry.setText(STRINGS.getString("country"));
        lblCity.setText(STRINGS.getString("city"));
        lblPostalCode.setText(STRINGS.getString("postal_code"));
        lblPhone.setText(STRINGS.getString("phone"));
        btnAddCustomer.setText(STRINGS.getString("add_customer"));
        btnSave.setText(STRINGS.getString("save"));
        btnModify.setText(STRINGS.getString("edit"));
        btnDelete.setText(STRINGS.getString("delete"));
        btnCancel.setText(STRINGS.getString("close"));
        btnAddCountry.setText(STRINGS.getString("add"));
        btnEditCountry.setText(STRINGS.getString("edit"));
        btnAddCity.setText(STRINGS.getString("add"));
        btnEditCity.setText(STRINGS.getString("edit"));

        cboCountry.setItems(COUNTRIES);
        cboCity.setItems(ACTIVECITIES);
        lvCustomer.setItems(CUSTOMERS);
        lvCustomer.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> handleCustomerSelected(nv));
        
        txtName.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= Customer.MAX_NAME_LEN ? change : null));
        txtAddress.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= Customer.MAX_ADDRESS_LEN ? change : null));
        txtAddress2.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= Customer.MAX_ADDRESS2_LEN ? change : null));
        txtPostalCode.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= Customer.MAX_POSTALCODE_LEN ? change : null));
        txtPhone.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= Customer.MAX_PHONE_LEN ? change : null));
        
        txtName.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtName, isNameValid(),
            STRINGS.getString("error_invalid_customer_name"), STRINGS.getString("error_invalid_customer_name_body"))));
        txtAddress.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtAddress, isAddressValid(),
            STRINGS.getString("error_invalid_customer_address"), STRINGS.getString("error_invalid_customer_address_body"))));
        txtPostalCode.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtPostalCode, isPostalCodeValid(),
            STRINGS.getString("error_invalid_customer_postalcode"), STRINGS.getString("error_invalid_customer_postalcode_body"))));
        txtPhone.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtPhone, isPhoneValid(),
            STRINGS.getString("error_invalid_customer_phone"), STRINGS.getString("error_invalid_customer_phone_body"))));
        cboCountry.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(cboCountry, isCountryValid(),
            STRINGS.getString("error_invalid_customer_country"), STRINGS.getString("error_invalid_customer_country_body"))));
        cboCity.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(cboCity, isCityValid(),
            STRINGS.getString("error_invalid_customer_city"), STRINGS.getString("error_invalid_customer_city_body"))));

        UI.fadeOut(loading);
        Task<Void> loadData = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                List customers = CustomerData.all();
                List countries = CountryData.all();
                List cities = CityData.all();
                Platform.runLater(() -> {
                    CUSTOMERS.setAll(customers);
                    COUNTRIES.setAll(countries);
                    CITIES.setAll(cities);
                    loading.getParent().getScene().getWindow().sizeToScene();
                    UI.fadeIn(loading);
                });
                return null;
            }
        };
        Main.execute(loadData);
        checkCanSave();
    }
    
    private void handleCustomerSelected(Customer customer) {
        if(customer != null) {
            this.customer = customer;
            txtName.setText(customer.getName());
            txtAddress.setText(customer.getAddress());
            txtAddress2.setText(customer.getAddress2());
            txtCountry.setText(customer.getCity().getCountry().getName());
            txtCity.setText(customer.getCity().getName());
            txtPhone.setText(customer.getPhone());
            txtPostalCode.setText(customer.getPostalCode());
            btnModify.setDisable(false);
            btnDelete.setDisable(false);
        } else {
            this.customer = null;
            txtName.setText("");
            txtAddress.setText("");
            txtAddress2.setText("");
            txtCountry.setText("");
            txtCity.setText("");
            txtPhone.setText("");
            txtPostalCode.setText("");
            btnModify.setDisable(true);
            btnDelete.setDisable(true);
        }   
    }
    
    public void handleCountryChanged() {
        boolean isCountrySelected = (cboCountry.getSelectionModel().getSelectedItem() != null);
        btnEditCountry.setDisable(!isCountrySelected);
        btnDeleteCountry.setDisable(!isCountrySelected);
        cboCity.setDisable(!isCountrySelected);
        btnAddCity.setDisable(!isCountrySelected);
        btnEditCity.setDisable(true);
        btnDeleteCity.setDisable(true);
        cboCity.getSelectionModel().clearSelection();
        cboCity.setValue(null);
        selectedCountry = cboCountry.getSelectionModel().getSelectedItem();
        if (isCountrySelected) {
            cboCountry.setStyle("");
            ACTIVECITIES.setAll(CITIES.filtered(city -> city.getCountry().equals(selectedCountry)));
        }
        checkCanSave();
    }
    
    public void handleCityChanged() {
        boolean isCitySelected = (cboCity.getSelectionModel().getSelectedItem() != null);
        btnEditCity.setDisable(!isCitySelected);
        btnDeleteCity.setDisable(!isCitySelected);
        selectedCity = cboCity.getSelectionModel().getSelectedItem();
        if (isCitySelected) {
            cboCity.setStyle("");
        }
        checkCanSave();
    }
    
    public void handleAddCountry() {
        ProcessCountry p = new ProcessCountry();
        UI.fadeOut(loading);
        UI.<Country>showDialog("/Presentation/View/AddModifyCountry.fxml", STRINGS.getString("add_country"), new Country())
            .ifPresent(country -> p.setSave(country));
        Main.execute(p);
    }
    
    public void handleModifyCountry() {
        ProcessCountry p = new ProcessCountry();
        UI.fadeOut(loading);
        UI.<Country>showDialog("/Presentation/View/AddModifyCountry.fxml", STRINGS.getString("edit_country"), selectedCountry)
            .ifPresent(country -> p.setSave(country));
        Main.execute(p);
    }
    
    public void handleDeleteCountry() {
        ProcessCountry p = new ProcessCountry();
        UI.fadeOut(loading);
        UI.showAlert(STRINGS.getString("delete_country"), STRINGS.getString("delete_country_body"), selectedCountry.getName())
            .filter(button -> button.equals(ButtonType.OK))
            .ifPresent(result -> p.setDelete(selectedCountry));
        Main.execute(p);
    }
    
    public void handleAddCity() {
        ProcessCity p = new ProcessCity();
        UI.fadeOut(loading);
        UI.<City>showDialog("/Presentation/View/AddModifyCity.fxml", STRINGS.getString("add_city"), new City("", selectedCountry))
            .ifPresent(city -> p.setSave(city));
        Main.execute(p);
    }
    
    public void handleModifyCity() {
        ProcessCity p = new ProcessCity();
        UI.fadeOut(loading);
        UI.<City>showDialog("/Presentation/View/AddModifyCity.fxml", STRINGS.getString("edit_city"), selectedCity)
            .ifPresent(city -> p.setSave(city));
        Main.execute(p);
    }
    
    public void handleDeleteCity() {
        ProcessCity p = new ProcessCity();
        UI.fadeOut(loading);
        UI.showAlert(STRINGS.getString("delete_city"), STRINGS.getString("delete_city_body"), selectedCity.getName())
            .filter(button -> button.equals(ButtonType.OK))
            .ifPresent(result -> p.setDelete(selectedCity));
        Main.execute(p);
    }
    
    public void handleAddCustomer() {
        this.customer = new Customer();
        lblTitle.setText(STRINGS.getString("add_customer"));
        txtName.setText("");
        txtAddress.setText("");
        txtAddress2.setText("");
        txtCountry.setText("");
        txtCity.setText("");
        txtPostalCode.setText("");
        txtPhone.setText("");
        cboCountry.getSelectionModel().select(null);
        cboCity.getSelectionModel().select(null);
        showEditView();
    }
    
    public void handleModifyCustomer() {
        lblTitle.setText(STRINGS.getString("edit_customer"));
        cboCountry.getSelectionModel().select(customer.getCity().getCountry());
        cboCity.getSelectionModel().select(customer.getCity());
        selectedCountry = customer.getCity().getCountry();
        selectedCity = customer.getCity();
        showEditView();
    }
    
    public void handleDeleteCustomer() {
        ProcessCustomer p = new ProcessCustomer();
        UI.showAlert(STRINGS.getString("delete_customer"), STRINGS.getString("delete_customer_body"))
            .filter(response -> response.equals(ButtonType.OK))
            .ifPresent(response -> p.setDelete(customer));
        Main.execute(p);
    }
    
    public void handleSaveCustomer() {
        ProcessCustomer p = new ProcessCustomer();
        UI.fadeOut(loading);
        process().ifPresent(customer -> p.setSave(customer));
        Main.execute(p);
    }
    
    public void handleCancel() {
        if(mode == MODE.EDITING) {
            showDefaultView();
        } else if (mode == MODE.VIEWING) {
             closeWindow();
        }
    }

    public Optional<Customer> process() {
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String address2 = txtAddress2.getText().trim();
        String phone = txtPhone.getText().trim();
        String postalCode = txtPostalCode.getText().trim();
        
        if (customer.getId() == 0) {
            // New Customer
            return Optional.of(new Customer(name, address, address2, selectedCity, postalCode, phone));
        } else {
            // Existing Customer
            customer.setName(name);
            customer.setAddress(address);
            customer.setAddress2(address2);
            customer.setCity(selectedCity);
            customer.setPhone(phone);
            customer.setPostalCode(postalCode);
            return Optional.of(customer);
        }
    }
    
    private void showEditView() {
        mode = MODE.EDITING;
        lvCustomer.setDisable(true);
        btnAddCustomer.setDisable(true);
        txtName.setDisable(false);
        txtAddress.setDisable(false);
        txtAddress2.setDisable(false);
        txtCountry.setVisible(false);
        
        cboCountry.setVisible(true);
        btnAddCountry.setVisible(true);
        btnEditCountry.setVisible(true);
        btnDeleteCountry.setVisible(true);
        txtCity.setVisible(false);
        
        cboCity.setVisible(true);
        btnAddCity.setVisible(true);
        btnEditCity.setVisible(true);
        btnDeleteCity.setVisible(true);
        txtPhone.setDisable(false);
        txtPostalCode.setDisable(false);
        btnModify.setVisible(false);
        btnDelete.setVisible(false);
        btnSave.setVisible(true);
        btnCancel.setText(STRINGS.getString("cancel"));
        checkCanSave();
    }
    
    private void showDefaultView() {
        lblTitle.setText(STRINGS.getString("manage_customer"));
        mode = MODE.VIEWING;
        lvCustomer.setDisable(false);
        btnAddCustomer.setDisable(false);
        txtName.setDisable(true);
        txtAddress.setDisable(true);
        txtAddress2.setDisable(true);
        txtCountry.setVisible(true);
        cboCountry.setVisible(false);
        btnAddCountry.setVisible(false);
        btnEditCountry.setVisible(false);
        btnDeleteCountry.setVisible(false);
        txtCity.setVisible(true);
        cboCity.setVisible(false);
        btnAddCity.setVisible(false);
        btnEditCity.setVisible(false);
        btnDeleteCity.setVisible(false);
        txtPhone.setDisable(true);
        txtPostalCode.setDisable(true);
        btnModify.setVisible(true);
        btnDelete.setVisible(true);
        btnSave.setVisible(false);
        txtName.setText("");
        txtName.setStyle("");
        txtAddress.setText("");
        txtAddress.setStyle("");
        txtAddress2.setText("");
        txtAddress2.setStyle("");
        txtCountry.setText("");
        txtCountry.setStyle("");
        txtCity.setText("");
        txtCity.setStyle("");
        txtPhone.setText("");
        txtPhone.setStyle("");
        txtPostalCode.setText("");
        txtPostalCode.setStyle("");
        selectedCountry = null;
        selectedCity = null;
        btnCancel.setText(STRINGS.getString("close"));
    }
    
    private void closeWindow() {
        ((Stage) lblTitle.getScene().getWindow()).close();
    }
    
    private boolean isNameValid() {
        return !txtName.getText().isEmpty();
    }
    
    private boolean isAddressValid() {
        return !txtAddress.getText().isEmpty();
    }
    
    private boolean isCountryValid() {
        return (selectedCountry != null);
    }
    
    private boolean isCityValid() {
        return (selectedCity != null);
    }
    
    private boolean isPostalCodeValid() {
        return !txtPostalCode.getText().isEmpty();
    }
    
    private boolean isPhoneValid() {
        return !txtPhone.getText().isEmpty();
    }
    
    public void checkCanSave() {
        boolean canSave = (isNameValid() && isAddressValid() && isCountryValid() &&
                isCityValid() && isPostalCodeValid() && isPhoneValid());
        btnSave.setDisable(!canSave);
    }
    
    private class ProcessCountry extends Task<Void> {
        private Country toSave;
        private Country toDelete;
        
        public void setSave(Country country) {
            this.toSave = country;
        }
        
        public void setDelete (Country country) {
            this.toDelete = country;
        }
        
        @Override
        public Void call() {
            if (toSave != null) {
                int id = CountryData.save(toSave);
                toSave.setId(id);
                selectedCountry = toSave;
                List countries = CountryData.all();
                Platform.runLater(() -> {
                    COUNTRIES.setAll(countries);
                    cboCountry.getSelectionModel().select(toSave);
                    cboCountry.setStyle("");
                });
            } else if (toDelete != null) {
                if (CountryData.isUsed(toDelete.getId())) {
                    Platform.runLater(() -> UI.showAlert(
                        STRINGS.getString("delete_country_inuse"), 
                        STRINGS.getString("delete_country_inuse_body")));
                } else {
                    CountryData.delete(toDelete.getId());   
                    Platform.runLater(() -> {
                        COUNTRIES.remove(toDelete);
                        cboCountry.getSelectionModel().clearSelection();
                        cboCountry.setValue(null);
                    });
                }
            }
            Platform.runLater(() -> UI.fadeIn(loading));
            return null;
        };
    }

    private class ProcessCity extends Task<Void> {
        private City toSave;
        private City toDelete;
        
        public void setSave(City city) {
            this.toSave = city;
        }
        
        public void setDelete (City city) {
            this.toDelete = city;
        }
        
        @Override
        public Void call() {
            if (toSave != null) {
                int id = CityData.save(toSave);
                toSave.setId(id);
                selectedCity = toSave;
                List cities = CityData.all();
                Platform.runLater(() -> {
                    CITIES.setAll(cities);
                    ACTIVECITIES.setAll(CITIES.filtered(city -> city.getCountry().equals(selectedCountry)));
                    cboCity.getSelectionModel().select(toSave);
                    cboCity.setStyle("");
                });
            } else if (toDelete != null) {
                if (CityData.isUsed(toDelete.getId())) {
                    Platform.runLater(() -> UI.showAlert(
                            STRINGS.getString("delete_city_inuse"), 
                            STRINGS.getString("delete_city_inuse_body")));
                } else {
                    CityData.delete(toDelete.getId());
                    Platform.runLater(() -> {
                        CITIES.remove(toDelete);                    
                        ACTIVECITIES.remove(toDelete);
                        cboCity.getSelectionModel().clearSelection();
                        cboCity.setValue(null);
                    });
                }
            }
            Platform.runLater(() -> UI.fadeIn(loading));
            return null;
        };
    }
    
    private class ProcessCustomer extends Task<Void> {
        private Customer toSave;
        private Customer toDelete;
        
        public void setSave(Customer customer) {
            this.toSave = customer;
        }
        
        public void setDelete (Customer customer) {
            this.toDelete = customer;
        }
        
        @Override
        public Void call() {
            if (toSave != null) {
                CustomerData.save(toSave);
                List customers = CustomerData.all();
                Platform.runLater(() -> {
                    CUSTOMERS.setAll(customers);
                    showDefaultView();
                });
            } else if (toDelete != null) {
                if (CustomerData.isUsed(toDelete.getId())) {
                    Platform.runLater(() -> UI.showAlert(
                            STRINGS.getString("delete_customer_inuse"), 
                            STRINGS.getString("delete_customer_inuse_body")));
                } else {
                    CustomerData.delete(customer.getId());   
                }
            }
            Platform.runLater(() ->  {
                CUSTOMERS.remove(customer);
                UI.fadeIn(loading);
            });
            return null;
        };
    }
}
