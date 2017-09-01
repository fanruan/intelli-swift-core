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
 * Created by Kary on 2017/2/27.
 */
public class SummaryCrossTableDataBuilder extends TableAbstractDataBuilder {

    public SummaryCrossTableDataBuilder(Map<Integer, BIDimensionConf[]> dimAndTar, JSONObject dataJSON, BIWidgetStyle styleSettings) throws Exception {
        super(dimAndTar, dataJSON, styleSettings);
    }

    @Override
    public void initAttrs() throws Exception {
        initAllAttrs();
        refreshDimsInfo();

    }

    @Override
    public void amendment() throws Exception {
        //仅有列表头的时候(有指标) 修正数据
        if (this.dimIds.size() == 0 && this.crossDimIds.size() > 0 && this.targetIds.size() > 0) {
            amendmentData();
        }
    }

    @Override
    public void createHeaders() throws Exception {
        //正常交叉表
        if (null != data && data.has("t")) {
            createCrossTableItems();
            createCrossTableHeader();
            return;
        }
        //仅有列表头的时候（无指标）
        if (this.dimIds.size() == 0 && this.crossDimIds.size() > 0 && this.targetIds.size() == 0) {
            getNoneTarCrossTable();
            return;
        }
        //无列表头(普通汇总表)
        createTableHeader();
        createTableItems();

    }

    @Override
    public void createItems() throws Exception {
    }

    private void getNoneTarCrossTable() throws Exception {
        createCrossHeader4OnlyCross();
        createCrossItems4OnlyCross();
        setOtherAttrs4OnlyCross();
    }

    @Override
    public DataConstructor createTableData() throws JSONException {
        DataConstructor tableDataForExport = new BISummaryDataConstructor(headers, items, crossHeaders, crossItems, styleSetting);
        return tableDataForExport;

    }
}
