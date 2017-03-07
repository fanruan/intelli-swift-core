package com.fr.bi.log;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.ComparatorUtils;
import junit.framework.TestCase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by roy on 2017/1/13.
 */
public class BILoggerTest extends TestCase {
    public void testLoggerCache() {
        try {
            long cacheContent = System.currentTimeMillis();
            BILoggerFactory.cacheLoggerInfo("BILoggerTest", "logCacheStartTime", cacheContent);
            assertTrue(ComparatorUtils.equals(cacheContent, BILoggerFactory.getLoggerCacheValue("BILoggerTest", "logCacheStartTime")));
            BILoggerFactory.clearLoggerCacheValue("BILoggerTest");
            assertTrue(ComparatorUtils.equals(null, BILoggerFactory.getLoggerCacheValue("BILoggerTest", "logCacheStartTime")));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }


    public void testMapClearGC() {
        int counter = 0;
        while (true) {
            counter++;
            Map<String, Map<String, String>> outMap = new ConcurrentHashMap<String, Map<String, String>>();
            Map<String, String> innerMap = new ConcurrentHashMap<String, String>();
            innerMap.put("innerKey", "value");
            outMap.put("outerKey", innerMap);
            outMap.clear();
            if (counter % 100 == 0) {
                System.out.println("Free memory after count " + counter + "is" + getFreeMemory() + "M");
            }
        }
    }

    private long getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / (1024 * 1024);
    }


    public void testVariable() {
        Map<String, Object> mapIn = new ConcurrentHashMap<String, Object>();
        Map<String, Map<String, Object>> mapOut = new ConcurrentHashMap<String, Map<String, Object>>();
        mapOut.put("in", mapIn);
        mapOut.get("in").put("number", 1);
        System.out.println(mapOut.get("in").get("number"));
        Map<String, Map<String, Object>> mapOutCopy = mapOut;
        mapOutCopy.get("in").put("number", 2);
        System.out.println(mapOut.get("in").get("number"));


    }

}