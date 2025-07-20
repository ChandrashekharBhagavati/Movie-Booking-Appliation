package com.movie.ticketBookingApp.repository;

import com.movie.ticketBookingApp.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
