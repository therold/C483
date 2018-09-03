package Data;

import static Data.UserData.process;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login implements AutoCloseable {
    private Connection conn;
    private User user;
    
    public Login(String name, String password) {
        User user = null;
        try {
            conn = DB.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM user WHERE BINARY userName = ? AND BINARY password = ?");
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                this.user = process(rs); // Return user if user/pass are found in DB
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public User login() throws UserNotFoundException {
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException(); // Throw exception if user/pass not found
        }
    }
    
    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
