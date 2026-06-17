package finalexam.vicheth_sokhsedtha.service;

import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * Handles photo upload validation (JPEG/PNG only, max 5 MB) and stores files
 * on the local filesystem under the configured {@code photo.upload-dir}.
 */
@Service
public class PhotoStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private final Path uploadDir;

    public PhotoStorageService(@Value("${photo.upload-dir:uploads/photos}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    /**
     * Validate and store an uploaded photo.
     *
     * @param profileUuid the owning profile's UUID (used for a unique file name)
     * @param file        the uploaded multipart file
     * @return the stored file name (relative to upload dir)
     * @throws IOException           if file cannot be stored
     * @throws IllegalArgumentException if validation fails
     */
    public String store(String profileUuid, MultipartFile file) throws IOException {
        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPEG and PNG images are allowed. Got: " + contentType);
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Photo file size must not exceed 5 MB. Got: " + file.getSize() + " bytes");
        }

        // Generate a unique file name to avoid collisions
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if (ext == null || ext.isBlank()) {
            ext = contentType.contains("png") ? "png" : "jpg";
        }
        String fileName = profileUuid + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;

        Path targetPath = uploadDir.resolve(fileName).normalize();

        // Ensure the file is within the upload directory (security check)
        if (!targetPath.startsWith(uploadDir)) {
            throw new IOException("Cannot store file outside of upload directory.");
        }

        Files.copy(file.getInputStream(), targetPath);
        return fileName;
    }

    /**
     * Load a stored photo as a {@link Resource}.
     *
     * @param fileName the file name (as returned by {@link #store})
     * @return the resource, or {@code null} if not found
     */
    public Resource loadAsResource(String fileName) {
        try {
            Path filePath = uploadDir.resolve(fileName).normalize();
            if (!filePath.startsWith(uploadDir)) {
                return null;
            }
            Resource resource = new FileSystemResource(filePath);
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        } catch (Exception ignored) {
            // fall through
        }
        return null;
    }
}