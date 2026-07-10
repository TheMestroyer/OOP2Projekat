package Errors;

public class FileFormatException extends RuntimeException {
    public FileFormatException(String message) {
        super(message);
    }
}
