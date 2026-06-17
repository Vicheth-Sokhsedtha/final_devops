package finalexam.vicheth_sokhsedtha.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "terrains")
@Data
public class Terrain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    private String location;

    @Column(name = "area_size")
    private BigDecimal areaSize;

    @Column(name = "price_per_day")
    private BigDecimal pricePerDay;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_to")
    private LocalDateTime availableTo;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "main_image_id")
    private Long mainImageId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}