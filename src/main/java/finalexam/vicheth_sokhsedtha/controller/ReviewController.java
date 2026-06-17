package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.entity.Review;
import finalexam.vicheth_sokhsedtha.repository.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository repository;

    public ReviewController(ReviewRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Review> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Review create(@RequestBody Review review) {
        return repository.save(review);
    }
}