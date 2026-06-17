package finalexam.vicheth_sokhsedtha;

import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.ProfileType;
import finalexam.vicheth_sokhsedtha.service.ProfileService;
import finalexam.vicheth_sokhsedtha.service.RegistrationNumberGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RegistrationNumberGeneratorTest {

    @Autowired
    private RegistrationNumberGenerator generator;

    @Autowired
    private ProfileService profileService;

    @Test
    void testGenerateRegistrationNumber() {
        String regNumber = generator.next("CS");
        assertNotNull(regNumber);
        assertTrue(regNumber.startsWith("2026-CS-"));
        assertTrue(regNumber.matches("^\\d{4}-[A-Z0-9]+-\\d{3}$"));
    }

    @Test
    void testGenerateWithNullDepartment() {
        String regNumber = generator.next(null);
        assertTrue(regNumber.contains("-GEN-"));
    }

    @Test
    void testGenerateWithBlankDepartment() {
        String regNumber = generator.next("  ");
        assertTrue(regNumber.contains("-GEN-"));
    }

    @Test
    void testSequentialNumbersAreDifferent() {
        // Insert a profile first so the generator sees an existing record
        Profile existing = Profile.builder()
                .uuid("seq-uuid-001")
                .registrationNumber("2026-MGT-005")
                .type(ProfileType.EMPLOYEE)
                .fullName("Existing Employee")
                .department("Management")
                .email("existing@test.com")
                .build();
        profileService.save(existing);

        // Now the next number should be 006
        String regNumber = generator.next("MGT");
        assertTrue(regNumber.endsWith("-006"));
    }
}
