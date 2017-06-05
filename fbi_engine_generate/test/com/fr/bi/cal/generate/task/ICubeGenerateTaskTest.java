package com.fr.bi.cal.generate.task;

import com.finebi.cube.conf.ICubeGenerateTask;
import com.fr.fs.control.UserControl;
import junit.framework.TestCase;

/**
 * Created by Lucifer on 2017-5-31.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class ICubeGenerateTaskTest extends TestCase {
    protected long userId = UserControl.getInstance().getSuperManagerID();
    protected String sourceId1 = "aaaa1111";
    protected String sourceId2 = "aaaa2222";
    protected String sourceId3 = "aaaa3333";
    protected String sourceId4 = "aaaa4444";
    protected String sourceId5 = "aaaa5555";
    protected String sourceId6 = "aaaa6666";
    protected Integer updateType0 = 0;
    protected Integer updateType1 = 1;
    protected Integer updateType2 = 2;

    public void test() {
        assertTrue(new EmptyCubeGenerateTask(userId) instanceof ICubeGenerateTask);
        assertTrue(new AllCubeGenerateTask(userId) instanceof ICubeGenerateTask);
        assertTrue(new SingleCubeGenerateTask(null, null, userId) instanceof ICubeGenerateTask);
        assertTrue(new PartCubeGenerateTask(userId) instanceof ICubeGenerateTask);
        assertTrue(new CustomCubeGenerateTask(userId) instanceof ICubeGenerateTask);
    }
}
