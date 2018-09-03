package Model;

public class User {
    private Integer userId;         // INT
    private String userName;        // VARCHAR(50)
    private String password;        // VARCHAR(50)
    private Integer active;         // TINYINT

    public User(Integer userId, String userName, String password, Integer active) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.active = active;
    }
    
    public Integer getId() {
        return userId;
    }
    
    private void setId(Integer id) {
        this.userId = id;
    }
    
    public String getName() {
        return userName;
    }
    
    public void setName(String name) {
        this.userName = name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Integer getActive() {
        return active;
    }
    
    public void setActive(Integer active) {
        this.active = active;
    } 
}
