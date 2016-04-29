package com.fr.bi.common.persistent.json.generator.tool;

import com.fr.bi.common.persistent.BIBeanWrapper;
import com.fr.bi.common.persistent.json.generator.BIKeyFieldSizeException;
import com.fr.bi.common.persistent.json.generator.anno.BIJSONElementKey;
import com.fr.bi.exception.BIObjectFrameException;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 提前出对象作为key的字段值
 * Created by Connery on 2016/1/20.
 */
public class JSONMapKeyExtractor {
    private Object target;
    private String fieldName;

    public JSONMapKeyExtractor(Object target) {
        this.target = target;
    }

    public String extractKeyFieldValue()
            throws BIKeyFieldSizeException, IntrospectionException, IllegalAccessException, InvocationTargetException, BIObjectFrameException {
        if (BITypeUtils.isBasicValue(target.getClass()) || ComparatorUtils.equals(String.class, target.getClass())) {
            return target.toString();
        } else {
            BIBeanWrapper wrapper = new BIBeanWrapper(target, target.getClass());
            List<Field> taggedFields = wrapper.seekSpecificTaggedAllField(BIJSONElementKey.class);
            if (taggedFields.size() == 1) {
                fieldName = taggedFields.get(0).getName();
                Object value = wrapper.getOriginalValue(taggedFields.get(0));
                if (!ComparatorUtils.equals(value.getClass().getName(), target.getClass().getName())) {
                    /**
                     * 递归进入
                     */
                    return new JSONMapKeyExtractor(wrapper.getOriginalValue(taggedFields.get(0))).extractKeyFieldValue();
                } else {
                    throw new BIObjectFrameException("the class:" + target.getClass().getName() + ", has a field which class type is same and was tagged map key.it" +
                            " cause in endless loop");
                }
            } else {
                throw new BIKeyFieldSizeException("Please check the class:" + target.getClass() + ",the BIJSONGeneratorMapKey annotation" +
                        " was tagged " + taggedFields.size() + " times,it only be tagged one time in single class");
            }
        }
    }

    public String getFieldName() {
        return fieldName;
    }
}