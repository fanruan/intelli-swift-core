package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/27.
 */
public class SummaryComplexTableBuilder extends SummaryAbstractTableDataBuilder {
private JSONArray converData=new JSONArray();
    public SummaryComplexTableBuilder(Map<Integer, List<JSONObject>> dimAndTar, JSONObject dataJSON) throws Exception {
        super(dimAndTar, dataJSON);
    }

    @Override
    public void initAttrs() throws JSONException {
        super.initAllAttrs();
        refreshDimsInfo();
    }

    private void refreshDimsInfo() {
    }

    @Override
    public void createHeadersAndItems() throws Exception {
        //正常复杂表
        if (this.isColRegionExist() && this.isRowRegionExist()) {
            createComplexTableItems();
        }
        //仅有列表头的时候（无指标）
        //仅有列表头的时候(有指标)
        //无列表头 有指标 当作多个普通分组表
    }
    /**
     * 基本的复杂表结构
     * 有几个维度的分组表示就有几个表
     * view: {10000: [a, b], 10001: [c, d]}, 20000: [e, f], 20001: [g, h], 20002: [i, j], 30000: [k]}
     * 表示横向（类似与交叉表）会有三个表，纵向会有两个表
     */
    private void createComplexTableItems() throws Exception {
        JSONArray tempItems=new JSONArray();
        JSONArray tempCrossItems=new JSONArray();
        // 如果行表头和列表头都只有一个region构造一个二维的数组

         if (dataJSON.has("l")&&dataJSON.has("t")){
            converData=new JSONArray().put(new JSONArray().put(dataJSON));
        }
        for (int i = 0; i < converData.length(); i++) {
            JSONArray rowValues=new JSONArray();
            JSONArray rowTables=converData.getJSONArray(i);
            for (int j = 0; j < rowTables.length(); j++) {
                //parse一个表结构
                JSONArray tableData=converData.getJSONArray(i);
//                createCrossTableItems();
            }
        }
    }

    //获取有效的列表头区域
    private boolean isRowRegionExist() {
        return dimAndTar.containsKey(BIReportConstant.REGION.DIMENSION1) && dimAndTar.get(BIReportConstant.REGION.DIMENSION1).size() > 0;
    }

    //行表头是否存在
    private boolean isColRegionExist() {
        return dimAndTar.containsKey(BIReportConstant.REGION.DIMENSION1) && dimAndTar.get(BIReportConstant.REGION.DIMENSION2).size() > 0;
    }


    @Override
    public BIExcelTableData createTableData() throws JSONException {
        return null;
    }
}
