package com.tiaadeals.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTest {

    @Test
    void testBasicAssertion() {
        String expected = "Hello World";
        String actual = "Hello World";
        assertEquals(expected, actual);
    }

    @Test
    void testTrueAssertion() {
        assertTrue(true);
    }
}
