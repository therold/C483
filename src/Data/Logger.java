package Data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final Path path = Paths.get("login.txt");
    private static final DateTimeFormatter logDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss O");
    
    private static void log(String log) {
        try(BufferedWriter w = Files.newBufferedWriter(path, Charset.forName("UTF-8"), 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(ZonedDateTime.now().format(logDateFormat) + " " + log + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loginSuccessful(String userName) {
        log("Login successful for: " + userName);
    }
    
    public static void loginFailed(String userName) {
        log("Login failed for: " + userName);
    }
}
