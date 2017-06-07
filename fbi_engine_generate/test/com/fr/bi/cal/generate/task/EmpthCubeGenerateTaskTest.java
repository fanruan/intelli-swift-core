package com.fr.bi.cal.generate.task;

import com.fr.bi.cal.generate.task.calculator.EmptyTaskCalculator;

/**
 * Created by Lucifer on 2017-5-31.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class EmpthCubeGenerateTaskTest extends ICubeGenerateTaskTest {

    public void testEmpthCubeGenerateTask() {
        EmptyCubeGenerateTask emptyCubeGenerateTask = new EmptyCubeGenerateTask(userId);
        assertTrue(emptyCubeGenerateTask.getUpdateType() == null);
        assertTrue(emptyCubeGenerateTask.getTableSourceId() == null);
        assertTrue(emptyCubeGenerateTask.getUserId() == userId);
        assertTrue(emptyCubeGenerateTask.getTaskCalculator() instanceof EmptyTaskCalculator);
        assertFalse(emptyCubeGenerateTask.isOk2Merge());
    }
}
