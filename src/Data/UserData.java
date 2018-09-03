package Data;

import Model.User;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserData {    
    public static User process(ResultSet rs) throws SQLException {
        return new User (rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("password"),
            rs.getInt("active"));
    }
}
