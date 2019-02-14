package com.fr.swift.db.impl;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.DatabaseTest;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class MetadataDifferTest {

    @Test
    public void diff() {
        SwiftMetaData meta1 = new SwiftMetaDataBean("A", Arrays.<SwiftMetaDataColumn>asList(
                new MetaDataColumnBean("a1", Types.INTEGER),
                new MetaDataColumnBean("a2", Types.FLOAT),
                new MetaDataColumnBean("a3", Types.TIMESTAMP)));
        SwiftMetaData meta2 = new SwiftMetaDataBean("A", Arrays.<SwiftMetaDataColumn>asList(
                new MetaDataColumnBean("a1", Types.INTEGER),
                new MetaDataColumnBean("a3", Types.TIMESTAMP),
                new MetaDataColumnBean("a4", Types.BIT)));

        MetadataDiffer differ = new MetadataDiffer(meta1, meta2);

        List<SwiftMetaDataColumn> added = differ.getAdded();
        Assert.assertEquals(1, added.size());
        DatabaseTest.assertEquals(new MetaDataColumnBean("a4", Types.BIT), added.get(0));
        List<SwiftMetaDataColumn> dropped = differ.getDropped();
        Assert.assertEquals(1, dropped.size());
        DatabaseTest.assertEquals(new MetaDataColumnBean("a2", Types.FLOAT), dropped.get(0));
    }
}