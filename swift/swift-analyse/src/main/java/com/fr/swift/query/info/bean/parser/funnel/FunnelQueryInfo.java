package com.fr.swift.query.info.bean.parser.funnel;

import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.result.SwiftResultSet;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public interface FunnelQueryInfo<T extends SwiftResultSet> extends QueryInfo<T> {
    FunnelQueryBean getQueryBean();
}
