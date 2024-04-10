package eu.eventstorm.util.unsafe;

import org.junit.jupiter.api.Test;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UnsafeStringTest {

    @Test
    void WellDefinedTest() throws Exception {
        assertUtilClassIsWellDefined(UnsafeString.class);
    }

    @Test
    void valueOfTest() {
        byte[] value = new byte[]{'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', ' ', '!', '!'};
        String stringValue = UnsafeString.valueOf(value);

        assertEquals("Hello World !!", stringValue);

        value[0] = 'h';
        assertEquals("hello World !!", stringValue);
    }

    @Test
    void getCharsTest() {
        String value = "Hello World !!";
        byte[] array = UnsafeString.getBytes(value);

        array[0] = 'T';
        assertEquals("Tello World !!", value);
        
    }


}
