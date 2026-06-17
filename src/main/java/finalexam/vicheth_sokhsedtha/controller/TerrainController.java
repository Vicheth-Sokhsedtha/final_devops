package finalexam.vicheth_sokhsedtha.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import finalexam.vicheth_sokhsedtha.entity.Terrain;
import finalexam.vicheth_sokhsedtha.repository.TerrainRepository;

@RestController
@RequestMapping("/api/terrains")
public class TerrainController {

    private final TerrainRepository repository;

    public TerrainController(TerrainRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Terrain> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Terrain create(@RequestBody Terrain terrain) {
        return repository.save(terrain);
    }
}