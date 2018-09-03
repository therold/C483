package heroldcalendar;

import Model.User;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static final Image APPICON = new Image("/Presentation/View/Icons/calendar.png");
    private static Stage stage;
    private static User user;
    private static Executor exec;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Presentation/View/Login.fxml"));        
        
        Scene scene = new Scene(root);
        this.stage = primaryStage;
    
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.getIcons().add(APPICON);
        primaryStage.setTitle("Herold Calendar");
        primaryStage.show();
    }

    public static void main(String[] args) {
        exec = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t ;
        });
        launch(args);
    }
    
    public static Stage getPrimaryStage() {
        return stage;
    }
    
    public static User getUser() {
        return user;
    }
    
    public static void setUser(User activeUser) {
        user = activeUser;
    }
    
    public static Image getIcon() {
        return APPICON;
    }
    
    public static void execute(Task task) {
        exec.execute(task);
    }
    
}
