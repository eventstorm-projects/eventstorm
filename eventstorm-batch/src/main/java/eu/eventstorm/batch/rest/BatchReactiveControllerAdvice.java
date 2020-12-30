package eu.eventstorm.batch.rest;

import eu.eventstorm.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice(basePackageClasses = BatchReactiveControllerAdvice.class)
public final class BatchReactiveControllerAdvice {

    @ExceptionHandler(DatabaseExecutionNotFoundException.class)
    public Mono<ResponseEntity<Problem>> on(DatabaseExecutionNotFoundException  ex, ServerHttpRequest request) {
        return Mono.just(ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(Problem.builder()
                        .withTitle("DatabaseExecutionNotFoundException")
                        .withDetail(ex.getMessage())
                        .withReactiveRequest(request)
                        .with("uuid", ex.getUuid())
                        .withStatus(400)
                        .build()));
    }

}