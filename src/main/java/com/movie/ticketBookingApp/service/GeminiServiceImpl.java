package com.movie.ticketBookingApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

/**
 * Service implementation for interacting with the Gemini AI model.
 * This class handles sending prompts to the Gemini API and parsing its responses.
 * It uses a direct HTTP client to communicate with the Generative Language API.
 */
@Service // Marks this class as a Spring service component
public class GeminiServiceImpl implements GeminiService {

    // The API key for authenticating with the Gemini API.
    // Injected from application.properties using @Value.
    private final String apiKey;

    /**
     * Constructor for GeminiServiceImpl.
     * Spring will automatically inject the value of 'gemini.api.key' from application.properties.
     *
     * @param apiKey The Gemini API key.
     */
    public GeminiServiceImpl(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        System.out.println("GeminiServiceImpl initialized with API Key (first 5 chars): " + apiKey.substring(0, Math.min(apiKey.length(), 5)) + "...");
    }

    /**
     * Sends a text prompt to the Gemini AI model (gemini-2.0-flash) and returns the generated response.
     * This method constructs an HTTP POST request to the Gemini API endpoint.
     *
     * @param prompt The text prompt (e.g., "Give a short movie overview for the film: Inception").
     * @return The AI's generated text response.
     * @throws RuntimeException if there's an error during the API call or response parsing.
     */
    @Override
    public String ask(String prompt) {
        try {
            System.out.println("Sending prompt to Gemini: " + prompt);

            // The API endpoint for the gemini-2.0-flash model for text generation.
            // We append the API key as a query parameter.
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            // Construct the JSON request body for the Gemini generateContent API.
            // The structure is specific to the Gemini API:
            // { "contents": [ { "role": "user", "parts": [ { "text": "YOUR_PROMPT_HERE" } ] } ] }
            // We escape any double quotes in the prompt to ensure valid JSON.
            String requestBody = String.format(
                    "{\"contents\": [{\"role\": \"user\", \"parts\": [{\"text\": \"%s\"}]}]}",
                    prompt.replace("\"", "\\\"")
            );

            // Create an instance of Java's built-in HttpClient (available from Java 11 onwards).
            HttpClient client = HttpClient.newHttpClient();

            // Build the HTTP POST request.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl)) // Set the target API URL
                    .header("Content-Type", "application/json") // Specify content type as JSON
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // Set the request body
                    .build();

            // Send the HTTP request and get the response.
            // BodyHandlers.ofString() tells the client to return the response body as a String.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the API call was successful (HTTP status code 200).
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                System.out.println("Received raw response from Gemini: " + responseBody);

                // Parse the JSON response to extract the generated text.
                // This is a simple, brittle parsing method. For production, consider using
                // a robust JSON library like Jackson (com.fasterxml.jackson.databind) or Gson (com.google.code.gson).
                String result = extractTextFromJsonResponse(responseBody);

                if (result != null) {
                    return result.trim(); // Return the extracted text, trimmed of whitespace
                } else {
                    // If text extraction fails, throw an error with the full response body for debugging.
                    throw new RuntimeException("Failed to extract text from Gemini response. Raw response: " + responseBody);
                }
            } else {
                // If the API call returns an error status code, log and throw an exception.
                String errorBody = response.body();
                System.err.println("Gemini API call failed with status code " + response.statusCode() + ". Error body: " + errorBody);
                throw new RuntimeException("Gemini API call failed: " + errorBody);
            }

        } catch (IOException | InterruptedException e) {
            // Catch network-related errors (IOException) or thread interruption (InterruptedException).
            e.printStackTrace();
            throw new RuntimeException("Gemini API call failed due to network or interruption error", e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions.
            e.printStackTrace();
            throw new RuntimeException("An unexpected error occurred during Gemini API call", e);
        }
    }

    /**
     * A simple utility method to extract the 'text' content from the Gemini API JSON response.
     * This method is basic and assumes a specific JSON structure.
     * For robust applications, use a dedicated JSON parsing library (e.g., Jackson, Gson).
     *
     * Expected format:
     * {
     * "candidates": [
     * {
     * "content": {
     * "parts": [
     * {
     * "text": "The generated text goes here."
     * }
     * ]
     * }
     * }
     * ]
     * }
     *
     * @param jsonResponse The raw JSON string received from the Gemini API.
     * @return The extracted text, or null if not found.
     */
    private String extractTextFromJsonResponse(String jsonResponse) {
        // Look for the "text": "..." pattern.
        // This is a very simple string search and will break if the JSON structure changes
        // or if "text" appears in other contexts.
        String searchString = "\"text\": \"";
        int textStartIndex = jsonResponse.indexOf(searchString);

        if (textStartIndex != -1) {
            // Adjust start index to point to the beginning of the actual text content.
            textStartIndex += searchString.length();
            // Find the closing double quote for the text.
            // We need to be careful with escaped quotes within the text.
            // A proper JSON parser handles this automatically.
            int textEndIndex = textStartIndex;
            while (textEndIndex < jsonResponse.length()) {
                char currentChar = jsonResponse.charAt(textEndIndex);
                if (currentChar == '"' && jsonResponse.charAt(textEndIndex - 1) != '\\') {
                    // Found an unescaped double quote, which is the end of the text.
                    break;
                }
                textEndIndex++;
            }

            if (textEndIndex < jsonResponse.length()) {
                // Extract the substring containing the text.
                String extracted = jsonResponse.substring(textStartIndex, textEndIndex);
                // Unescape common JSON escaped characters if necessary (e.g., \\n -> \n, \\" -> ")
                // For simplicity, we'll just handle basic unescaping here.
                return extracted.replace("\\n", "\n").replace("\\\"", "\"");
            }
        }
        return null; // Return null if the text part could not be found or parsed.
    }
}
