package com.fr.swift.cloud.query.info.bean.element.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.cloud.query.filter.SwiftDetailFilterType;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.AllShowFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.EmptyFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NullFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.StringOneValueFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.WorkDayFilterInfoBean;
import com.fr.swift.cloud.query.query.FilterBean;

/**
 * @author yee
 * @date 2018/6/22
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = SwiftDetailFilterType.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InFilterBean.class, name = "IN"),
        @JsonSubTypes.Type(value = StringOneValueFilterBean.class, name = "STRING_LIKE"),
        @JsonSubTypes.Type(value = StringOneValueFilterBean.class, name = "STRING_ENDS_WITH"),
        @JsonSubTypes.Type(value = StringOneValueFilterBean.class, name = "STRING_STARTS_WITH"),
        @JsonSubTypes.Type(value = NumberInRangeFilterBean.class, name = "NUMBER_IN_RANGE"),
        @JsonSubTypes.Type(value = NFilterBean.class, name = "BOTTOM_N"),
        @JsonSubTypes.Type(value = NFilterBean.class, name = "TOP_N"),
        @JsonSubTypes.Type(value = NullFilterBean.class, name = "NULL"),
        @JsonSubTypes.Type(value = AndFilterBean.class, name = "AND"),
        @JsonSubTypes.Type(value = OrFilterBean.class, name = "OR"),
        @JsonSubTypes.Type(value = NotFilterBean.class, name = "NOT"),
        @JsonSubTypes.Type(value = WorkDayFilterInfoBean.class, name = "WORK_DAY"),
        @JsonSubTypes.Type(value = AllShowFilterBean.class, name = "ALL_SHOW"),
        @JsonSubTypes.Type(value = EmptyFilterBean.class, name = "EMPTY")
})
@JsonIgnoreProperties(value = {"childrenExpr", "child"})
public interface FilterInfoBean<T> extends FilterBean<T> {
}
