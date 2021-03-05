package com.fr.swift.cloud.query.info.bean.query;

import com.fr.swift.cloud.query.info.bean.element.DimensionBean;

import java.util.List;

/**
 * Create by lifan on 2019-06-14 10:47
 */
public interface SingleInfoBean extends QueryInfoBean {

    List<DimensionBean> getDimensions();

}
