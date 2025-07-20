package com.movie.ticketBookingApp.entity;

// Step 1: Seat Entity

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "seats")
@Data
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;  // e.g., A1, B3

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "booked", nullable = false)
    private boolean booked; // false = available, true = booked

    // Constructors
    public Seat() {}

}



