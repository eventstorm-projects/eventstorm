package eu.eventstorm.starter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.problem.Problem;
import reactor.core.publisher.Mono;

@RestControllerAdvice
final class CommandValidationRestControllerAdvice {

    @ExceptionHandler(CommandValidationException.class)
    public Mono<ResponseEntity<Problem>> on(CommandValidationException  ex, ServerHttpRequest request) { 
        return Mono.just(ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(Problem.builder()
                        .withTitle("CommandValidationException")
                        .withDetail(ex.getMessage())
                        .withReactiveRequest(request)
                        .with("command", ex.getCommand())
                        .with("violations", ex.getConstraintViolations())
                        .withStatus(400)
                        .build()));
    }
    
}
