package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.detail.PolyCubeDetailECBlock;
import com.fr.bi.cal.analyze.executor.detail.DetailExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.DetailTableBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.BITableCellDateFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.BITableCellNumberFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.BITableCellStringOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.setting.BICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.constructor.DataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableConstructHelper;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailReportSetting;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailSetting;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.conf.BIWidgetSettings;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.target.detailtarget.BIDetailTargetFactory;
import com.fr.bi.field.target.detailtarget.field.BIDateDetailTarget;
import com.fr.bi.field.target.detailtarget.field.BIStringDetailTarget;
import com.fr.bi.field.target.detailtarget.formula.BINumberFormulaDetailTarget;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.report.poly.TemplateBlock;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BIDetailWidget extends AbstractBIWidget {
    private static final long serialVersionUID = 3558768164064392671L;
    @BICoreField
    private BIDetailSetting data;
    @BICoreField
    private BIDetailTarget[] dimensions = new BIDetailTarget[0];
    @BICoreField
    private Map<String, TargetFilter> targetFilterMap = new LinkedHashMap<String, TargetFilter>();

    @BICoreField
    private BusinessTable target;//目标表
    private String[] sortTargets = new String[0];
    //page from 1~ max
    private int page = 1;

    private TableWidget linkedWidget;

    private Map<String, JSONArray> clicked = new HashMap<String, JSONArray>();

    public BIDetailTarget[] getDimensions() {
        return dimensions;
    }

    public BIDetailTarget[] getViewDimensions() {
        return this.getDimensions();
    }

    public BIDetailTarget[] getTargets() {
        return getDimensions();
    }

    public BIDetailTarget[] getViewTargets() {
        return getViewDimensions();
    }

    public List<BusinessTable> getUsedTableDefine() {
        List<BusinessTable> result = new ArrayList<BusinessTable>();
        BIDetailTarget[] dm = this.getDimensions();
        if (dm != null) {
            for (int i = 0; i < dm.length; i++) {
                BIDetailTarget dt = dm[i];
                if (null != dt.createColumnKey() && null != dt.createColumnKey().getTableBelongTo()) {
                    result.add(dt.createColumnKey().getTableBelongTo());
                }
            }
        }
        return result;
    }

    public List<BusinessField> getUsedFieldDefine() {
        List<BusinessField> result = new ArrayList<BusinessField>();
        BIDetailTarget[] dm = this.getDimensions();
        if (dm != null) {
            for (int i = 0; i < dm.length; i++) {
                BIDetailTarget dt = dm[i];
                result.add(dt.createColumnKey());
            }
        }
        return result;
    }

    public int isOrder() {
        return data.isOrder();
    }

    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {
        JSONObject jo = new JSONObject();
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.GROUP100);
        paging.setCurrentPage(page);
        DetailExecutor exe = new DetailExecutor(this, paging, (BISession) session);
        jo.put("data", exe.getCubeNode());
        jo.put("row", paging.getTotalSize());
        jo.put("size", paging.getPageSize());
        return jo;
    }

    public WidgetType getType() {
        return WidgetType.DETAIL;
    }

    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyCubeDetailECBlock(this, session, page);
    }

    public void reSetDetailTarget() {
        for (BIDetailTarget ele : getDimensions()) {
            if (ele != null) {
                ele.reSetDetailGetter();
            }
        }
        for (BIDetailTarget ele : getViewDimensions()) {
            if (ele != null) {
                ele.reSetDetailGetter();
            }
        }
    }

    public JSONObject generateResult (BIWidgetConf widgetConf, JSONObject data) {
        //TODO 明细表数据样式分离
        return data;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("view")) {
            data = new BIDetailReportSetting();
            data.parseJSON(jo);
        }
        if (jo.has("sortSequence")) {
            JSONArray ja = jo.getJSONArray("sortSequence");
            int len = ja.length();
            sortTargets = new String[len];
            for (int i = 0; i < len; i++) {
                sortTargets[i] = ja.getString(i);
            }
        }
        if (jo.has("page")) {
            this.page = jo.getInt("page");
        }
        if (jo.has("dimensions")) {
            try {
                parseDimensions(jo, userId);
            } catch (Exception e) {
                BILoggerFactory.getLogger().info(e.getMessage(), e);
            }
        }
        if (jo.has("filterValue")) {
            JSONObject targetFilter = jo.getJSONObject("filterValue");
            Iterator it = targetFilter.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                targetFilterMap.put(key, TargetFilterFactory.parseFilter(targetFilter.getJSONObject(key), userId));
            }
        }

        if (jo.has("linkedWidget")) {
            JSONObject linkedWidgetJSON = jo.getJSONObject("linkedWidget");
            if (linkedWidgetJSON.length() > 0) {
                this.linkedWidget = (TableWidget) BIWidgetFactory.parseWidget(linkedWidgetJSON, userId);
            }
        }

        if (jo.has("clicked")) {
            JSONObject c = jo.getJSONObject("clicked");
            Iterator it = c.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                clicked.put(key, c.getJSONArray(key));
            }
        }
    }

    @Override
    public void refreshSources() {
        if (target == null) {
            return;
        }
        try {
            BusinessTable newTable = BIModuleUtils.getAnalysisBusinessTableById(target.getID());
            if (null != newTable) {
                CubeTableSource newSource = newTable.getTableSource();
                if (isAnalysisSource(newSource)) {
                    newSource.refresh();
                }
                target.setSource(newSource);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(BIStringUtils.append("error: the analysisTable " + target.getID().getIdentityValue() + " is absent", "\n", e.getMessage()), e);
        }
    }

    @Override
    public JSONObject getPostOptions(BISessionProvider session, HttpServletRequest req) throws Exception {
        JSONObject data = this.createDataJSON(session, req);
        JSONObject dataJSON = data.getJSONObject("data");
        Map<Integer, List<JSONObject>> viewMap = createViewMap();
        IExcelDataBuilder builder = new DetailTableBuilder(viewMap, dataJSON, getWidgetSettings());
        DataConstructor tableData = BITableConstructHelper.buildTableData(builder);
        BITableConstructHelper.formatCells(tableData, createChartDimensions(), getWidgetSettings());
        JSONObject res = new JSONObject();
        res.put("row", data.optLong("row", 0));
        res.put("header", tableData.createJSON().get("header"));
        JSONArray itemsArray = new JSONArray();
        for (ITableItem item : tableData.getItems()) {
            JSONArray itemArray = new JSONArray();
            for (ITableItem tableItem : item.getChildren()) {
                itemArray.put(tableItem.createJSON());
            }
            itemsArray.put(itemArray);
        }
        res.put("items", itemsArray);
        res.put("widgetType", getType().getType());
        res.put("dimensionLength", getViewDimensions().length).put("row", data.optLong("row", 0)).put("size", data.optLong("size", 0));
        res.put("settings", tableData.getWidgetSettings().createJSON());
        return res;
    }

    private Map<String, ITableCellFormatOperation> createChartDimensions() {
        Map<String, ITableCellFormatOperation> formatOperationMap = new HashMap<String, ITableCellFormatOperation>();
        for (BIDetailTarget detailTarget : this.getTargets()) {
            try {
                if (!detailTarget.isUsed()) {
                    continue;
                }


                BICellFormatSetting setting = new BICellFormatSetting();
                setting.parseJSON(detailTarget.getChartSetting().getSettings());
                if (detailTarget instanceof BINumberFormulaDetailTarget) {
                    ITableCellFormatOperation op = new BITableCellNumberFormatOperation(setting);
                    formatOperationMap.put(detailTarget.getId(), op);
                    continue;
                }
                boolean isStringColumn = detailTarget instanceof BIStringDetailTarget && detailTarget.createColumnKey().getFieldType() == DBConstant.COLUMN.STRING;
                if (isStringColumn) {
                    formatOperationMap.put(detailTarget.getId(), new BITableCellStringOperation(setting));
                    continue;
                }
                if (detailTarget.createColumnKey().getFieldType() == DBConstant.COLUMN.DATE) {
                    int groupType = ((BIDateDetailTarget) detailTarget).getGroup().getType();
                    ITableCellFormatOperation op = new BITableCellDateFormatOperation(groupType, setting);
                    formatOperationMap.put(detailTarget.getId(), op);
                } else {
                    if (detailTarget.createColumnKey().getFieldType() == DBConstant.COLUMN.NUMBER) {
                        ITableCellFormatOperation op = new BITableCellNumberFormatOperation(setting);
                        formatOperationMap.put(detailTarget.getId(), op);
                    }
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        return formatOperationMap;
    }

    private Map<Integer, List<JSONObject>> createViewMap() throws Exception {
        Map<Integer, List<JSONObject>> dimAndTar = new HashMap<Integer, List<JSONObject>>();
        List<JSONObject> dims = new ArrayList<JSONObject>();
        for (BIDetailTarget detailTarget : this.getDimensions()) {
            if (!detailTarget.isUsed()) {
                continue;
            }
            String dId = detailTarget.getId();
            String text = detailTarget.getText();
            JSONObject jo = JSONObject.create().put("dId", dId).put("text", text);
            //计算指标不一定有type
            if (null != detailTarget.createColumnKey()) {
                jo.put("type", detailTarget.createColumnKey().getFieldType());
            }
            dims.add(jo);
        }
        dimAndTar.put(Integer.valueOf(BIReportConstant.REGION.DIMENSION1), dims);
        return dimAndTar;
    }

    private boolean isAnalysisSource(CubeTableSource newSource) {
        List analysisTypes = new ArrayList();
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.BASE);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.ETL);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.TEMP);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.USER_BASE);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.USER_ETL);
        return analysisTypes.contains(newSource.getType());
    }

    private void parseDimensions(JSONObject jo, long userId) throws Exception {
        JSONObject dims = jo.getJSONObject("dimensions");
        JSONArray view = jo.getJSONObject("view").getJSONArray(BIReportConstant.REGION.DIMENSION1);
        this.dimensions = new BIDetailTarget[view.length()];
        for (int i = 0; i < view.length(); i++) {
            JSONObject dimObject = dims.getJSONObject(view.getString(i));
            dimObject.put("dId", view.getString(i));
            this.dimensions[i] = BIDetailTargetFactory.parseTarget(dimObject, userId);
            JSONObject dimensionMap = dimObject.getJSONObject("dimensionMap");
            Iterator it = dimensionMap.keys();
            JSONArray relationJa = dimensionMap.optJSONObject(it.next().toString()).getJSONArray("targetRelation");
            List<BITableRelation> relationList = new ArrayList<BITableRelation>();
            for (int j = 0; j < relationJa.length(); j++) {
                relationList.add(BITableRelationHelper.getAnalysisRelation(relationJa.getJSONObject(j)));
            }
            this.dimensions[i].setRelationList(relationList);
        }
        setTargetTable(userId);
    }

    public void setTargetTable(long userID) {
        BITableID targetTableID = new BITableID();
        for (BIDetailTarget target : dimensions) {
            if (!(target instanceof BINumberFormulaDetailTarget)) {
                targetTableID = target.createTableKey().getID();
                break;
            }
        }
        target = BIModuleUtils.getAnalysisBusinessTableById(new BITableID(targetTableID));
        for (int i = 0; i < dimensions.length; i++) {
            List<BITableRelation> relations = dimensions[i].getRelationList(null, userID);
            if (!relations.isEmpty()) {
                target = relations.get(relations.size() - 1).getForeignTable();
                break;
            }
        }
    }

    public BusinessTable getTargetDimension() {
        return target;
    }

    public String[] getSortTargets() {
        return sortTargets;
    }

    public String[] getView() {
        if (data != null) {
            return data.getView();
        }
        return new String[0];
    }

    public TableWidget getLinkWidget() {
        return linkedWidget;
    }

    public void setLinkWidget(TableWidget linkedWidget) {
        this.linkedWidget = linkedWidget;
    }

    public Map<String, JSONArray> getClicked() {
        return this.clicked;
    }

    public void setClicked(Map<String, JSONArray> clicked) {
        this.clicked = clicked;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Map<String, TargetFilter> getTargetFilterMap() {
        return targetFilterMap;
    }
}