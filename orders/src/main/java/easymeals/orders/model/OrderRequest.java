package easymeals.orders.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest (
    
    @NotNull(message = "{order.status.required}")
    Integer statusId,
    
    @NotEmpty(message = "{order.items.required}")
    @Valid
    List<ItemRequest> items
) {
    public OrderRequest {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("An order must have at least one item");
        }
        
        items = List.copyOf(items);
    }
    
    public record ItemRequest(
        @NotBlank(message = "{order.items.description.required}")
        String description,
        
        @NotNull(message = "{order.items.quantity.required}")
        @Positive(message = "{order.items.quyantity.positive}")
        Integer quantity) {} 
}