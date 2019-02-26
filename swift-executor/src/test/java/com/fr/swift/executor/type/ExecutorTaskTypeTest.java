package com.fr.swift.executor.type;

import org.junit.Test;

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
        assertEquals(ExecutorTaskType.getAllTypeList().size(), 9);
        assertEquals(ExecutorTaskType.getAllTypeList().toString(), "[REALTIME, TRANSFER, INDEX, DELETE, COLLATE, UPLOAD, DOWNLOAD, HISTORY, QUERY]");
    }

    @Test
    public void testGetTypeList() {
        assertEquals(ExecutorTaskType.getTypeList(ExecutorTaskType.REALTIME, ExecutorTaskType.DELETE).size(), 2);
        assertEquals(ExecutorTaskType.getTypeList(ExecutorTaskType.REALTIME, ExecutorTaskType.DELETE).toString(), "[REALTIME, DELETE]");
    }
}
