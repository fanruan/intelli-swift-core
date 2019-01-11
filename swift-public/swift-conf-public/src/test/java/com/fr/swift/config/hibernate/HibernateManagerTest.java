package com.fr.swift.config.hibernate;

import com.fr.swift.util.FileUtil;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author yee
 * @date 2019-01-08
 */
public class HibernateManagerTest {

    @Test
    public void getFactory() {
        assertNotNull(HibernateManager.INSTANCE.getFactory());
    }

    @After
    public void tearDown() throws Exception {
        FileUtil.delete(System.getProperty("user.home") + "/testDb.mv.db");
    }
}