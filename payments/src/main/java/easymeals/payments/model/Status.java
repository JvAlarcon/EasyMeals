package easymeals.payments.model;

public enum Status {

    CREATED(1, "CREATED"),
    CONFIRMED(2, "CONFIRMED"),
    CANCELED(3, "CANCELED");
    
    private final int id;
    private final String description;
    
    Status(int id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public int getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static Status fromId(int id) {
        for(Status status : values()) {
            if(status.getId() == id) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Invalid Status id: " + id);
    }
}