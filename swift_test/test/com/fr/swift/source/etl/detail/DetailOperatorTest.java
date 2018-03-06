package com.fr.swift.source.etl.detail;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.etl.ETLSource;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/1/15.
 */
public class DetailOperatorTest extends TestCase {

    public void setUp() throws Exception {
        TestConnectionProvider.createConnection();
    }

    public void testGetMetaData() throws Exception {

        TableDBSource tableContract = new TableDBSource("DEMO_CONTRACT", "demo");
        List<DataSource> sources = new ArrayList<>();
        sources.add(tableContract);
        TableDBSource tableSALESMAN = new TableDBSource("DEMO_HR_SALESMAN", "demo");
        List<SwiftMetaData> baseMeta = new ArrayList<>();
        baseMeta.add(tableSALESMAN.getMetadata());
        ColumnKey columnKey = new ColumnKey("SALES_NAME");
        ColumnKey[] columnKeys = new ColumnKey[]{columnKey};
        List<ColumnKey[]> fields = new ArrayList<>();
        fields.add(columnKeys);
        DetailOperator operator = new DetailOperator(fields, null, baseMeta);
        ETLSource source = new ETLSource(sources, operator);
        SwiftMetaData metaData = source.getMetadata();
        assertEquals(metaData.getColumnCount(), 11);

        //如果字表没有全选，则用使用部分字段实现
        Map<Integer, String> partFiels = new HashMap<>();
        partFiels.put(0, "SALES_NAME");
        partFiels.put(1, "合同ID");
        ETLSource partSource = new ETLSource(sources, operator, partFiels);
        SwiftMetaData partMeta = partSource.getMetadata();
        assertEquals(partMeta.getColumnCount(), 2);
    }
}