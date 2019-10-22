package eu.eventstorm.sql.type.common;

import eu.eventstorm.sql.type.Xml;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobXml extends DefaultBlob implements Xml {

	public BlobXml(byte[] buf) {
		super(buf);
	}

}