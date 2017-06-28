package com.fr.bi.manager;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kary on 2017/6/28.
 */
public class PropertiesWriteAndReadTest extends TestCase {
   private PerformancePluManagerTest manager = new PerformancePluManagerTest();
    public void setUp() {
    }
    public void testProperties() {
        Map<String, Object> propertiesMap = createPropertiesMap();
        testWriteProperties(propertiesMap);
        Map<String, Object> map = testReadProperties();
        assertEquals(map.size(),propertiesMap.size());
        Iterator<String> iterator = propertiesMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            assertEquals(propertiesMap.get(key).toString(),map.get(key).toString());
        }
    }
    private Map<String, Object>  testReadProperties() {
        return manager.getAllConf();
    }

    private void testWriteProperties(Map<String, Object> configMap) {
        manager.resetConf(configMap);
    }

    private Map<String, Object> createPropertiesMap() {
        Map<String ,Object> configMap=new HashMap<String, Object>();
        configMap.put("useDiskSort",true);
        configMap.put("useMultiThreadCal",true);
        return configMap;
    }

}
