package eu.eventstorm.sql.domain;

import eu.eventstorm.util.ToStringBuilder;

final class PageableImpl implements Pageable {

    private final int page;
    private final int size;

    PageableImpl(int page, int size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public int getPageSize() {
        return this.size;
    }

    @Override
    public int getPageNumber() {
        return this.page;
    }

    @Override
    public Pageable next() {
        return new PageableImpl(page+1, size);
    }

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("page", page)
				.append("size", size)
				.toString();
	}
    
}
