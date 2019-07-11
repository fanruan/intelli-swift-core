package com.fr.swift.query.info.funnel.filter;

import com.fr.swift.base.json.annotation.JsonIgnoreProperties;
import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

/**
 * TODO 这个感觉可以直接用DetailFilter的东西，应该不需要另外写一套
 *
 * @author yee
 * @date 2019-06-28
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = DayFilterInfo.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "DAY", value = DayFilterInfo.class)
})
@JsonIgnoreProperties("timeSegment")
public interface TimeFilterInfo {
    /**
     * 时间过滤类型
     *
     * @return
     */
    TimeFilterType getType();

    /**
     * 真正的过滤器
     *
     * @return
     * @throws Exception
     */
    FilterInfoBean createFilter();

    long getTimeStart();

    int getTimeSegCount();

    long timeSegment();
}
