package eu.eventstorm.sql.dialect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PostgresDialectTest {

    @Test
    void testRewritePath() {
        Assertions.assertEquals("->>'name'", PostgresDialect.rewritePath("$.name"));
        Assertions.assertEquals("->'name'->>'value'", PostgresDialect.rewritePath("$.name.value"));
        Assertions.assertEquals("->'name'->'value'->>'toto'", PostgresDialect.rewritePath("$.name.value.toto"));
    }
}
