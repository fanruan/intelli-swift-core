package com.fr.swift.log.impl;

import com.fr.swift.structure.Pair;
import com.fr.swift.util.Optional;
import com.fr.swift.util.Strings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author anchore
 * @date 2019/1/9
 */
public class MsgFmtTest {

    @Test
    public void fmt() {
        Pair<String, Optional<Throwable>> formatted = MsgFmt.fmt("abc");
        assertEquals("abc", formatted.getKey());
        assertFalse(formatted.getValue().isPresent());

        formatted = MsgFmt.fmt("ab{}d{}", "c", 'e');
        assertEquals("abcde", formatted.getKey());
        assertFalse(formatted.getValue().isPresent());

        formatted = MsgFmt.fmt("ab{}d{} {}", "c", 'e');
        assertEquals("abcde {}", formatted.getKey());
        assertFalse(formatted.getValue().isPresent());

        formatted = MsgFmt.fmt("ab{}d", "c", 'e');
        assertEquals("abcd", formatted.getKey());
        assertFalse(formatted.getValue().isPresent());

        Exception ex = new Exception();
        formatted = MsgFmt.fmt("ab", ex);
        assertEquals("ab", formatted.getKey());
        assertEquals(ex, formatted.getValue().get());

        ex = new RuntimeException();
        formatted = MsgFmt.fmt("ab{}", "c", ex);
        assertEquals("abc", formatted.getKey());
        assertEquals(ex, formatted.getValue().get());

        ex = new Exception();
        formatted = MsgFmt.fmt(null, 1, 2, ex);
        assertTrue(Strings.isEmpty(formatted.getKey()));
        assertEquals(ex, formatted.getValue().get());
    }
}