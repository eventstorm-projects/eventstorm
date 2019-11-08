package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_ADAPTEE;

import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobJson extends DefaultBlob implements Json {

    private BlobJsonAdaptee adaptee;

	public BlobJson(byte[] buf) {
        super(buf);
        adaptee = null;
    }

    public BlobJson(BlobJsonAdaptee adaptee) {
        super(DefaultBlob.EMTPY);
        this.adaptee = adaptee;
    }

	@Override
	public JsonMap asMap() {
        if (adaptee == null) {
            this.adaptee = new BlobJsonMap(this.getBuf());
        }
        if (adaptee instanceof JsonMap) {
            return JsonMap.class.cast(adaptee);
        }
		throw new SqlTypeException(SqlTypeException.Type.AS_MAP_INVALID, of(PARAM_ADAPTEE, adaptee));
	}

	@Override
	public JsonList asList() {
		if (adaptee == null) {
            this.adaptee = new BlobJsonList(this.getBuf());
        }
        if (adaptee instanceof JsonList) {
            return JsonList.class.cast(adaptee);
        }
		throw new SqlTypeException(SqlTypeException.Type.AS_LIST_INVALID, of(PARAM_ADAPTEE, adaptee));
	}

    @Override
	public void flush() {
        if (adaptee != null) {
            byte[] content = this.adaptee.write();
            if (content != null) {
                setBuf(content);
            }
        }
	}
}