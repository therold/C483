package Data;

import Model.Customer;
import heroldcalendar.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CustomerData {
    public static Customer find(int id) {
        String sql = "SELECT * FROM customer "
                + "JOIN address ON customer.addressId = address.addressId "
                + "JOIN city ON address.cityId = city.cityId "
                + "JOIN country on city.countryId = country.countryId "
                + "WHERE customerId = ?";
        Customer customer = null;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);       // customerId
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                customer = process(rs);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    public static int save(Customer customer) {
        return (customer.getId() == 0) ? add(customer) : update(customer);
    }
    
    public static int add(Customer customer) {
        int key = 0;
        String sql = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "values (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getAddress());     // address
            ps.setString(2, customer.getAddress2());    // address2
            ps.setInt(3, customer.getCity().getId());   // cityId
            ps.setString(4, customer.getPostalCode());  // postalCode
            ps.setString(5, customer.getPhone());       // phone
            ps.setTimestamp(6, now);                    // createDate
            ps.setString(7, Main.getUser().getName());  // createdBy
            ps.setTimestamp(8, now);                    // lastUpdate
            ps.setString(9, Main.getUser().getName());  // lastUpdateBy
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int addressId = 0;
            if (rs.first()) {
                addressId = rs.getInt(1);
            }
            
            ps = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());        // customerName
            ps.setInt(2, addressId);                    // addressId
            ps.setInt(3, customer.getActive());         // active
            ps.setTimestamp(4, now);                    // createDate
            ps.setString(5, Main.getUser().getName());  // createdBy
            ps.setTimestamp(6, now);                    // lastUpdate
            ps.setString(7, Main.getUser().getName());  // lastUpdateBy
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.first()) {
                key = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return key;
    }
    
    public static int update(Customer customer) {
        String sql = "UPDATE customer "
                + "SET customerName = ?, addressId = ?, active = ?, lastUpdate = ?, lastUpdateBy = ? "
                + "WHERE customerId = ?";
        String sql2 = "Update address "
                + "SET address = ?, address2 = ?, cityId = ?, postalCode = ?, phone = ?, lastUpdate = ?, lastUpdateBy = ? "
                + "WHERE addressId = ?";
        try (Connection conn = DB.getConnection()) {
            Timestamp now = DB.getUTCts(ZonedDateTime.now());
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());        // customerName
            ps.setInt(2, customer.getAddressId());      // addressId
            ps.setInt(3, customer.getActive());         // active
            ps.setTimestamp(4, now);                    // lastUpdate
            ps.setString(5, Main.getUser().getName());  // lastUpdateBy
            ps.setInt(6, customer.getId());             // customerId
            ps.executeUpdate();
            
            ps = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getAddress());     // address
            ps.setString(2, customer.getAddress2());    // address2
            ps.setInt(3, customer.getCity().getId());   // cityId
            ps.setString(4, customer.getPostalCode());  // postalCode
            ps.setString(5, customer.getPhone());       // phone
            ps.setTimestamp(6, now);                    // lastUpdate
            ps.setString(7, Main.getUser().getName());  // lastUpdateBy
            ps.setInt(8, customer.getAddressId());      // addressId
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer.getId();
    }
    
    public static void delete(int id) {
        Customer customer = find(id);
        String sql = "DELETE FROM address WHERE addressId = ?";
        String sql2 = "DELETE FROM customer WHERE customerId = ?";
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customer.getAddressId());  // addressId
            ps.executeUpdate();
            
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, id);                       // customerId
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<Customer> all() {
        String sql = "SELECT * FROM customer "
                + "JOIN address ON customer.addressId = address.addressId "
                + "JOIN city ON address.cityId = city.cityId "
                + "JOIN country on city.countryId = country.countryId "
                + "ORDER BY customerName ASC";
        ArrayList<Customer> all = new ArrayList<>();
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
        String sql = "SELECT (COUNT(customer.customerId) > 0) " 
                + "FROM customer JOIN appointment ON customer.customerId = appointment.customerId "
                + "WHERE customer.customerId = ?";
        boolean isUsed = false;
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, countryId);        // customerId
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isUsed = rs.getBoolean(1);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUsed;
    }    
    
    public static Customer process(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt("customerId"),
            rs.getString("customerName"),
            rs.getInt("addressId"),
            rs.getString("address"),
            rs.getString("address2"),
            CityData.process(rs),
            rs.getString("postalCode"),
            rs.getString("phone"),
            rs.getInt("active"));
    }
}
