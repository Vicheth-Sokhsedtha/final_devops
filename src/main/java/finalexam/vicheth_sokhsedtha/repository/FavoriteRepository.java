package finalexam.vicheth_sokhsedtha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import finalexam.vicheth_sokhsedtha.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}