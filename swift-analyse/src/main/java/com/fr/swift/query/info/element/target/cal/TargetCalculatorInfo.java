package com.fr.swift.query.info.element.target.cal;

import com.fr.swift.query.info.bean.type.cal.CalTargetType;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorInfo {

    private int paramIndex;
    private int resultIndex;
    private CalTargetType type;

    public TargetCalculatorInfo(int paramIndex, int resultIndex, CalTargetType type) {
        this.paramIndex = paramIndex;
        this.resultIndex = resultIndex;
        this.type = type;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public int getResultIndex() {
        return resultIndex;
    }

    public CalTargetType getType() {
        return type;
    }
}
