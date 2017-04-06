package com.fr.bi.cal.analyze.report.report.widget;


import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.table.PolyCubeECBlock;
import com.fr.bi.cal.analyze.executor.BIEngineExecutor;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.executor.table.*;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager.TableDirector;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build.SummaryCrossTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build.SummaryGroupTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.table.BITableReportSetting;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.field.target.target.cal.target.configure.BIPeriodConfiguredCalculateTarget;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.TemplateBlock;
import com.fr.stable.StringUtils;
import com.fr.web.core.SessionDealWith;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * BI表格控件
 *
 * @author Daniel-pc
 */
public class TableWidget extends BISummaryWidget {
    private static final long serialVersionUID = -4736577206434772688L;
    /**
     * 保存列字段等内容
     */
    @BICoreField
    private BITableReportSetting data = new BITableReportSetting();
    private int[] pageSpinner = new int[5];
    private int operator = BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH;
    private int table_type = BIReportConstant.TABLE_WIDGET.GROUP_TYPE;
    @BIIgnoreField
    private transient BIDimension[] usedDimension;
    @BIIgnoreField
    private transient BISummaryTarget[] usedTargets;
    private DetailChartSetting settings = new DetailChartSetting();
    protected Map<String, JSONArray> clicked = new HashMap<String, JSONArray>();
    protected Map<String, BIDimension> dimensionsIdMap = new HashMap<String, BIDimension>();
    private Map<String, BISummaryTarget> targetsIdMap = new HashMap<String, BISummaryTarget>();

    protected Map<Integer, List<String>> view = new HashMap<Integer, List<String>>();

    @Override
    public void setPageSpinner(int index, int value) {
        this.pageSpinner[index] = value;
    }

    private TableWidget linkedWidget;

    @Override
    public BIDimension[] getViewDimensions() {
        if (usedDimension != null) {
            return usedDimension;
        }
        BIDimension[] dimensions = getDimensions();
        if (data != null) {
            String[] array = data.getRow();
            ArrayList<BIDimension> usedDimensions = new ArrayList<BIDimension>();
            for (int i = 0; i < array.length; i++) {
                BIDimension dimension = BITravalUtils.getTargetByName(array[i], dimensions);
                if (dimension.isUsed()) {
                    usedDimensions.add(dimension);
                }
            }
            dimensions = usedDimensions.toArray(new BIDimension[usedDimensions.size()]);
        }
        usedDimension = dimensions;
        return dimensions;
    }

    @Override
    public BISummaryTarget[] getViewTargets() {
        if (usedTargets != null) {
            return usedTargets;
        }
        BISummaryTarget[] targets = getTargets();
        if (data != null) {
            String[] array = data.getSummary();
            ArrayList<BISummaryTarget> usedTargets = new ArrayList<BISummaryTarget>();
            for (int i = 0; i < array.length; i++) {
                BISummaryTarget target = BITravalUtils.getTargetByName(array[i], targets);
                if (target.isUsed()) {
                    usedTargets.add(target);
                }
            }
            targets = usedTargets.toArray(new BISummaryTarget[usedTargets.size()]);
        }
        usedTargets = targets;
        return targets;
    }

    public BIDimension[] getViewTopDimensions() {
        BIDimension[] dimensions = getDimensions();
        if (data != null) {
            String[] array = data.getColumn();
            ArrayList<BIDimension> usedDimensions = new ArrayList<BIDimension>();
            for (int i = 0; i < array.length; i++) {
                BIDimension dimension = BITravalUtils.getTargetByName(array[i], dimensions);
                if (dimension.isUsed()) {
                    usedDimensions.add(dimension);
                }
            }
            return usedDimensions.toArray(new BIDimension[usedDimensions.size()]);
        }
        return dimensions;
    }

    public boolean useRealData() {
        return data.useRealData();
    }

    /**
     * 有无编号
     *
     * @return 编号
     */
    @Override
    public int isOrder() {
        return settings.isOrder();
    }

    public BIEngineExecutor getExecutor(BISession session) {
        boolean calculateTarget = targetSort != null || !targetFilterMap.isEmpty();
        CrossExpander expander = new CrossExpander(complexExpander.getXExpander(0), complexExpander.getYExpander(0));
        boolean hasTarget = calculateTarget || getViewTargets().length > 0;
        if (this.table_type == BIReportConstant.TABLE_WIDGET.COMPLEX_TYPE || this.table_type == BIReportConstant.WIDGET.DOT) {
            return createComplexExecutor(session, hasTarget, complexExpander, expander);
        } else {
            return createNormalExecutor(session, hasTarget, getViewDimensions(), getViewTopDimensions(), expander);
        }
    }

