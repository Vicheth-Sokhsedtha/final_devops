package finalexam.vicheth_sokhsedtha.service;

import finalexam.vicheth_sokhsedtha.model.Template;
import finalexam.vicheth_sokhsedtha.repository.TemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    private final TemplateRepository repository;

    public TemplateService(TemplateRepository repository) {
        this.repository = repository;
    }

    public List<Template> findAll() {
        return repository.findAll();
    }

    public Optional<Template> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Template> findByCode(String code) {
        return repository.findByCode(code);
    }

    public List<Template> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Template> findDefault() {
        return repository.findTopByOrderById();
    }

    public Template save(Template template) {
        return repository.save(template);
    }

    public Template update(Long id, Template updated) {
        Template existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id: " + id));

        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setOrganizationName(updated.getOrganizationName());
        existing.setLayout(updated.getLayout());
        existing.setPrimaryColor(updated.getPrimaryColor());
        existing.setSecondaryColor(updated.getSecondaryColor());
        existing.setTextColor(updated.getTextColor());
        existing.setTagline(updated.getTagline());

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}