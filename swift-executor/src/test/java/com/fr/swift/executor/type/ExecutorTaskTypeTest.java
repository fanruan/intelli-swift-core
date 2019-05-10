package com.fr.swift.executor.type;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
public class ExecutorTaskTypeTest {

    @Test
    public void testGetAllTypeList() {
        assertEquals(ExecutorTaskType.getAllTypeList().size(), 11);
        assertEquals(ExecutorTaskType.getAllTypeList(), Arrays.asList(
                ExecutorTaskType.REALTIME,
                ExecutorTaskType.RECOVERY,
                ExecutorTaskType.TRANSFER,
                ExecutorTaskType.INDEX,
                ExecutorTaskType.DELETE,
                ExecutorTaskType.TRUNCATE,
                ExecutorTaskType.COLLATE,
                ExecutorTaskType.UPLOAD,
                ExecutorTaskType.DOWNLOAD,
                ExecutorTaskType.HISTORY,
                ExecutorTaskType.QUERY,
                ExecutorTaskType.TREASURE_UPLOAD));
    }

    @Test
    public void testGetTypeList() {
        assertEquals(ExecutorTaskType.getTypeList(ExecutorTaskType.REALTIME, ExecutorTaskType.DELETE).size(), 2);
        assertEquals(ExecutorTaskType.getTypeList(ExecutorTaskType.REALTIME, ExecutorTaskType.DELETE).toString(), "[REALTIME, DELETE]");
    }
}
