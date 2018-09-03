package Model;

public class City implements Comparable<City> {
    public static final int MAX_NAME_LEN = 50;
    private int cityId;         // cityId INT(10)
    private String city;        // city VARCHAR(50)
    private Country country;    // countryId INT(10)
    
    public City(int cityId, String city, Country country) {
        this.cityId = cityId;
        this.city = city;
        this.country = country;
    }

    public City(String city, Country country) {
        this(0, city, country);
    }
    
    public City() { }
    
    public int getId() { 
        return cityId; 
    }
    
    public String getName() { 
        return city; 
    }
    
    public Country getCountry() { 
        return country; 
    }
    
    public void setId(int cityId) { 
        this.cityId = cityId; 
    }
    
    public void setName(String city) { 
        this.city = city; 
    }
    
    public void setCountry(Country country) { 
        this.country = country; 
    }

    @Override
    public String toString() {
        return city;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof City)) {
            return false;
        } else {
            City other = (City) obj;
            return other.getId() == getId()
                    && other.getName().equals(getName());
        }
    }
    
    @Override
    public int compareTo (City other) {
        return this.getName().compareTo(other.getName());
    }
}
