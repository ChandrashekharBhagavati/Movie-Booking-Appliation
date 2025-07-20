package com.movie.ticketBookingApp.controller;

import com.movie.ticketBookingApp.entity.Movie;
import com.movie.ticketBookingApp.entity.User;
import com.movie.ticketBookingApp.repository.MovieRepository;
import com.movie.ticketBookingApp.repository.UserRepository;
import com.movie.ticketBookingApp.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class HomeController {

    private final MovieService movieService;
    private final MovieRepository movieRepository;

    private final UserRepository userRepository;
    public HomeController(MovieService movieService, MovieRepository movieRepository, UserRepository userRepository) {
        this.movieService = movieService;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";  // landing page before login
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login"; // Not logged in
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login"; // User not found in DB
        }

        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("user", user); // 🔥 Add full user object
        model.addAttribute("movies", movies);

        return "home"; // Loads home.html
    }



    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            // User already logged in, redirect to home
            return "redirect:/home";
        }
        return "login"; // show login page
    }


    @GetMapping("/seats")
    public String showSeatsPage(
            @RequestParam("movieId") Long movieId,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            Model model
    ) {
        // Fetch movie info (optional, for header/title display)
        Movie movie = movieRepository.findById(movieId).orElse(null);

        model.addAttribute("movie", movie);
        model.addAttribute("date", date);
        model.addAttribute("time", time);

        return "seats"; // This resolves to `seats.html` in `templates/`
    }

    @GetMapping("/booking-details")
    public String showBookingDetails(
            @RequestParam("movieId") Long movieId,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("seats") String seats,
            Model model
    ) {
        Movie movie = movieRepository.findById(movieId).orElse(null);

        model.addAttribute("movie", movie);
        model.addAttribute("date", date);
        model.addAttribute("time", time);
        model.addAttribute("seats", seats.split(",")); // List of seat codes

        return "booking-details"; // booking-details.html (you'll create this)
    }

}
