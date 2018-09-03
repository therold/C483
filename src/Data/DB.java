package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

public class DB {
    private static final ResourceBundle dbInfo = ResourceBundle.getBundle("Data.DBInfo");

    public static Connection getConnection() throws SQLException {
        String db = dbInfo.getString("db");
        String url = dbInfo.getString("url") + db;
        String user = dbInfo.getString("user");
        String pass = dbInfo.getString("pass");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, pass);
    }
    
    public static ZonedDateTime getLocal(Timestamp timestamp) {
        return timestamp != null ? ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp.getTime()), ZoneOffset.systemDefault())
                .withZoneSameLocal(ZoneOffset.UTC) : null;
    }
    
    public static Timestamp getUTCts(ZonedDateTime zdt) {
        return Timestamp.valueOf(zdt.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }
}
