package com.fr.bi.cal.analyze.report.report.widget;


import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.executor.BIEngineExecutor;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.executor.table.AbstractTableWidgetExecutor;
import com.fr.bi.cal.analyze.executor.table.ComplexCrossExecutor;
import com.fr.bi.cal.analyze.executor.table.ComplexGroupExecutor;
import com.fr.bi.cal.analyze.executor.table.ComplexHorGroupExecutor;
import com.fr.bi.cal.analyze.executor.table.CrossExecutor;
import com.fr.bi.cal.analyze.executor.table.GroupExecutor;
import com.fr.bi.cal.analyze.executor.table.HorGroupExecutor;
import com.fr.bi.cal.analyze.executor.utils.GlobalFilterUtils;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.ITableSCDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.SummaryComplexTableBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.SummaryCrossTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.SummaryGroupTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellDateFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellNumberFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellStringOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.BICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils.BITableConstructHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor.DataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.table.BITableReportSetting;
import com.fr.bi.cal.analyze.report.report.widget.util.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.common.persistent.annotation.PersistNameHistory;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.fs.BIChartStyleAttr;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.fs.tablechartstyle.BIWidgetBackgroundAttr;
import com.fr.bi.conf.report.SclCalculator;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.conf.settings.BIWidgetSettings;
import com.fr.bi.conf.report.conf.dimension.BIDimensionConf;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.BIWidgetStyle;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.filtervalue.number.rangefilter.NumberInRangeFilterValue;
import com.fr.bi.field.target.filter.field.ColumnFieldFilter;
import com.fr.bi.field.target.filter.general.GeneralANDFilter;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.TargetType;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.field.target.target.cal.target.configure.BIPeriodConfiguredCalculateTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BIResult;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.operation.group.group.AutoGroup;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * BI表格控件
 *
 * @author Daniel-pc
 */
public class TableWidget extends SummaryWidget implements SclCalculator {

    private static final long serialVersionUID = -4736577206434772688L;

    private int PAGE_SPINNER = 5;

    /**
     * 保存列字段等内容
     */
    @BICoreField
    private BITableReportSetting data = new BITableReportSetting();

    private int[] pageSpinner = new int[PAGE_SPINNER];

    private int operator = BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH;

    @PersistNameHistory(historyNames = {"table_type"})
    private int tableType = BIReportConstant.TABLE_WIDGET.GROUP_TYPE;

    @BIIgnoreField
    private transient BIDimension[] usedDimension;

    @BIIgnoreField
    private transient BISummaryTarget[] usedTargets;

    @BICoreField
    protected Map<String, JSONArray> clicked = new HashMap<String, JSONArray>();

    protected Map<String, BIDimension> dimensionsIdMap = new HashMap<String, BIDimension>();

    private Map<String, BISummaryTarget> targetsIdMap = new HashMap<String, BISummaryTarget>();

    protected Map<Integer, List<String>> view = new HashMap<Integer, List<String>>();

    private BIWidgetStyle style;

    private List<String> drillSequence = new ArrayList<String>();

    @Override
    public void setPageSpinner(int index, int value) {

        this.pageSpinner[index] = value;
    }

    public int[] getPageSpinner() {

        return pageSpinner;
    }

    public void setPageSpinner(int[] pageSpinner) {

        this.pageSpinner = pageSpinner;
    }

    @BICoreField
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

