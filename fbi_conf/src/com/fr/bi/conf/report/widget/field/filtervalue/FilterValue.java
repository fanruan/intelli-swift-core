package com.fr.bi.conf.report.widget.field.filtervalue;

import com.fr.bi.conf.report.filter.FieldFilter;
import com.fr.bi.conf.report.filter.RowFilter;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.stable.xml.XMLable;


public interface FilterValue<T> extends FieldFilter, XMLable, RowFilter<T> {


    public boolean canCreateFilterIndex();


    boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader);

    boolean isAllCalculatorFilter();
}