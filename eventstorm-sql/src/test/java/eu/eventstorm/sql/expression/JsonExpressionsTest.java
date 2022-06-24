package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.dialect.Dialects;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static eu.eventstorm.sql.expression.JsonPathExpressions.and;
import static eu.eventstorm.sql.expression.JsonPathExpressions.array;
import static eu.eventstorm.sql.expression.JsonPathExpressions.field;
import static eu.eventstorm.sql.expression.JsonPathExpressions.fields;
import static eu.eventstorm.sql.expression.JsonPathFieldExpression.Operation.EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonExpressionsTest {

    @Test
    void simpleTest() {
        JsonPathExpression ep = fields(and(
                field("type", EQUALS, "votingSpace"),
                field("value", EQUALS, "1ebd58f8-5995-6430-5c05-29fffa970b01")));

        Dialect dialect = Dialects.postgres(Mockito.mock(Database.class));
        assertEquals("?((@.type==\"votingSpace\") && (@.value==\"1ebd58f8-5995-6430-5c05-29fffa970b01\"))",dialect.toSql(ep));

        dialect = Dialects.oracle(Mockito.mock(Database.class));
        assertEquals(".((@.type==\"votingSpace\") && (@.value==\"1ebd58f8-5995-6430-5c05-29fffa970b01\"))", dialect.toSql(ep));
    }


    @Test
    void simpleArray() {

        JsonPathExpression ep = array(fields(and(
                field("type", EQUALS, "votingSpace"),
                field("value", EQUALS, "1ebd58f8-5995-6430-5c05-29fffa970b01")))
        );

        Dialect dialect = Dialects.postgres(Mockito.mock(Database.class));
        assertEquals("$[*]?((@.type==\"votingSpace\") && (@.value==\"1ebd58f8-5995-6430-5c05-29fffa970b01\"))",dialect.toSql(ep));

        dialect = Dialects.oracle(Mockito.mock(Database.class));
        assertEquals("$.((@.type==\"votingSpace\") && (@.value==\"1ebd58f8-5995-6430-5c05-29fffa970b01\"))", dialect.toSql(ep));

    }


}
