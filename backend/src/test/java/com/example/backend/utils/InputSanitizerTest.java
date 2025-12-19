package com.example.backend.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InputSanitizerTest {
    @Test
    public void testSanitizeTrimAndCut() {
        String in = "   <hola>mundo</hola>   ";
        String out = InputSanitizer.sanitize(in, 10);
        assertFalse(out.contains("<"));
        assertFalse(out.contains(">"));
        assertTrue(out.length() <= 10);
    }

    @Test
    public void testValidEmail() {
        assertTrue(InputSanitizer.isValidEmail("user@example.com"));
        assertFalse(InputSanitizer.isValidEmail("user@@example"));
    }
}
