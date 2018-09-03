package Model;

public class AppointmentTypeByMonth {
    private final String month;
    private final int consultationCount;
    private final int meetingCount;
    private final int salesCount;
    
    public AppointmentTypeByMonth(String month, int consultationCount, int meetingCount, int salesCount) {
        this.month = month;
        this.consultationCount = consultationCount;
        this.meetingCount = meetingCount;
        this.salesCount = salesCount;
    }
    
    public String getMonth() { 
        return month; 
    }
    
    public int getConsultationCount() { 
        return consultationCount; 
    }
    
    public int getMeetingCount() { 
        return meetingCount; 
    }
    
    public int getSalesCount() { 
        return salesCount; 
    }
}
