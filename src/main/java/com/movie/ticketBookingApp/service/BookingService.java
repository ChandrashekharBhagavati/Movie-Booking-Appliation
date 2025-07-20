package com.movie.ticketBookingApp.service;

import com.movie.ticketBookingApp.dto.BookingRequest;
import com.movie.ticketBookingApp.entity.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingRequest request,Long userId);

    List<Booking> getBookingByUserId(Long userId);
}
