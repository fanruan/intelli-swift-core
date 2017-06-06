package com.fr.bi.cal.generate.task;

import com.fr.bi.cal.generate.task.calculator.AllTaskCalculator;

/**
 * Created by Lucifer on 2017-5-31.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class AllCubeGenerateTaskTest extends ICubeGenerateTaskTest {
    public void testAllCubeGenerateTask() {
        AllCubeGenerateTask allCubeGenerateTask = new AllCubeGenerateTask(userId);
        assertTrue(allCubeGenerateTask.getUpdateType() == null);
        assertTrue(allCubeGenerateTask.getTableSourceId() == null);
        assertTrue(allCubeGenerateTask.getUserId() == userId);
        assertTrue(allCubeGenerateTask.getTaskCalculator() instanceof AllTaskCalculator);
        assertTrue(allCubeGenerateTask.isOk2Merge());
    }
}
