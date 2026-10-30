package easymeals.payments.controller.errors;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

/**
 * Helper class with functions to be used in handlers
 */
public class ProblemDetailHelper {

    public static ProblemDetail build(HttpStatus status, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
        return pd;
    }
    
    public static ProblemDetail build(HttpStatus status, String title, String detail) {
        ProblemDetail pd = build(status, detail);
        pd.setTitle(title);
        return pd;
    }
    
    public static ProblemDetail conflict(String detail) {
        return build(HttpStatus.CONFLICT, detail);
    }
}