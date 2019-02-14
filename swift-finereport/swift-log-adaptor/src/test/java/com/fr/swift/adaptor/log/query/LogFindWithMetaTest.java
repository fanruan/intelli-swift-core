package com.fr.swift.adaptor.log.query;

import com.fr.general.ComparatorUtils;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.condition.impl.QueryConditionImpl;
import com.fr.stable.query.data.DataColumn;
import com.fr.stable.query.data.func.SimpleColumnFunc;
import com.fr.stable.query.restriction.Restriction;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.adaptor.log.MetricProxy;
import com.fr.swift.db.Table;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.db.QueryDBSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Created by lyon on 2018/11/12.
 */
public class LogFindWithMetaTest extends LogBaseTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Rule
    public TestRule getRule() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Test
    public void testDetail() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
            if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
                db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("DEMO_CONTRACT"));
            transportAndIndex(dataSource, table);

            QueryCondition condition = new QueryConditionImpl();
            List<DataColumn> dataColumns = new ArrayList<DataColumn>();
            DataColumn column = new DataColumn();
            column.setName("合同类型");
            dataColumns.add(column);
            ResultSet resultSet = MetricProxy.getInstance().findWithMetaData(ContractBean.class, condition, dataColumns);
            assertTrue(resultSet.next());
            do {
                resultSet.getString(1);
            } while (resultSet.next());
        } catch (Exception e) {
            LOGGER.error(e);
            fail();
        }
    }

    @Test
    public void testGroupBy1() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
            if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
                db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("DEMO_CONTRACT"));
            transportAndIndex(dataSource, table);

            QueryCondition condition = new QueryConditionImpl();
            condition.addGroupBy("合同类型");
            ResultSet resultSet = MetricProxy.getInstance().findWithMetaData(ContractBean.class, condition, new ArrayList<DataColumn>());
            assertTrue(resultSet.next());
            do {
                resultSet.getString(1);
            } while (resultSet.next());
        } catch (Exception e) {
            LOGGER.error(e);
            fail();
        }
    }

    @Test
    public void testGroupBy2() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
            if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
                db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("DEMO_CONTRACT"));
            transportAndIndex(dataSource, table);

            QueryCondition condition = new QueryConditionImpl();
            condition.addGroupBy("合同类型");
            condition.addGroupBy("合同付款类型");
            ResultSet resultSet = MetricProxy.getInstance().findWithMetaData(ContractBean.class, condition, new ArrayList<DataColumn>());
            assertTrue(resultSet.next());
            do {
                resultSet.getString(1);
                resultSet.getString(2);
            } while (resultSet.next());
        } catch (Exception e) {
            LOGGER.error(e);
            fail();
        }
    }

    @Test
    public void testGroupBy0AndMetric() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
            if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
                db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("DEMO_CONTRACT"));
            transportAndIndex(dataSource, table);

            List<DataColumn> dataColumns = new ArrayList<DataColumn>();
            DataColumn column = new DataColumn();
            column.setName("总金额");
            column.setFunc(SimpleColumnFunc.SUM);
            dataColumns.add(column);

            QueryCondition condition = new QueryConditionImpl();
            ResultSet resultSet = MetricProxy.getInstance().findWithMetaData(ContractBean.class, condition, dataColumns);
            assertTrue(resultSet.next());
            assertTrue(resultSet.getLong(1) > 0);
            ResultSetMetaData metaData = resultSet.getMetaData();
            assertTrue(ComparatorUtils.equals(metaData.getColumnName(1), "总金额"));
        } catch (Exception e) {
            LOGGER.error(e);
            fail();
        }
    }

    @Test
    public void testGroupBy0AndMetricWithFilter() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
            if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
                db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("DEMO_CONTRACT"));
            transportAndIndex(dataSource, table);

            List<DataColumn> dataColumns = new ArrayList<DataColumn>();
            DataColumn column = new DataColumn();
            column.setName("总金额");
            column.setFunc(SimpleColumnFunc.SUM);
            Restriction restriction = RestrictionFactory.eq("总金额", 800000);
            column.setRestriction(restriction);
            dataColumns.add(column);

            QueryCondition condition = new QueryConditionImpl();
            ResultSet resultSet = MetricProxy.getInstance().findWithMetaData(ContractBean.class, condition, dataColumns);
            assertTrue(resultSet.next());
            assertTrue(resultSet.getLong(1) > 0 && resultSet.getLong(1) % 800000 == 0);
            ResultSetMetaData metaData = resultSet.getMetaData();
            assertTrue(ComparatorUtils.equals(metaData.getColumnName(1), "总金额"));
        } catch (Exception e) {
            LOGGER.error(e);
            fail();
        }
    }

    @Test
    public void testGroupBy() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
            if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
                db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("DEMO_CONTRACT"));
            transportAndIndex(dataSource, table);

            QueryCondition condition = new QueryConditionImpl();
            condition.addGroupBy("合同类型");
            condition.addGroupBy("合同付款类型");

            List<DataColumn> dataColumns = new ArrayList<DataColumn>();
            DataColumn column = new DataColumn();
            column.setName("总金额");
            column.setAlias("总金额字段别名");
            column.setFunc(SimpleColumnFunc.SUM);
            Restriction restriction = RestrictionFactory.eq("总金额", 800000);
            column.setRestriction(restriction);
            dataColumns.add(column);

            ResultSet resultSet = MetricProxy.getInstance().findWithMetaData(ContractBean.class, condition, dataColumns);
            assertTrue(resultSet.next());
            do {
                resultSet.getString(1);
                resultSet.getString("合同类型");
                resultSet.getString(2);
                resultSet.getString("合同付款类型");
                resultSet.getLong("总金额字段别名");
                long sum = resultSet.getLong(3);
                assertTrue(sum == 0 || (sum > 0 && sum % 800000 == 0));
            } while (resultSet.next());

            ResultSetMetaData metaData = resultSet.getMetaData();
            assertTrue(ComparatorUtils.equals(metaData.getColumnName(1), "合同类型"));
            assertTrue(ComparatorUtils.equals(metaData.getColumnName(2), "合同付款类型"));
            assertTrue(ComparatorUtils.equals(metaData.getColumnName(3), "总金额字段别名"));
        } catch (Exception e) {
            LOGGER.error(e);
            fail();
        }
    }
}