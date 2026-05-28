package com.movie.ticketBookingApp.service;

import com.movie.ticketBookingApp.entity.Seat;
import com.movie.ticketBookingApp.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    // Main method to get (or create) seats for given movieId-date-time
    public List<Seat> getOrCreateSeats(Long movieId, LocalDate date, LocalTime time) {
        boolean exists = seatRepository.existsByMovieIdAndDateAndTime(movieId, date, time);

        if (!exists) {
            List<Seat> generatedSeats = generateSeats(movieId, date, time);
            seatRepository.saveAll(generatedSeats);
        }

        return seatRepository.findByMovieIdAndDateAndTime(movieId, date, time);
    }

    // Lazy seat generation (A1 to H8 → 8 rows × 8 columns = 64 seats)
    private List<Seat> generateSeats(Long movieId, LocalDate date, LocalTime time) {
        List<Seat> seats = new ArrayList<>();

        for (char row = 'A'; row <= 'H'; row++) {
            for (int col = 1; col <= 8; col++) {
                String seatNumber = row + String.valueOf(col);

                Seat seat = new Seat();
                seat.setSeatNumber(seatNumber);
                seat.setMovieId(movieId);
                seat.setDate(date);
                seat.setTime(time);
                seat.setBooked(false); // initially available

                seats.add(seat);
            }
        }

        return seats;
    }
    public List<String> recommendSeats(
            Long movieId,
            LocalDate date,
            LocalTime time,
            int count
    ) {

        List<Seat> seats =
                getOrCreateSeats(movieId, date, time);

        List<List<Seat>> possibleGroups =
                new ArrayList<>();

        // Preferred row order
        char[] preferredRows =
                {'D', 'E', 'C', 'F', 'B', 'G', 'A', 'H'};

        for (char row : preferredRows) {

            List<Seat> rowSeats = seats.stream()

                    .filter(seat ->
                            seat.getSeatNumber().charAt(0) == row
                    )

                    .sorted((s1, s2) -> {

                        int col1 = Integer.parseInt(
                                s1.getSeatNumber().substring(1)
                        );

                        int col2 = Integer.parseInt(
                                s2.getSeatNumber().substring(1)
                        );

                        return Integer.compare(col1, col2);
                    })

                    .toList();

            for (int i = 0; i <= rowSeats.size() - count; i++) {

                List<Seat> group =
                        rowSeats.subList(i, i + count);

                boolean allAvailable = group.stream()

                        .allMatch(seat -> !seat.isBooked());

                if (allAvailable) {
                    possibleGroups.add(group);
                }
            }
        }

        if (possibleGroups.isEmpty()) {
            return new ArrayList<>();
        }

        List<Seat> bestGroup = null;

        double bestScore = -9999;

        for (List<Seat> group : possibleGroups) {

            double score = calculateSeatScore(group);

            if (score > bestScore) {

                bestScore = score;

                bestGroup = group;
            }
        }

        return bestGroup.stream()
                .map(Seat::getSeatNumber)
                .toList();
    }
    private double calculateSeatScore(
            List<Seat> group
    ) {

        double score = 0;

        for (Seat seat : group) {

            String seatNumber =
                    seat.getSeatNumber();

            char row =
                    seatNumber.charAt(0);

            int col =
                    Integer.parseInt(
                            seatNumber.substring(1)
                    );

        /*
            Horizontal center preference
            Center seats: 4,5
        */

            score -= Math.abs(col - 4.5) * 10;

        /*
            Vertical preference
            Middle rows preferred
        */

            score += switch (row) {

                case 'D', 'E' -> 40;

                case 'C', 'F' -> 30;

                case 'B', 'G' -> 20;

                default -> 10;
            };
        }

        return score;
    }
}
