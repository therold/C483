package Presentation;

import Model.User;
import heroldcalendar.Main;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import Presentation.Controller.DialogController;
import java.util.concurrent.Callable;
import java.util.function.UnaryOperator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Parent;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class UI {
    public final static String ERROR_STYLE = "    -fx-border-color: red; " +
        "-fx-border-width: 1; " +
        "-fx-border-style: solid; ";
    private static final ResourceBundle strings = ResourceBundle.getBundle("Presentation.Strings");
    
    public static void loadMainStage(User user) {
        Main.setUser(user);
        try {
            Parent root = FXMLLoader.load(UI.class.getResource("/Presentation/View/Main.fxml"));

            Stage stage = Main.getPrimaryStage();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setOnCloseRequest(e -> {
                Optional<ButtonType> result = UI.showCancelDialog("Calendar");
                if (result.get() == ButtonType.CANCEL) {
                    e.consume();
                }
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void show(String file, String title) {
        try {
            Parent root = FXMLLoader.load(UI.class.getResource(file));
            Stage stage = new Stage(StageStyle.DECORATED);
            Scene scene = new Scene(root);
            
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.getPrimaryStage());
            stage.getIcons().add(Main.getIcon());
            stage.setTitle(title);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.focusedProperty().addListener(UI.focusListener(stage));

            stage.setOnCloseRequest(e -> {
                Optional<ButtonType> result = UI.showCancelDialog(title);
                if (result.get() == ButtonType.CANCEL) {
                    e.consume();
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static <T> Optional<T> showDialog(String file, String title, T t) {
        DialogController controller = null;
        T objToSave = null;
        try {
            FXMLLoader loader = new FXMLLoader(UI.class.getResource(file));
            Stage stage = new Stage(StageStyle.DECORATED);
            Scene scene = new Scene((Pane) loader.load());

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.getPrimaryStage());
            stage.setTitle(title);
            stage.getIcons().add(Main.getIcon());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.focusedProperty().addListener(UI.focusListener(stage));
            
            stage.setOnCloseRequest(e -> {
                Optional<ButtonType> result = UI.showCancelDialog(title);
                if (result.get() == ButtonType.CANCEL) {
                    e.consume();
                }
            });
        
            controller = loader.getController();
            controller.postInit(t);
            stage.showAndWait();
            objToSave = (T) controller.process();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (objToSave == null) ? Optional.empty() : Optional.of(objToSave);
    }
    
    public static Optional<ButtonType> showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        return alert.showAndWait();
    }
        
    public static Optional<ButtonType> showAlert(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        return alert.showAndWait();
    }
    
    public static Optional<ButtonType> showCancelDialog(String title) {
        return UI.showAlert(
            strings.getString("cancel") + " " + title, 
            strings.getString("cancel_body") + " " + title + "?"
        );
    }    
    
    public static Optional<ButtonType> showError(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        return alert.showAndWait();
    }
    
    public static ChangeListener<Boolean> focusListener(Stage stage) {
        return new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean hidden, Boolean shown)
            {
                FilteredList<Node> loading = stage.getScene().getRoot().getChildrenUnmodifiable().filtered(x -> x instanceof StackPane);
                if (loading.size() > 0) {
                    ((StackPane)loading.get(0)).getChildren().filtered(x -> x instanceof ImageView).get(0).setVisible(shown);
                }
            }
        };
    }
    
    public static void fadeOut(Node loading) {
        loading.requestFocus();
        loading.toFront();
        
        FadeTransition bg = new FadeTransition();
        bg.setNode(loading);
        bg.setDuration(new Duration(200));
        bg.setFromValue(0.2);
        bg.setToValue(0.5);
            
        bg.play();
    }
    
    public static void fadeIn(Node loading) {
        FadeTransition bg = new FadeTransition();
        bg.setNode(loading);
        bg.setDuration(new Duration(200));
        bg.setFromValue(0.5);
        bg.setToValue(0.0);
        bg.setOnFinished(x -> loading.toBack());
        bg.play();
    }

    public static void lostFocus(boolean isFocused, Callable<Node> validator) {
        if (!isFocused) {
            try {
                Node node = validator.call(); // throws InvalidInputException on invalid input
                node.setStyle("");
            } catch (InvalidInputException e) {
                e.getNode().setStyle(UI.ERROR_STYLE);
                showError(e.getErrorMessage(), e.getErrorMessageBody());
            } catch (Exception e) {}
        }
    }
    
    public static UnaryOperator<TextFormatter.Change> timeFilter = (TextFormatter.Change change) -> {
        int pos = change.getRangeStart(); 
        if (change.isDeleted()) {
            StringBuilder replacement = new StringBuilder();
            change.getControlText().substring(change.getRangeStart(), change.getRangeEnd()).chars()
                .forEach(x -> replacement.append(x == ":".charAt(0) ? ":" : "0"));
            change.setText(replacement.toString());
            return change;
        } else if (!change.isContentChange()) {
            return change;
        } else {
            System.out.println("RStart: " + change.getRangeStart());
            System.out.println("REnd: " + change.getRangeEnd());
            System.out.println("Anchor: " + change.getAnchor());
            System.out.println("Caret: " + change.getCaretPosition());
            String oldText = change.getControlText();
            String newText = change.getText();
            int num = 0;
            
            if (newText.matches("\\d\\d:\\d\\d")) {
                return change;
            } else if (!newText.matches("[\\d:]")) {
                return null;
            } else if (newText.matches("[\\d]")) {
                num = Integer.parseInt(newText);
            }
            
            if (newText.equals(":") && pos != 2) {
                return null;
            } 
            
            switch (pos) {
                case 0:
                    int hrOnesDigit =  Integer.parseInt(String.valueOf(oldText.charAt(1)));
                    if (hrOnesDigit > 3) {
                        change.setRange(0,2);
                        change.setText(newText + "0");
                    } else {
                        change.setRange(0,1);
                    }
                    if (num >= 0 && num < 3) {
                        return change;
                    }
                    break;
                case 1:
                    change.setRange(1,2);
                    String hrTensDigit =  String.valueOf(oldText.charAt(0));
                    if (hrTensDigit.equals("2") && (num >= 0 && num < 4)) {
                        return change;
                    } else if (hrTensDigit.equals("0") || hrTensDigit.equals("1")) {
                        return change;
                    }
                    break;
                case 2:
                    if (newText.matches(":")) {
                        change.setRange(2,3);
                        change.setAnchor(3);
                        change.setCaretPosition(3);
                        return change;
                    }
                case 3:
                    change.setRange(3,4);
                    change.setAnchor(4);
                    change.setCaretPosition(4);
                    if (num >= 0 && num < 6) {
                        return change;
                    }
                    break;
                case 4:
                    change.setRange(4,5);
                    return change;
            }
            return null;
        }
    };
}
