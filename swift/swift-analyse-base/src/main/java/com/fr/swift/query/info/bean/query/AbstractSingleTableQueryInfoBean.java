package com.fr.swift.query.info.bean.query;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public abstract class AbstractSingleTableQueryInfoBean extends AbstractQueryInfoBean {

    @JsonProperty
    private String tableName;
    @JsonProperty
    private FilterInfoBean filter;
    @JsonProperty
    private List<DimensionBean> dimensions = new ArrayList<DimensionBean>(0);
    @JsonProperty
    private List<SortBean> sorts = new ArrayList<SortBean>(0);

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
    }

    public List<DimensionBean> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<DimensionBean> dimensions) {
        this.dimensions = dimensions;
    }

    public List<SortBean> getSorts() {
        return sorts;
    }

    public void setSorts(List<SortBean> sorts) {
        this.sorts = sorts;
    }
}
