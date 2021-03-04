package com.fr.swift.cloud.executor.type;

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
        assertEquals(SwiftTaskType.getAllTypeList().size(), 11);
        assertEquals(SwiftTaskType.getAllTypeList(), Arrays.asList(
                SwiftTaskType.REALTIME,
                SwiftTaskType.RECOVERY,
                SwiftTaskType.TRANSFER,
                SwiftTaskType.INDEX,
                SwiftTaskType.DELETE,
                SwiftTaskType.TRUNCATE,
                SwiftTaskType.COLLATE,
                SwiftTaskType.UPLOAD,
                SwiftTaskType.DOWNLOAD,
                SwiftTaskType.HISTORY,
                SwiftTaskType.QUERY));
    }

    @Test
    public void testGetTypeList() {
        assertEquals(SwiftTaskType.getTypeList(SwiftTaskType.REALTIME, SwiftTaskType.DELETE).size(), 2);
        assertEquals(SwiftTaskType.getTypeList(SwiftTaskType.REALTIME, SwiftTaskType.DELETE).toString(), "[REALTIME, DELETE]");
    }
}
