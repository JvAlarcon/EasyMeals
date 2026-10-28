package easymeals.orders.controller;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static ProblemDetail conflict(String detail) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail);
    }
    
    private static ProblemDetail fromConstraint(ServerErrorMessage error) {
        return switch (error.getConstraint()) {
            case "order_must_have_item" ->
                ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, "An order must have at least one item");
            case "order_status_transition_allowed" -> conflict(error.getMessage());
            case null, default -> conflict("Data integrity violation");
        };
    }
	
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handle(DataIntegrityViolationException e) {
        return switch(NestedExceptionUtils.getMostSpecificCause(e)) {
            case PSQLException pe when pe.getServerErrorMessage() !=  null ->
                fromConstraint(pe.getServerErrorMessage());
            default -> conflict("Data integrity violation");
        };
    }
    
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}