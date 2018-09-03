package Model;

public class Customer implements Comparable<Customer> {
    public static final int MAX_NAME_LEN = 45;
    public static final int MAX_ADDRESS_LEN = 50;
    public static final int MAX_ADDRESS2_LEN = 50;
    public static final int MAX_POSTALCODE_LEN = 10;
    public static final int MAX_PHONE_LEN = 20;
    
    private int customerId;         // customerId INT(10)
    private String customerName;    // customerName VARCHAR(45)
    private int addressId;          // addressId INT(10)
    private String address;         // address VARCHAR(50)
    private String address2;        // address2 VARCHAR(50)
    private City city;              // cityId INT(10)
    private String postalCode;      // postalCode VARCHAR(10)
    private String phone;           // phone VARCHAR(20)
    private int active;             // active TINYINT(1)
    
    public Customer() {} 
    
    public Customer(String customerName, String address, String address2, City city, 
            String postalCode, String phone) {
        this(0, customerName, 0, address, address2, city, postalCode, phone, 0);
    }
    
    public Customer(int customerId, String customerName, int addressId, String address, String address2, City city, 
            String postalCode, String phone, int active) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
        this.active = active;
    }
    
    public int getId() { 
        return customerId; 
    }
    
    public String getName() { 
        return customerName; 
    }
    
    public int getAddressId() { 
        return addressId; 
    }
    
    public String getAddress() { 
        return address; 
    }
    
    public String getAddress2() { 
        return address2; 
    }
    
    public City getCity() { 
        return city; 
    }
    
    public String getPostalCode() { 
        return postalCode; 
    }
    
    public String getPhone() { 
        return phone; 
    }
    
    public int getActive() { 
        return active; 
    }
    
    public void setId(int customerId) { 
        this.customerId = customerId; 
    }
    
    public void setName(String customerName) { 
        this.customerName = customerName; 
    }
    
    public void setAddressId(int addressId) {
        this.addressId = addressId; 
    }
    
    public void setAddress(String address) { 
        this.address = address; 
    }
    
    public void setAddress2(String address2) { 
        this.address2 = address2; 
    }
    
    public void setCity(City city) { 
        this.city = city; 
    }
    
    public void setPostalCode(String postalCode) { 
        this.postalCode = postalCode; 
    }
    
    public void setPhone(String phone) { 
        this.phone = phone; 
    }
    
    public void setActive(int active) { 
        this.active = active; 
    }
    
    @Override
    public int compareTo (Customer other) {
        return this.getName().compareTo(other.getName());
    }
    
    @Override
    public String toString() {
        return customerName;
    }
}
