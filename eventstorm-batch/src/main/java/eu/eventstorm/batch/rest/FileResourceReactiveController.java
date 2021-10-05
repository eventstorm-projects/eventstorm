package eu.eventstorm.batch.rest;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.batch.file.FileResource;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Conditional(FileResourceReactiveControllerCondition.class)
@RestController
public final class FileResourceReactiveController {

	private final FileResource fileResource;
	
	public FileResourceReactiveController(FileResource fileResource) {
		this.fileResource = fileResource;
	}

	@PostMapping(path = "${eu.eventstorm.batch.resource.context-path:}/upload")
	public Mono<UploadResponse> upload(ServerHttpRequest serverRequest) throws IOException {

		java.util.UUID uuid = java.util.UUID.randomUUID();
		
		FileChannel channel = FileChannel.open(fileResource.touch(uuid.toString()), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

		return DataBufferUtils.write(serverRequest.getBody(), channel)
				.doFinally(t -> {
					try {
						channel.close();
					} catch (IOException e) {

					}
				})
				.map(DataBufferUtils::release)
				.reduce((l, r) -> l && r)
				.map(result -> new UploadResponse(uuid.toString()));

	}
	
	@GetMapping(path = "${eu.eventstorm.batch.resource.context-path:}/download/{uuid}")
	public Mono<Void> download(@PathVariable("uuid") String uuid, ServerHttpResponse response) throws IOException {
		if (response instanceof ZeroCopyHttpOutputMessage) {
			ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
			Path file = fileResource.get(uuid);
			return zeroCopyResponse.writeWith(file, 0, Files.size(file));
		} else {
			return null;
		}
	}
	
}
