package com.fr.swift.segment;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.manager.LocalDataOperatorProvider;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.core.Core;
import junit.framework.TestCase;

import java.io.File;

/**
 * @author yee
 * @date 2018/1/9
 */
public class SegmentOperatorTest extends TestCase {
    private SourceKey intKey;
    private SourceKey longKey;
    private SourceKey doubleKey;
    private SourceKey stringKey;
    private SourceKey dateKey;

    @Override
    protected void setUp() throws Exception {
        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir")));
        File file = new File(System.getProperty("user.dir"), "resources");
        file.deleteOnExit();
        intKey = new SourceKey("int_table");
        longKey = new SourceKey("long_table");
        doubleKey = new SourceKey("double_table");
        stringKey = new SourceKey("string_table");
        dateKey = new SourceKey("date_table");
    }

    private void transport(SourceKey key, SwiftResultSet set) {
        boolean success = true;
        try {
            DataSource dataSource = new DataSource() {
                @Override
                public SwiftMetaData getMetadata() {
                    try {
                        return set.getMetaData();
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                public SourceKey getSourceKey() {
                    return key;
                }

                @Override
                public Core fetchObjectCore() {
                    return null;
                }
            };

            Inserter inserter = SwiftContext.getInstance().getBean(LocalDataOperatorProvider.class).getHistoryBlockSwiftInserter(dataSource);
            inserter.insertData(set);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        assertTrue(success);
    }

    public void testIntTransport() {
        SwiftResultSet set = new IntResultSet();
        transport(intKey, set);
    }

    public void testLongTransport() {
        SwiftResultSet set = new LongResultSet();
        transport(longKey, set);
    }

    public void testDoubleTransport() {
        SwiftResultSet set = new DoubleResultSet();
        transport(doubleKey, set);
    }

    public void testStringTransport() {
        SwiftResultSet set = new StringResultSet();
        transport(stringKey, set);
    }

    public void testDateTransport() {
        SwiftResultSet set = new DateResultSet();
        transport(dateKey, set);
    }
}