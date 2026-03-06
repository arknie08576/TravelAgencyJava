package pl.travelagency.api;

public final class ErrorType {
    private ErrorType() {}

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_AUTHENTICATED = "NOT_AUTHENTICATED";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String LOGIN_ALREADY_EXISTS = "LOGIN_ALREADY_EXISTS";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
}