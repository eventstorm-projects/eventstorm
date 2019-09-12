package eu.eventstorm.sql.jdbc;

import java.sql.PreparedStatement;

public interface M3PreparedStatement extends PreparedStatement {

    void free();

}