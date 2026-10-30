package easymeals.orders.repository;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import easymeals.orders.model.Order;
import easymeals.orders.model.Order.Item;
import easymeals.orders.model.Order.NewOrder;
import easymeals.orders.model.Order.NewOrder.NewItem;
import easymeals.orders.model.Status;

@Repository
public class OrderRepository {
    
    private final JdbcClient jdbc;
    
    public OrderRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }
    
    private record OrderRow (
        Long orderId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Integer statusId,
        Long itemId,
        String description,
        Integer quantity
    ) {}
    
    private static Order toOrder(List<OrderRow> rows) {
        if(rows.isEmpty()) {
            throw new IllegalStateException("Operation returned no rows!");
        }
        
        OrderRow first = rows.getFirst();
        return new Order(
            first.orderId(),
            first.createdAt(),
            first.updatedAt(),
            Status.fromId(first.statusId()),
            rows.stream()
                .map(r -> new Item(r.itemId(), r.description(), r.quantity()))
                .toList()
        );
    }
    
    public long count() {
        String query = "SELECT COUNT(id) FROM orders";
        return jdbc.sql(query).query(Long.class).single();
    }
    
    public List<Order> findAll(Pageable pageable) {
        String query =
        """
        SELECT
            o.id AS order_id,
            o.created_at,
            o.updated_at,
            o.status_id,
            oi.id AS item_id,
            oi.description,
            oi.quantity
        FROM (
            SELECT id, created_at, updated_at, status_id
            FROM orders
            ORDER BY id DESC
            LIMIT :limit
            OFFSET :offset
        ) o
        INNER JOIN order_items oi ON o.id = oi.order_id
        ORDER BY o.id DESC, oi.id
        """;
    
    List<OrderRow> rows = jdbc.sql(query)
        .param("limit", pageable.getPageSize())
        .param("offset", pageable.getOffset())
        .query((rs, _) -> new OrderRow (
            rs.getLong("order_id"),
    	    rs.getObject("created_at", OffsetDateTime.class),
    	    rs.getObject("updated_at", OffsetDateTime.class),
    	    rs.getInt("status_id"),
    	    rs.getLong("item_id"),
    	    rs.getString("description"),
    	    rs.getInt("quantity")))
        .list();
    
    if (rows.isEmpty()) {
        return List.of();
    }
    
    Map<Long, List<OrderRow>> groupedRows = rows.stream()
        .collect(Collectors.groupingBy(
            OrderRow::orderId,
            LinkedHashMap::new,
            Collectors.toList()
        ));
    
    return groupedRows.values().stream()
        .map(orderRows -> {
            return toOrder(orderRows);
        })
        .toList();
    }

    public Optional<Order> findById(Long id) {
        String query =
        """
        SELECT
            o.id AS order_id,
            o.created_at,
            o.updated_at,
            o.status_id,
            oi.id AS item_id,
            oi.description,
            oi.quantity
        FROM orders o
        INNER JOIN order_items oi ON o.id = oi.order_id
        WHERE o.id = :id
        ORDER BY oi.id
        """;
        
        List<OrderRow> rows = jdbc.sql(query)
            .param("id", id)
            .query((rs, _) -> new OrderRow (
                rs.getLong("order_id"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class),
                rs.getInt("status_id"),
                rs.getLong("item_id"),
                rs.getString("description"),
                rs.getInt("quantity")
            ))
            .list();
        
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(toOrder(rows));
    }

    public @NonNull Order insert(NewOrder newOrder) {
        String query =
	    """
	    WITH new_order AS (
            INSERT INTO orders (status_id)
            VALUES (:statusId)
            RETURNING id, created_at, updated_at, status_id	   
        ),
        new_order_items AS (
            INSERT INTO order_items (description, quantity, order_id)
            SELECT t.description, t.quantity, (SELECT id FROM new_order)
            FROM unnest(:descriptions::text[], :quantities::int[]) AS t(description, quantity)
            RETURNING id, description, quantity, order_id
        )
        SELECT o.id AS order_id, o.created_at, o.updated_at, o.status_id,
            oi.id AS item_id, oi.description, oi.quantity
        FROM new_order o
        INNER JOIN new_order_items oi ON o.id = oi.order_id
        ORDER by oi.id;
	    """;
        
	    String[] descriptions = newOrder.items().stream()
	        .map(NewItem::description)
	        .toArray(String[]::new);
	    
	    Integer[] quantities = newOrder.items().stream()
	        .map(NewItem::quantity)
	        .toArray(Integer[]::new);
        
	    List<OrderRow> rows = jdbc.sql(query)
	        .param("statusId", newOrder.status().getId())
	        .param("descriptions", descriptions)
	        .param("quantities", quantities)
	        .query((rs, _) -> new OrderRow (
	            rs.getLong("order_id"),
	    	    rs.getObject("created_at", OffsetDateTime.class),
	    	    rs.getObject("updated_at", OffsetDateTime.class),
	    	    rs.getInt("status_id"),
	    	    rs.getLong("item_id"),
	    	    rs.getString("description"),
	    	    rs.getInt("quantity"))
	        ).list();
	    
	    return toOrder(rows);
    }
    
    public Optional<Order> transitionStatus(Long id, Status status) {
        String query =
        """
        WITH updated AS (
          UPDATE orders
          SET status_id = :statusId
          WHERE id = :id
          RETURNING id, created_at, updated_at, status_id
        )
        SELECT u.id AS order_id, u.created_at, u.updated_at, u.status_id,
          i.id AS item_id, i.description, i.quantity
        FROM updated u INNER JOIN order_items i ON u.id = i.order_id
        ORDER BY i.id;
        """;
        
        List<OrderRow> rows = jdbc.sql(query)
            .param("statusId", status.getId())
            .param("id", id)
            .query((rs, _) -> new OrderRow(
                rs.getLong("order_id"),
	    	    rs.getObject("created_at", OffsetDateTime.class),
	    	    rs.getObject("updated_at", OffsetDateTime.class),
	    	    rs.getInt("status_id"),
	    	    rs.getLong("item_id"),
	    	    rs.getString("description"),
	    	    rs.getInt("quantity")
            ))
            .list();
        
        return rows.isEmpty() ? Optional.empty() : Optional.of(toOrder(rows));
    }
}
