package Presentation.Controller;

import Data.AppointmentData;
import Data.CustomerData;
import Model.Appointment;
import Model.AppointmentType;
import Model.Customer;
import Presentation.AppointmentCell;
import Presentation.InputValidator;
import Presentation.UI;
import heroldcalendar.Main;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AddModifyAppointmentController implements Initializable, DialogController<Appointment> {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Presentation.Strings");
    private static final ObservableList<Customer> CUSTOMERS = FXCollections.observableArrayList();
    private static final ObservableList<AppointmentType> TYPES = FXCollections.observableArrayList(AppointmentType.getTypes());
    private static final LocalTime BUSINESSSTART = LocalTime.of(8, 0);
    private static final LocalTime BUSINESSEND = LocalTime.of(17, 0);
    @FXML private Label lblUser;
    @FXML private Label lblTitle;
    @FXML private Label lblCustomer;
    @FXML private Label lblName;
    @FXML private Label lblDescription;
    @FXML private Label lblLocation;
    @FXML private Label lblContact;
    @FXML private Label lblType;
    @FXML private Label lblStart;
    @FXML private Label lblEnd;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private ComboBox<Customer> cboCustomer;
    @FXML private TextField txtUser;
    @FXML private TextField txtName;
    @FXML private TextField txtLocation;
    @FXML private TextField txtContact;
    @FXML private ComboBox<AppointmentType> cboType;
    @FXML private TextField txtStartTime;
    @FXML private TextField txtEndTime;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private StackPane loading;
    private Appointment appointment;
    private boolean save = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblCustomer.setText(STRINGS.getString("customer"));
        lblUser.setText(STRINGS.getString("consultant"));
        lblName.setText(STRINGS.getString("title"));
        lblDescription.setText(STRINGS.getString("description"));
        lblLocation.setText(STRINGS.getString("location"));
        lblContact.setText(STRINGS.getString("contact"));
        lblType.setText(STRINGS.getString("type"));
        lblStart.setText(STRINGS.getString("start"));
        lblEnd.setText(STRINGS.getString("end"));
        btnSave.setText(STRINGS.getString("save"));
        btnCancel.setText(STRINGS.getString("cancel"));
        
        cboType.setItems(TYPES);
        cboCustomer.setItems(CUSTOMERS);
        dpEndDate.valueProperty().bindBidirectional(dpStartDate.valueProperty());
        txtStartTime.setTextFormatter(new TextFormatter<>(UI.timeFilter)); 
        txtEndTime.setTextFormatter(new TextFormatter<>(UI.timeFilter)); 
        txtStartTime.addEventFilter(KeyEvent.KEY_TYPED, noKeySelect);
        txtStartTime.addEventFilter(MouseEvent.MOUSE_DRAGGED, noMouseSelect);
        txtEndTime.addEventFilter(KeyEvent.KEY_TYPED, noKeySelect);
        txtEndTime.addEventFilter(MouseEvent.MOUSE_DRAGGED, noMouseSelect);
        txtName.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().length() <= Appointment.MAX_TITLE_LEN ? change : null));
        
        txtName.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtName, isNameValid(),
            STRINGS.getString("error_invalid_appointment_name"), STRINGS.getString("error_invalid_appointment_name_body"))));
        txtLocation.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtLocation, isLocationValid(),
            STRINGS.getString("error_invalid_appointment_location"), STRINGS.getString("error_invalid_appointment_location_body"))));
        txtContact.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtContact, isContactValid(),
            STRINGS.getString("error_invalid_appointment_contact"), STRINGS.getString("error_invalid_appointment_contact_body"))));
        txtStartTime.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtStartTime, isStartTimeValid(),
            getStartTimeErrorMessage().get(0), getStartTimeErrorMessage().get(1))));
        txtEndTime.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtEndTime, isEndTimeValid(),
            getEndTimeErrorMessage().get(0), getEndTimeErrorMessage().get(1))));
        txtDescription.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(txtDescription, isDescriptionValid(),
            STRINGS.getString("error_invalid_appointment_description"), STRINGS.getString("error_invalid_appointment_description_body"))));
        dpStartDate.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(dpStartDate, isStartDateValid(),
            STRINGS.getString("error_invalid_appointment_startdate"), STRINGS.getString("error_invalid_appointment_startdate_body"))));
        dpEndDate.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(dpEndDate, isEndDateValid(),
            STRINGS.getString("error_invalid_appointment_enddate"), STRINGS.getString("error_invalid_appointment_enddate_body"))));
        cboType.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(cboType, isTypeValid(),
            STRINGS.getString("error_invalid_appointment_type"), STRINGS.getString("error_invalid_appointment_type_body"))));
        cboCustomer.focusedProperty().addListener((o, ov, nv) -> UI.lostFocus(nv, new InputValidator(cboCustomer, isCustomerValid(),
            STRINGS.getString("error_invalid_appointment_customer"), STRINGS.getString("error_invalid_appointment_customer_body"))));  
        cboType.setButtonCell(new AppointmentCell());
        cboType.setCellFactory((ListView<AppointmentType> a) -> new AppointmentCell());
        
        UI.fadeOut(loading);
        Task<Void> loadData = new Task<Void>() {
            @Override
            public Void call() {
                CUSTOMERS.setAll(CustomerData.all());
                Platform.runLater(() -> UI.fadeIn(loading));
                return null;
            }
        };
        Main.execute(loadData);
    }
    
    @Override
    public void postInit(Appointment appointment) {
        this.appointment = appointment;
        if (appointment.getId() == 0) {
            // New Appointment
            lblTitle.setText(STRINGS.getString("add_appointment"));
            txtUser.setText(Main.getUser().getName());
            txtStartTime.setText("00:00");
            txtEndTime.setText("00:00");
        } else {
            // Edit existing Appointment
            lblTitle.setText(STRINGS.getString("edit_appointment"));
            txtUser.setText(appointment.getUser());
            cboCustomer.getSelectionModel().select(appointment.getCustomer());
            txtName.setText(appointment.getTitle());
            txtLocation.setText(appointment.getLocation());
            txtContact.setText(appointment.getContact());
            cboType.getSelectionModel().select(AppointmentType.find(appointment.getType()));
            dpStartDate.setValue(appointment.getLocalStart().toLocalDate());
            txtStartTime.setText(appointment.getLocalStart().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            txtEndTime.setText(appointment.getLocalEnd().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            txtDescription.setText(appointment.getDescription());
        }
        checkCanSave();
    }

    @Override
    public void checkCanSave() {
        boolean canSave = (isCustomerValid() && isNameValid() && isLocationValid() &&
            isContactValid() &&isTypeValid() &&isStartTimeValid() && isEndTimeValid() &&
            isDescriptionValid() && isStartDateValid() && isEndDateValid());
        btnSave.setDisable(!canSave);
    }
    
    @Override
    public Appointment process() {
        String name = txtName.getText().trim();
        String location = txtLocation.getText().trim();
        String contact = txtContact.getText().trim();
        String description = txtDescription.getText();    
        if (save && appointment.getId() == 0) {
            // New appointment
            return new Appointment(getSelectedCustomer(), name, description, location, contact, getSelectedType(), getStart(), getEnd(), Main.getUser().getName());
        } else if (save) {
            // Editing existing appointment
            appointment.setTitle(name);
            appointment.setLocation(location);
            appointment.setContact(contact);
            appointment.setDescription(description);
            appointment.setCustomer(getSelectedCustomer());
            appointment.setType(getSelectedType());
            appointment.setStart(getStart());
            appointment.setEnd(getEnd());
            return appointment;
        } else {
            // Action canceled
            return null;
        }
    }
    
    @Override
    public void handleSave() {
        ZonedDateTime start = getStart().plusSeconds(1);
        ZonedDateTime end = getEnd().minusSeconds(1);
        if (AppointmentData.isOverlapping(start, end, Main.getUser().getName(), appointment.getId())) {
            UI.showError(STRINGS.getString("error_invalid_appointment_date_overlapping"), STRINGS.getString("error_invalid_appointment_date_overlapping_body"));
        } else if (start.isAfter(end)) {
            UI.showError(STRINGS.getString("error_invalid_appointment_end_before_start"), STRINGS.getString("error_invalid_appointment_end_before_start_body"));
        } else {
            save = true;
            closeWindow();
        }
    }
    
    @Override
    public void handleCancel() {
        save = false;
        closeWindow();
    }
    
    private void closeWindow() {
        ((Stage) lblTitle.getScene().getWindow()).close();
    }
    
    private Customer getSelectedCustomer() {
        return cboCustomer.getSelectionModel().getSelectedItem();
    }
    
    private String getSelectedType() {
        AppointmentType type = cboType.getSelectionModel().getSelectedItem();
        return type != null ? type.getName() : null;
    }
    
    private LocalDate getStartDate() {
        return dpStartDate.getValue();
    }
    
    private LocalDate getEndDate() {
        return dpEndDate.getValue();
    }
    
    private LocalTime getStartTime() {
        return LocalTime.parse(txtStartTime.getText());
    }
    
    private LocalTime getEndTime() {
        return LocalTime.parse(txtEndTime.getText());
    }
    
    private ZonedDateTime getStart() {
        return getStartDate() != null ? ZonedDateTime.of(getStartDate(), getStartTime(), 
                    ZoneOffset.systemDefault()).withZoneSameInstant(ZoneOffset.UTC) : null;
    }

    private ZonedDateTime getEnd() {
        return getEndDate() != null ? ZonedDateTime.of(getEndDate(), getEndTime(), 
                    ZoneOffset.systemDefault()).withZoneSameInstant(ZoneOffset.UTC) : null;
    }
    
    private boolean isCustomerValid() {
        return (getSelectedCustomer() != null);
    }

    private boolean isNameValid() {
        return !txtName.getText().isEmpty();
    }
    
    private boolean isLocationValid() {
        return !txtLocation.getText().isEmpty();
    }
    
    private boolean isContactValid() {
        return !txtContact.getText().isEmpty();
    }
    
    private boolean isTypeValid() {
        return (getSelectedType() != null);
    }
    
    private boolean isStartTimeValid() {
        LocalTime start = getStartTime();
        return (start.isAfter(BUSINESSSTART.minusSeconds(1)) && start.isBefore(BUSINESSEND.plusSeconds(1)));
    }
    
    private List<String> getStartTimeErrorMessage() {
        LocalTime start = getStartTime();
        if (start.isBefore(BUSINESSSTART)) {
            return Arrays.asList(STRINGS.getString("error_invalid_appointment_starttime"), STRINGS.getString("error_invalid_appointment_starttime_before_body"));
        } else if (start.isAfter(BUSINESSEND)) {
            return Arrays.asList(STRINGS.getString("error_invalid_appointment_starttime"), STRINGS.getString("error_invalid_appointment_starttime_after_body"));
        }
        return Arrays.asList("", ""); 
    }
    
    private boolean isEndTimeValid() {
        LocalTime end = getEndTime();
        return (end.isAfter(BUSINESSSTART.minusSeconds(1)) && end.isBefore(BUSINESSEND.plusSeconds(1)));
    }

    private List<String> getEndTimeErrorMessage() {
        LocalTime end = getEndTime();
        if (end.isBefore(BUSINESSSTART)) {
            return Arrays.asList(STRINGS.getString("error_invalid_appointment_endtime"), STRINGS.getString("error_invalid_appointment_endtime_before_body"));
        } else if (end.isAfter(BUSINESSEND)) {
            return Arrays.asList(STRINGS.getString("error_invalid_appointment_endtime"), STRINGS.getString("error_invalid_appointment_endtime_after_body"));
        }
        return Arrays.asList("", "");
    }
    
    private boolean isDescriptionValid() {
        return !txtDescription.getText().isEmpty();
    }
    
    private boolean isStartDateValid() {
        return (dpStartDate.getValue() != null);
    }
    
    private boolean isEndDateValid() {
        return (dpEndDate.getValue() != null);
    }
    
    private final EventHandler<MouseEvent> noMouseSelect = (MouseEvent event) -> {
        TextField source = (TextField) event.getSource();
        if (!source.getSelectedText().isEmpty()) {
            source.deselect();
        }
    };
    
    private final EventHandler<KeyEvent> noKeySelect = (KeyEvent event) -> {
        TextField source = (TextField) event.getSource();
        if (!source.getSelectedText().isEmpty()) {
            source.deselect();
        }
    };
}
