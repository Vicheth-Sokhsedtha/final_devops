package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.entity.Booking;
import finalexam.vicheth_sokhsedtha.repository.BookingRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository repository;

    public BookingController(BookingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Booking> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Booking create(@RequestBody Booking booking) {
        return repository.save(booking);
    }
}