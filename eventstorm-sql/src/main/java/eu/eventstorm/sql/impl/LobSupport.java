package eu.eventstorm.sql.impl;

import java.sql.Blob;
import java.sql.Clob;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface LobSupport {

	Blob createBlob();
	
	Clob createClob();
	
}
