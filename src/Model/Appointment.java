package Model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    public static final int MAX_TITLE_LEN = 255;
    public static final int MAX_URL_LEN = 255;
    
    private int appointmentId;      // appointmentId INT(10)
    private Customer customer;      // customerId INT(10)
    private String title;           // title VARCHAR(255)
    private String description;     // description TEXT
    private String location;        // location TEXT
    private String contact;         // contact TEXT
    private String type;            // url VARCHAR(255)
    private ZonedDateTime start;    // start DATETIME
    private ZonedDateTime end;      // end DATETIME
    private String user;            // createdBy VARCHAR(40)
    
    public Appointment() { }
    
    public Appointment(Customer customer, String title, String description,
            String location, String contact, String type, ZonedDateTime start, ZonedDateTime end, String user) {
        this(0, customer, title, description, location, contact, type, start, end, user);
    }
    
    public Appointment(int appointmentId, Customer customer, String title, String description,
            String location, String contact, String type, ZonedDateTime start, ZonedDateTime end, String user) {
        this.appointmentId = appointmentId;
        this.customer = customer;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;
        this.user = user;
    }
    
    public int getId() { 
        return appointmentId; 
    }
    
    public Customer getCustomer() { 
        return customer; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public String getLocation() { 
        return location; 
    }
    
    public String getContact() { 
        return contact; 
    }
    
    public String getType() { 
        return type; 
    }
    
    public ZonedDateTime getStart() { 
        return start; 
    }
    
    public ZonedDateTime getLocalStart() { 
        return start.withZoneSameInstant(ZoneId.systemDefault()); 
    }
    
    public ZonedDateTime getEnd() { 
        return end; 
    }
    
    public ZonedDateTime getLocalEnd() { 
        return end.withZoneSameInstant(ZoneId.systemDefault()); 
    }
    
    public String getUser() { 
        return user; 
    }
    
    public void setId(int appointmentId) { 
        this.appointmentId = appointmentId; 
    }
    
    public void setCustomer(Customer customer) { 
        this.customer = customer; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public void setLocation(String location) { 
        this.location = location; 
    }
    
    public void setContact(String contact) { 
        this.contact = contact; 
    }
    
    public void setType(String type) { 
        this.type = type; 
    }
    
    public void setStart(ZonedDateTime start) { 
        this.start = start; 
    }
    
    public void setEnd(ZonedDateTime end) { 
        this.end = end; 
    }
    
    public void setUser(String user) { 
        this.user = user; 
    }
    
    @Override
    public String toString() {
        if (start != null) {
            return getLocalStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                    getLocalEnd().format(DateTimeFormatter.ofPattern("HH:mm")) + ": " + title;
        } else {
            return Integer.toString(appointmentId);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Appointment)) {
            return false;
        } else {
            Appointment other = (Appointment) o;
            return this.getId() == other.getId();
        }
    }
}
