package finalexam.vicheth_sokhsedtha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import finalexam.vicheth_sokhsedtha.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}