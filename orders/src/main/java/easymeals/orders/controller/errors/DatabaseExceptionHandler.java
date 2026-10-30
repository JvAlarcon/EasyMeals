package easymeals.orders.controller.errors;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler for PostgreSQL exceptions. It would not work for other databases.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseExceptionHandler {
	
    private static final Logger log = LoggerFactory.getLogger(DatabaseExceptionHandler.class);

    private static ProblemDetail fromConstraint(ServerErrorMessage error) {
        String constraint = error.getConstraint();
        return switch (error.getConstraint()) {
            case "order_must_have_items" -> {
                log.warn("Constraing violated: {}", constraint);
                yield ProblemDetailHelper.build(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Order without items",
                     "An order must have at least one item");
            }
            case "order_status_transition_allowed" -> {
                log.warn("Constraing violated: {}, ({})", constraint, error.getMessage());
                yield ProblemDetailHelper.build(HttpStatus.CONFLICT,
                    "Invalid status transition for order",
                    error.getMessage());
            }
            case null, default -> {
                log.warn("Unmapped constraint violation: constraint={}, sqlState={}", constraint, error.getSQLState());
                yield ProblemDetailHelper.conflict("Data integrity violation");
            }
        };
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handle(DataIntegrityViolationException e) {
        return switch(NestedExceptionUtils.getMostSpecificCause(e)) {
            case PSQLException pe when pe.getServerErrorMessage() !=  null ->
                fromConstraint(pe.getServerErrorMessage());
            default -> {
                log.error("Data integrity violation without PostgreSQL details", e);
                yield ProblemDetailHelper.conflict("Data integrity violation");
            }
        };
    }
}