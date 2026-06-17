package finalexam.vicheth_sokhsedtha.service;

import finalexam.vicheth_sokhsedtha.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Optional;

/**
 * Generates unique registration numbers in YEAR-DEPT-### format.
 * <p>
 * Example: {@code 2026-ENG-001}, {@code 2026-CS-042}
 * </p>
 */
@Service
public class RegistrationNumberGenerator {

    private final ProfileRepository profileRepository;

    public RegistrationNumberGenerator(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Generate the next registration number for the given department prefix.
     *
     * @param departmentCode short department code, e.g. {@code ENG}, {@code CS}, {@code MGT}
     * @return registration string like {@code 2026-ENG-007}
     */
    public String next(String departmentCode) {
        int year = Year.now().getValue();
        String prefix = year + "-" + sanitize(departmentCode) + "-";

        // Find the highest existing sequence number for this year+department
        Optional<String> lastReg = profileRepository.findTopByRegistrationNumberStartingWith(prefix);

        int nextSeq = 1;
        if (lastReg.isPresent()) {
            String last = lastReg.get();
            String seqPart = last.substring(last.lastIndexOf('-') + 1);
            try {
                nextSeq = Integer.parseInt(seqPart) + 1;
            } catch (NumberFormatException ignored) {
                // fall through, start at 1
            }
        }

        return prefix + String.format("%03d", nextSeq);
    }

    private static String sanitize(String code) {
        if (code == null || code.isBlank()) {
            return "GEN";
        }
        return code.toUpperCase().replaceAll("[^A-Z0-9]", "").substring(0, Math.min(5, code.length()));
    }
}