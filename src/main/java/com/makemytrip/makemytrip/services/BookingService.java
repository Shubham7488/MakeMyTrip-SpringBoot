package com.makemytrip.makemytrip.services;
import com.makemytrip.makemytrip.models.Refund;
import com.makemytrip.makemytrip.repositories.RefundRepository;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.CancellationReason;
import com.makemytrip.makemytrip.repositories.UserRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.CancellationReasonRepository;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @SuppressWarnings("null")
    public Users.Booking bookFlight(String userId,String flightId,int seats,double price){
        Optional<Users> usersOptional =userRepository.findById(userId);
        Optional<Flight> flightOptional =flightRepository.findById(flightId);
        if(usersOptional.isPresent() && flightOptional.isPresent()){
            Users user=usersOptional.get();
            Flight flight=flightOptional.get();
            if(flight.getAvailableSeats() >= seats){
                flight.setAvailableSeats(flight.getAvailableSeats()- seats);
                flightRepository.save(flight);

                Users.Booking booking=new Users.Booking();
                booking.setType("Flight");
                booking.setBookingId(flightId);
                booking.setDate(LocalDate.now().toString());
                booking.setQuantity(seats);
                booking.setTotalPrice(price);
                user.getBookings().add(booking);
                userRepository.save(user);
                return booking;
            }else {
                throw new RuntimeException("Not enough seats available");
            }
        }
        throw new RuntimeException("User or flight not found");
    }
    @SuppressWarnings("null")
    public Users.Booking bookhotel(String userId,String hotelId,int rooms,double price, String checkInDate){
        Optional<Users> usersOptional =userRepository.findById(userId);
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if(usersOptional.isPresent() && hotelOptional.isPresent()){
            Users user=usersOptional.get();
            Hotel hotel=hotelOptional.get();
            if(hotel.getAvailableRooms() >= rooms){
                hotel.setAvailableRooms(hotel.getAvailableRooms()- rooms);
                hotelRepository.save(hotel);

                Users.Booking booking=new Users.Booking();
                booking.setType("Hotel");
                booking.setBookingId(hotelId);
                booking.setDate(checkInDate != null ? checkInDate : LocalDate.now().toString());
                booking.setQuantity(rooms);
                booking.setTotalPrice(price);
                user.getBookings().add(booking);
                userRepository.save(user);
                return booking;
            }else {
                throw new RuntimeException("Not enough rooms available");
            }
        }
        throw new RuntimeException("User or flight not found");
    }

    public List<Map<String, Object>> getCancellationReasons() {
        List<Map<String, Object>> reasons = new ArrayList<>();
        
        reasons.add(createReason("1", "Change of Plans", "My travel plans have changed"));
        reasons.add(createReason("2", "Found Better Price", "Found a better deal elsewhere"));
        reasons.add(createReason("3", "Schedule Conflict", "There is a conflict with my schedule"));
        reasons.add(createReason("4", "Medical Emergency", "Medical emergency or health reasons"));
        reasons.add(createReason("5", "Family Issue", "Family issues or urgent matters"));
        reasons.add(createReason("6", "Financial Reasons", "Financial constraints"));
        reasons.add(createReason("7", "Other", "Other reason not listed above"));
        
        return reasons;
    }

    private Map<String, Object> createReason(String id, String name, String description) {
        Map<String, Object> reason = new HashMap<>();
        reason.put("_id", id);
        reason.put("reasonName", name);
        reason.put("description", description);
        reason.put("active", true);
        return reason;
    }

    @SuppressWarnings("null")
    public Map<String, Object> getRefundEligibility(String bookingId, String userId) {
        Optional<Users> usersOptional = userRepository.findById(userId);
        Map<String, Object> response = new HashMap<>();

        if (usersOptional.isPresent()) {
            Users user = usersOptional.get();
            // Find booking by unique ID field (booking.id). If not found, try matching by booking.bookingId (legacy)
            Users.Booking booking = findBookingByIdOrReference(user, bookingId);

            if (booking != null) {
                LocalDate today = LocalDate.now();
                LocalDate travelDate;

                if ("Flight".equals(booking.getType())) {
                    Optional<Flight> flightOpt = flightRepository.findById(booking.getBookingId());
                    if (flightOpt.isPresent()) {
                        LocalDateTime depTime = LocalDateTime.parse(flightOpt.get().getDepartureTime());
                        travelDate = depTime.toLocalDate();
                    } else {
                        travelDate = LocalDate.parse(booking.getDate());
                    }
                } else {
                    travelDate = LocalDate.parse(booking.getDate());
                }

                long daysDiff = ChronoUnit.DAYS.between(today, travelDate);
                
                double refundPercentage = daysDiff > 1 ? 50 : 0;
                double refundAmount = booking.getTotalPrice() * (refundPercentage / 100);

                response.put("eligible", refundPercentage > 0);
                response.put("refundPercentage", refundPercentage);
                response.put("refundAmount", refundAmount);
                response.put("daysUntilTravel", daysDiff);
                response.put("message", refundPercentage > 0 
                    ? "You are eligible for " + refundPercentage + "% refund" 
                    : "No refund available within 24 hours of travel");
                
                return response;
            }
        }

        response.put("eligible", false);
        response.put("refundPercentage", 0);
        response.put("refundAmount", 0);
        response.put("message", "Booking not found");
        return response;
    }

    @SuppressWarnings("null")
    public Map<String, Object> cancelBooking(String bookingId, String userId, 
                                              String reasonId, String notes) {
        Optional<Users> usersOptional = userRepository.findById(userId);
        Map<String, Object> response = new HashMap<>();

        if (usersOptional.isPresent()) {
            Users user = usersOptional.get();
            
                // Search by unique ID (booking.id). If not found, fall back to booking.bookingId so older records still cancel.
                Users.Booking booking = findBookingByIdOrReference(user, bookingId);

            if (booking != null) {
                // Calculate refund based on travel date
                LocalDate today = LocalDate.now();
                LocalDate travelDate;

                if ("Flight".equals(booking.getType())) {
                    Optional<Flight> flightOpt = flightRepository.findById(booking.getBookingId());
                    if (flightOpt.isPresent()) {
                        LocalDateTime depTime = LocalDateTime.parse(flightOpt.get().getDepartureTime());
                        travelDate = depTime.toLocalDate();
                    } else {
                        travelDate = LocalDate.parse(booking.getDate());
                    }
                } else {
                    travelDate = LocalDate.parse(booking.getDate());
                }

                long daysDiff = ChronoUnit.DAYS.between(today, travelDate);
                
                double refundPercentage = daysDiff > 1 ? 50.0 : 0.0;
                double refundAmount = booking.getTotalPrice() * (refundPercentage / 100.0);

                // Get cancellation reason
                String reasonText = "Customer Request";
                if (reasonId != null) {
                    Optional<CancellationReason> reasonOpt = cancellationReasonRepository.findById(reasonId);
                    if (reasonOpt.isPresent()) {
                        reasonText = reasonOpt.get().getReasonName();
                    }
                }

                // Create refund record
                Refund refund = new Refund();
                refund.setRefundId("REF-" + System.currentTimeMillis());
                refund.setUserId(userId);
                refund.setBookingId(bookingId);
                refund.setBookingType(booking.getType());
                refund.setOriginalAmount(booking.getTotalPrice());
                refund.setRefundAmount(refundAmount);
                refund.setRefundPercentage(refundPercentage);
                refund.setStatus(Refund.RefundStatus.PENDING);
                refund.setCancellationReason(reasonText);
                refund.setAdminNotes(notes != null ? notes : "");
                refund.setCreatedDate(LocalDateTime.now());
                refundRepository.save(refund);

                // Remove booking from user's list
                user.getBookings().remove(booking);
                userRepository.save(user);

                // Restore seats/rooms to flight or hotel
                restoreAvailability(booking);

                response.put("success", true);
                response.put("refundId", refund.getRefundId());
                response.put("refundAmount", refundAmount);
                response.put("refundPercentage", refundPercentage);
                response.put("message", "Booking cancelled successfully");
                return response;
            }
        }

        response.put("success", false);
        response.put("error", "Booking not found");
        return response;
    }

    @SuppressWarnings("null")
    private void restoreAvailability(Users.Booking booking) {
        if ("Flight".equals(booking.getType())) {
            Optional<Flight> flight = flightRepository.findById(booking.getBookingId());
            flight.ifPresent(f -> {
                f.setAvailableSeats(f.getAvailableSeats() + booking.getQuantity());
                flightRepository.save(f);
            });
        } else if ("Hotel".equals(booking.getType())) {
            Optional<Hotel> hotel = hotelRepository.findById(booking.getBookingId());
            hotel.ifPresent(h -> {
                h.setAvailableRooms(h.getAvailableRooms() + booking.getQuantity());
                hotelRepository.save(h);
            });
        }
    }

    // Helper: find booking by unique booking.id first, then by booking.bookingId (legacy)
    private Users.Booking findBookingByIdOrReference(Users user, String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) return null;

        // Try unique ID match
        for (Users.Booking b : user.getBookings()) {
            if (b.getId() != null && b.getId().equals(bookingId)) {
                return b;
            }
        }

        // Fallback: try matching the reference bookingId (flight/hotel id)
        for (Users.Booking b : user.getBookings()) {
            if (b.getBookingId() != null && b.getBookingId().equals(bookingId)) {
                return b;
            }
        }

        return null;
    }
}

