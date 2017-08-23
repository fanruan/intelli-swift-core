package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder;

import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor.DataConstructor;
import com.fr.json.JSONException;

/**
 * Created by Kary on 2017/2/26.
 */
public interface ITableSCDataBuilder {
    void initAttrs() throws Exception;

    void amendment() throws Exception;

    void createTargetStyles();

    void createHeaders() throws Exception;

    void createItems() throws Exception;

    DataConstructor createTableData() throws JSONException;
}
