package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppointmentType {
    private static final List<AppointmentType> TYPES = new ArrayList<>(Arrays.asList(
        new AppointmentType("Consultation", "#FFC2B2"),
        new AppointmentType("Meeting", "#C4E79A"), 
        new AppointmentType("Sales", "#88ACBE")));
    private final String type;
    private final String color;
    
    private AppointmentType(String type, String color) {
        this.type = type;
        this.color = color;
    }
    
    public static List<AppointmentType> getTypes() {
        return TYPES;
    }
    
    public static AppointmentType find(String name) {
        return TYPES.stream().filter(type -> type.getName().equals(name)).findFirst().orElse(null);
    }
    
    public String getName() {
        return type;
    }
    
    public String getColor() {
        return color;
    }
}
