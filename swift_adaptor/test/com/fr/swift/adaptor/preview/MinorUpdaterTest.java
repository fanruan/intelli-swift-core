package com.fr.swift.adaptor.preview;

import com.fr.swift.generate.BaseTest;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.join.JoinColumn;
import com.fr.swift.source.etl.join.JoinOperator;
import com.fr.swift.source.etl.utils.ETLConstant;

import java.util.Arrays;

/**
 * @author anchore
 * @date 2018/2/2
 */
public class MinorUpdaterTest extends BaseTest {

    public void testUpdate() throws Exception {
        DataSource demoCustomer = new TableDBSource("DEMO_CAPITAL_RETURN", "allTest");
        DataSource demoContract = new TableDBSource("DEMO_CONTRACT", "allTest");
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