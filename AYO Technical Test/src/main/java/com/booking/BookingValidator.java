package com.booking;

import java.util.*;

public class BookingValidator {

    public static String getExpectedPrice(Map<String, String> booking, List<Map<String, String>> schedules) {
        for (Map<String, String> schedule : schedules) {
            boolean sameVenue = schedule.get("venue_id").equals(booking.get("venue_id"));
            boolean sameDate  = schedule.get("date").equals(booking.get("date"));
            boolean sameStart = schedule.get("start_time").equals(booking.get("start_time"));
            boolean sameEnd   = schedule.get("end_time").equals(booking.get("end_time"));

            if (sameVenue && sameDate && sameStart && sameEnd) {
                return schedule.get("price");
            }
        }
        return null;
    }

    public static boolean isSameSlot(Map<String, String> a, Map<String, String> b) {
        return a.get("venue_id").equals(b.get("venue_id"))
            && a.get("date").equals(b.get("date"))
            && a.get("start_time").equals(b.get("start_time"))
            && a.get("end_time").equals(b.get("end_time"));
    }
}
