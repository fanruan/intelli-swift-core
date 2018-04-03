package com.fr.swift.source.etl.detail;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.etl.EtlSource;
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
        List<ColumnKey> baseColumns = new ArrayList<>();
        SwiftMetaData tableContractMeta = tableContract.getMetadata();
        for (int i = 0; i < tableContractMeta.getColumnCount(); i++) {
            baseColumns.add(new ColumnKey(tableContractMeta.getColumnName(i + 1)));
        }
        DetailOperator operator = new DetailOperator(fields, baseColumns, baseMeta);
        EtlSource source = new EtlSource(sources, operator);
        SwiftMetaData metaData = source.getMetadata();
        assertEquals(metaData.getColumnCount(), 11);

        //如果字表没有全选，则用使用部分字段实现
        Map<Integer, String> partFiels = new HashMap<>();
        partFiels.put(0, "SALES_NAME");
        partFiels.put(1, "合同ID");
        EtlSource partSource = new EtlSource(sources, operator, partFiels);
        SwiftMetaData partMeta = partSource.getMetadata();
        assertEquals(partMeta.getColumnCount(), 2);
    }
}