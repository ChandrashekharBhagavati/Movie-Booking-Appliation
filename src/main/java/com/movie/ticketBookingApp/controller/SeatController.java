package com.movie.ticketBookingApp.controller;

import com.movie.ticketBookingApp.entity.Seat;
import com.movie.ticketBookingApp.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping
    public List<Seat> getSeatsForMovie(
            @RequestParam Long movieId,
            @RequestParam String date,
            @RequestParam String time
    ) {
        LocalDate parsedDate = LocalDate.parse(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        LocalTime parsedTime = LocalTime.parse(time, formatter);


        return seatService.getOrCreateSeats(movieId, parsedDate, parsedTime);
    }
}
