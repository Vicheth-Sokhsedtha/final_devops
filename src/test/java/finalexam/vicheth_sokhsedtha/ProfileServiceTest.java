package finalexam.vicheth_sokhsedtha;

import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.ProfileType;
import finalexam.vicheth_sokhsedtha.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @Test
    void testCreateAndFindProfile() {
        Profile profile = Profile.builder()
                .uuid("test-uuid-001")
                .registrationNumber("2026-TEST-001")
                .type(ProfileType.STUDENT)
                .fullName("Test Student")
                .department("CS")
                .email("student@test.com")
                .build();

        Profile saved = profileService.save(profile);
        assertNotNull(saved.getId());
        assertEquals("Test Student", saved.getFullName());

        Profile found = profileService.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("2026-TEST-001", found.getRegistrationNumber());
    }

    @Test
    void testUpdateProfile() {
        Profile profile = Profile.builder()
                .uuid("test-uuid-002")
                .registrationNumber("2026-TEST-002")
                .type(ProfileType.EMPLOYEE)
                .fullName("John Doe")
                .department("Engineering")
                .email("john@test.com")
                .build();

        Profile saved = profileService.save(profile);

        saved.setFullName("Jane Doe");
        saved.setDepartment("HR");
        Profile updated = profileService.update(saved.getId(), saved);

        assertEquals("Jane Doe", updated.getFullName());
        assertEquals("HR", updated.getDepartment());
    }

    @Test
    void testDeleteProfile() {
        Profile profile = Profile.builder()
                .uuid("test-uuid-003")
                .registrationNumber("2026-TEST-003")
                .type(ProfileType.USER)
                .fullName("Delete Me")
                .email("delete@test.com")
                .build();

        Profile saved = profileService.save(profile);
        assertTrue(profileService.findById(saved.getId()).isPresent());

        profileService.delete(saved.getId());
        assertTrue(profileService.findById(saved.getId()).isEmpty());
    }

    @Test
    void testSearchByName() {
        Profile p1 = Profile.builder()
                .uuid("search-uuid-01")
                .registrationNumber("2026-SCH-001")
                .type(ProfileType.STUDENT).fullName("Alice Wonderland")
                .email("alice@test.com").build();
        Profile p2 = Profile.builder()
                .uuid("search-uuid-02")
                .registrationNumber("2026-SCH-002")
                .type(ProfileType.EMPLOYEE).fullName("Bob Builder")
                .email("bob@test.com").build();

        profileService.save(p1);
        profileService.save(p2);

        var aliceResults = profileService.searchByName("Alice");
        assertEquals(1, aliceResults.size());
        assertEquals("Alice Wonderland", aliceResults.get(0).getFullName());

        var allResults = profileService.searchByName("");
        assertTrue(allResults.size() >= 2);
    }

    @Test
    void testFindByType() {
        // Clean up first - delete any existing test profiles with our UUIDs
        profileService.findByEmail("student-type@test.com").ifPresent(p -> profileService.delete(p.getId()));
        profileService.findByEmail("employee-type@test.com").ifPresent(p -> profileService.delete(p.getId()));

        Profile student = Profile.builder()
                .uuid("type-uuid-01").registrationNumber("2026-TYP-001")
                .type(ProfileType.STUDENT).fullName("Student A")
                .email("student-type@test.com").build();
        Profile employee = Profile.builder()
                .uuid("type-uuid-02").registrationNumber("2026-TYP-002")
                .type(ProfileType.EMPLOYEE).fullName("Employee B")
                .email("employee-type@test.com").build();

        profileService.save(student);
        profileService.save(employee);

        var students = profileService.findByType(ProfileType.STUDENT);
        assertFalse(students.isEmpty());
        assertTrue(students.stream().allMatch(p -> p.getType() == ProfileType.STUDENT));
    }
}