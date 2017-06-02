package com.fr.bi.cal.generate.task;

import com.fr.bi.cal.generate.task.calculator.CustomTaskCalculator;
import com.fr.bi.cal.generate.task.calculator.PartTaskCalculator;

/**
 * Created by Lucifer on 2017-5-31.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class ParkCubeGenerateTaskTest extends ICubeGenerateTaskTest {

    public void testParkCubeGenerateTask() {
        PartCubeGenerateTask partCubeGenerateTask = new PartCubeGenerateTask(userId);
        assertTrue(partCubeGenerateTask.getUpdateType() == null);
        assertTrue(partCubeGenerateTask.getTableSourceId() == null);
        assertTrue(partCubeGenerateTask.getUserId() == userId);
        assertTrue(partCubeGenerateTask.getTaskCalculator() instanceof PartTaskCalculator);
        assertTrue(partCubeGenerateTask.getTaskCalculator() instanceof CustomTaskCalculator);
        assertFalse(partCubeGenerateTask.isOk2Merge());
    }
}
