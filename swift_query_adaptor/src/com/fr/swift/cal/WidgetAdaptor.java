package com.fr.swift.cal;

import com.fr.swift.cal.builder.DetailWidgetAdaptor;
import com.fr.swift.cal.builder.TableWidgetAdaptor;

/**
 * Created by pony on 2017/12/21.
 */
public interface WidgetAdaptor {
    WidgetAdaptor TABLE = new TableWidgetAdaptor();

    WidgetAdaptor DETAIL = new DetailWidgetAdaptor();

}
