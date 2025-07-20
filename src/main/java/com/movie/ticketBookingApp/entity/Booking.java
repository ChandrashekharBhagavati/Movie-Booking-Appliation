package com.movie.ticketBookingApp.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id", insertable = false, updatable = false)
    private Movie movie;

    @Column(name = "movie_id")
    private Long movieId;

    private LocalDate date;
    private LocalTime time;

    @ElementCollection
    private List<String> seats;

    private boolean paymentMode;

    private int totalAmount;



    private LocalDateTime createdDate;
    private Long userId;

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    // Getters & setters


    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public void setPaymentMode(boolean paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

}
