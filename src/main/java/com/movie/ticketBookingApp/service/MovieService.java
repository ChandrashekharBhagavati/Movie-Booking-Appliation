package com.movie.ticketBookingApp.service;

import com.movie.ticketBookingApp.entity.Movie;
import com.movie.ticketBookingApp.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository repo;

    public MovieService(MovieRepository repo) {
        this.repo = repo;
    }

    public List<Movie> getAllMovies() {
        return repo.findAll();
    }
}
