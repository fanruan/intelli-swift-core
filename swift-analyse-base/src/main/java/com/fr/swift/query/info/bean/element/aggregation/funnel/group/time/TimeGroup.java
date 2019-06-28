package com.fr.swift.query.info.bean.element.aggregation.funnel.group.time;

import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;

/**
 * 时间分组
 * TODO 暂时只知道Type，不连续的需要考虑下
 *
 * @author yee
 * @date 2019-06-28
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = ContinousTimeGroup.class)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "WORK_DAY", value = ContinousTimeGroup.class)
})
public interface TimeGroup {
    /**
     * 时间分组类型
     *
     * @return
     */
    TimeGroupType getType();
}
