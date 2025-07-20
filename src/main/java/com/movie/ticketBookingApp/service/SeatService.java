package com.movie.ticketBookingApp.service;

import com.movie.ticketBookingApp.entity.Seat;
import com.movie.ticketBookingApp.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    // Main method to get (or create) seats for given movieId-date-time
    public List<Seat> getOrCreateSeats(Long movieId, LocalDate date, LocalTime time) {
        boolean exists = seatRepository.existsByMovieIdAndDateAndTime(movieId, date, time);

        if (!exists) {
            List<Seat> generatedSeats = generateSeats(movieId, date, time);
            seatRepository.saveAll(generatedSeats);
        }

        return seatRepository.findByMovieIdAndDateAndTime(movieId, date, time);
    }

    // Lazy seat generation (A1 to H8 → 8 rows × 8 columns = 64 seats)
    private List<Seat> generateSeats(Long movieId, LocalDate date, LocalTime time) {
        List<Seat> seats = new ArrayList<>();

        for (char row = 'A'; row <= 'H'; row++) {
            for (int col = 1; col <= 8; col++) {
                String seatNumber = row + String.valueOf(col);

                Seat seat = new Seat();
                seat.setSeatNumber(seatNumber);
                seat.setMovieId(movieId);
                seat.setDate(date);
                seat.setTime(time);
                seat.setBooked(false); // initially available

                seats.add(seat);
            }
        }

        return seats;
    }
}
