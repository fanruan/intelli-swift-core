package com.finebi.cube.structure;

import com.finebi.cube.tools.BICubeResourceLocationTestTool;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIndexTest extends TestCase {
    public void testIndex() {
        try {
            BICubeIndexData column = new BICubeIndexData(BICubeResourceLocationTestTool.getBasic("testIndex"));
            column.addIndex(0, GroupValueIndexTestTool.generateSampleIndex());
            assertEquals(GroupValueIndexTestTool.generateSampleIndex(), column.getBitmapIndex(0));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testNullIndex() {
        try {
            BICubeIndexData column = new BICubeIndexData(BICubeResourceLocationTestTool.getBasic("testNullIndex"));
            column.addNULLIndex(0, GroupValueIndexTestTool.generateSampleIndex());
            assertEquals(GroupValueIndexTestTool.generateSampleIndex(), column.getNULLIndex(0));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }
}
