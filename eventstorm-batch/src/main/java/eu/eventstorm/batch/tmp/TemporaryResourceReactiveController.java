package eu.eventstorm.batch.tmp;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public final class TemporaryResourceReactiveController {

	private final TemporaryResource temporaryResource;
	
	public TemporaryResourceReactiveController(eu.eventstorm.batch.tmp.TemporaryResource temporaryResource) {
		this.temporaryResource = temporaryResource;
	}

	@PostMapping(path = "${eu.eventstorm.batch.temporary.context-path:}/upload")
	public Mono<UploadResponse> upload(ServerHttpRequest serverRequest) throws IOException {

		java.util.UUID uuid = java.util.UUID.randomUUID();
		
		FileChannel channel = FileChannel.open(temporaryResource.touch(uuid.toString()), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

		return DataBufferUtils.write(serverRequest.getBody(), channel)
				.map(DataBufferUtils::release)
				.reduce((l, r) -> l && r)
				.doOnNext(t -> {
					try {
						channel.close();
					} catch (IOException e) {
						
					}
				})
				.map(result -> new UploadResponse(uuid.toString()));

	}
	
	@GetMapping(path = "${eu.eventstorm.batch.temporary.context-path:}/download/{uuid}")
	 public Mono<Void> download(@PathVariable("uuid") String uuid, ServerHttpResponse response) throws IOException {
		if (response instanceof ZeroCopyHttpOutputMessage) {
			ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
	        Path file = Paths.get(System.getProperty("java.io.tmpdir"), uuid); 
	        return zeroCopyResponse.writeWith(file, 0, Files.size(file));	
		} else {
			return null;
		}
		
		
    }
	
}
