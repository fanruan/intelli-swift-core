package com.fr.swift.query.info.bean.element.filter;

import com.fr.swift.base.json.annotation.JsonIgnoreProperties;
import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.impl.AllShowFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.EmptyFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NullFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.StringOneValueFilterBean;
import com.fr.swift.query.query.FilterBean;

/**
 * @author yee
 * @date 2018/6/22
 */
@JsonTypeInfo(
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
        @JsonSubTypes.Type(value = AllShowFilterBean.class, name = "ALL_SHOW"),
        @JsonSubTypes.Type(value = EmptyFilterBean.class, name = "EMPTY")
})
@JsonIgnoreProperties(value = {"childrenExpr", "child"})
public interface FilterInfoBean<T> extends FilterBean<T> {
}
