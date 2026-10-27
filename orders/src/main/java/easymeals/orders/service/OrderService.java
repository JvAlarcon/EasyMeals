package easymeals.orders.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import easymeals.orders.model.Order;
import easymeals.orders.model.Order.NewOrder;
import easymeals.orders.model.Order.NewOrder.NewItem;
import easymeals.orders.model.OrderRequest;
import easymeals.orders.model.OrderRequest.ItemRequest;
import easymeals.orders.model.PageResponse;
import easymeals.orders.model.Status;
import easymeals.orders.repository.OrderRepository;

@Service
public class OrderService {
    
    private OrderRepository repository;
    
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public PageResponse<Order> getOrderPage(Pageable pageable) {
        long totalElements = repository.count();
        List<Order> content = repository.findAll(pageable);
        return new PageResponse<>(
            content,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            totalElements);
    }
    
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return repository.findById(id);
    }
    
    private List<NewItem> mapItemRequest(List<ItemRequest> items) {
    	List<NewItem> newItems = new LinkedList<>();
        for(ItemRequest item : items) {
            newItems.add(new NewItem(item.description(), item.quantity()));
        }
        
        return newItems;
    }
    
    @Transactional
    public Order insertOrder(OrderRequest request) {
        NewOrder newOrder = new NewOrder(
            Status.fromId(request.statusId()),
            mapItemRequest(request.items()));
        
        return repository.insert(newOrder);
    }
}