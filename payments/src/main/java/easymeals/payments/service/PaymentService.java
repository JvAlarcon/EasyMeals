package easymeals.payments.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import easymeals.payments.model.PageResponse;
import easymeals.payments.model.Payment;
import easymeals.payments.model.Payment.NewPayment;
import easymeals.payments.model.PaymentRequest;
import easymeals.payments.model.Status;
import easymeals.payments.repository.PaymentRepository;

@Service
public class PaymentService {

    private PaymentRepository repository;
    
    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public PageResponse<Payment> getPaymentPage(Pageable pageable) {
        long totalElements = repository.count();
        List<Payment> content = repository.findAll(pageable);
        return new PageResponse<>(
            content,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            totalElements);
    }
    
    @Transactional(readOnly = true)
    public Optional<Payment> findById(Long id) {
        return repository.findById(id);
    }
   
    @Transactional
    public Payment insertPayment(PaymentRequest request) {
	    NewPayment payment = new NewPayment(request.amount(), request.name(), request.cardNumber(),
	        request.expiration(), request.cardSecurityCode(), Status.fromId(request.statusId()), request.orderId(), request.paymentFormId());
        return repository.insert(payment);
    }
   
    @Transactional
    public Payment updatePayment(Long id, PaymentRequest request) {
 	    Payment payment = new Payment(id, request.amount(), request.name(), request.cardNumber(),
 		    request.expiration(), request.cardSecurityCode(), Status.fromId(request.statusId()), request.orderId(), request.paymentFormId());
 	    return repository.update(payment);
    }
    
    @Transactional
    public Optional<Payment> deletePayment(Long id) {
 	   return repository.deleteById(id);
    }
}