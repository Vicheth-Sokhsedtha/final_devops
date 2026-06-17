package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.ProfileType;
import finalexam.vicheth_sokhsedtha.service.PhotoStorageService;
import finalexam.vicheth_sokhsedtha.service.ProfileService;
import finalexam.vicheth_sokhsedtha.service.RegistrationNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final RegistrationNumberGenerator regNumberGenerator;
    private final PhotoStorageService photoStorageService;

    public ProfileController(ProfileService profileService,
                             RegistrationNumberGenerator regNumberGenerator,
                             PhotoStorageService photoStorageService) {
        this.profileService = profileService;
        this.regNumberGenerator = regNumberGenerator;
        this.photoStorageService = photoStorageService;
    }

    /** List all profiles. */
    @GetMapping
    public List<Profile> getAll() {
        return profileService.findAll();
    }

    /** Get a single profile by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Profile> getById(@PathVariable Long id) {
        return profileService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Get a single profile by UUID. */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<Profile> getByUuid(@PathVariable String uuid) {
        return profileService.findByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Search profiles by name. */
    @GetMapping("/search")
    public List<Profile> search(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String department,
                                @RequestParam(required = false) ProfileType type) {
        if (type != null) {
            return profileService.findByType(type);
        }
        if (name != null && !name.isBlank()) {
            return profileService.searchByName(name);
        }
        if (department != null && !department.isBlank()) {
            return profileService.searchByDepartment(department);
        }
        return profileService.findAll();
    }

    /**
     * Create a new profile. Optionally provide a departmentCode for automatic
     * registration number generation (e.g. "ENG", "CS").
     */
    @PostMapping
    public Profile create(@Valid @RequestBody Profile profile,
                          @RequestParam(required = false) String departmentCode) {
        // Auto-generate registration number if not provided
        if (profile.getRegistrationNumber() == null || profile.getRegistrationNumber().isBlank()) {
            String deptCode = departmentCode != null ? departmentCode : "GEN";
            profile.setRegistrationNumber(regNumberGenerator.next(deptCode));
        }
        return profileService.save(profile);
    }

    /** Update an existing profile. */
    @PutMapping("/{id}")
    public ResponseEntity<Profile> update(@PathVariable Long id, @Valid @RequestBody Profile profile) {
        try {
            Profile updated = profileService.update(id, profile);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Delete a profile. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (profileService.findById(id).isPresent()) {
            profileService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ─────────────────────────────────────────────
    //  Photo Upload / Download
    // ─────────────────────────────────────────────

    /** Upload a photo for a profile. */
    @PostMapping("/{id}/photo")
    public ResponseEntity<Profile> uploadPhoto(@PathVariable Long id,
                                                @RequestParam MultipartFile file) throws IOException {
        Optional<Profile> opt = profileService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Profile profile = opt.get();
        String fileName = photoStorageService.store(profile.getUuid(), file);
        profile.setPhotoFileName(fileName);
        profile.setPhotoContentType(file.getContentType());
        profileService.save(profile);
        return ResponseEntity.ok(profile);
    }

    /** Download a profile's photo. */
    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) throws IOException {
        Optional<Profile> opt = profileService.findById(id);
        if (opt.isEmpty() || !opt.get().hasPhoto()) {
            return ResponseEntity.notFound().build();
        }
        Profile profile = opt.get();
        Resource resource = photoStorageService.loadAsResource(profile.getPhotoFileName());
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        profile.getPhotoContentType() != null ? profile.getPhotoContentType() : "image/jpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}