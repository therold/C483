package Data;

import Model.Country;
import heroldcalendar.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CountryData {
    public static Country find(int id) {
        String sql = "SELECT * FROM country WHERE countryId = ?";
        Country country = null;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);   // countryId
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                country = process(rs);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return country;
    }

    public static int save(Country country) {
        return (country.getId() == 0) ? add(country) : update(country);
    }
    
    public static int add(Country country) {
        int key = 0;
        String sql = "INSERT INTO country (country, createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "values (?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, country.getName());         // country
            ps.setTimestamp(2, now);                    // createDate
            ps.setString(3, Main.getUser().getName());  // createdBy
            ps.setTimestamp(4, now);                    // lastUpdate
            ps.setString(5, Main.getUser().getName());  // lastUpdateBy
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
    
    public static int update(Country country) {
        String sql = "UPDATE country "
                + "SET country = ?, lastUpdate = ?, lastUpdateBy = ? "
                + "WHERE countryId = ?";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, country.getName());         // country
            ps.setTimestamp(2, now);                    // lastUpdate
            ps.setString(3, Main.getUser().getName());  // lastUpdateBy
            ps.setInt(4, country.getId());              // countryId
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return country.getId();
    }
    
    public static void delete(int id) {
        String sql = "DELETE FROM country WHERE countryId = ?";
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);       // countryId
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<Country> all() {
        String sql = "SELECT * FROM country ORDER BY country ASC";
        ArrayList<Country> all = new ArrayList<>();
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

    public static boolean isUsed(int countryId) {
        String sql = "SELECT (COUNT(country.countryId) > 0) " 
                + "FROM city JOIN country ON city.countryId = country.countryId "
                + "WHERE country.countryId = ?";
        boolean isUsed = false;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, countryId);    // countryId
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isUsed = rs.getBoolean(1);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUsed;
    }
        
    public static Country process(ResultSet rs) throws SQLException {
        return new Country(rs.getInt("countryId"), rs.getString("country"));
    }
}
