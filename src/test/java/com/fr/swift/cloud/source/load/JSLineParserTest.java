package com.fr.swift.cloud.source.load;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.Test;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/3/12.
 */
public class JSLineParserTest {

    @Test
    public void parseToMap() {
        String fn = "function(input) { return {a: 1, b: 'foo'}; }";
        List<SwiftMetaDataColumn> fields = Arrays.<SwiftMetaDataColumn>asList(
                new MetaDataColumnBean("a", Types.BIGINT),
                new MetaDataColumnBean("b", Types.VARCHAR)
        );
        LineParser parser = new JSLineParser(fn, fields);
        Map<String, Object> row = parser.parseToMap("aaaaaaaaaaa");
        assertEquals(2, row.size());
        assertEquals(1L, row.get("a"));
        assertEquals("foo", row.get("b"));

        fn = "function(input) { return [1, 'foo']; }";
        parser = new JSLineParser(fn, fields);
        try {
            row = parser.parseToMap("aaaaaaaa");
            fail();
        } catch (Exception ignored) {
        }

        fn = "function(input) { return {a: 1}; }";
        parser = new JSLineParser(fn, fields);
        try {
            row = parser.parseToMap("aaaaaaaa");
            fail();
        } catch (Exception ignored) {
        }

        fn = "function(input) { return {a: 1, c: 'foo'}; }";
        parser = new JSLineParser(fn, fields);
        try {
            row = parser.parseToMap("aaaaaaaa");
            fail();
        } catch (Exception ignored) {
        }
    }
}