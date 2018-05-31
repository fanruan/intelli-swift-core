package com.fr.swift.adaptor.log.query;

import com.fr.annotation.Test;
import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.adaptor.log.QueryConditionAdaptor;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.info.QueryInfo;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.db.QueryDBSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/4/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LogDetailSortTest extends LogBaseTest {

    @Test
    public void testSortedAsc() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestSortedAsc");
            if (!SwiftDatabase.getInstance().existsTable(new SourceKey("testSortedAsc"))) {
                SwiftDatabase.getInstance().createTable(new SourceKey("testSortedAsc"), dataSource.getMetadata());
            }
            Table table = SwiftDatabase.getInstance().getTable(new SourceKey("testSortedAsc"));
            transportAndIndex(dataSource, table);

            QueryCondition sortQueryCondition = QueryFactory.create().addSort("总金额");

            QueryInfo sortQueryInfo = QueryConditionAdaptor.adaptCondition(sortQueryCondition, table);
            SwiftResultSet sortResultSet = QueryRunnerProvider.getInstance().executeQuery(sortQueryInfo);
            int sortindex = table.getMeta().getColumnIndex("总金额");
            List<Double> dataList = new ArrayList<Double>();
            while (sortResultSet.next()) {
                Row row = sortResultSet.getRowData();
                dataList.add(((Long) row.getValue(sortindex)).doubleValue());
            }
            for (int i = 0; i < dataList.size() - 1; i++) {
                assertTrue(dataList.get(i) <= dataList.get(i + 1));
            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

    @Test
    public void testSortedDesc() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestSortedDesc");
            if (!SwiftDatabase.getInstance().existsTable(new SourceKey("testSortedDesc"))) {
                SwiftDatabase.getInstance().createTable(new SourceKey("testSortedDesc"), dataSource.getMetadata());
            }
            Table table = SwiftDatabase.getInstance().getTable(new SourceKey("testSortedDesc"));
            transportAndIndex(dataSource, table);

            QueryCondition sortQueryCondition = QueryFactory.create().addSort("总金额", true);

            QueryInfo sortQueryInfo = QueryConditionAdaptor.adaptCondition(sortQueryCondition, table);
            SwiftResultSet sortResultSet = QueryRunnerProvider.getInstance().executeQuery(sortQueryInfo);
            int sortindex = table.getMeta().getColumnIndex("总金额");
            List<Double> dataList = new ArrayList<Double>();
            while (sortResultSet.next()) {
                Row row = sortResultSet.getRowData();
                dataList.add(((Long) row.getValue(sortindex)).doubleValue());
            }
            for (int i = 0; i < dataList.size() - 1; i++) {
                assertTrue(dataList.get(i) >= dataList.get(i + 1));
            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }
}
