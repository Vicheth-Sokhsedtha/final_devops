package finalexam.vicheth_sokhsedtha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import finalexam.vicheth_sokhsedtha.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}