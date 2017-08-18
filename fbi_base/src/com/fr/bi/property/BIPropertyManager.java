package com.fr.bi.property;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.manager.PerformanceParamTools;
import com.fr.bi.manager.PerformancePlugManager;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This class created on 2017/8/17.
 *
 * @author Each.Zhang
 */
public class BIPropertyManager implements PropertyManager {

    private PropertyOperate propertyOperate = new BIPropertyOperate();
    private PerformancePlugManager manager = PerformancePlugManager.getInstance();
    private static final String PROPERTY_NAME = "propertyName";

    /**
     * 获取配置信息，包括paramConfig和properties
     * 将上面两个信息进行合并
     *
     * @return
     */
    @Override
    public Map<String, Map<String, String>> getProperties(String paramType) {
        List<PropertiesConfig> propertiesList = propertyOperate.read();
        Map<String, Map<String, String>> propertiesMap = convert2Map(propertiesList);
        Map<String, String> paramsMap = manager.getConfigByType(paramType);
        return mergeProperties(propertiesMap, paramsMap);
    }

    /**
     * 将获取到的param和property进行合并
     *
     * @param propertiesMap
     * @param paramValueMap
     * @return
     */
    private Map<String, Map<String, String>> mergeProperties(Map<String, Map<String, String>> propertiesMap, Map<String, String> paramValueMap) {
        paramValueMap = PerformanceParamTools.convert2File(paramValueMap);
        Iterator<String> propertyIterator = propertiesMap.keySet().iterator();
        String propertyKey;
        String propertyName;
        String value;
        while (propertyIterator.hasNext()) {
            propertyKey = propertyIterator.next();
            Map<String, String> tempMap = propertiesMap.get(propertyKey);
            propertyName = tempMap.get(PROPERTY_NAME);
            value = paramValueMap.get(propertyName);
            tempMap.put("value", value);
            propertiesMap.put(propertyKey, tempMap);
        }
        return propertiesMap;
    }

    /**
     * 将获取到的List<Property>转换成Map
     *
     * @return
     */
    private Map<String, Map<String, String>> convert2Map(List<PropertiesConfig> propertiesConfigList) {
        Map<String, Map<String, String>> convertMap = new HashMap<String, Map<String, String>>();
        String propertyKey;
        String fieldName;
        String fieldValue;
        try {
            PropertiesConfig propertiesConfig;
            for (int i = 0; i < propertiesConfigList.size(); i++) {
                Map<String, String> propertyMap = new HashMap<String, String>();
                propertiesConfig = propertiesConfigList.get(i);
                propertyKey = propertiesConfig.getPropertyKey();
                Field[] propertiesFields = propertiesConfig.getClass().getDeclaredFields();
                for (Field field : propertiesFields) {
                    field.setAccessible(true);
                    fieldName = field.getName();
                    fieldValue = String.valueOf(field.get(propertiesConfig));
                    propertyMap.put(fieldName, fieldValue);
                }
                convertMap.put(propertyKey, propertyMap);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return convertMap;
    }
}
