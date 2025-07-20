package com.movie.ticketBookingApp.service;

import com.movie.ticketBookingApp.dto.LoginRequest;
import com.movie.ticketBookingApp.dto.SignupRequest;
import com.movie.ticketBookingApp.entity.User;
import com.movie.ticketBookingApp.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {


    private final UserService userService;


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public String registerUser(SignupRequest request) {
        if (userService.findByEmail(request.getEmail()).isPresent()) {
            return "Email already registered";
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // encrypt password
        user.setUserRoleId(2);
        userService.saveUser(user);

        return "User registered successfully";
    }
    public User loginUser(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) return null;

        User user = userOptional.get();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return user; // ✅ Return user instead of boolean
        }

        return null;
    }

}
