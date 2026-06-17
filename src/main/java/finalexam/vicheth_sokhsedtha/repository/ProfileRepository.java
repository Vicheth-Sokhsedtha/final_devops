package finalexam.vicheth_sokhsedtha.repository;

import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Profile> findByRegistrationNumber(String registrationNumber);

    Optional<Profile> findByUuid(String uuid);

    List<Profile> findByType(ProfileType type);

    List<Profile> findByFullNameContainingIgnoreCase(String name);

    List<Profile> findByDepartmentContainingIgnoreCase(String department);

    /**
     * Find the last (highest) registration number with a given prefix.
     * Used by {@code RegistrationNumberGenerator} for sequential numbering.
     */
    @Query("SELECT p.registrationNumber FROM Profile p WHERE p.registrationNumber LIKE :prefix% ORDER BY p.registrationNumber DESC")
    Optional<String> findTopByRegistrationNumberStartingWith(@Param("prefix") String prefix);
}