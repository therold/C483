package Data;

import Model.Appointment;
import heroldcalendar.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class AppointmentData {
    public static Appointment find(int id) {
        String sql = "SELECT * FROM appointment "
                + "JOIN customer ON appointment.customerId = customer.customerId "
                + "JOIN address ON customer.addressId = address.addressId "
                + "JOIN city ON address.cityId = city.cityId "
                + "JOIN country on city.countryId = country.countryId "
                + "WHERE appointmentId = ?";
        Appointment appointment = null;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);   // appointmentId
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                appointment = process(rs);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointment;
    }

    public static ArrayList<Appointment> findBetween(ZonedDateTime start, ZonedDateTime end, String name) {
        String sql = "SELECT * FROM appointment "
                + "JOIN customer ON appointment.customerId = customer.customerId "
                + "JOIN address ON customer.addressId = address.addressId "
                + "JOIN city ON address.cityId = city.cityId "
                + "JOIN country on city.countryId = country.countryId "
                + "WHERE (start BETWEEN ? AND ?) AND appointment.createdBy = ? "
                + "ORDER BY start ASC";
        ArrayList<Appointment> found = new ArrayList<>();
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, DB.getUTCts(start));     // start
            ps.setTimestamp(2, DB.getUTCts(end));       // end
            ps.setString(3, name);                      // createdBy
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                found.add(process(rs));
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }
    
    public static int save(Appointment appointment) {
        return (appointment.getId() == 0) ? add(appointment) : update(appointment);
    }
    
    public static int add(Appointment appointment) {
        int key = 0;
        String sql = "INSERT INTO appointment (customerId, title, description, location, "
                + "contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, appointment.getCustomer().getId());            // customerId
            ps.setString(2, appointment.getTitle());                    // title
            ps.setString(3, appointment.getDescription());              // description
            ps.setString(4, appointment.getLocation());                 // location
            ps.setString(5, appointment.getContact());                  // contact 
            ps.setString(6, appointment.getType());                     // url
            ps.setTimestamp(7, DB.getUTCts(appointment.getStart()));    // start 
            ps.setTimestamp(8, DB.getUTCts(appointment.getEnd()));      // end
            ps.setTimestamp(9, now);                                    // createDate 
            ps.setString(10, Main.getUser().getName());                 // createdBy 
            ps.setTimestamp(11, now);                                   // lastUpdate 
            ps.setString(12, Main.getUser().getName());                 // lastUpdateBy 

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.first()) {
                key = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return key;
    }
    
    public static int update(Appointment appointment) {
        String sql = "UPDATE appointment "
                + "SET customerId = ?, title = ?, description = ?, location = ?, contact = ?, " 
                + "url = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? "
                + "WHERE appointmentId = ?";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, appointment.getCustomer().getId());            // customerId
            ps.setString(2, appointment.getTitle());                    // title
            ps.setString(3, appointment.getDescription());              // description
            ps.setString(4, appointment.getLocation());                 // location
            ps.setString(5, appointment.getContact());                  // contact 
            ps.setString(6, appointment.getType());                     // url
            ps.setTimestamp(7, DB.getUTCts(appointment.getStart()));    // start
            ps.setTimestamp(8, DB.getUTCts(appointment.getEnd()));      // end
            ps.setTimestamp(9, now);                                    // lastUpdate
            ps.setString(10, Main.getUser().getName());                 // lastUpdateBy
            ps.setInt(11, appointment.getId());                         // appointmentId
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointment.getId();
    }
    
    public static void delete(int id) {
        String sql = "DELETE FROM appointment WHERE appointmentId = ?";
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);   // appointmentId
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Appointment> all() {
        String sql = "SELECT * FROM appointment "
                + "JOIN customer ON appointment.customerId = customer.customerId "
                + "JOIN address ON customer.addressId = address.addressId "
                + "JOIN city ON address.cityId = city.cityId "
                + "JOIN country on city.countryId = country.countryId "
                + "ORDER BY start ASC";
        ArrayList<Appointment> all = new ArrayList<>();
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

    public static boolean isOverlapping(ZonedDateTime start, ZonedDateTime end, String name, int id) {
        String sql = "SELECT (count(appointmentId) > 0) FROM appointment "
            + "WHERE ? <= end AND ? >= start AND createdBy = ? AND appointmentId != ?";
        boolean isOverlapping = false;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, DB.getUTCts(start));     // start
            ps.setTimestamp(2, DB.getUTCts(end));       // end
            ps.setString(3, name);                      // createdBy
            ps.setInt(4, id);                           // appointmentId
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                isOverlapping = rs.getBoolean(1);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isOverlapping;
    }

    
    private static Appointment process(ResultSet rs) throws SQLException {
        return new Appointment(rs.getInt("appointmentId"), 
            CustomerData.process(rs),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("location"),
            rs.getString("contact"),
            rs.getString("url"),
            DB.getLocal(rs.getTimestamp("start")),
            DB.getLocal(rs.getTimestamp("end")),
            rs.getString("createdBy"));
    }
}