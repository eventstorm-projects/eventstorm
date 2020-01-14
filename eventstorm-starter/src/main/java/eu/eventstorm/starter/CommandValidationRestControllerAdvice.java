package eu.eventstorm.starter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import eu.eventstorm.core.validation.CommandValidationException;
import eu.eventstorm.problem.Problem;

@RestControllerAdvice
final class CommandValidationRestControllerAdvice {

    @ExceptionHandler(CommandValidationException.class)
    public ResponseEntity<Problem> on(CommandValidationException  ex, HttpServletRequest request) { 
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(Problem.builder()
                        .withTitle("CommandValidationException")
                        .withDetail(ex.getMessage())
                        .with(request)
                        .with("command", ex.getCommand())
                        .with("violations", ex.getConstraintViolations())
                        .withStatus(400)
                        .build());
    }
    
}
