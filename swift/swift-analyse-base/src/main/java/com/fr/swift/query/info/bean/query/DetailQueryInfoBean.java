package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.query.QueryType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class DetailQueryInfoBean extends AbstractSingleTableQueryInfoBean implements Serializable {

    private static final long serialVersionUID = 1085839731012225722L;

    {
        queryType = QueryType.DETAIL;
    }

    public static Builder builder(String table) {
        return new Builder(table);
    }

    public static class Builder {
        private DetailQueryInfoBean bean;

        public Builder(String table) {
            bean = new DetailQueryInfoBean();
            bean.setTableName(table);
        }

        public Builder setFetchSize(int fetchSize) {
            bean.setFetchSize(fetchSize);
            return this;
        }

        public Builder setFilter(FilterInfoBean filter) {
            bean.setFilter(filter);
            return this;
        }

        public Builder setDimensions(List<DimensionBean> dimensions) {
            bean.setDimensions(dimensions);
            return this;
        }

        public Builder setDimensions(DimensionBean... dimensions) {
            bean.setDimensions(Arrays.asList(dimensions));
            return this;
        }

        public Builder setSorts(List<SortBean> sorts) {
            bean.setSorts(sorts);
            return this;
        }

        public DetailQueryInfoBean build() {
            return bean;
        }
    }
}
