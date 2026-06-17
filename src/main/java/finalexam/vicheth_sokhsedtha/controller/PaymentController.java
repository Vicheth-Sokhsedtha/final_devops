package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.entity.Payment;
import finalexam.vicheth_sokhsedtha.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository repository;

    public PaymentController(PaymentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Payment> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Payment create(@RequestBody Payment payment) {
        return repository.save(payment);
    }
}