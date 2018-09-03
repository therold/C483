package Data;

import Model.City;
import heroldcalendar.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CityData {
    public static City find(int id) {
        String sql = "SELECT * FROM city " 
                + "JOIN country on city.countryId = country.countryId "
                + "WHERE cityId = ?";
        City city = null;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);   // cityId
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                city = process(rs);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return city;
    }
    
    public static int save(City city) {
         return (city.getId() == 0) ? add(city) : update(city);
    }
            
    public static int add(City city) {
        int key = 0;
        String sql = "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "values (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, city.getName());            // city
            ps.setInt(2, city.getCountry().getId());    // countryId
            ps.setTimestamp(3, now);                    // createDate
            ps.setString(4, Main.getUser().getName());  // createdBy
            ps.setTimestamp(5, now);                    // lastUpdate
            ps.setString(6, Main.getUser().getName());  // lastUpdateBy
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
    
    public static int update(City city) {
        String sql = "UPDATE city "
                + "SET city = ?, countryId = ?, lastUpdate = ?, lastUpdateBy = ? "
                + "WHERE cityId = ?";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, city.getName());            // city
            ps.setInt(2, city.getCountry().getId());    // countryId
            ps.setTimestamp(3, now);                    // lastUpdate
            ps.setString(4, Main.getUser().getName());  // lastUpdateBy
            ps.setInt(5, city.getId());                 // cityId
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return city.getId();
    }
    
    public static void delete(int id) {
        String sql = "DELETE FROM city WHERE cityId = ?";
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);   // cityId
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<City> all() {
        String sql = "SELECT * FROM city " 
                + "JOIN country on city.countryId = country.countryId "
                + "ORDER BY city ASC";
        ArrayList<City> all = new ArrayList<>();
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

    public static boolean isUsed(int cityId) {
        String sql = "SELECT (COUNT(city.cityId) > 0) " 
                + "FROM customer JOIN address ON customer.addressId = address.addressId "
                + "JOIN city ON address.cityId = city.cityId "
                + "WHERE city.cityId = ?";
        boolean isUsed = false;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cityId);   // cityId
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isUsed = rs.getBoolean(1);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUsed;
    }
    
    public static City process(ResultSet rs) throws SQLException {
        return new City(rs.getInt("cityId"), rs.getString("city"), CountryData.process(rs));
    }
}
