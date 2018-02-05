package com.fr.swift.source.core;

import com.fr.swift.exception.AmountLimitUnmetException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.TypeUtils;

import javax.activation.UnsupportedDataTypeException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 辅助计算对象的Core
 * 取出目标对象内标注了BICoreField的字段
 * 生成BICore对象，并计算Core的ID（算ID的原因是
 * 目前BI系统中，判断对象唯一都依据ID，这样做到统一）
 * Created by Connery on 2016/1/25.
 */
public class CoreGenerator implements CoreService {
    private Object targetObject;
    private Core targetCore;

    public CoreGenerator(Object targetObject) {
        this.targetObject = targetObject;
        generateBICore();
    }

    public void addAdditionalAttribute(Object attribute) throws UnsupportedDataTypeException, AmountLimitUnmetException {
        targetCore.registerAttribute(attribute);
    }

    @Override
    public Core fetchObjectCore() {
        return targetCore;
    }

    private void generateBICore() {
        targetCore = new BasicCore();
        for (Object attribute : extractCoreField()) {
            try {
                targetCore.registerAttribute(attribute);
            } catch (Exception ignore) {
                SwiftLoggers.getLogger().error(ignore.getMessage(), ignore);
                continue;
            }
        }
    }

    private List<Object> extractCoreField() {
        CoreBeanWrapper wrapper = new CoreBeanWrapper(targetObject, targetObject.getClass());
        List<Object> result = new ArrayList<Object>();
        Iterator<Field> it = wrapper.seekSpecificTaggedAllField(CoreField.class).iterator();
        while (it.hasNext()) {
            try {
                Field field = it.next();
                Object value = wrapper.getOriginalValue(field);
                addValue(value, result);
            } catch (Exception ignore) {
                SwiftLoggers.getLogger().error(ignore.getMessage(), ignore);
                continue;
            }
        }
        return result;
    }

    private void addValue(Object value, List<Object> result) {
        if (value != null) {
            if (TypeUtils.isIterableObject(value)) {
                Iterator<Object> iterator = ((Iterable) value).iterator();
                while (iterator.hasNext()) {
                    addValue(iterator.next(), result);
                }
            } else if (TypeUtils.isArrayObject(value)) {
                int len = Array.getLength(value);
                for (int i = 0; i < len; i++){
                    addValue(Array.get(value, i), result);
                }
            } else {
                result.add(value);
            }
        }
    }
}