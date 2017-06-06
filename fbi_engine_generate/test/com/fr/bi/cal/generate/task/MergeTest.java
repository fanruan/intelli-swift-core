package com.fr.bi.cal.generate.task;

import com.finebi.cube.conf.ICubeGenerateTask;
import com.fr.fs.control.UserControl;

/**
 * Created by Lucifer on 2017-5-31.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class MergeTest extends ICubeGenerateTaskTest {
    private long userId = UserControl.getInstance().getSuperManagerID();

    public void testAllTaskMerge() {
        ICubeGenerateTask cubeGenerateTask = new AllCubeGenerateTask(userId);
        cubeGenerateTask = cubeGenerateTask.merge(new SingleCubeGenerateTask(null, null, userId));
        assertTrue(cubeGenerateTask instanceof AllCubeGenerateTask);
        cubeGenerateTask = cubeGenerateTask.merge(new CustomCubeGenerateTask(userId));
        assertTrue(cubeGenerateTask instanceof AllCubeGenerateTask);
        cubeGenerateTask = cubeGenerateTask.merge(new AllCubeGenerateTask(userId));
        assertTrue(cubeGenerateTask instanceof AllCubeGenerateTask);
    }

    public void testSingleTaskMerge() {
        ICubeGenerateTask cubeGenerateTask = new SingleCubeGenerateTask(sourceId1, updateType0, userId);
        cubeGenerateTask = cubeGenerateTask.merge(new AllCubeGenerateTask(userId));
        assertTrue(cubeGenerateTask instanceof AllCubeGenerateTask);

        ICubeGenerateTask cubeGenerateTask1 = new SingleCubeGenerateTask(sourceId1, updateType0, userId);
        ICubeGenerateTask cubeGenerateTask2 = new SingleCubeGenerateTask(sourceId2, updateType0, userId);
        ICubeGenerateTask cubeGenerateTask3 = new SingleCubeGenerateTask(sourceId3, updateType0, userId);
        cubeGenerateTask = cubeGenerateTask1.merge(cubeGenerateTask2).merge(cubeGenerateTask3);
        assertTrue(cubeGenerateTask instanceof CustomCubeGenerateTask);
        assertTrue(((CustomCubeGenerateTask) cubeGenerateTask).getSourceIdUpdateTypeMap().size() == 3);
    }

    public void testCustomTaskMerge() {
        ICubeGenerateTask cubeGenerateTask = new CustomCubeGenerateTask(userId);
        cubeGenerateTask = cubeGenerateTask.merge(new AllCubeGenerateTask(userId));
        assertTrue(cubeGenerateTask instanceof AllCubeGenerateTask);

        ICubeGenerateTask cubeGenerateTask1 = new CustomCubeGenerateTask(userId);
        cubeGenerateTask = cubeGenerateTask1.merge(new SingleCubeGenerateTask(sourceId1, updateType0, userId));
        assertTrue(cubeGenerateTask instanceof CustomCubeGenerateTask);
        assertTrue(((CustomCubeGenerateTask) cubeGenerateTask).getSourceIdUpdateTypeMap().size() == 1);

        ICubeGenerateTask cubeGenerateTask2 = new CustomCubeGenerateTask(userId);
        cubeGenerateTask = cubeGenerateTask2.merge(cubeGenerateTask);
        assertTrue(cubeGenerateTask instanceof CustomCubeGenerateTask);
        assertTrue(((CustomCubeGenerateTask) cubeGenerateTask).getSourceIdUpdateTypeMap().size() == 1);
    }
}
