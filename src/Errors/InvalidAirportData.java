package Errors;

public class InvalidAirportData extends RuntimeException {
    public static final int BAD_CODE = 0;
    public static final int BAD_X    = 1;
    public static final int BAD_Y    = 2;

    public InvalidAirportData(int type) {
        super(messageFor(type));
    }

    private static String messageFor(int type) {
        return switch (type) {
            case BAD_X -> "X coordinate must be a whole number between -180 and 180.";
            case BAD_Y -> "Y coordinate must be a whole number between -90 and 90.";
            default -> "Airport code must be exactly 3 uppercase letters (A-Z).";
        };
    }
}
