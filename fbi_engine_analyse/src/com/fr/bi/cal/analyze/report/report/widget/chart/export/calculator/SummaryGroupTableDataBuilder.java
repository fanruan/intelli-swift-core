package com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.DimAndTargetStyle;
import com.fr.bi.conf.report.widget.IWidgetStyle;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/16.
 */
public class SummaryGroupTableDataBuilder extends SummaryCrossTableDataBuilder {

    public SummaryGroupTableDataBuilder(Map<Integer, List<JSONObject>> dimAndTar, List<DimAndTargetStyle> dimAndTargetStyles, JSONObject dataJSON, IWidgetStyle styleSetting) throws Exception {
        super(dimAndTar, dimAndTargetStyles, dataJSON, styleSetting);
    }

    @Override
    public void initAttrs() throws Exception {
        initAllAttrs();
        refreshDimsInfo();
        //仅有列表头的时候(有指标) 修正数据
//        if (this.dimIds.size() == 0 && this.crossDimIds.size() > 0 && this.targetIds.size() > 0) {
//            amendment();
//        }
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
    public BIExcelTableData createTableData() throws JSONException {
        BIExcelTableData tableDataForExport = new BIExcelTableData(headers, items,styleSetting);
        return tableDataForExport;

    }


}
