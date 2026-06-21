package com.taskengine.coordinator.splitter;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DefaultJobSplitterTest {

    private final DefaultJobSplitter splitter = new DefaultJobSplitter();

    @Test
    void testSplitStandardMultilineString() {
        String input = "line1\nline2\nline3";
        List<String> result = splitter.split(input);
        
        assertEquals(3, result.size());
        assertEquals("line1", result.get(0));
        assertEquals("line2", result.get(1));
        assertEquals("line3", result.get(2));
    }

    @Test
    void testSplitWithEmptyLinesAndWhitespace() {
        String input = "line1\n\n  \nline2\r\nline3\n";
        List<String> result = splitter.split(input);
        
        assertEquals(3, result.size());
        assertEquals("line1", result.get(0));
        assertEquals("line2", result.get(1));
        assertEquals("line3", result.get(2));
    }

    @Test
    void testSplitNullOrEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> splitter.split(null));
        assertThrows(IllegalArgumentException.class, () -> splitter.split(""));
        assertThrows(IllegalArgumentException.class, () -> splitter.split("   "));
    }
}
