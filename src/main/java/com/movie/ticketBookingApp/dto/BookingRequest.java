package com.movie.ticketBookingApp.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BookingRequest {
    public Long movieId;
    public String date;
    public String time;
    public List<String> seats;
    public boolean paymentMode;
    public String userEmail;

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public void setPaymentMode(boolean paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
