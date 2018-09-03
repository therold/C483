package Model;

public class Country implements Comparable<Country> {
    public static final int MAX_NAME_LEN = 50;
    private int countryId;  // countryId INT(10)
    private String country; // country VARCHAR(50)
    
    public Country(int countryId, String country) {
        this.countryId = countryId;
        this.country = country;
    }
    
    public Country(String country) {
        this(0, country);
    }
    
    public Country() {}
    
    public int getId() { 
        return countryId; 
    }
    
    public String getName() { 
        return country; 
    }
    
    public void setId(int countryId) { 
        this.countryId = countryId; 
    }
    
    public void setName(String country) { 
        this.country = country; 
    }
    
    @Override
    public String toString() {
        return country;
    }
    
    
    @Override
    public int compareTo (Country other) {
        return this.getName().compareTo(other.getName());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Country)) {
            return false;
        } else {
            Country other = (Country) obj;
            return other.getId() == getId();
        }
    }
}
