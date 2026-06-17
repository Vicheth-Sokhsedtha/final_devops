package finalexam.vicheth_sokhsedtha.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PAID;

    @Column(name = "transaction_id")
    private String transactionId;
}