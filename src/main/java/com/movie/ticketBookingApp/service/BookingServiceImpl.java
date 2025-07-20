package com.movie.ticketBookingApp.service;

import com.movie.ticketBookingApp.dto.BookingRequest;
import com.movie.ticketBookingApp.entity.Booking;
import com.movie.ticketBookingApp.entity.Seat;
import com.movie.ticketBookingApp.repository.BookingRepository;
import com.movie.ticketBookingApp.repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repo;
    private final SeatRepository seatRepository;

    public BookingServiceImpl(BookingRepository repo, SeatRepository seatRepository) {
        this.repo = repo;
        this.seatRepository = seatRepository;
    }

    @Transactional
    @Override
    public Booking createBooking(BookingRequest request, Long userId) {
        Booking booking = new Booking();
        booking.setMovieId(request.movieId);
        booking.setDate(LocalDate.parse(request.date));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        String timeStr = request.time.trim().toUpperCase();
        LocalTime parsedTime = LocalTime.parse(timeStr, formatter);
        LocalDate parsedDate = LocalDate.parse(request.date);

        booking.setTime(parsedTime);
        booking.setSeats(request.seats);
        booking.setPaymentMode(request.paymentMode);
        booking.setUserId(userId);
        booking.setCreatedDate(LocalDateTime.now());

        int pricePerTicket = 25;
        booking.setTotalAmount(request.paymentMode ? pricePerTicket * request.seats.size() : 0);

        // ✅ Fetch & lock selected seats
        List<Seat> selectedSeats = seatRepository.findSeatsForUpdate(
                request.movieId,
                parsedDate,
                parsedTime,
                request.seats
        );

        // ✅ Validate and book
        for (Seat seat : selectedSeats) {
            if (seat.isBooked()) {
                throw new IllegalStateException("Seat already booked: " + seat.getSeatNumber());
            }
            seat.setBooked(true);
        }

        // ✅ Save booked seats
        seatRepository.saveAll(selectedSeats);

        // ✅ Save booking info
        return repo.save(booking);
    }


    @Override
    public List<Booking> getBookingByUserId(Long userId){
        return repo.findByUserId(userId);
    }
}
