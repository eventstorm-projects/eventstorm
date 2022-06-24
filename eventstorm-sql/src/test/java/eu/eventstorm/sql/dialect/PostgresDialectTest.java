package eu.eventstorm.sql.dialect;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.expression.JsonPathExpressions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgresDialectTest {

    @Test
    void testRewritePath() {

        Dialect dialect = Dialects.postgres(Mockito.mock(Database.class));

        assertEquals("col->>'name'", dialect.functionJsonValue("col", JsonPathExpressions.path("name")));
        assertEquals("col->'name'->>'value'", dialect.functionJsonValue("col", JsonPathExpressions.path("name","value")));
        assertEquals("col->'name'->'value'->>'toto'", dialect.functionJsonValue("col", JsonPathExpressions.path("name","value","toto")));

    }
}
