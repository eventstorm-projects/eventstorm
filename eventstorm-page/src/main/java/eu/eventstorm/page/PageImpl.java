package eu.eventstorm.page;

import java.util.stream.Stream;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageImpl<T> implements Page<T> {

    private final Stream<T> content;
    private final long total;
    private final Range range;

    public PageImpl(Stream<T> content, long total, Range range) {
        this.content = content;
        this.total = total;
        this.range = range;
    }

    @Override
    public long getTotalElements() {
        return this.total;
    }

    @Override
    public Stream<T> getContent() {
        return this.content;
    }

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("total", total)
				.append("range", range)
				.toString();
	}

	@Override
	public Range getRange() {
		return this.range;
	}

}

