package eu.eventstorm.starter;

import eu.eventstorm.cqrs.CommandGatewayException;
import eu.eventstorm.problem.Problem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@RestControllerAdvice
final class CommandGatewayControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandGatewayControllerAdvice.class);

    @ExceptionHandler(CommandGatewayException.class)
    public Mono<ResponseEntity<Problem>> on(CommandGatewayException ex, ServerHttpRequest request) {

        LOGGER.info("onCommandGatewayException [{}]", ex.getMessage());

        return Mono.just(ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(Problem.builder()
                        .withTitle("CommandGatewayException")
                        .withDetail(ex.getMessage())
                        .withReactiveRequest(request)
                        .with(ex.getValues())
                        .withStatus(400)
                        .build()));
    }
    
}
