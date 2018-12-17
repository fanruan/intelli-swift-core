package com.fr.swift.query;

import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import junit.framework.TestCase;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryBeanTest extends TestCase {


    public void testJson1Parse() {
        try {
            FunnelQueryBean info = (FunnelQueryBean) QueryBeanFactory.create(FunnelJsonsForTest.json1);
            assertNotNull(info);
            assertEquals(info.getTableName(), "tableA");
            assertEquals(info.getSegments().size(), 2);
            assertTrue(info.getSegments().contains("seg0"));
            assertTrue(info.getSegments().contains("seg1"));

            assertEquals(info.getId(), "json1Id");
            assertEquals(info.getCombine(), "json1Combine");
            assertEquals(info.getStepName(), "eventName1");
            assertEquals(info.getDate(), "json1date");

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testJson2Parse() {
        try {
            FunnelQueryBean info = (FunnelQueryBean) QueryBeanFactory.create(FunnelJsonsForTest.json2);
            assertNotNull(info);
            assertEquals(info.getTableName(), "tableB");

            assertEquals(info.getId(), "json2Id");
            assertEquals(info.getCombine(), "json2Combine");
            assertEquals(info.getStepName(), "eventName2");
            assertEquals(info.getDate(), "json2date");

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testJson3Parse() {
        try {
            FunnelQueryBean info = (FunnelQueryBean) QueryBeanFactory.create(FunnelJsonsForTest.json3);
            assertNotNull(info);
            assertEquals(info.getTableName(), "tableC");

            assertEquals(info.getId(), "json3Id");
            assertEquals(info.getCombine(), "json3Combine");
            assertEquals(info.getStepName(), "eventName3");
            assertEquals(info.getDate(), "json3date");

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testJson4Parse() {
        try {
            FunnelQueryBean info = (FunnelQueryBean) QueryBeanFactory.create(FunnelJsonsForTest.json4);
            assertNotNull(info);
            assertEquals(info.getTableName(), "tableD");

            assertEquals(info.getId(), "json4Id");
            assertEquals(info.getCombine(), "json4Combine");
            assertEquals(info.getStepName(), "eventName4");
            assertEquals(info.getDate(), "json4date");

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testJson5Parse() {
        try {
            FunnelQueryBean info = (FunnelQueryBean) QueryBeanFactory.create(FunnelJsonsForTest.json5);
            assertNotNull(info);
            assertEquals(info.getTableName(), "tableE");

            assertEquals(info.getId(), "json5Id");
            assertEquals(info.getCombine(), "json5Combine");
            assertEquals(info.getStepName(), "eventName5");
            assertEquals(info.getDate(), "json5date");

        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
