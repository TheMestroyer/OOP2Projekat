package Errors;

public class SingletonNotInitialized extends InternalErrors {
    public SingletonNotInitialized(String message) {
        super("The singleton was not initialized before use atempt.");
    }
}
