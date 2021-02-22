package eu.eventstorm.starter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.problem.Problem;

@RestControllerAdvice
final class CommandValidationRestControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandValidationRestControllerAdvice.class);

    @ExceptionHandler(CommandValidationException.class)
    public ResponseEntity<Problem> on(CommandValidationException  ex, HttpServletRequest request) {

        LOGGER.info("onCommandValidationException [{}]", ex.getMessage());

        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(Problem.builder()
                        .withTitle("CommandValidationException")
                        .withDetail(ex.getMessage())
                        .withServletRequest(request)
                        .with("command", ex.getCommand())
                        .with("code", ex.getCode())
                        .with("violations", ex.getConstraintViolations())
                        .withStatus(400)
                        .build());
    }
    
}
