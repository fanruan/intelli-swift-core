package com.fr.swift.adaptor;

import com.fr.swift.adaptor.builder.DetailWidgetAdaptor;
import com.fr.swift.adaptor.builder.TableWidgetAdaptor;

/**
 * @author pony
 * @date 2017/12/21
 */
public interface WidgetAdaptor {
    WidgetAdaptor TABLE = new TableWidgetAdaptor();

    WidgetAdaptor DETAIL = new DetailWidgetAdaptor();
}