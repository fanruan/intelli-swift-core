package com.fr.swift.executor.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * This class created on 2019/3/1
 *
 * @author Lucifer
 * @description
 */
public class ExecutorTaskDaoTest {

    @Test
    public void testDao() {
        ExecutorTaskDao dao = new ExecutorTaskDaoImpl();
        Assert.assertNotNull(dao);
    }
}
