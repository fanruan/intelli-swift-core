package com.fr.swift.adaptor.log.query;

import com.fr.general.ComparatorUtils;
import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.Restriction;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.adaptor.log.QueryConditionAdaptor;
import com.fr.swift.cal.info.QueryInfo;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.service.SwiftAnalyseService;
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
public class LogDetailAndFilterTest extends LogBaseTest {

    public void testAnd() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestAnd");
            if (!SwiftDatabase.getInstance().existsTable(new SourceKey("testAnd"))) {
                SwiftDatabase.getInstance().createTable(new SourceKey("testAnd"), dataSource.getMetadata());
            }
            Table table = SwiftDatabase.getInstance().getTable(new SourceKey("testAnd"));
            transportAndIndex(dataSource, table);

            QueryCondition eqQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"))
                    .addRestriction(RestrictionFactory.gte("总金额", 1000000)).addRestriction(RestrictionFactory.lte("总金额", 2000000));

            QueryInfo eqQueryInfo = QueryConditionAdaptor.adaptCondition(eqQueryCondition, table);
            SwiftResultSet eqResultSet = SwiftAnalyseService.getInstance().executeQuery(eqQueryInfo);
            int index1 = table.getMeta().getColumnIndex("合同类型");
            int index2 = table.getMeta().getColumnIndex("总金额");

            while (eqResultSet.next()) {
                Row row = eqResultSet.getRowData();
                assertEquals(row.getValue(index1), "购买合同");
                assertTrue(((Long) row.getValue(index2)).doubleValue() >= 1000000 && ((Long) row.getValue(index2)).doubleValue() <= 2000000);

            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

    public void testOr() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestAnd");
            if (!SwiftDatabase.getInstance().existsTable(new SourceKey("testAnd"))) {
                SwiftDatabase.getInstance().createTable(new SourceKey("testAnd"), dataSource.getMetadata());
            }
            Table table = SwiftDatabase.getInstance().getTable(new SourceKey("testAnd"));
            transportAndIndex(dataSource, table);

            List<Restriction> restrictions = new ArrayList<Restriction>();
            restrictions.add(RestrictionFactory.eq("合同类型", "购买合同"));
            restrictions.add(RestrictionFactory.eq("合同类型", "长期协议订单"));
            restrictions.add(RestrictionFactory.eq("合同类型", "长期协议"));
            QueryCondition eqQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.or(restrictions));

            QueryInfo eqQueryInfo = QueryConditionAdaptor.adaptCondition(eqQueryCondition, table);
            SwiftResultSet eqResultSet = SwiftAnalyseService.getInstance().executeQuery(eqQueryInfo);
            int index1 = table.getMeta().getColumnIndex("合同类型");
            int count1 = 0, count2 = 0, count3 = 0;
            while (eqResultSet.next()) {
                Row row = eqResultSet.getRowData();
                if (ComparatorUtils.equals(row.getValue(index1), "购买合同")) {
                    count1++;
                }
                if (ComparatorUtils.equals(row.getValue(index1), "长期协议订单")) {
                    count2++;
                }
                if (ComparatorUtils.equals(row.getValue(index1), "长期协议")) {
                    count3++;
                }
            }
            assertTrue(count1 != 0);
            assertTrue(count2 != 0);
            assertTrue(count3 != 0);
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

}
