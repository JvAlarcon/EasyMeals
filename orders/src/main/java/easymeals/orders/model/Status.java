package easymeals.orders.model;

public enum Status {

    PLACED(1, "PLACED"),
    CANCELLED(2, "CANCELLED"),
    PAID(3, "PAID"),
    NOT_AUTHORIZED(4, "NOT_AUTHORIZED"),
    CONFIRMED(5, "CONFIRMED"),
    READY(6, "READY"),
    SHIPPED(7, "SHIPPED"),
    DELIVERED(8, "DELIVERED");

    private final int id;
    private final String description;

    private Status(int id, String description) {
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
