package finalexam.vicheth_sokhsedtha.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "terrain_images")
@Data
public class TerrainImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "terrain_id")
    private Long terrainId;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
}