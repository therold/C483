package Data;

import Model.AppointmentTypeByMonth;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppointmentTypeByMonthData {
    public static ArrayList<AppointmentTypeByMonth> all() {
        String sql = "SELECT DATE_FORMAT(start, '%M') AS month, IFNULL(consultation, 0) AS consultation, IFNULL(meeting, 0) AS meeting, IFNULL(sales, 0) AS sales " +
            "FROM appointment a1 " +
            "LEFT JOIN (SELECT COUNT(url) AS consultation, month(start) AS month " +
            "FROM appointment WHERE url='Consultation' GROUP BY Month(start)) as a2 ON MONTH(a1.start) = a2.month " +
            "LEFT JOIN (SELECT COUNT(url) AS meeting, month(start) AS month " +
            "FROM appointment WHERE url='Meeting' GROUP BY Month(start)) as a3 ON MONTH(a1.start) = a3.month " +
            "LEFT JOIN (SELECT COUNT(url) AS sales, month(start) AS month " +
            "FROM appointment WHERE url='Sales' GROUP BY Month(start)) as a4 ON MONTH(a1.start) = a4.month " +
            "GROUP BY MONTH(start) ORDER BY MONTH(start) ASC";
        ArrayList<AppointmentTypeByMonth> all = new ArrayList<>();
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                all.add(process(rs));
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return all;
    }
    
    private static AppointmentTypeByMonth process(ResultSet rs) throws SQLException {
        return new AppointmentTypeByMonth(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
    }
}
