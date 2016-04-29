package com.fr.bi.base;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.BICoreService;
import com.fr.bi.common.persistent.BIBeanWrapper;
import com.fr.bi.exception.BIAmountLimitUnmetException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BITypeUtils;

import javax.activation.UnsupportedDataTypeException;
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
public class BICoreGenerator implements BICoreService {
    private Object targetObject;
    private BICore targetCore;

    public BICoreGenerator(Object targetObject) {
        this.targetObject = targetObject;
        generateBICore();
    }

    @Override
    public BICore fetchObjectCore() {
        return targetCore;
    }

    public void addAdditionalAttribute(Object attribute) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        targetCore.registerAttribute(attribute);
    }

    private void generateBICore() {
        targetCore = new BIBasicCore();
        Iterator<Object> it = extractCoreField().iterator();
        while (it.hasNext()) {
            Object attribute = it.next();
            try {
                targetCore.registerAttribute(attribute);
            } catch (Exception ignore) {
                BILogger.getLogger().error(ignore.getMessage(), ignore);
                continue;
            }
        }
    }

    private List<Object> extractCoreField() {
        BIBeanWrapper wrapper = new BIBeanWrapper(targetObject, targetObject.getClass());
        List<Object> result = new ArrayList<Object>();
        Iterator<Field> it = wrapper.seekSpecificTaggedAllField(BICoreField.class).iterator();
        while (it.hasNext()) {
            try {
                Field field = it.next();
                Object value = wrapper.getOriginalValue(field);
                addValue(value, result);
            } catch (Exception ignore) {
                BILogger.getLogger().error(ignore.getMessage(), ignore);
                continue;
            }
        }
        return result;
    }

    private void addValue(Object value, List<Object> result) {
        if (value != null) {
            if (BITypeUtils.isIterableObject(value)) {
                Iterator<Object> iterator = ((Iterable)value).iterator();
                while (iterator.hasNext()) {
                    addValue(iterator.next(), result);
                }
            }else if (BITypeUtils.isArrayObject(value)){
                for (Object o : (Object[])value){
                    addValue(o, result);
                }
            } else {
                result.add(value);
            }
        }
    }
}