package com.fr.swift.adaptor.widget.datamining;

import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Jonas on 2018/5/9.
 */
public interface SwiftAlgorithmResultAdapter<B, T extends FineWidget, R extends SwiftResultSet, I extends QueryInfo> {
    SwiftResultSet getResult(B bean, T widget, R result, I info) throws Exception;
}
