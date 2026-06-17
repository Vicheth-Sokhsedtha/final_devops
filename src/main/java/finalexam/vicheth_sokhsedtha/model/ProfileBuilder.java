package finalexam.vicheth_sokhsedtha.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Builder helper to create {@link Profile} instances with sensible defaults
 * for each {@link ProfileType}. The caller can override any field afterwards.
 */
public class ProfileBuilder {

    private final Profile.ProfileBuilder builder;

    public ProfileBuilder() {
        this.builder = Profile.builder();
        // Every profile gets a fresh UUID
        builder.uuid(UUID.randomUUID().toString());
        builder.issueDate(LocalDate.now());
        builder.expiryDate(LocalDate.now().plusYears(3));
        builder.barcodeType(BarcodeType.CODE_128);
    }

    /** Start building a profile for the given type. */
    public static ProfileBuilder forType(ProfileType type) {
        return new ProfileBuilder().type(type);
    }

    public ProfileBuilder type(ProfileType type) {
        builder.type(type);
        return this;
    }

    public ProfileBuilder fullName(String fullName) {
        builder.fullName(fullName);
        return this;
    }

    public ProfileBuilder department(String department) {
        builder.department(department);
        return this;
    }

    public ProfileBuilder title(String title) {
        builder.title(title);
        return this;
    }

    public ProfileBuilder email(String email) {
        builder.email(email);
        return this;
    }

    public ProfileBuilder phone(String phone) {
        builder.phone(phone);
        return this;
    }

    public ProfileBuilder bloodGroup(String bloodGroup) {
        builder.bloodGroup(bloodGroup);
        return this;
    }

    public ProfileBuilder dateOfBirth(LocalDate dateOfBirth) {
        builder.dateOfBirth(dateOfBirth);
        return this;
    }

    public ProfileBuilder registrationNumber(String registrationNumber) {
        builder.registrationNumber(registrationNumber);
        return this;
    }

    public ProfileBuilder template(Template template) {
        builder.template(template);
        return this;
    }

    public ProfileBuilder barcodeType(BarcodeType barcodeType) {
        builder.barcodeType(barcodeType);
        return this;
    }

    /** Build a STUDENT profile with sensible defaults. */
    public static Profile createDefaultStudent(String fullName, String department, String email) {
        return Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .type(ProfileType.STUDENT)
                .fullName(fullName)
                .department(department)
                .email(email)
                .issueDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(4))
                .barcodeType(BarcodeType.CODE_128)
                .build();
    }

    /** Build an EMPLOYEE profile with sensible defaults. */
    public static Profile createDefaultEmployee(String fullName, String department, String title, String email) {
        return Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .type(ProfileType.EMPLOYEE)
                .fullName(fullName)
                .department(department)
                .title(title)
                .email(email)
                .issueDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(3))
                .barcodeType(BarcodeType.CODE_128)
                .build();
    }

    /** Build a USER profile with sensible defaults. */
    public static Profile createDefaultUser(String fullName, String email) {
        return Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .type(ProfileType.USER)
                .fullName(fullName)
                .email(email)
                .issueDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(1))
                .barcodeType(BarcodeType.CODE_128)
                .build();
    }

    public Profile build() {
        return builder.build();
    }
}