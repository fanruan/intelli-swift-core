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
    public List<List<PropertiesConfig>> getProperties(String paramType) {
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
    private List<List<PropertiesConfig>> trimProperties (List<PropertiesConfig> propertiesConfigList) {
        List<List<PropertiesConfig>> resultList = new ArrayList<List<PropertiesConfig>>();
        Map<String, PropertiesConfig> noRelationPropertiesMap = new HashMap<String, PropertiesConfig>();
        Map<String, List<PropertiesConfig>> withRelationPropertiesMap = new HashMap<String, List<PropertiesConfig>>();
        for (PropertiesConfig property : propertiesConfigList) {
            if (property.getRelationKey() == null) {
                //没有和别的配置存在关联的，放到noRelationProperties中，并且将propertyKey和property作为key-value保存
                noRelationPropertiesMap.put(property.getPropertyKey(), property);
            } else {
                //如果存在对应的关联的，放到withRelationPropertiesMap中
                if (withRelationPropertiesMap.get(property.getRelationKey()) != null) {
                    withRelationPropertiesMap.get(property.getRelationKey()).add(property);
                } else {
                    List<PropertiesConfig> tempList = new ArrayList<PropertiesConfig>();
                    tempList.add(property);
                    withRelationPropertiesMap.put(property.getRelationKey(), tempList);
                }
            }
        }

        Iterator<String> withRelationIterator = withRelationPropertiesMap.keySet().iterator();
        //将存在relation的property进行整理
        while (withRelationIterator.hasNext()) {
            String propertyKey = withRelationIterator.next();
            List<PropertiesConfig> withRelationList = withRelationPropertiesMap.get(propertyKey);
            withRelationList.add(0, noRelationPropertiesMap.remove(propertyKey));
            resultList.add(withRelationList);
        }
        Iterator<String> noRelationIterator = noRelationPropertiesMap.keySet().iterator();
        //处理没有relation的property
        while (noRelationIterator.hasNext()) {
            List<PropertiesConfig> tempList = new ArrayList<PropertiesConfig>();
            tempList.add(noRelationPropertiesMap.get(noRelationIterator.next()));
            resultList.add(tempList);
        }
        return resultList;
    }


}
