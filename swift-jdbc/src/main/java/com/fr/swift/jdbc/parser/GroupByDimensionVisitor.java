package com.fr.swift.jdbc.parser;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.GroupBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/8/20.
 */
public class GroupByDimensionVisitor extends GroupQueryBeanVisitor{
    public GroupByDimensionVisitor(GroupQueryInfoBean queryBean) {
        super(queryBean);
    }

    @Override
    protected void addColumn(String columnName) {
        List<DimensionBean> dimensionBeans = queryBean.getDimensionBeans();
        if (dimensionBeans == null){
            dimensionBeans = new ArrayList<DimensionBean>();
            queryBean.setDimensionBeans(dimensionBeans);
        }
        DimensionBean bean = new DimensionBean();
        bean.setColumn(columnName);
        bean.setDimensionType(DimensionType.GROUP);
        GroupBean groupBean = new GroupBean();
        groupBean.setType(GroupType.NONE);
        bean.setGroupBean(groupBean);
        dimensionBeans.add(bean);
    }
}
