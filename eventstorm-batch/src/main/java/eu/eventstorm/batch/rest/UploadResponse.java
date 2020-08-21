package eu.eventstorm.batch.rest;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class UploadResponse {

	private final String uuid;

	public UploadResponse(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}
	
}