        return getWidgetConf().isOrder();
    }

    public BIEngineExecutor getExecutor(BISession session) {

        boolean calculateTarget = targetSort != null || !targetFilterMap.isEmpty();
        CrossExpander expander = new CrossExpander(complexExpander.getXExpander(0), complexExpander.getYExpander(0));
        boolean hasTarget = calculateTarget || getViewTargets().length > 0;
        if (this.tableType == BIReportConstant.TABLE_WIDGET.COMPLEX_TYPE) {
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
        if (b0 || b1) {
            executor = new ComplexHorGroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), column, session, complexExpander);
        } else if (b2 || b3) {
            executor = new ComplexGroupExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), row, session, complexExpander);
        } else {
            executor = new ComplexCrossExecutor(this, PagingFactory.createPaging(PagingFactory.PAGE_PER_GROUP_20, operator), row, column, session, complexExpander);
        }
        return executor;
    }

    private BIEngineExecutor createNormalExecutor(BISession session, boolean hasTarget, BIDimension[] usedRows, BIDimension[] usedColumn, CrossExpander expander) {

        BIEngineExecutor executor;
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
    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {

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

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

        super.parseJSON(jo, userId);
        // 放在创建expander之前，因为创建expander的时候需要用到维度的信息
        createDimAndTars(jo);
        if (jo.has("linkedWidget")) {
            JSONObject linkedWidgetJSON = jo.getJSONObject("linkedWidget");
            if (linkedWidgetJSON.length() > 0) {
                this.linkedWidget = (TableWidget) BIWidgetFactory.parseWidget(linkedWidgetJSON, userId);
            }
        }

        if (jo.has("type")) {
            tableType = jo.optInt("type");
        }

        if (jo.has("page")) {
            this.operator = jo.getInt("page");
        }
        if (jo.has(BIJSONConstant.JSON_KEYS.EXPANDER)) {
            parsExpander(jo);
        }
        if (jo.has("clicked")) {
            JSONObject c = jo.getJSONObject("clicked");
            Iterator it = c.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                clicked.put(key, c.getJSONArray(key));
            }
        }
        // 钻取队列
        if (jo.has("drillSequence")) {
            JSONArray ds = jo.optJSONArray("drillSequence");
            for (int i = 0; i < ds.length(); i++) {
                drillSequence.add(ds.optString(i));
            }
        }
        changeCalculateTargetStartGroup();
        createDimensionAndTargetMap();
        // 钻取的过滤条件现在不直接放在前端进行解析了，因为数值分组的钻取点击最后一个分组的时候会有问题 BI-9303
        dealWithDrill();
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

        Set<String> dimensionIds = new LinkedHashSet<String>();
        for (BISummaryTarget target : this.getTargets()) {
            if (target.isUsed()) {
                dimensionIds.add(target.getValue());
            }

        }
        return dimensionIds.toArray(new String[0]);
    }

    public String[] getAllDimensionIds() {

        Set<String> dimensionIds = new HashSet<String>();
        for (BIDimension dimension : this.getDimensions()) {
            dimensionIds.add(dimension.getValue());
        }
        return dimensionIds.toArray(new String[0]);
    }

    protected String[] getUsedDimensionID() {

        Set<String> dimensionIds = new LinkedHashSet<String>();
        for (BIDimension dimension : this.getDimensions()) {
            if (dimension.isUsed()) {
                dimensionIds.add(dimension.getValue());
            }
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
            if (clicked.containsKey(key) && drillSequence.contains(key)) {
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

        return getWidgetSettings(widgetConf).isShowRowTotal();
    }

    @Override
    public boolean showColumnTotal() {

        return getWidgetSettings(widgetConf).isShowColTotal();
    }

    @Override
    public WidgetType getType() {

        return WidgetType.TABLE;
    }

    public void setOperator(int operator) {

        this.operator = operator;
    }

    public String getThemeColor() {

        switch (tableType) {
            case BIReportConstant.WIDGET.TABLE:
            case BIReportConstant.WIDGET.CROSS_TABLE:
            case BIReportConstant.WIDGET.COMPLEX_TABLE:
                return getWidgetSettings().getThemeColor();
            default:
                return BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        }
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
                Map<String, BITarget> targetMap = new ConcurrentHashMap<String, BITarget>();
                for (int i = 0; i < targets.length; i++) {
                    targets[i].setTargetMap(targetMap);
                    targetMap.put(targets[i].getValue(), targets[i]);
                }
            }
        }
    }

    /**
     * 生成联动过滤gvi
     *
     * @param targetKey 被联动组件的基础表
     * @param session
     * @return
     */
    public GroupValueIndex createLinkedFilterGVI(BusinessTable targetKey, BISession session) {

        if (linkedWidget != null) {
            GroupValueIndex fatherWidgetLinkedFilterGVI = linkedWidget.createLinkedFilterGVI(targetKey, session);
            try {
                GroupValueIndex linkFilter = getLinkFilter(this.linkedWidget, targetKey, this.clicked, session);
                return GVIUtils.AND(fatherWidgetLinkedFilterGVI, linkFilter);
            } catch (Exception e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 获取联动过滤器
     *
     * @param linkedWidget 联动的组件
     * @param targetKey    被联动组件的基础表
     * @param clicked      联动组件被点击的值
     * @param session
     * @return
     * @throws Exception
     */
    public GroupValueIndex getLinkFilter(TableWidget linkedWidget, BusinessTable targetKey, Map<String, JSONArray> clicked, BISession session) throws Exception {

        BIEngineExecutor linkExecutor = linkedWidget.getExecutor(session);
        GroupValueIndex linkGvi = null;
        // 分组表,交叉表,复杂表的时候才有联动的必要
        if (linkExecutor instanceof AbstractTableWidgetExecutor) {
            return ((AbstractTableWidgetExecutor) linkExecutor).getClickGvi(clicked, targetKey);
        }
        return linkGvi;
    }


    private GroupValueIndex getTargetIndex(String target, Map<TargetGettingKey, GroupValueIndex> targetIndexs) {

        for (TargetGettingKey k : targetIndexs.keySet()) {
            if (k.getTargetName().equals(target)) {
                return targetIndexs.get(k);
            }
        }
        return null;
    }


    @Override
    public void reSetDetailTarget() {
        // do nothing
    }

    /*todo 想办法把数据和样式格式分离出来*/
    public JSONObject getPostOptions(BISessionProvider session, HttpServletRequest req) throws Exception {

        JSONObject res = this.createDataJSON(session, req);
        return calculateSCData(widgetConf, res).put("page", res.getJSONArray("page")).put("viewDimensionsLength", getViewDimensions().length).put("viewTopDimensionsLength", getViewTopDimensions().length).put("widgetType", this.tableType);
    }

    @Override
    public JSONObject calculateSCData(BIWidgetConf widgetConf, JSONObject data) throws Exception {

        Map<Integer, BIDimensionConf[]> viewMap = this.createViewMap(widgetConf);
        BIWidgetSettings widgetSettings = getWidgetSettings(widgetConf);
        Map<String, ITableCellFormatOperation> operationMap = createOperationMap(widgetConf);
        ITableSCDataBuilder builder = null;
        switch (widgetConf.getType()) {
            case BIReportConstant.TABLE_WIDGET.CROSS_TYPE:
                builder = new SummaryCrossTableDataBuilder(viewMap, data.getJSONObject("data"), widgetSettings);
                break;
            case BIReportConstant.TABLE_WIDGET.GROUP_TYPE:
                builder = new SummaryGroupTableDataBuilder(viewMap, data.getJSONObject("data"), widgetSettings);
                break;
            case BIReportConstant.TABLE_WIDGET.COMPLEX_TYPE:
                builder = new SummaryComplexTableBuilder(viewMap, data.getJSONObject("data"), widgetSettings);
                break;
            default:
                break;
        }

        DataConstructor res = BITableConstructHelper.buildTableData(builder);
        BITableConstructHelper.formatCells(res, operationMap, widgetSettings, getBackgroundColor(widgetConf));
        return res.createJSON().put("row", data.optLong("row", 0)).put("page", data.opt("page"));
    }

    /*
    * 此处仅需要考虑widget背景颜色,其他内容在他处计算
    * 基本逻辑如下：
    * 样式共四层，优先级由低到高依次为：系统设置样式，该模板全局样式，widget样式，指标样式，此处处理前三个
    * 当样式不一致时，优先级高的覆盖低的，选择纯色背景切设置为自动或透明时，展示效果同次一级的样式
    * */
    private BIWidgetBackgroundAttr getBackgroundColor(BIWidgetConf widgetConf) throws Exception {

        BIChartStyleAttr systemStyle = FBIConfig.getInstance().getChartStyleAttr();
        BIChartStyleAttr globalStyle = widgetConf.getGlobalStyleAttr();
        BIChartStyleAttr widgetStyle = getWidgetSettings(widgetConf).getWidgetStyle();
        BIWidgetBackgroundAttr finalBackgroundStyle = new BIWidgetBackgroundAttr();
        if (!widgetStyle.getWidgetBackground().isUseSuperiorStyle()) {
            finalBackgroundStyle = widgetStyle.getWidgetBackground();
        } else if (!globalStyle.getWidgetBackground().isUseSuperiorStyle()) {
            finalBackgroundStyle = globalStyle.getWidgetBackground();
        } else {
            finalBackgroundStyle = systemStyle.getWidgetBackground();
        }
        return finalBackgroundStyle;
    }


    private Map<String, ITableCellFormatOperation> createOperationMap(BIWidgetConf config) throws Exception {

        Map<String, ITableCellFormatOperation> formOperationsMap = new HashMap<String, ITableCellFormatOperation>();
        Map<Integer, BIDimensionConf[]> viewMap = config.getViewMap().getDetailViewMap();
        for (Integer integer : viewMap.keySet()) {
            BIDimensionConf[] dimJo = viewMap.get(integer);
            for (BIDimensionConf dimConf : dimJo) {
                if (dimConf.isDimensionUsed()) {
                    String dId = dimConf.getDimensionID();
                    int type = dimConf.getDimensionType();
                    ICellFormatSetting setting = new BICellFormatSetting();
                    if (config.getDimensions().getJSONObject(dId).has("settings")) {
                        setting.parseJSON(config.getDimensions().getJSONObject(dId).optJSONObject("settings"));
                    }
                    ITableCellFormatOperation op = null;
                    switch (type) {
                        case BIReportConstant.TARGET_TYPE.STRING:
                            op = new BITableCellStringOperation(setting);
                            break;
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
                        case BIReportConstant.TARGET_TYPE.DATE:
                            op = new BITableCellDateFormatOperation(config.getDimensions().getJSONObject(dId).getJSONObject("group").getInt("type"), setting);
                            break;
                        default:
                            op = new BITableCellStringOperation(setting);
                            break;
                    }
                    formOperationsMap.put(dId, op);
                }
            }

        }
        return formOperationsMap;
    }

    public String getDimensionNameByID(String dID) throws Exception {

        return getBITargetAndDimension(dID).getText();
    }

    public boolean isUsedById(String dID) throws Exception {

        for (int i = 0; i < this.dimensions.length; i++) {
            if (ComparatorUtils.equals(dID, dimensions[i].getId())) {
                return dimensions[i].isUsed();
            }
        }
        for (int i = 0; i < this.targets.length; i++) {
            if (ComparatorUtils.equals(dID, targets[i].getId())) {
                return targets[i].isUsed();
            }
        }
        throw new Exception();
    }

    protected BITargetAndDimension getBITargetAndDimension(String dID) throws Exception {

        for (BIDimension dimension : getDimensions()) {
            if (ComparatorUtils.equals(dimension.getId(), dID)) {
                return dimension;
            }
        }
        return this.getBITargetByID(dID);
    }

    public BISummaryTarget getBITargetByID(String id) throws Exception {

        for (BISummaryTarget target : getTargets()) {
            if (ComparatorUtils.equals(target.getId(), id)) {
                return target;
            }
        }
        throw new Exception();
    }

    private Map<Integer, BIDimensionConf[]> createViewMap(BIWidgetConf widgetConf) throws Exception {

        if (widgetConf != null) {
            return widgetConf.getViewMap().getDetailViewMap();
        } else {
            return this.widgetConf.getViewMap().getDetailViewMap();
        }
    }

    public BITableStyle getTableStyle() {

        String themeColor;
        switch (tableType) {
            case BIReportConstant.WIDGET.TABLE:
            case BIReportConstant.WIDGET.CROSS_TABLE:
            case BIReportConstant.WIDGET.COMPLEX_TABLE:
                themeColor = getWidgetSettings().getThemeColor();
                break;
            default:
                themeColor = BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
                break;
        }
        return new BITableStyle(themeColor);
    }

    public BIWidgetStyle getStyle() {

        return style;
    }

    /**
     * 组件的基础表
     * 只处理那种指标在同一个基础表里面的,取其中的一个指标进行获取其中关联的计算指标.
     *
     * @return
     */
    public BusinessTable getBaseTable() {

        BISummaryTarget[] targets = getTargets();
        for (BISummaryTarget target : targets) {
            //这边只加普通指标，计算指标在其他地方处理
            if (target == null || target.getType() != TargetType.NORMAL) {
                continue;
            }
            TargetCalculator summary = target.createSummaryCalculator();
            BusinessTable targetKey = summary.createTableKey();
            return targetKey;
        }
        // TODO 维度上面的
        BIDimension[] dimension = getDimensions();
        for (BIDimension dim : dimension) {
            return dim.createColumnKey().getTableBelongTo();
        }
        return null;
    }

    /**
     * 跳转过滤
     *
     * @return
     */
    public GroupValueIndex getJumpLinkFilter(BusinessTable targetKey, long userId, BISession session) {

        if (targetKey == null) {
            return null;
        } else if (getGlobalFilterWidget() != null) {
            // 如果是跳转打开的才需要进行设置
            // 如果已经设置了源字段和目标字段
            if (((AbstractBIWidget) getGlobalFilterWidget()).getGlobalSourceAndTargetFieldList().size() > 0) {
                return GlobalFilterUtils.getSettingSourceAndTargetJumpFilter(this, userId, session, targetKey, ((AbstractBIWidget) getGlobalFilterWidget()).getBaseTable());
            } else {
                return GlobalFilterUtils.getNotSettingSourceAndTargetJumpFilter(session, targetKey, this, false);
            }
        }
        return null;
    }

    public BIDimension getDimensionBydId(String dId) {

        if (dimensionsIdMap.containsKey(dId)) {
            return dimensionsIdMap.get(dId);
        }
        return null;
    }

    public BIResult getExportData(BISessionProvider session) {

        BIEngineExecutor executor = getExecutor((BISession) session);
        try {
            if (executor != null) {
                return executor.getResult();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).info("error in get result data");
        }
        return null;
    }

    /**
     * 处理钻取情况
     */
    public void dealWithDrill() throws Exception {

        try {
            JSONObject ret = getWidgetDrill();
            Iterator<String> ki = ret.keys();
            while (ki.hasNext()) {
                String k = ki.next();
                JSONArray kv = ret.optJSONArray(k);
                BIDimension dimension = getDimensionBydId(k);
                IGroup group = dimension.getGroup();
                if (group == null || group.getType() != BIReportConstant.GROUP.AUTO_GROUP) {
                    continue;
                }
                JSONObject vs = kv.optJSONObject(0);
                if (!vs.has("values")) {
                    continue;
                }
                JSONArray va = vs.optJSONArray("values");
                String v = va.getJSONObject(0).optJSONArray("value").getString(0);
                AutoGroup autoGroup = (AutoGroup) group;
                double rMin;
                double rMax;
                // 负数分组开头
                if (v.startsWith("-")) {
                    String temp = v.substring(1);
                    rMin = Double.parseDouble(v.substring(0, temp.indexOf("-")));
                    rMax = Double.parseDouble(temp.substring(temp.indexOf("-")));
                } else {
                    rMin = Double.parseDouble(v.substring(0, v.indexOf("-")));
                    rMax = Double.parseDouble(v.substring(v.indexOf("-") + 1));
                }
                // 自动分组的最大值与最小值
                double max = autoGroup.getMaxValue();
                double min = autoGroup.getMinValue();
                boolean isMaxClose = false;
                // 钻取的是自动分组的最后一个分组
                if (ComparatorUtils.equals(rMax, max)) {
                    // 前端的外层都是包装的and filter
                    isMaxClose = true;
                }
                GeneralANDFilter filter = (GeneralANDFilter) getFilter();
                NumberInRangeFilterValue rf = new NumberInRangeFilterValue(rMin, true, rMax, isMaxClose);
                BusinessField field = dimension.createColumnKey();
                ColumnFieldFilter ff = new ColumnFieldFilter(field, rf);
                JSONObject jo=JSONObject.create();
                JSONObject srcJ =JSONObject.create();
                srcJ.put(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT, field.getFieldID().getIdentity());
                jo.put(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT, srcJ);
                ff.setValueJo(jo);
                filter.addChild(ff);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().warnCache("error in deal with drill ", e);
        }
    }
}