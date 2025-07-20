package com.movie.ticketBookingApp.controller;

import com.movie.ticketBookingApp.dto.BookingRequest;
import com.movie.ticketBookingApp.entity.Booking;
import com.movie.ticketBookingApp.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class BookingController {

    private final BookingService service; // interface

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<?> handleBooking(@RequestBody BookingRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            Booking savedBooking = service.createBooking(request, userId);
            return ResponseEntity.ok(savedBooking);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }

    }

    @GetMapping("/booking-history")
    public String showBookingHistory(Model model, HttpSession session){
        Long userId = (Long)session.getAttribute("userId");

        if(userId==null){
            System.out.println("USER ID NULL");
            return "redirect:/Home";
        }
        List<Booking> bookingHistory = service.getBookingByUserId(userId);
        model.addAttribute("bookingHistory",bookingHistory);
        return "booking-history";
    }




}

