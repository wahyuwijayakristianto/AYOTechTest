package com.booking;

import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    static List<Map<String, String>> schedules;
    static List<Map<String, String>> bookings;

    @BeforeAll
    static void loadData() throws Exception {
        schedules = ExcelReader.read("data/schedules.xlsx");
        bookings  = ExcelReader.read("data/bookings.xlsx");

        for (Map<String, String> b : bookings) {
            System.out.println(b);
        }
    }

    @Test
    @DisplayName("Valid bookings")
    void validBookings() {
        List<String> valid = new ArrayList<>();

        for (Map<String, String> booking : bookings) {
            String id = booking.get("booking_id");
            if (id == null || id.isEmpty()) continue;

            String expectedPrice = BookingValidator.getExpectedPrice(booking, schedules);
            if (expectedPrice == null) continue;

            boolean priceOk     = booking.get("price").equals(expectedPrice);
            boolean duplicateOk = !hasDuplicate(booking, bookings);

            if (priceOk && duplicateOk) {
                valid.add(id + " — venue " + booking.get("venue_id")
                        + ", date " + booking.get("date")
                        + ", time " + booking.get("start_time") + " to " + booking.get("end_time")
                        + ", price " + booking.get("price"));
            }
        }

        assertFalse(valid.isEmpty(), "No valid bookings found");
        System.out.println("\nValid bookings:");
        for (String v : valid) System.out.println("  " + v);
    }

    @Test
    @DisplayName("Invalid bookings")
    void invalidBookings() {
        List<String> invalid = new ArrayList<>();

        for (Map<String, String> booking : bookings) {
            String id = booking.get("booking_id");
            if (id == null || id.isEmpty()) continue;

            String expectedPrice = BookingValidator.getExpectedPrice(booking, schedules);
            if (expectedPrice == null) {
                invalid.add(id + ": No matching schedule");
                continue;
            }

            if (!booking.get("price").equals(expectedPrice)) {
                invalid.add(id + ": Wrong price - Expected " + booking.get("price") + ", Actual " + expectedPrice);
            }
        }

        List<String> seen = new ArrayList<>();
        for (int i = 0; i < bookings.size(); i++) {
            for (int j = i + 1; j < bookings.size(); j++) {
                Map<String, String> a = bookings.get(i);
                Map<String, String> b = bookings.get(j);
                if (a.get("booking_id") == null || b.get("booking_id") == null) continue;

                if (BookingValidator.isSameSlot(a, b)) {
                    String idA = a.get("booking_id");
                    String idB = b.get("booking_id");

                    String existing = null;
                    for (String s : seen) {
                        if (s.contains(idA) || s.contains(idB)) {
                            existing = s;
                            break;
                        }
                    }

                    if (existing != null) {
                        seen.remove(existing);
                        if (!existing.contains(idA)) existing = existing + ", " + idA;
                        if (!existing.contains(idB)) existing = existing + ", " + idB;
                        seen.add(existing);
                    } else {
                        seen.add(idA + ", " + idB);
                    }
                }
            }
        }
        for (String group : seen) {
            invalid.add("Duplicate " + group);
        }

        assertTrue(invalid.isEmpty(), "\nInvalid bookings:\n  " + String.join("\n  ", invalid));
    }

    private boolean hasDuplicate(Map<String, String> target, List<Map<String, String>> all) {
        for (Map<String, String> other : all) {
            if (other == target) continue;
            if (other.get("booking_id") == null) continue;
            if (BookingValidator.isSameSlot(target, other)) return true;
        }
        return false;
    }
}