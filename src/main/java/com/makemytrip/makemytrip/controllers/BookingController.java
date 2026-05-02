package com.makemytrip.makemytrip.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.services.BookingService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/flight")
    public Users.Booking bookFlight(@RequestParam String userId,@RequestParam String flightId,@RequestParam int seats,@RequestParam double price){
        return bookingService.bookFlight(userId,flightId,seats,price);
    }
    @PostMapping("/hotel")
    public Users.Booking bookhotel (@RequestParam String userId,@RequestParam String hotelId,@RequestParam int rooms,@RequestParam double price, @RequestParam(required = false) String checkInDate){
        return bookingService.bookhotel(userId,hotelId,rooms,price,checkInDate);
    }

    @GetMapping("/cancel/reasons/active")
    public List<Map<String, Object>> getCancellationReasons() {
        return bookingService.getCancellationReasons();
    }

    @GetMapping("/{bookingId}/refund-eligibility")
    public Map<String, Object> getRefundEligibility(@PathVariable String bookingId, @RequestParam String userId) {
        return bookingService.getRefundEligibility(bookingId, userId);
    }

    @PostMapping("/cancel/{bookingId}")
    public Map<String, Object> cancelBooking(
            @PathVariable String bookingId,
            @RequestParam String userId,
            @RequestParam(required = false) String cancellationReasonId,
            @RequestParam(required = false) String adminNotes) {
        return bookingService.cancelBooking(bookingId, userId, cancellationReasonId, adminNotes);
    }
}
