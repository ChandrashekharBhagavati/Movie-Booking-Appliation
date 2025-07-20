package com.movie.ticketBookingApp.controller;

import com.movie.ticketBookingApp.dto.LoginRequest;
import com.movie.ticketBookingApp.dto.SignupRequest;
import com.movie.ticketBookingApp.entity.User;
import com.movie.ticketBookingApp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;


    public AuthController(AuthService authService) {

        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest request) {
        // 🔒 Add validation & user persistence logic here later
        System.out.println("Received signup request:");
        System.out.println("Name: " + request.getFirstName() + " " + request.getLastName());
        System.out.println("Email: " + request.getEmail());

        String result = authService.registerUser(request);
        HttpStatus status = result.equals("User registered successfully") ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(result, status);
    }
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest request, HttpSession session) {
        User user = authService.loginUser(request);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // Store user ID in session
        session.setAttribute("userId", user.getId());

        return ResponseEntity.ok("Login successful");
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpSession session) {
        session.invalidate();  // clear session and logout
        return ResponseEntity.ok("Logged out successfully");

    }
}
