package com.movie.ticketBookingApp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "movie_master")
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "movie_name", nullable = false)
    private String movieName;

    @Column(name = "description")
    private String description;

    // Constructors
    public Movie() {}


    public Movie(String movieName, String description) {
        this.movieName = movieName;
        this.description = description;
    }

    // Getters and Setters

}
