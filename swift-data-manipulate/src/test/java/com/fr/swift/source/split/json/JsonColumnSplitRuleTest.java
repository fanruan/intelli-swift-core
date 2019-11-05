package com.fr.swift.source.split.json;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

/**
 * @author lucifer
 * @date 2019-07-31
 * @description
 * @since advanced swift 1.0
 */
public class JsonColumnSplitRuleTest {


    @Test
    public void test() throws Exception {
        SwiftMetaData metaData = Mockito.mock(SwiftMetaData.class);
        Mockito.when(metaData.getColumnIndex("test")).thenReturn(1);
        JsonColumnSplitRule splitRule = new JsonColumnSplitRule("test", metaData);
        String json = "{\"id\":\"2\",\"score\":99,\"age\":28}";
        Row row = new ListBasedRow(json);
        JsonSubRow jsonSubRow = splitRule.split(row);
        Map<String, Object> result = jsonSubRow.getSubRow();
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.get("test.id"), "2");
        Assert.assertEquals(result.get("test.score"), 99);
        Assert.assertEquals(result.get("test.age"), 28);

    }
}