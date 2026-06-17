package finalexam.vicheth_sokhsedtha.service;

import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.ProfileType;
import finalexam.vicheth_sokhsedtha.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public List<Profile> findAll() {
        return repository.findAll();
    }

    public Optional<Profile> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Profile> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    public Optional<Profile> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Profile> findByRegistrationNumber(String registrationNumber) {
        return repository.findByRegistrationNumber(registrationNumber);
    }

    public List<Profile> findByType(ProfileType type) {
        return repository.findByType(type);
    }

    public List<Profile> searchByName(String name) {
        return repository.findByFullNameContainingIgnoreCase(name);
    }

    public List<Profile> searchByDepartment(String department) {
        return repository.findByDepartmentContainingIgnoreCase(department);
    }

    public Profile save(Profile profile) {
        return repository.save(profile);
    }

    public Profile update(Long id, Profile updated) {
        Profile existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + id));

        existing.setFullName(updated.getFullName());
        existing.setDepartment(updated.getDepartment());
        existing.setTitle(updated.getTitle());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setBloodGroup(updated.getBloodGroup());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setExpiryDate(updated.getExpiryDate());
        existing.setPhotoFileName(updated.getPhotoFileName());
        existing.setPhotoContentType(updated.getPhotoContentType());
        existing.setTemplate(updated.getTemplate());
        existing.setBarcodeType(updated.getBarcodeType());

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}