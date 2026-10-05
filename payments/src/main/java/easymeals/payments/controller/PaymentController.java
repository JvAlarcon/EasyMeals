package easymeals.payments.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import easymeals.payments.model.PageResponse;
import easymeals.payments.model.Payment;
import easymeals.payments.model.PaymentRequest;
import easymeals.payments.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    private PaymentService service;
    
    public PaymentController(PaymentService service) {
        this.service = service;
    }
    
    @GetMapping
    public PageResponse<Payment> list(@PageableDefault(size = 10) Pageable pageable) {
        return service.getPaymentPage(pageable);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Payment> details(@PathVariable @NotNull Long id) {
        return service.findById(id)
            .map(payment -> ResponseEntity.ok(payment))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Payment> insert(@RequestBody @Valid PaymentRequest request, UriComponentsBuilder uriBuilder) {
        Payment payment = service.insertPayment(request);
        URI address = uriBuilder.path("/payments/{id}")
            .buildAndExpand(payment.id())
            .toUri();
        
        return ResponseEntity.created(address).body(payment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Payment> update(@PathVariable @NotNull Long id, @RequestBody @Valid PaymentRequest request) {
        Payment payment = service.updatePayment(id, request);
        return ResponseEntity.ok(payment);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Payment> delete(@PathVariable @NotNull Long id) {
        return service.deletePayment(id)
            .map(payment -> ResponseEntity.ok(payment))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}