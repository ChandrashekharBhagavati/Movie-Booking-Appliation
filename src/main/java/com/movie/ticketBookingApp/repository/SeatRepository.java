package com.movie.ticketBookingApp.repository;

import com.movie.ticketBookingApp.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByMovieIdAndDateAndTime(Long movieId, LocalDate date, LocalTime time);

    boolean existsByMovieIdAndDateAndTime(Long movieId, LocalDate date, LocalTime time);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.movieId = :movieId AND s.date = :date AND s.time = :time AND s.seatNumber IN :seatNumbers")
    List<Seat> findSeatsForUpdate(
            @Param("movieId") Long movieId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("seatNumbers") List<String> seatNumbers
    );

}
