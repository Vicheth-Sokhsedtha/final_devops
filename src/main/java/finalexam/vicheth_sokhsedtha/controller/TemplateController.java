package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.model.Template;
import finalexam.vicheth_sokhsedtha.service.TemplateService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @GetMapping
    public List<Template> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Template> search(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return service.searchByName(name);
        }
        return service.findAll();
    }

    @PostMapping
    public Template create(@Valid @RequestBody Template template) {
        return service.save(template);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Template> update(@PathVariable Long id, @Valid @RequestBody Template template) {
        try {
            return ResponseEntity.ok(service.update(id, template));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}