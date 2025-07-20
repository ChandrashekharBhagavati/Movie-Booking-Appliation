
package com.movie.ticketBookingApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import  com.movie.ticketBookingApp.service.GeminiService;

@RestController
@RequestMapping("/api")
public class AiController {

    private final GeminiService geminiService; // Change type and name

    public AiController(GeminiService geminiService) { // Change constructor parameter
        this.geminiService = geminiService;
    }

    @GetMapping("/ai-overview")
    public ResponseEntity<String> getAiOverview(@RequestParam String movieName) {
        System.out.println("movieName ..." + movieName);
        // Your static prompt remains the same
        String prompt = "Give a short movie overview for the film: " + movieName;
        try {
            String response = geminiService.ask(prompt); // Call the new service method
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI failed to fetch info: " + e.getMessage()); // Provide more detail in error
        }
    }
}

