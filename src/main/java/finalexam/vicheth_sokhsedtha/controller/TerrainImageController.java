package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.entity.TerrainImage;
import finalexam.vicheth_sokhsedtha.repository.TerrainImageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrain-images")
public class TerrainImageController {

    private final TerrainImageRepository repository;

    public TerrainImageController(TerrainImageRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TerrainImage> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public TerrainImage create(@RequestBody TerrainImage terrainImage) {
        return repository.save(terrainImage);
    }
}