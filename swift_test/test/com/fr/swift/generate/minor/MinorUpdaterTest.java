package com.fr.swift.generate.minor;

import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.join.JoinColumn;
import com.fr.swift.source.etl.join.JoinOperator;
import com.fr.swift.source.etl.utils.ETLConstant;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author anchore
 * @date 2018/2/2
 */
public class MinorUpdaterTest extends TestCase {
    @Override
    protected void setUp() {
        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        ConnectionManager.getInstance().registerConnectionInfo("allTest", TestConnectionProvider.createConnection());
    }

    public void testUpdate() throws Exception {
        DataSource demoCustomer = new TableDBSource("DEMO_CAPITAL_RETURN", "h2");
        DataSource demoContract = new TableDBSource("DEMO_CONTRACT", "h2");
        String joinOn = "合同ID";
        ETLOperator joinOp = new JoinOperator(
                Arrays.asList(new JoinColumn(joinOn + "1", true, joinOn)),
                new ColumnKey[]{new ColumnKey(joinOn)},
                new ColumnKey[]{new ColumnKey(joinOn)},
                ETLConstant.JOINTYPE.LEFT
        );
        DataSource join = new ETLSource(Arrays.asList(demoContract, demoCustomer), joinOp);

        MinorUpdater.update(join);

        Segment joinSeg = MinorSegmentManager.getInstance().getSegment(join.getSourceKey()).get(0);

        assertEquals(768, joinSeg.getRowCount());

        MinorSegmentManager.getInstance().clear();
    }
}