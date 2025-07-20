package com.movie.ticketBookingApp.repository;


import com.movie.ticketBookingApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId); // you are creating a custom query using this method which internally
                                             // works select * from bookings where user=? through spring data jpa
}
