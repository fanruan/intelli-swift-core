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
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.DetailTableBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellDateFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellNumberFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellStringOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.BICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils.BITableConstructHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor.DataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailReportSetting;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailSetting;
import com.fr.bi.cal.analyze.report.report.widget.util.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.SclCalculator;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.conf.BIWidgetSettings;
import com.fr.bi.conf.report.conf.dimension.BIDimensionConf;
import com.fr.bi.conf.report.widget.BIWidgetStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.target.detailtarget.BIDetailTargetFactory;
import com.fr.bi.field.target.detailtarget.formula.BINumberFormulaDetailTarget;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.report.result.BIDetailTableResult;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
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

public class DetailWidget extends AbstractBIWidget implements SclCalculator {
    private static final long serialVersionUID = 3558768164064392671L;
    @BICoreField
    private BIDetailSetting data;
    @BICoreField
    private BIDetailTarget[] dimensions = new BIDetailTarget[0];
    @BIIgnoreField
    private /*transient*/ BIDetailTarget[] usedDimensions;
    @BICoreField
    private Map<String, TargetFilter> targetFilterMap = new LinkedHashMap<String, TargetFilter>();

    @BICoreField
    private BusinessTable target;//目标表
    private List<String> parent_widget = new ArrayList<String>();
    private String[] sortTargets = new String[0];
    private BIWidgetStyle widgetStyle;
    //page from 1~ max
    private int page = 1;

    private TableWidget linkedWidget;

    private Map<String, JSONArray> clicked = new HashMap<String, JSONArray>();

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public BIDetailTarget[] getDimensions() {
        return dimensions;
    }

    @Override
    public BIDetailTarget[] getViewDimensions() {
        //后台不判断是不是使用状态，默认就是所有都使用
        return this.getDimensions();
    }

    @Override
    public BIDetailTarget[] getTargets() {
        return getDimensions();
    }

    @Override
    public BIDetailTarget[] getViewTargets() {
        return getViewDimensions();
    }

    @Override
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

    @Override
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


    public Map<String, TargetFilter> getTargetFilterMap() {
        return targetFilterMap;
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
                BusinessTable table = relations.get(relations.size() - 1).getForeignTable();
                if (isTableUsedInDimensions(table)) {
                    target = table;
                    break;
                } else {
                    relations.remove(relations.size() - 1);
                }
            }
        }
    }

    private boolean isTableUsedInDimensions(BusinessTable target) {
        for (BIDetailTarget dimension : dimensions) {
            if (dimension.createTableKey().getID().equals(target.getID())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("view")) {
            data = new BIDetailReportSetting();
            data.parseJSON(jo);
        }
        parseWidgetStyle(jo);
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

    private void parseWidgetStyle(JSONObject jo) throws Exception {
        widgetStyle = new BIWidgetSettings();
        if (jo.has("settings")) {
            widgetStyle.parseJSON(jo);
        }
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

    public BIWidgetStyle getWidgetStyle() {
        return widgetStyle;
    }

    public BusinessTable getTargetDimension() {
        return target;
    }

    public String[] getSortTargets() {
        return sortTargets;
    }

    @Override
    public int isOrder() {
        return widgetStyle.isShowNumber() ? 1 : 0;
    }

    public String[] getView() {
        if (data != null) {
            return data.getView();
        }
        return new String[0];
    }

    @Override
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

    private boolean isAnalysisSource(CubeTableSource newSource) {
        List analysisTypes = new ArrayList();
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.BASE);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.ETL);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.TEMP);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.USER_BASE);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.USER_ETL);
        return analysisTypes.contains(newSource.getType());
    }

    @Override
    public WidgetType getType() {
        return WidgetType.DETAIL;
    }

