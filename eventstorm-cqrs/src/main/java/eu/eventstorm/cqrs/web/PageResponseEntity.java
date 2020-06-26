package eu.eventstorm.cqrs.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import eu.eventstorm.sql.page.Page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageResponseEntity<T> extends ResponseEntity<Page<T>> {

	public PageResponseEntity(Page<T> body) {
		super(body, getHeaders(body), getStatus(body));
	}
	
	private static <T> HttpStatus getStatus(Page<T> body) {
		if (body.getRange().getStart() == 0 && body.getRange().getEnd() + 1 == body.getTotalElements()) {
			return HttpStatus.OK;
		}
		return HttpStatus.PARTIAL_CONTENT;
	}
	
	private static <T> MultiValueMap<String, String> getHeaders(Page<T> body) {
		HttpHeaders headers = new HttpHeaders();
		if (body.getRange().getEnd() + 1 < body.getTotalElements()) {
			headers.set(HttpHeaders.CONTENT_RANGE, "" + body.getRange().getStart() +"-" + body.getRange().getEnd() + '/' + body.getTotalElements());
		} else {
			headers.set(HttpHeaders.CONTENT_RANGE, "" + body.getRange().getStart() +"-" + (body.getTotalElements()-1) + '/' + body.getTotalElements());
		}
		return headers;
	}
}
