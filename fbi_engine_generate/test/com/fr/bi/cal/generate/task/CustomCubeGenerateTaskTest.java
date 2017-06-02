package com.fr.bi.cal.generate.task;

import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.cal.generate.task.calculator.CustomTaskCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucifer on 2017-5-31.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomCubeGenerateTaskTest extends ICubeGenerateTaskTest {

    public void testCustomCubeGenerateTask1() {
        CustomCubeGenerateTask cubeGenerateTask = new CustomCubeGenerateTask(userId);
        assertTrue(cubeGenerateTask.isOk2Merge());
        assertTrue(cubeGenerateTask.getTableSourceId() == null);
        assertTrue(cubeGenerateTask.getSourceIdUpdateTypeMap().isEmpty());
        assertTrue(cubeGenerateTask.getTableRelations().isEmpty());
        assertTrue(cubeGenerateTask.getUpdateType() == null);
        assertTrue(cubeGenerateTask.getUserId() == userId);
        assertTrue(cubeGenerateTask.getTaskCalculator() instanceof CustomTaskCalculator);
    }

    public void testCustomCubeGenerateTask2() {
        Map<String, List<Integer>> sourceIdUpdateTypeMap = new HashMap<String, List<Integer>>();
        sourceIdUpdateTypeMap.put(sourceId1, new ArrayList<Integer>());
        sourceIdUpdateTypeMap.put(sourceId2, new ArrayList<Integer>());
        List<BITableRelation> tableRelations = new ArrayList<BITableRelation>();
        tableRelations.add(new BITableRelation());

        CustomCubeGenerateTask cubeGenerateTask = new CustomCubeGenerateTask(userId, sourceIdUpdateTypeMap, tableRelations);
        assertTrue(cubeGenerateTask.isOk2Merge());
        assertTrue(cubeGenerateTask.getTableSourceId() == null);
        assertTrue(cubeGenerateTask.getSourceIdUpdateTypeMap().size() == 2);
        assertTrue(cubeGenerateTask.getTableRelations().size() == 1);
        assertTrue(cubeGenerateTask.getUpdateType() == null);
        assertTrue(cubeGenerateTask.getUserId() == userId);
    }

    public void testCustomCubeGenerateTask3() {
        int updateType1 = 1;
        int updateType2 = 2;
        CustomCubeGenerateTask cubeGenerateTask = new CustomCubeGenerateTask(userId, sourceId1, updateType1);
        cubeGenerateTask.addTable(sourceId2, updateType2);
        assertTrue(cubeGenerateTask.getTableSourceId() == null);
        assertTrue(cubeGenerateTask.getSourceIdUpdateTypeMap().size() == 2);
        assertTrue(cubeGenerateTask.getTableRelations().isEmpty());
        assertTrue(cubeGenerateTask.getUpdateType() == null);
        assertTrue(cubeGenerateTask.getUserId() == userId);
        assertTrue(cubeGenerateTask.isOk2Merge());
        assertTrue(cubeGenerateTask.getTaskCalculator() instanceof CustomTaskCalculator);

        String sourceId3 = "aaaa3333";
        String sourceId4 = "aaaa4444";
        Map<String, List<Integer>> sourceIdUpdateTypeMap = new HashMap<String, List<Integer>>();
        sourceIdUpdateTypeMap.put(sourceId3, new ArrayList<Integer>());
        sourceIdUpdateTypeMap.put(sourceId4, new ArrayList<Integer>());
        cubeGenerateTask.addTables(sourceIdUpdateTypeMap);
        assertTrue(cubeGenerateTask.getSourceIdUpdateTypeMap().size() == 4);
    }
}
