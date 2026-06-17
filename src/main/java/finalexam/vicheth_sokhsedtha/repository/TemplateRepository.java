package finalexam.vicheth_sokhsedtha.repository;

import finalexam.vicheth_sokhsedtha.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByCode(String code);

    List<Template> findByNameContainingIgnoreCase(String name);

    Optional<Template> findTopByOrderById();
}