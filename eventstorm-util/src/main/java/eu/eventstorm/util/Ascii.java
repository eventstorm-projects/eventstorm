package eu.eventstorm.util;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Ascii {

	private Ascii() {
	}

	public static boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	public static int digit(char c) {
		return c - '0';
	}
	
}