//    @Override
//    protected TemplateBlock createBIBlock(BISession session) {
//        return new PolyCubeDetailECBlock(this, session, page);
//    }


    @Override
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

    public JSONObject getPostOptions(BISessionProvider session, HttpServletRequest req) throws Exception {
        JSONObject data = this.createDataJSON(session, req);
        JSONObject res = calculateSCData(this.getWidgetConf(), data.getJSONObject("data"));
        res.put("row", data.optLong("row", 0));
        res.put("dimensionLength", getViewDimensions().length).put("row", data.optLong("row", 0)).put("size", data.optLong("size", 0));
        res.put("settings", this.getWidgetConf().getWidgetSettings().createJSON());
        return res;
    }

    @Override
    public JSONObject calculateSCData(BIWidgetConf widgetConf, JSONObject data) throws Exception {
        Map<Integer, List<BIDimensionConf>> viewMap = widgetConf.getDetailViewMap();
//        IExcelDataBuilder builder = new DetailTableBuilder(viewMap, data, getWidgetSettings(widgetConf));
//        DataConstructor tableData = BITableConstructHelper.buildTableData(builder);
//        BITableConstructHelper.formatCells(tableData, createOperationMap(widgetConf), getWidgetSettings(widgetConf));
        JSONObject res = new JSONObject();
//        res.put("header", tableData.createJSON().get("header"));
//        JSONArray itemsArray = new JSONArray();
//        for (ITableItem item : tableData.getItems()) {
//            JSONArray itemArray = new JSONArray();
//            for (ITableItem tableItem : item.getChildren()) {
//                itemArray.put(tableItem.createJSON());
//            }
//            itemsArray.put(itemArray);
//        }
//        res.put("items", itemsArray);
        return res;
    }

    private Map<String, ITableCellFormatOperation> createOperationMap(BIWidgetConf config) throws Exception {
        Map<String, ITableCellFormatOperation> formOperationsMap = new HashMap<String, ITableCellFormatOperation>();
        Map<Integer, List<BIDimensionConf>> viewMap = config.getDetailViewMap();
        for (Integer integer : viewMap.keySet()) {
            List<BIDimensionConf> dimJo = viewMap.get(integer);
            for (BIDimensionConf dimConf : dimJo) {
                if (dimConf.isDimensionUsed()) {
                    String dId = dimConf.getDimensionID();
                    int type = dimConf.getDimensionType();
                    ICellFormatSetting setting = new BICellFormatSetting();
                    if (config.getDimensions().getJSONObject(dId).has("settings")) {
                        setting.parseJSON(config.getDimensions().getJSONObject(dId).optJSONObject("settings"));
                    }
                    ITableCellFormatOperation op;
                    switch (type) {
                        case BIReportConstant.TARGET_TYPE.NUMBER:
                        case BIReportConstant.TARGET_TYPE.COUNTER:
                        case BIReportConstant.TARGET_TYPE.FORMULA:
                        case BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE:
                        case BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP:
                        case BIReportConstant.TARGET_TYPE.SUM_OF_ALL:
                        case BIReportConstant.TARGET_TYPE.SUM_OF_ALL_IN_GROUP:
                        case BIReportConstant.TARGET_TYPE.RANK:
                        case BIReportConstant.TARGET_TYPE.RANK_IN_GROUP:
                        case BIReportConstant.TARGET_TYPE.YEAR_ON_YEAR_RATE:
                        case BIReportConstant.TARGET_TYPE.MONTH_ON_MONTH_RATE:
                        case BIReportConstant.TARGET_TYPE.YEAR_ON_YEAR_VALUE:
                        case BIReportConstant.TARGET_TYPE.MONTH_ON_MONTH_VALUE:
                            op = new BITableCellNumberFormatOperation(setting);
                            break;
                        case BIReportConstant.TARGET_TYPE.STRING:
                            op = new BITableCellStringOperation(setting);
                            break;
                        case BIReportConstant.TARGET_TYPE.DATE:
                            op = new BITableCellDateFormatOperation(config.getDimensions().getJSONObject(dId).getJSONObject("group").getInt("type"), setting);
                            break;
                        default:
                            op = new BITableCellStringOperation(setting);
                    }
                    formOperationsMap.put(dId, op);
                }
            }
        }
        return formOperationsMap;
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

    public BIDetailTableResult getExportData(BISessionProvider session) {

        Paging paging = PagingFactory.createPaging(-1);
        paging.setCurrentPage(page);
        DetailExecutor exe = new DetailExecutor(this, paging, (BISession) session);
        return exe.getResult();
    }
}