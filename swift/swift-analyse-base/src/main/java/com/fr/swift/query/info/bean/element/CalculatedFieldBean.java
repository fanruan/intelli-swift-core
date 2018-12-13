package com.fr.swift.query.info.bean.element;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.type.cal.CalTargetType;

import java.util.List;

/**
 * Created by Lyon on 2018/6/2.
 */
public class CalculatedFieldBean {

    @JsonProperty
    private CalTargetType type;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> parameters;

    public CalTargetType getType() {
        return type;
    }

    public void setType(CalTargetType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}
