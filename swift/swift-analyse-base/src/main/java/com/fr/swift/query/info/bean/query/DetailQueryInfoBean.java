package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryType;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/6/3.
 */
public class DetailQueryInfoBean extends AbstractSingleTableQueryInfoBean implements Serializable {

    private static final long serialVersionUID = 1085839731012225722L;

    {
        queryType = QueryType.DETAIL;
    }
}