    /**
     * 返回费复杂报表时的excute
     *
     * @param hasTarget 是否需要指标
     * @return 表格处理excute
     */
    public BIEngineExecutor createComplexExecutor(BISession session, boolean hasTarget, ComplexExpander complexExpander, CrossExpander expander) {
        BIEngineExecutor executor;
        int summaryLen = getViewTargets().length;
        ArrayList<ArrayList<String>> row = data.getComplex_x_dimension();
        ArrayList<ArrayList<String>> column = data.getComplex_y_dimension();
        BIComplexExecutData rowData = new BIComplexExecutData(row, dimensions);
        BIComplexExecutData columnData = new BIComplexExecutData(column, dimensions);
        int columnLen = columnData.getDimensionArrayLength();
        int rowLen = rowData.getDimensionArrayLength();
        if (rowData.getDimensionArrayLength() <= 1 && columnData.getDimensionArrayLength() <= 1) {
            return this.createNormalExecutor(session, hasTarget, rowData.getDimensionArray(0), columnData.getDimensionArray(0), expander);
        }
        //行表头区域里没有维度
        boolean b0 = !column.isEmpty() && rowLen == 0 && hasTarget;
        boolean b1 = !column.isEmpty() && rowLen == 0 && summaryLen == 0;
        //列表头区域里没有维度
        boolean b2 = !row.isEmpty() && columnLen == 0 && hasTarget;
        boolean b3 = !row.isEmpty() && columnLen == 0 && summaryLen == 0;
        if (b0) {
            executor = new ComplexHorGroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), column, session, complexExpander);
        } else if (b1) {
            //原来是ComplexHorGroupNoneExecutor, 先改成ComplexHorGroupExecutor，有问题再改回来
            executor = new ComplexHorGroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), column, session, complexExpander);
        } else if (b2) {
            executor = new ComplexGroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), row, session, complexExpander);
        } else if (b3) {
            //同b1
            executor = new ComplexGroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), row, session, complexExpander);
        } else {
            executor = new ComplexCrossExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), row, column, session, complexExpander);
        }
        return executor;
    }

    private BIEngineExecutor createNormalExecutor(BISession session, boolean hasTarget, BIDimension[] usedRows, BIDimension[] usedColumn, CrossExpander expander) {
        BIEngineExecutor executor;
        int summaryLen = getViewTargets().length;
        //有列表头和指标 horGroupExecutor 垂直的分组表
        boolean b0 = usedColumn.length > 0 && usedRows.length == 0 && hasTarget;
        //有表头没有指标
        boolean b2 = usedRows.length >= 0 && usedColumn.length == 0;
        if (b0) {
            executor = new HorGroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), session, expander);
        } else if (b2) {
            executor = new GroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), session, expander);
        } else {
            executor = new CrossExecutor(this, usedRows, usedColumn, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), session, expander);
        }

        return executor;
    }

    @Override
    public JSONObject createDataJSON(BISessionProvider session) throws Exception {
        BIEngineExecutor executor = getExecutor((BISession) session);
        JSONObject jo = new JSONObject();
        if (executor != null) {
            jo.put("data", executor.createJSONObject());
        }
        JSONArray ja = new JSONArray();
        for (int i : pageSpinner) {
            ja.put(i);
        }
        jo.put("page", ja);
        return jo;
    }

    /**
     * 创建表格的Block
     */
    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyCubeECBlock(this, session, operator);
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("linkedWidget")) {
            JSONObject linkedWidgetJSON = jo.getJSONObject("linkedWidget");
            if (linkedWidgetJSON.length() > 0) {
                this.linkedWidget = (TableWidget) BIWidgetFactory.parseWidget(linkedWidgetJSON, userId);
            }
        }

        if (jo.has("type")) {
            table_type = jo.optInt("type");
        }

        if (jo.has("page")) {
            this.operator = jo.getInt("page");
        }
        if (jo.has(BIJSONConstant.JSON_KEYS.EXPANDER)) {
            parsExpander(jo);
        }
        if (jo.has("settings")) {
            settings = new DetailChartSetting();
            settings.parseJSON(jo);
        }
        if (jo.has("clicked")) {
            JSONObject c = jo.getJSONObject("clicked");
            Iterator it = c.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                clicked.put(key, c.getJSONArray(key));
            }
        }
        createDimAndTars(jo);
        changeCalculateTargetStartGroup();
        createDimensionAndTargetMap();
    }

    private void createDimAndTars(JSONObject jo) throws Exception {
        if (jo.has("view")) {
            parseView(jo.optJSONObject("view"));
            data.parseJSON(jo);
        }
    }

    private void createDimensionAndTargetMap() {
        for (BIDimension dimension : this.getDimensions()) {
            for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
                Integer key = entry.getKey();
                if (key <= Integer.parseInt(BIReportConstant.REGION.DIMENSION2)) {
                    List<String> dIds = entry.getValue();
                    if (dIds.contains(dimension.getValue())) {
                        dimensionsIdMap.put(dimension.getValue(), dimension);
                        break;
                    }
                }
            }
        }
        for (BISummaryTarget target : this.getTargets()) {
            for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
                Integer key = entry.getKey();
                if (key >= Integer.parseInt(BIReportConstant.REGION.TARGET1)) {
                    List<String> dIds = entry.getValue();
                    if (dIds.contains(target.getValue())) {
                        targetsIdMap.put(target.getValue(), target);
                        break;
                    }
                }
            }
        }
    }

    public void parseView(JSONObject jo) throws Exception {
        Iterator it = jo.keys();
        while (it.hasNext()) {
            Integer region = Integer.parseInt(it.next().toString());
            List<String> dimensionIds = new ArrayList<String>();
            view.put(region, dimensionIds);
            JSONArray tmp = jo.getJSONArray(region.toString());
            for (int j = 0; j < tmp.length(); j++) {
                dimensionIds.add(tmp.getString(j));
            }
        }
    }

    private boolean isUsed(String dId) {
        boolean isDimUsed = dimensionsIdMap.containsKey(dId) && dimensionsIdMap.get(dId).isUsed();
        boolean isTargetUsed = targetsIdMap.containsKey(dId) && targetsIdMap.get(dId).isUsed();
        return isDimUsed || isTargetUsed;
    }

    public void setComplexExpander(ComplexExpander complexExpander) {
        this.complexExpander = complexExpander;
    }

    private void parsExpander(JSONObject jo) throws Exception {
        complexExpander = new ComplexExpander(data.getColumn().length, data.getRow().length);
        complexExpander.parseJSON(jo.getJSONObject(BIJSONConstant.JSON_KEYS.EXPANDER));
        if (jo.has(BIJSONConstant.JSON_KEYS.CLICKEDVALUE)) {
            JSONArray ja = jo.getJSONArray(BIJSONConstant.JSON_KEYS.CLICKEDVALUE);
            clickValue = new Object[ja.length()];
            for (int i = 0; i < ja.length(); i++) {
                clickValue[i] = ja.getString(i);
            }
        }
    }

    public String getDimensionName(String id) {
        BISummaryTarget target = this.targetsIdMap.get(id);
        return target == null ? StringUtils.EMPTY : target.getText();
    }

    public String[] getUsedTargetID() {
        Set<String> dimensionIds = new HashSet<String>();
        for (BISummaryTarget target : this.getTargets()) {
            if (target.isUsed()) {
                dimensionIds.add(target.getValue());
            }

        }
        return dimensionIds.toArray(new String[0]);
    }

    public DetailChartSetting getChartSetting() {
        return settings;
    }

    public String[] getAllDimensionIds() {
        Set<String> dimensionIds = new HashSet<String>();
        for (BIDimension dimension : this.getDimensions()) {
            dimensionIds.add(dimension.getValue());
        }
        return dimensionIds.toArray(new String[0]);
    }

    public String[] getAllTargetIds() {
        Set<String> targetIds = new HashSet<String>();
        for (BISummaryTarget target : this.getTargets()) {
            targetIds.add(target.getValue());
        }
        return targetIds.toArray(new String[0]);
    }

    public JSONObject getWidgetDrill() throws JSONException {
        JSONObject drills = JSONObject.create();
        String[] dimensionIds = this.getAllDimensionIds();

        for (int i = dimensionIds.length - 1; i >= 0; i--) {
            String key = dimensionIds[i];
            if (clicked.containsKey(key)) {
                drills.put(key, clicked.get(key));
            }
        }

        return drills;
    }

    public BIDimension getDrillDimension(JSONArray drill) throws JSONException {
        if (drill == null || drill.length() == 0) {
            return null;
        }
        String id = drill.getJSONObject(drill.length() - 1).getString("dId");
        return dimensionsIdMap.get(id);
    }

    public boolean showRowToTal() {
        return settings.showRowTotal();
    }

    @Override
    public boolean showColumnTotal() {
        return settings.showColTotal();
    }

    @Override
    public WidgetType getType() {
        return WidgetType.TABLE;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public boolean hasVerticalPrePage() {
        return pageSpinner[BIReportConstant.TABLE_PAGE.VERTICAL_PRE] > 0;
    }

    public boolean hasVerticalNextPage() {
        return pageSpinner[BIReportConstant.TABLE_PAGE.VERTICAL_NEXT] > 0;
    }

    public boolean hasHorizonPrePage() {
        return pageSpinner[BIReportConstant.TABLE_PAGE.HORIZON_PRE] > 0;
    }

    public boolean hasHorizonNextPage() {
        return pageSpinner[BIReportConstant.TABLE_PAGE.HORIZON_NEXT] > 0;
    }

    private void changeCalculateTargetStartGroup() {
        boolean changed = false;
        BISummaryTarget[] targets = getTargets();
        if (this.getViewDimensions().length <= 1 && this.getViewDimensions().length != 0) {
            for (int i = 0; i < targets.length; i++) {
                BISummaryTarget target = targets[i];
                if (target instanceof BIConfiguredCalculateTarget && !(target instanceof BIPeriodConfiguredCalculateTarget)) {
                    BIConfiguredCalculateTarget configuredCalculateTarget = (BIConfiguredCalculateTarget) target;
                    if ((configuredCalculateTarget.getStart_group() == 1)) {
                        configuredCalculateTarget.setStart_group(0);
                        changed = true;
                    }
                }
            }
            if (changed) {
                Map<String, TargetGettingKey> targetMap = new ConcurrentHashMap<String, TargetGettingKey>();
                for (int i = 0; i < targets.length; i++) {
                    targets[i].setTargetMap(targetMap);
                    targetMap.put(targets[i].getValue(), targets[i].createSummaryCalculator().createTargetGettingKey());
                }
            }
        }
    }

    public GroupValueIndex createLinkedFilterGVI(BusinessTable targetKey, BISession session) {
        if (linkedWidget != null) {
            GroupValueIndex fatherWidgetLinkedFilterGVI = linkedWidget.createLinkedFilterGVI(targetKey, session);
            List<MetricGroupInfo> metricGroupInfoList = session.getMetricGroupInfoList(this.linkedWidget.getWidgetName());
            if (metricGroupInfoList == null) {
                return null;
            }
            for (MetricGroupInfo mergerInfo : metricGroupInfoList) {
                if (ComparatorUtils.equals(mergerInfo.getMetric(), (targetKey))) {
                    return GVIUtils.AND(fatherWidgetLinkedFilterGVI, GVIUtils.AND(mergerInfo.getFilterIndex(), mergerInfo.getFilterIndex()));
                }
            }
        }
        return null;
    }


    @Override
    public void reSetDetailTarget() {
    }

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject dataJSON = this.createDataJSON((BISession) SessionDealWith.getSessionIDInfor(sessionId)).getJSONObject("data");
        Map<Integer, List<JSONObject>> viewMap = this.createViewMap();
        IExcelDataBuilder builder = null;

//        if (dataJSON.has("t")) {
//            builder = new SumaryCrossTableAbstractDataBuilder(viewMap, dataJSON);
//        } else {
//            builder = new SummaryNormalTableAbstractDataBuilder(viewMap, dataJSON);
//        }
        if (this.table_type == BIReportConstant.TABLE_WIDGET.CROSS_TYPE) {
            builder = new SummaryCrossTableDataBuilder(viewMap, dataJSON);
        }
        if (this.table_type == BIReportConstant.TABLE_WIDGET.GROUP_TYPE) {
            builder = new SummaryGroupTableDataBuilder(viewMap, dataJSON);
        }
        if (null == builder) {
            return new JSONObject();
        }
        TableDirector director = new TableDirector(builder);
        director.construct();
        return director.buildTableData().createJSON();
    }

    public String getDimensionNameByID(String dID) throws Exception {
        return getBITargetAndDimension(dID).getText();
    }

    public int getFieldTypeByDimensionID(String dID) throws Exception {
        return getBITargetAndDimension(dID).createColumnKey().getFieldType();
    }

    protected BITargetAndDimension getBITargetAndDimension(String dID) throws Exception {
        for (BIDimension dimension : getDimensions()) {
            if (ComparatorUtils.equals(dimension.getId(), dID)) {
                return dimension;
            }
        }
        for (BISummaryTarget target : getTargets()) {
            if (ComparatorUtils.equals(target.getId(), dID)) {
                return target;
            }
        }
        throw new Exception();
    }

    private Map<Integer, List<JSONObject>> createViewMap() throws Exception {
        Map<Integer, List<JSONObject>> dimAndTar = new HashMap<Integer, List<JSONObject>>();
        Iterator<Integer> iterator = view.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            List<JSONObject> list = new ArrayList<JSONObject>();
            List<String> ids = view.get(next);
            for (String dId : ids) {
                if (isUsed(dId)) {
                    int type = getFieldTypeByDimensionID(dId);
                    String text = getDimensionNameByID(dId);
                    list.add(new JSONObject().put("dId", dId).put("text", text).put("type", type));
                }
            }
            dimAndTar.put(next, list);
        }
        return dimAndTar;
    }
}