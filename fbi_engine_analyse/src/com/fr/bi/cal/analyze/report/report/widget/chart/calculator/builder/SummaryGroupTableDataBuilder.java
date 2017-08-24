package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder;

import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor.BISummaryDataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor.DataConstructor;
import com.fr.bi.conf.report.conf.dimension.BIDimensionConf;
import com.fr.bi.conf.report.widget.BIWidgetStyle;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/16.
 */
public class SummaryGroupTableDataBuilder extends SummaryCrossTableDataBuilder {

    public SummaryGroupTableDataBuilder(Map<Integer, List<BIDimensionConf>> dimAndTar, JSONObject dataJSON, BIWidgetStyle styleSetting) throws Exception {
        super(dimAndTar, dataJSON, styleSetting);
    }

    @Override
    public void initAttrs() throws Exception {
        initAllAttrs();
        refreshDimsInfo();
    }

    @Override
    public void createItems() throws Exception {
        createTableItems();
    }

    @Override
    public void createHeaders() throws Exception {
        createTableHeader();
    }

    @Override
    public DataConstructor createTableData() throws JSONException {
        DataConstructor tableDataForExport = new BISummaryDataConstructor(headers, items, styleSetting);
        return tableDataForExport;

    }


}
