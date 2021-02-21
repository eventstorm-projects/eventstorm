package eu.eventstorm.starter;

import eu.eventstorm.cqrs.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.problem.Problem;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@RestControllerAdvice
final class CommandValidationRestControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandValidationRestControllerAdvice.class);

    @ExceptionHandler(CommandValidationException.class)
    public Mono<ResponseEntity<Problem>> on(CommandValidationException  ex, ServerHttpRequest request) {

        LOGGER.info("onCommandValidationException [{}]", ex.getMessage());

        return Mono.just(ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(Problem.builder()
                        .withTitle("CommandValidationException")
                        .withDetail(ex.getMessage())
                        .withReactiveRequest(request)
                        .with("command", ex.getCommand())
                        .with("violations", ex.getConstraintViolations())
                        .with("code" , ex.getCode())
                        .withStatus(400)
                        .build()));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<Problem>> on(ValidationException  ex, ServerHttpRequest request) {

        LOGGER.info("onValidationException [{}]", ex.getMessage());

        return Mono.just(ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(Problem.builder()
                        .withTitle("ValidationException")
                        .withDetail(ex.getMessage())
                        .withReactiveRequest(request)
                        .with("violations", ex.getConstraintViolations())
                        .with("code" , ex.getCode())
                        .withStatus(400)
                        .build()));
    }
    
}
