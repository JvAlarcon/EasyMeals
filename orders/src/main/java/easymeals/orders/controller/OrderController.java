package easymeals.orders.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import easymeals.orders.model.Order;
import easymeals.orders.model.OrderRequest;
import easymeals.orders.model.PageResponse;
import easymeals.orders.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private OrderService service;
	
	public OrderController(OrderService service) {
	    this.service = service;
	}
	
	@GetMapping
	public PageResponse<Order> list(@PageableDefault(size = 10) Pageable pageable) {
	    return service.getOrderPage(pageable);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Order> details(@PathVariable @NotNull Long id) {
	    return service.findById(id)
	        .map(order -> ResponseEntity.ok(order))
	        .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public ResponseEntity<Order> insert(@RequestBody @Valid OrderRequest request, UriComponentsBuilder uriBuilder) {
	    Order order = service.insertOrder(request);
	    URI address = uriBuilder.path("/orders/{id}")
	        .buildAndExpand(order.id())
	        .toUri();
	    
	    return ResponseEntity.created(address).body(order);
	}
	
	//TODO implement update and delete
}