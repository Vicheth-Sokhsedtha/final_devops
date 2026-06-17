package finalexam.vicheth_sokhsedtha.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import finalexam.vicheth_sokhsedtha.entity.Booking;
import finalexam.vicheth_sokhsedtha.entity.Favorite;
import finalexam.vicheth_sokhsedtha.entity.Payment;
import finalexam.vicheth_sokhsedtha.entity.Review;
import finalexam.vicheth_sokhsedtha.entity.Terrain;
import finalexam.vicheth_sokhsedtha.entity.TerrainImage;
import finalexam.vicheth_sokhsedtha.repository.BookingRepository;
import finalexam.vicheth_sokhsedtha.repository.FavoriteRepository;
import finalexam.vicheth_sokhsedtha.repository.PaymentRepository;
import finalexam.vicheth_sokhsedtha.repository.ReviewRepository;
import finalexam.vicheth_sokhsedtha.repository.TerrainImageRepository;
import finalexam.vicheth_sokhsedtha.repository.TerrainRepository;

@Configuration
public class DatabaseSeeder {

    @Bean
        CommandLineRunner seed(
            TerrainRepository terrainRepo,
            TerrainImageRepository imageRepo,
            BookingRepository bookingRepo,
            PaymentRepository paymentRepo,
            ReviewRepository reviewRepo,
            FavoriteRepository favoriteRepo) {

            return args -> {

                if (terrainRepo.count() == 0) {

                    Terrain terrain = new Terrain();
                    terrain.setTitle("Football Field A");
                    terrain.setLocation("Phnom Penh");

                    terrain = terrainRepo.save(terrain);

                    TerrainImage image = new TerrainImage();
                    image.setTerrainId(terrain.getId());
                    image.setImagePath("fieldA.jpg");
                    imageRepo.save(image);

                    Booking booking = new Booking();
                    booking.setTerrainId(terrain.getId());
                    booking.setRenterId(1L);
                    bookingRepo.save(booking);

                    Payment payment = new Payment();
                    payment.setBookingId(booking.getId());
                    payment.setPaymentMethod("ABA");
                    paymentRepo.save(payment);

                    Review review = new Review();
                    review.setTerrainId(terrain.getId());
                    review.setUserId(1L);
                    review.setRating(5);
                    review.setComment("Excellent field");
                    reviewRepo.save(review);

                    Favorite favorite = new Favorite();
                    favorite.setTerrainId(terrain.getId());
                    favorite.setUserId(1L);
                    favoriteRepo.save(favorite);
                }
            };
    }
}