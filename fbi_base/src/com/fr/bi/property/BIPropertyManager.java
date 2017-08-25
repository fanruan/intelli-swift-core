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

    /**
     * 获取配置信息，包括paramConfig和properties
     *
     * @return
     */
    @Override
    public List<SystemProperty> getProperties(String paramType) {
        List<PropertiesConfig> propertiesList = propertyOperate.read();
        Map<String, String> paramsMap = manager.getConfigByType(paramType);
        propertiesList = mergePropertiesValue(propertiesList, PerformanceParamTools.convert2File(paramsMap));
        return trimProperties(propertiesList);
    }

    /**
     * 将获取到的配置信息和对应的参数值进行合并
     * @param propertiesConfigList
     * @param paramsMap
     * @return
     */
    private List<PropertiesConfig> mergePropertiesValue (List<PropertiesConfig> propertiesConfigList, Map<String, String> paramsMap) {
        String propertyKey;
        for (PropertiesConfig property : propertiesConfigList) {
            propertyKey = property.getPropertyKey();
            property.setValue(paramsMap.get(propertyKey));
        }
        return propertiesConfigList;
    }

    /**
     * 将配置信息进行整理
     * @param propertiesConfigList
     * @return
     */
    private List<SystemProperty> trimProperties (List<PropertiesConfig> propertiesConfigList) {
        Map<String, PropertiesConfig> noRelationPropertiesMap = new HashMap<String, PropertiesConfig>();
        Map<String, PropertiesConfig> propertiesMap = new HashMap<String, PropertiesConfig>();
        for (PropertiesConfig property : propertiesConfigList) {
            //没有和别的配置存在关联的，放到noRelationProperties中，并且将propertyKey和property作为key-value保存
            if (property.getRelationKey() == null) {
                noRelationPropertiesMap.put(property.getPropertyKey(), property);
            } else {
                propertiesMap.put(property.getPropertyKey(), property);
            }
        }
        return constructResultObject(noRelationPropertiesMap, propertiesMap);
    }

    /**
     * 构建返回响应对象
     * @return
     */
    private List<SystemProperty> constructResultObject (Map<String, PropertiesConfig> noRelationPropertiesMap, Map<String, PropertiesConfig> withRelationPropertiesMap) {
        String withRelationPropertiesKey;
        List<SystemProperty> resultList = new ArrayList<SystemProperty>();
        Iterator<String> withRelationIt = withRelationPropertiesMap.keySet().iterator();
        while (withRelationIt.hasNext()) {
            withRelationPropertiesKey = withRelationIt.next();
            PropertiesConfig withRelationProperty = withRelationPropertiesMap.get(withRelationPropertiesKey);
            PropertiesConfig noRelationProperty = noRelationPropertiesMap.remove(withRelationProperty.getRelationKey());
            resultList.add(SystemProperty.construectObject(noRelationProperty, withRelationProperty));
        }
        Iterator<String> noRelationIt = noRelationPropertiesMap.keySet().iterator();
        while (noRelationIt.hasNext()) {
            resultList.add(SystemProperty.constructObject(noRelationPropertiesMap.get(noRelationIt.next())));
        }
        return resultList;
    }

}
