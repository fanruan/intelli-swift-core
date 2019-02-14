package com.fr.swift.adaptor.widget.datamining;

import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Jonas on 2018/5/9.
 */
public abstract class SwiftAlgorithmResultAdapter<B, T extends FineWidget, R extends SwiftResultSet, I extends QueryInfo> {
    protected B bean;
    protected T widget;
    protected R result;
    protected I info;
    protected DMErrorWrap errorWrap;

    public SwiftAlgorithmResultAdapter(B bean, T widget, R result, I info, DMErrorWrap errorWrap) {
        this.bean = bean;
        this.widget = widget;
        this.result = result;
        this.info = info;
        this.errorWrap = errorWrap;
    }

    public abstract SwiftResultSet getResult() throws Exception;
}
