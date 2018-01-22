package com.fr.swift.adapter;

import com.fr.swift.adaptor.builder.DetailWidgetAdaptor;
import com.fr.swift.adaptor.builder.TableWidgetAdaptor;

/**
 * Created by pony on 2017/12/21.
 */
public interface WidgetAdaptor {
    WidgetAdaptor TABLE = new TableWidgetAdaptor();

    WidgetAdaptor DETAIL = new DetailWidgetAdaptor();

}
