package com.fr.bi.conf.report.widget.field.dimension.filter;


/**
 * Created by Hiram on 2015/2/5.
 */
public interface IDissolubleResultFilter extends DimensionFilter {

    public DimensionFilter getTraversingResultFilter();

    public boolean hasTraversingResultFilter();

}