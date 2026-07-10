package Errors;

public class InvalidFlightData extends RuntimeException {
    public static final int BAD_TIME     = 0;
    public static final int BAD_DURATION = 1;
    public static final int SAME_AIRPORT = 2;

    public InvalidFlightData(int type) {
        super(messageFor(type));
    }

    private static String messageFor(int type) {
        return switch (type) {
            case BAD_DURATION -> "Duration must be a whole number of minutes greater than 0.";
            case SAME_AIRPORT -> "Start and destination airport must differ.";
            default -> "Departure time must be in HH:MM format (e.g. 08:30).";
        };
    }
}
