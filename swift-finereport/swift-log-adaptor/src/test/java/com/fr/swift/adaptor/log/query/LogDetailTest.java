package com.fr.swift.adaptor.log.query;

import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.adaptor.log.MetricProxy;
import com.fr.swift.db.Table;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.db.QueryDBSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static junit.framework.TestCase.assertTrue;

/**
 * This class created on 2018/4/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LogDetailTest extends LogBaseTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Rule
    public TestRule getRule() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Test
    public void testFind() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
            if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
                db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("DEMO_CONTRACT"));
            transportAndIndex(dataSource, table);

            QueryCondition sortQueryCondition = QueryFactory.create();
            DataList dataList = MetricProxy.getInstance().find(ContractBean.class, sortQueryCondition);
            assertTrue(!dataList.getList().isEmpty());
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }
}
