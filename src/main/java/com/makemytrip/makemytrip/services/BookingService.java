package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Refund;
import com.makemytrip.makemytrip.repositories.RefundRepository;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.CancellationReason;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.models.Users.Booking;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.repositories.UserRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.CancellationReasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private CancellationReasonRepository cancellationReasonRepository;

    // ================= BOOKING =================

    public Booking bookFlight(String userId, String flightId, int seats, double price) {
        Users user = userRepository.findById(userId).orElseThrow();
        Flight flight = flightRepository.findById(flightId).orElseThrow();

        if (flight.getAvailableSeats() < seats) {
            throw new RuntimeException("Not enough seats available");
        }

        flight.setAvailableSeats(flight.getAvailableSeats() - seats);
        flightRepository.save(flight);

        Booking booking = new Booking();
        booking.setId("BK-" + UUID.randomUUID().toString());
        booking.setType("Flight");
        booking.setBookingId(flightId);
        booking.setDate(LocalDate.now().toString()); // booking date (ISO yyyy-MM-dd)
        booking.setQuantity(seats);
        booking.setTotalPrice(price);

        user.getBookings().add(booking);
        userRepository.save(user);

        return booking;
    }

    public Booking bookhotel(String userId, String hotelId, int rooms, double price, String checkInDate) {
        Users user = userRepository.findById(userId).orElseThrow();
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow();

        if (hotel.getAvailableRooms() < rooms) {
            throw new RuntimeException("Not enough rooms available");
        }

        hotel.setAvailableRooms(hotel.getAvailableRooms() - rooms);
        hotelRepository.save(hotel);

        Booking booking = new Booking();
        booking.setId("BK-" + UUID.randomUUID().toString());
        booking.setType("Hotel");
        booking.setBookingId(hotelId);
        booking.setDate(checkInDate != null ? checkInDate : LocalDate.now().toString());
        booking.setQuantity(rooms);
        booking.setTotalPrice(price);

        user.getBookings().add(booking);
        userRepository.save(user);

        return booking;
    }

    // ================= REFUND ELIGIBILITY =================

    public Map<String, Object> getRefundEligibility(String bookingId, String userId) {
        Map<String, Object> response = new HashMap<>();

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return defaultResponse();

        Booking booking = findBookingByIdOrReference(user, bookingId);
        if (booking == null) return defaultResponse();

        // NEW LOGIC: refund 50% if more than 24 hours before travel, else 0%
        double refundPercentage = 0.0;
        double refundAmount = 0.0;
        try {
            LocalDate bookingDate;
            try {
                bookingDate = LocalDate.parse(booking.getDate());
            } catch (DateTimeParseException ex) {
                // fallback to parse date-time then take date
                bookingDate = LocalDateTime.parse(booking.getDate()).toLocalDate();
            }
            LocalDateTime travelStart = bookingDate.atStartOfDay();
            long hoursUntilTravel = ChronoUnit.HOURS.between(LocalDateTime.now(), travelStart);
            if (hoursUntilTravel >= 24) {
                refundPercentage = 50.0;
            } else {
                refundPercentage = 0.0;
            }
            refundAmount = booking.getTotalPrice() * (refundPercentage / 100);
        } catch (Exception ex) {
            // on parse errors assume not eligible
            refundPercentage = 0.0;
            refundAmount = 0.0;
        }

        response.put("eligible", refundPercentage > 0);
        response.put("refundPercentage", refundPercentage);
        response.put("refundAmount", refundAmount);
        response.put("message",
                refundPercentage > 0
                        ? "Eligible for 50% refund within 24 hours"
                        : "No refund after 24 hours");

        return response;
    }

    // ================= CANCEL BOOKING =================

    public Map<String, Object> cancelBooking(String bookingId, String userId,
                                             String reasonId, String notes) {

        Map<String, Object> response = new HashMap<>();

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return fail(response);

        Booking booking = findBookingByIdOrReference(user, bookingId);
        if (booking == null) return fail(response);

        // NEW LOGIC: refund 50% if more than 24 hours before travel, else 0%
        double refundPercentage = 0.0;
        double refundAmount = 0.0;
        try {
            LocalDate bookingDate;
            try {
                bookingDate = LocalDate.parse(booking.getDate());
            } catch (DateTimeParseException ex) {
                bookingDate = LocalDateTime.parse(booking.getDate()).toLocalDate();
            }
            LocalDateTime travelStart = bookingDate.atStartOfDay();
            long hoursUntilTravel = ChronoUnit.HOURS.between(LocalDateTime.now(), travelStart);
            if (hoursUntilTravel >= 24) {
                refundPercentage = 50.0;
            } else {
                refundPercentage = 0.0;
            }
            refundAmount = booking.getTotalPrice() * (refundPercentage / 100);
        } catch (Exception ex) {
            refundPercentage = 0.0;
            refundAmount = 0.0;
        }

        // reason
        String reasonText = "Customer Request";
        if (reasonId != null) {
            Optional<CancellationReason> reason = cancellationReasonRepository.findById(reasonId);
            if (reason.isPresent()) {
                reasonText = reason.get().getReasonName();
            }
        }

        // create refund
        Refund refund = new Refund();
        refund.setRefundId("REF-" + System.currentTimeMillis());
        refund.setUserId(userId);
        // Use the internal booking ID (not the flight/hotel reference)
        refund.setBookingId(booking.getId());
        refund.setBookingType(booking.getType());
        refund.setOriginalAmount(booking.getTotalPrice());
        refund.setRefundAmount(refundAmount);
        refund.setRefundPercentage(refundPercentage);
        refund.setStatus(Refund.RefundStatus.PENDING);
        refund.setCancellationReason(reasonText);
        refund.setAdminNotes(notes != null ? notes : "");
        refund.setCreatedDate(LocalDateTime.now());

        refundRepository.save(refund);

        // remove booking
        user.getBookings().remove(booking);
        userRepository.save(user);

        restoreAvailability(booking);

        response.put("success", true);
        response.put("refundId", refund.getRefundId());
        response.put("refundAmount", refundAmount);
        response.put("refundPercentage", refundPercentage);

        return response;
    }

    // ================= HELPERS =================

    private Map<String, Object> defaultResponse() {
        Map<String, Object> res = new HashMap<>();
        res.put("eligible", false);
        res.put("refundPercentage", 0);
        res.put("refundAmount", 0);
        return res;
    }

    private Map<String, Object> fail(Map<String, Object> res) {
        res.put("success", false);
        res.put("error", "Booking not found");
        return res;
    }

    private void restoreAvailability(Booking booking) {
        if ("Flight".equals(booking.getType())) {
            flightRepository.findById(booking.getBookingId()).ifPresent(f -> {
                f.setAvailableSeats(f.getAvailableSeats() + booking.getQuantity());
                flightRepository.save(f);
            });
        } else {
            hotelRepository.findById(booking.getBookingId()).ifPresent(h -> {
                h.setAvailableRooms(h.getAvailableRooms() + booking.getQuantity());
                hotelRepository.save(h);
            });
        }
    }

    private Booking findBookingByIdOrReference(Users user, String bookingId) {
        for (Booking b : user.getBookings()) {
            if (bookingId.equals(b.getId()) || bookingId.equals(b.getBookingId())) {
                return b;
            }
        }
        return null;
    }
}