package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.entity.Favorite;
import finalexam.vicheth_sokhsedtha.repository.FavoriteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository repository;

    public FavoriteController(FavoriteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Favorite> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Favorite create(@RequestBody Favorite favorite) {
        return repository.save(favorite);
    }
}