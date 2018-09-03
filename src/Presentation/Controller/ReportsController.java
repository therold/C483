package Presentation.Controller;

import Data.AppointmentData;
import Data.AppointmentTypeByMonthData;
import Data.CustomerData;
import Model.Appointment;
import Model.AppointmentTypeByMonth;
import Model.Customer;
import Presentation.UI;
import heroldcalendar.Main;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

public class ReportsController implements Initializable {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Presentation.Strings");
    private static final ObservableList<String> REPORTS = FXCollections.observableArrayList(Arrays.asList("Appointment Types by Month", "Schedule by Consultant", "All Customers"));
    private static final ObservableList<Customer> CUSTOMERS = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> APPOINTMENTS = FXCollections.observableArrayList();
    private static final ObservableList<AppointmentTypeByMonth> APPOINTMENTTYPES = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("LL/dd/yyyy");
    private static final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm");
    @FXML private StackPane loading;
    @FXML private Label lblType;
    @FXML private ComboBox<String> cboType;
    @FXML private TableView<AppointmentTypeByMonth> tblTypesByMonth;
    @FXML private TableView<Customer> tblCustomers;
    @FXML private TreeView trSchedule;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblType.setText(STRINGS.getString("report_type"));
        cboType.setItems(REPORTS);
        cboType.getSelectionModel().select(0);
        tblTypesByMonth.setPlaceholder(new Label());
        
        UI.fadeOut(loading);
        Task<Void> loadData = new Task<Void>() {
            @Override
            public Void call() {
                APPOINTMENTS.setAll(AppointmentData.all());
                CUSTOMERS.setAll(CustomerData.all());
                APPOINTMENTTYPES.setAll(AppointmentTypeByMonthData.all());
                Platform.runLater(() -> {
                    fillScheduleReport();
                    UI.fadeIn(loading);
                });
                return null;
            }
        };
        Main.execute(loadData);
    }
    
    public ObservableList<AppointmentTypeByMonth> getAppointmentTypesByMonth() {
        return APPOINTMENTTYPES;
    }

    public ObservableList<Customer> getCustomers() {
        return CUSTOMERS;
    }

    
    public void handleTypeChanged() {
        switch (cboType.getValue()) {
            case "Appointment Types by Month":
                tblTypesByMonth.setVisible(true);
                trSchedule.setVisible(false);
                tblCustomers.setVisible(false);
                break;
            case "Schedule by Consultant":
                tblTypesByMonth.setVisible(false);
                trSchedule.setVisible(true);
                tblCustomers.setVisible(false);
                break;
            case "All Customers":
                tblTypesByMonth.setVisible(false);
                trSchedule.setVisible(false);
                tblCustomers.setVisible(true);
                break;
        }
    }
    
    private void fillScheduleReport() {
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);
        
        for (Appointment appointment : APPOINTMENTS) {
            String display = appointment.getStart().format(TIMEFORMAT) + " - " +
                appointment.getEnd().format(TIMEFORMAT) + ": '" +
                appointment.getTitle() + "' with " + appointment.getCustomer().getName();
            TreeItem<String> appointmentItem = new TreeItem<>(display);
            boolean found = false;
            
            for (TreeItem<String> user : root.getChildren()) {
                if (user.getValue().contentEquals(appointment.getUser())){
                    found = true;
                    boolean foundDate = false;
                    
                    for (TreeItem<String> date : user.getChildren()) {
                        if (date.getValue().contentEquals(appointment.getStart().format(DATEFORMAT))) {
                            foundDate = true;
                            date.getChildren().add(appointmentItem);
                            break;
                        }
                    }
                    if (!foundDate) {
                        TreeItem<String> date = new TreeItem<>(appointment.getStart().format(DATEFORMAT));
                        user.getChildren().add(date);
                        date.getChildren().add(appointmentItem);
                        break;
                    }
                }
            }
            if (!found) {
                TreeItem<String> user = new TreeItem<>(appointment.getUser());
                TreeItem<String> date = new TreeItem<>(appointment.getStart().format(DATEFORMAT));
                root.getChildren().add(user);
                user.getChildren().add(date);
                date.getChildren().add(appointmentItem);
            }
        }
        
        trSchedule.setRoot(root);
    }
}
