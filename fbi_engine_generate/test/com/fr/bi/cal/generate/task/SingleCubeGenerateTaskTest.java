package com.fr.bi.cal.generate.task;

import com.fr.bi.cal.generate.task.calculator.CustomTaskCalculator;
import com.fr.bi.cal.generate.task.calculator.SingleTaskCalculator;

/**
 * Created by Lucifer on 2017-5-31.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class SingleCubeGenerateTaskTest extends ICubeGenerateTaskTest {

    public void testSingleCubeGenerateTask() {
        SingleCubeGenerateTask partCubeGenerateTask = new SingleCubeGenerateTask(sourceId1, updateType1, userId);
        assertTrue(partCubeGenerateTask.getUpdateType() == updateType1);
        assertTrue(partCubeGenerateTask.getTableSourceId() == sourceId1);
        assertTrue(partCubeGenerateTask.getUserId() == userId);
        assertTrue(partCubeGenerateTask.getTaskCalculator() instanceof SingleTaskCalculator);
        assertTrue(partCubeGenerateTask.getTaskCalculator() instanceof CustomTaskCalculator);
        assertTrue(partCubeGenerateTask.isOk2Merge());
    }
}
