package Presentation.Controller;

import Data.AppointmentData;
import Model.Appointment;
import Presentation.CalendarCellFactory;
import Presentation.UI;
import heroldcalendar.Main;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class MainController implements Initializable {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Presentation.Strings");
    private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("LL/dd/yyyy");
    private static final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final ArrayList<ListView> WEEK = new ArrayList<>();
    private static final ArrayList<ListView> MONTH = new ArrayList<>();
    private static enum VIEW { byWeek, byMonth };
    @FXML private StackPane loading;
    @FXML private Label lblTitle;
    @FXML private Label lblView;
    @FXML private TextField txtDateOf;
    @FXML private RadioButton rbWeek;
    @FXML private RadioButton rbMonth;
    @FXML private TableView<Appointment> tblWeek;
    @FXML private TableColumn tbcSunday;
    @FXML private TableColumn tbcMonday;
    @FXML private TableColumn tbcTuesday;
    @FXML private TableColumn tbcWednesday;
    @FXML private TableColumn tbcThursday;
    @FXML private TableColumn tbcFriday;
    @FXML private TableColumn tbcSaturday;
    @FXML private ListView lvSunday;
    @FXML private ListView lvMonday;
    @FXML private ListView lvTuesday;
    @FXML private ListView lvWednesday;
    @FXML private ListView lvThursday;
    @FXML private ListView lvFriday;
    @FXML private ListView lvSaturday;
    @FXML private GridPane grdMonth;
    @FXML private GridPane grdWeek;
    @FXML private GridPane grdCalendar;
    @FXML private Button btnAddAppointment;
    @FXML private Button btnEditAppointment;
    @FXML private Button btnDeleteAppointment;
    @FXML private Button btnCustomer;
    @FXML private Button btnReport;
    @FXML private ImageView imgLoading;
    private Appointment selectedAppointment;
    private LocalDate currentDate;
    private VIEW mode = VIEW.byWeek;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnAddAppointment.setText(STRINGS.getString("add"));
        btnEditAppointment.setText(STRINGS.getString("edit"));
        btnCustomer.setText(STRINGS.getString("customers"));
        btnReport.setText(STRINGS.getString("reports"));
        lblTitle.setText(STRINGS.getString("welcome") + ", " + Main.getUser().getName());
        lblView.setText(STRINGS.getString("view") + ":");
        rbWeek.setText(STRINGS.getString("week"));
        rbMonth.setText(STRINGS.getString("month"));
        tbcSunday.setText(STRINGS.getString("sunday"));
        tbcMonday.setText(STRINGS.getString("monday"));
        tbcTuesday.setText(STRINGS.getString("tuesday"));
        tbcWednesday.setText(STRINGS.getString("wednesday"));
        tbcThursday.setText(STRINGS.getString("thursday"));
        tbcFriday.setText(STRINGS.getString("friday"));
        tbcSaturday.setText(STRINGS.getString("saturday"));
        tblWeek.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        grdCalendar.prefWidthProperty().bind(tblWeek.widthProperty());
        grdCalendar.prefHeightProperty().bind(tblWeek.heightProperty());
        grdWeek.prefWidthProperty().bind(tblWeek.widthProperty());
        grdWeek.prefHeightProperty().bind(tblWeek.heightProperty());        
        imgLoading.visibleProperty().bind(loading.focusedProperty());
        
        for (ListView lv : Arrays.asList(lvSunday, lvMonday, lvTuesday, lvWednesday, lvThursday, lvFriday, lvSaturday)) {
            lv.addEventHandler(MOUSE_PRESSED, afterClick);
            lv.getSelectionModel().selectedItemProperty().addListener(setSelectedAppointment());
            lv.setCellFactory(new CalendarCellFactory());
            lv.prefWidthProperty().bind(tbcSunday.widthProperty());
            WEEK.add(lv);
        }
        
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 5; j++) {
                ListView lv = new ListView();
                lv.addEventHandler(MOUSE_PRESSED, afterClick);
                lv.setCellFactory(new CalendarCellFactory());
                lv.getSelectionModel().selectedItemProperty().addListener(setSelectedAppointment());
                lv.prefWidthProperty().bind(tbcSunday.widthProperty());
                grdMonth.add(lv, i, j);
                MONTH.add(lv);
            }
        }
        
        Platform.runLater(() -> {
            viewChanged();
            List<Appointment> soon = AppointmentData.findBetween(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(15), Main.getUser().getName());
            if (soon.size() > 0) {
                StringBuilder appointments = new StringBuilder();
                soon.forEach(appointment -> appointments.append(appointment.getLocalStart().format(TIMEFORMAT) + " - " + 
                        appointment.getLocalEnd().format(TIMEFORMAT) + ": '" + 
                        appointment.getTitle() + "' with " + 
                        appointment.getCustomer().getName() + "\n"));
                UI.showAlert(STRINGS.getString("appointment_soon"), STRINGS.getString("appointment_soon_body"), appointments.toString());
            }
        });
    }
    
    public void fillWeekCalendar(LocalDate date) {
        Task<Void> getData = new Task<Void>() {
            @Override
             public Void call() {
                ArrayList<Appointment> app = getAppointments();
                Platform.runLater(() -> {
                    for (int i = 0; i < 7; i++) {
                        Separator s = new Separator();
                        Label l = new Label();
                        l.setText(date.plusDays(i).format(DATEFORMAT));
                        GridPane g = new GridPane();
                        s.prefWidthProperty().bind(lvSunday.widthProperty().subtract(100));
                        g.add(s, 0, 1);
                        g.add(l, 0, 0);
                        GridPane.setHgrow(l, Priority.ALWAYS);
                        GridPane.setHalignment(l, HPos.CENTER);
                        WEEK.get(i).getItems().clear();
                        WEEK.get(i).getItems().add(g);

                        LocalDate day = date.plusDays(i);
                        ListView lvToday = WEEK.get(i);
                        app.stream().filter(x -> x.getLocalStart().getDayOfMonth() == day.getDayOfMonth()).forEach(x ->  {
                            lvToday.getItems().add(x);
                        });
                        
                    }
                    UI.fadeIn(loading);
                });
                return null;
            }
        };        
        Main.execute(getData);
    }
    
    private void fillMonthCalendar(LocalDate date) {
        Task<Void> getData = new Task<Void>() {
            @Override
             public Void call() {
                ArrayList<Appointment> app = getAppointments();
                Platform.runLater(() -> {
                    grdMonth.getChildren().filtered(child -> child.getClass() == ListView.class)
                        .forEach(x -> ((ListView)x).getItems().clear());
                    int y = 0;
                    for (int i = 1; i <= date.lengthOfMonth(); i++) {
                        int x = date.plusDays(i - 1).getDayOfWeek().getValue() % 7;
                        LocalDate day = date.plusDays(i - 1);
                        ListView lvDay = getCalendarCell(x, y);
                        lvDay.getItems().add(date.plusDays(i - 1).format(DATEFORMAT));

                        app.stream().filter(r -> r.getLocalStart().getDayOfMonth() == day.getDayOfMonth())
                            .forEach(r ->  lvDay.getItems().add(r));

                        if (date.plusDays(i - 1).getDayOfWeek().getValue() == 6 && y < 4) {
                            y++;
                        }
                    }
                    UI.fadeIn(loading);
                });
                return null;
             };
        };
        Main.execute(getData);
    }
    
    private ListView getCalendarCell(int x, int y) {
        return ((ListView) grdMonth.getChildren().filtered(cell -> GridPane.getRowIndex(cell) != null 
                && GridPane.getRowIndex(cell) == y && GridPane.getColumnIndex(cell) == x).get(0));
    }
    
    private ArrayList<Appointment> getAppointments() {
        ZonedDateTime start = null;
        ZonedDateTime end = null;
        if (mode == VIEW.byWeek) {
            start = ZonedDateTime.of(currentDate, LocalTime.MIDNIGHT, ZoneOffset.systemDefault());
            end = ZonedDateTime.of(currentDate.plusDays(7), LocalTime.MIDNIGHT, ZoneOffset.systemDefault());
        } else if (mode == VIEW.byMonth) {
            start = ZonedDateTime.of(currentDate, LocalTime.MIDNIGHT, ZoneOffset.systemDefault());
            end = ZonedDateTime.of(currentDate.plusMonths(1), LocalTime.MIDNIGHT, ZoneOffset.systemDefault());
        }
        return new ArrayList<>(AppointmentData.findBetween(start, end, Main.getUser().getName()));
    }
    
    private ChangeListener setSelectedAppointment() {
        return (ChangeListener) (ObservableValue o, Object ov, Object nv) -> {
            if (nv != null && nv instanceof Appointment) {
                selectedAppointment = (Appointment) nv;
            }
        };
    }
    
    private final EventHandler<MouseEvent> afterClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            ListView source = ((ListView) event.getSource());
            Object selected = source.getSelectionModel().getSelectedItem();
            if (selected instanceof Appointment && mode == VIEW.byWeek) {
                WEEK.forEach(lv -> { if (!(lv.getItems().contains(selected))) { lv.getSelectionModel().clearSelection(); }});
            } else if (selected instanceof Appointment && mode == VIEW.byMonth) {
                MONTH.forEach(lv -> { if (!(lv.getItems().contains(selected))) { lv.getSelectionModel().clearSelection(); }});
            } else if (source.getItems().contains(selectedAppointment)) {
                source.getSelectionModel().select(selectedAppointment);
            } else {
                source.getSelectionModel().clearSelection();                    
            }

            boolean noAppointment = true;
            if (mode == VIEW.byWeek) {
                noAppointment = (WEEK.stream().filter(lv -> ((ListView)lv).getSelectionModel().getSelectedItem() != null).count() == 0);
            } else if (mode == VIEW.byMonth) {                
                noAppointment = (MONTH.stream().filter(lv -> ((ListView)lv).getSelectionModel().getSelectedItem() != null).count() == 0);
            }
            btnEditAppointment.setDisable(noAppointment);
            btnDeleteAppointment.setDisable(noAppointment);
        }
    };
    
    public void cancelKeyPressed(KeyEvent event) {
        event.consume();
    }
    
    private LocalDate getLastSaturday(LocalDate day) {
        return day.minusDays(LocalDate.now().getDayOfWeek().getValue() % 7);
    }
    
    private LocalDate getFirstOfMonth(LocalDate day) {
        return day.minusDays(LocalDate.now().getDayOfMonth() - 1);
    }
    
    public void prev() {
        UI.fadeOut(loading);
        btnEditAppointment.setDisable(true);
        btnDeleteAppointment.setDisable(true);
        if (mode == VIEW.byWeek) {
            currentDate = currentDate.minusDays(7);    
        } else if (mode == VIEW.byMonth) {
            currentDate = currentDate.minusMonths(1);    
        }
        refreshView();
    }
    
    public void next() {
        UI.fadeOut(loading);
        btnEditAppointment.setDisable(true);
        btnDeleteAppointment.setDisable(true);
        if (mode == VIEW.byWeek) {
            currentDate = currentDate.plusDays(7);
        } else if (mode == VIEW.byMonth) {
            currentDate = currentDate.plusMonths(1);    
        }
        refreshView();
    }
    
    public void viewChanged() {
        UI.fadeOut(loading);
        btnEditAppointment.setDisable(true);
        btnDeleteAppointment.setDisable(true);
        if(rbWeek.isSelected()) {
            mode = VIEW.byWeek;
            grdCalendar.setVisible(false);
            currentDate = getLastSaturday(LocalDate.now());
        } else if (rbMonth.isSelected()) {
            mode = VIEW.byMonth;
            grdCalendar.setVisible(true);
            currentDate = getFirstOfMonth(LocalDate.now());
        }
        refreshView();
    }
    
    private void refreshView() {
        if (mode == VIEW.byWeek) {
            txtDateOf.setText(STRINGS.getString("week_of") + " " + currentDate.format(DATEFORMAT));
            fillWeekCalendar(currentDate);
        } else if (mode == VIEW.byMonth) {
            fillMonthCalendar(currentDate);
            txtDateOf.setText(STRINGS.getString("month_of") + " " + currentDate.format(DATEFORMAT));
        }
    }

    public void reports() {
        UI.fadeOut(loading);
        UI.show("/Presentation/View/Reports.fxml", STRINGS.getString("reports"));
        UI.fadeIn(loading);
    }
    
    public void manageCustomer() {
        UI.fadeOut(loading);
        UI.show("/Presentation/View/AddModifyCustomer.fxml", STRINGS.getString("manage_customer"));
        UI.fadeIn(loading);
    }
    
    public void addAppointment() {        
        ProcessAppointment p = new ProcessAppointment();
        UI.fadeOut(loading);
        UI.<Appointment>showDialog("/Presentation/View/AddModifyAppointment.fxml", STRINGS.getString("add_appointment"), new Appointment())
            .ifPresent(appointment -> p.setSave(appointment));
        Main.execute(p);
    }

    public void editAppointment() {
        ProcessAppointment p = new ProcessAppointment();
        UI.fadeOut(loading);
        UI.<Appointment>showDialog("/Presentation/View/AddModifyAppointment.fxml", STRINGS.getString("edit_appointment"), selectedAppointment)
            .ifPresent(appointment -> p.setSave(appointment));
        Main.execute(p);
    }

    public void deleteAppointment() {
        ProcessAppointment p = new ProcessAppointment();
        UI.fadeOut(loading);
        UI.showAlert(STRINGS.getString("delete_appointment"), STRINGS.getString("delete_appointment_body"), selectedAppointment.toString())
            .ifPresent(button ->  {
                if (button.equals(ButtonType.OK)) {
                    p.setDelete(selectedAppointment);
                }
            });
        Main.execute(p);
    }
    
    private class ProcessAppointment extends Task<Void> {
        private Appointment toSave;
        private Appointment toDelete;
        
        public void setSave(Appointment appointment) {
            this.toSave = appointment;
        }
        
        public void setDelete (Appointment appointment) {
            this.toDelete = appointment;
        }
        
        @Override
        public Void call() {
            if (toSave != null) {
                AppointmentData.save(toSave);
            } else if (toDelete != null) {
                AppointmentData.delete(toDelete.getId());
            }
                Platform.runLater(() -> refreshView());
            return null;
        };
    }
}
