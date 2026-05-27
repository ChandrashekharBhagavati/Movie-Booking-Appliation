package com.movie.ticketBookingApp.service;

import com.movie.ticketBookingApp.dto.BookingRequest;
import com.movie.ticketBookingApp.entity.Booking;
import com.movie.ticketBookingApp.entity.Seat;
import com.movie.ticketBookingApp.repository.BookingRepository;
import com.movie.ticketBookingApp.repository.SeatRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repo;
    private final SeatRepository seatRepository;

    public BookingServiceImpl(BookingRepository repo, SeatRepository seatRepository) {
        this.repo = repo;
        this.seatRepository = seatRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public Booking createBooking(BookingRequest request, Long userId) {
        // 1. Init booking
        Booking booking = new Booking();
        booking.setMovieId(request.movieId);
        booking.setDate(LocalDate.parse(request.date));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        LocalTime parsedTime = LocalTime.parse(request.time.trim().toUpperCase(), formatter);

        booking.setTime(parsedTime);
        booking.setSeats(request.seats);
        booking.setPaymentMode(request.paymentMode);
        booking.setUserId(userId);
        booking.setCreatedDate(LocalDateTime.now());

        int pricePerTicket = 25;
        booking.setTotalAmount(request.paymentMode ? pricePerTicket * request.seats.size() : 0);

        // 2. Fetch & lock seats
        LocalDate parsedDate = LocalDate.parse(request.date);
        List<Seat> selectedSeats = seatRepository.findSeatsForUpdate(
                request.movieId, parsedDate, parsedTime, request.seats
        );

        // 3. Validate seats
        for (Seat seat : selectedSeats) {
            if (seat.isBooked()) {
                throw new IllegalStateException("Seat already booked: " + seat.getSeatNumber());
            }
            seat.setBooked(true);
        }

        // 4. Save booking first
        Booking savedBooking = repo.save(booking);

        // 5. Save updated seats
        seatRepository.saveAll(selectedSeats);

        return savedBooking;
    }



    @Override
    public List<Booking> getBookingByUserId(Long userId){
        return repo.findByUserId(userId);
    }
}
