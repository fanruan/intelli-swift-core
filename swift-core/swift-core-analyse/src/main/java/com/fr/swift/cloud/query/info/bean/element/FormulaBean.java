package com.fr.swift.cloud.query.info.bean.element;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.cloud.query.formula.FormulaType;

/**
 * @author yee
 * @date 2019-07-24
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = ToDateFormulaBean.class, name = "TO_DATE"),
                @JsonSubTypes.Type(value = ToDateFormatFormulaBean.class, name = "TO_DATE_FORMAT")
        }
)
public interface FormulaBean {
    String getFormula();

    String[] getColumns();

    FormulaType getType();
}
