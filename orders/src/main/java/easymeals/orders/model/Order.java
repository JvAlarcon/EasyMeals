package easymeals.orders.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "orders")
public record Order(
    @Id Long id,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    Status status,
    List<Item> items
) {
    public Order {
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(updatedAt);
        Objects.requireNonNull(status);
        if(items == null || items.isEmpty()) {
            throw new IllegalArgumentException("An order must have at least one item");
        }
        
        items = List.copyOf(items);
    }
    
    @Table(name = "order_items")
    public record Item(@Id Long id, String description, Integer quantity) {}
    
    public record NewOrder(Status status, List<NewItem> items) {
    	public NewOrder {
    	    Objects.nonNull(status);
    	    if(items == null || items.isEmpty()) {
                throw new IllegalArgumentException("An order must have at least one item");
            }
            
            items = List.copyOf(items);
    	}
    	
    	public record NewItem(String description, Integer quantity) {}
    }
}
