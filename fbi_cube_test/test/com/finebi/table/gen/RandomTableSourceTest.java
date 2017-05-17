package com.finebi.table.gen;
/**
 * This class created on 2017/5/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.tool.BITestConstants;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class RandomTableSourceTest extends TestCase {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(RandomTableSourceTest.class);

    /**
     * Detail:
     * Author:Connery
     * Date:2017/5/17
     */
    public void testRandomTableSource() {
        try {
            RandomTableSource tableSource = new RandomTableSource(BITestConstants.TEN);

            List<BIRandomFieldSource> fields = new ArrayList<BIRandomFieldSource>();
            fields.add(new BICubeLongRandomFieldSource(tableSource, "id", DBConstant.CLASS.LONG, 2, 10, 10));
            fields.add(new BICubeStringRandomFieldSource(tableSource, "name", DBConstant.CLASS.STRING, 6, 10, 10));
            fields.add(new BICubeDoubeRandomFieldSource(tableSource, "age", DBConstant.CLASS.DOUBLE, 6, 10, 10));
            tableSource.setFieldGenerator(fields);
            final StringBuffer sb = new StringBuffer();
            tableSource.read(new Traversal<BIDataValue>() {
                @Override
                public void actionPerformed(BIDataValue data) {
                    sb.append("column:").append(data.getCol()).append(" row:").append(data.getRow()).append(" value:").append(data.getValue()).append("\n");
                }
            }, null, null);
            LOGGER.info(sb.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }
}
