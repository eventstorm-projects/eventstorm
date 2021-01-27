package eu.eventstorm.page;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Range {

	private final int start;
	private final int end;
	
	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("start", this.start)
				.append("end", this.end)
				.toString();
	}

}
