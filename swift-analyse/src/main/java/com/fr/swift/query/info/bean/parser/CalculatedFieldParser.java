package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.query.info.bean.type.cal.CalTargetType;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.cal.GroupTargetImpl;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/7.
 */
public class CalculatedFieldParser {

    static GroupTarget parse(CalculatedFieldBean bean, Map<String, Integer> fieldIndexMap) {
        CalTargetType type = bean.getType();
        switch (type) {
            case ARITHMETIC_ADD:
            case ARITHMETIC_DIV:
            case ARITHMETIC_MUL:
            case ARITHMETIC_SUB:
                return new GroupTargetImpl(0, fieldIndexMap.get(bean.getName()),
                        getParamIndexes(bean.getParameters(), fieldIndexMap), bean.getType());
            case ALL_MAX:
                return new GroupTargetImpl(0, fieldIndexMap.get(bean.getName()),
                        getParamIndexes(bean.getParameters(), fieldIndexMap), bean.getType());
            case ALL_MIN:
                return new GroupTargetImpl(0, fieldIndexMap.get(bean.getName()),
                        getParamIndexes(bean.getParameters(), fieldIndexMap), bean.getType());
        }
        return null;
    }

    private static int[] getParamIndexes(List<String> params, Map<String, Integer> fieldIndexMap) {
        int[] indexes = new int[params.size()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = fieldIndexMap.get(params.get(i));
        }
        return indexes;
    }
}
