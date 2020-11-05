package eu.eventstorm.cqrs.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.page.Page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageResponseEntity<T> extends ResponseEntity<Page<T>> {

	private static final ImmutableList<String> ACCESS_CONTROL_EXPOSE_HEADERS = ImmutableList.of(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_RANGE);

	public PageResponseEntity(Page<T> body) {
		super(body, getHeaders(body), getStatus(body));
	}

	private static <T> HttpStatus getStatus(Page<T> body) {
		if (body.getRange().getStart() == 0 && body.getRange().getEnd() + 1 == body.getTotalElements() || body.getTotalElements() == 0) {
			return HttpStatus.OK;
		}
		return HttpStatus.PARTIAL_CONTENT;
	}

	private static <T> MultiValueMap<String, String> getHeaders(Page<T> body) {
		HttpHeaders headers = new HttpHeaders();
		if (body.getRange().getEnd() + 1 < body.getTotalElements()) {
			headers.set(HttpHeaders.CONTENT_RANGE, "" + body.getRange().getStart() + "-" + body.getRange().getEnd() + '/' + body.getTotalElements());
		} else {
			headers.set(HttpHeaders.CONTENT_RANGE, "" + body.getRange().getStart() + "-" + (body.getTotalElements() - 1) + '/' + body.getTotalElements());
		}
		// The XMLHttpRequest 2 object has a getResponseHeader() method that returns the value of a particular response header.
		// During a CORS request, the getResponseHeader() method can only access simple response headers.
		headers.setAccessControlExposeHeaders(ACCESS_CONTROL_EXPOSE_HEADERS);
		return headers;
	}
}
