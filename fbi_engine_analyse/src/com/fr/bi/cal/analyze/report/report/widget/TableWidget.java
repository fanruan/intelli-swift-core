package com.fr.bi.cal.analyze.report.report.widget;


import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.table.PolyCubeECBlock;
import com.fr.bi.cal.analyze.executor.BIEngineExecutor;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.executor.table.AbstractTableWidgetExecutor;
import com.fr.bi.cal.analyze.executor.table.ComplexCrossExecutor;
import com.fr.bi.cal.analyze.executor.table.ComplexGroupExecutor;
import com.fr.bi.cal.analyze.executor.table.ComplexHorGroupExecutor;
import com.fr.bi.cal.analyze.executor.table.CrossExecutor;
import com.fr.bi.cal.analyze.executor.table.GroupExecutor;
import com.fr.bi.cal.analyze.executor.table.HorGroupExecutor;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.SummaryComplexTableBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.SummaryCrossTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.SummaryGroupTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellDateFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.BITableCellNumberFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.BICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor.DataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils.BITableConstructHelper;
import com.fr.bi.cal.analyze.report.report.widget.table.BITableReportSetting;
import com.fr.bi.cal.analyze.report.report.widget.util.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.common.persistent.annotation.PersistNameHistory;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.SclCalculator;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.conf.BIWidgetSettings;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.field.target.target.cal.target.configure.BIPeriodConfiguredCalculateTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.TemplateBlock;
import com.fr.stable.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * BI表格控件
 *
 * @author Daniel-pc
 */
public class TableWidget extends SummaryWidget implements SclCalculator {

    private static final long serialVersionUID = -4736577206434772688L;

    /**
     * 保存列字段等内容
     */
    @BICoreField
    private BITableReportSetting data = new BITableReportSetting();

    private int[] pageSpinner = new int[5];

    private int operator = BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH;

    @PersistNameHistory(historyNames = {"table_type"})
    private int tableType = BIReportConstant.TABLE_WIDGET.GROUP_TYPE;

    @BIIgnoreField
    private transient BIDimension[] usedDimension;

    @BIIgnoreField
    private transient BISummaryTarget[] usedTargets;
    @BICoreField
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
        if (this.tableType == BIReportConstant.TABLE_WIDGET.COMPLEX_TYPE || this.tableType == BIReportConstant.WIDGET.DOT) {
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

    }

    /*todo 想办法把数据和样式格式分离出来*/
    public JSONObject getPostOptions(BISessionProvider session, HttpServletRequest req) throws Exception {
        JSONObject res = this.createDataJSON(session, req);
        return calculateSCData(widgetConf, res.getJSONObject("data")).put("page", res.getJSONArray("page")).put("viewDimensionsLength", getViewDimensions().length).put("viewTopDimensionsLength", getViewTopDimensions().length).put("widgetType", this.tableType);
    }

    @Override
    public JSONObject calculateSCData(BIWidgetConf widgetConf, JSONObject data) throws Exception {
        Map<Integer, List<JSONObject>> viewMap = this.createViewMap(widgetConf);
        BIWidgetSettings widgetSettings = getWidgetSettings(widgetConf);
        Map<String, ITableCellFormatOperation> operationMap = getITableCellFormatOperationMap();
        IExcelDataBuilder builder = null;
        switch (this.tableType) {
            case BIReportConstant.TABLE_WIDGET.CROSS_TYPE:
                builder = new SummaryCrossTableDataBuilder(viewMap, data, widgetSettings);
                break;
            case BIReportConstant.TABLE_WIDGET.GROUP_TYPE:
                builder = new SummaryGroupTableDataBuilder(viewMap, data, widgetSettings);
                break;
            case BIReportConstant.TABLE_WIDGET.COMPLEX_TYPE:
                builder = new SummaryComplexTableBuilder(viewMap, data, widgetSettings);
                break;
        }
        DataConstructor res = BITableConstructHelper.buildTableData(builder);
        BITableConstructHelper.formatCells(res, operationMap, widgetSettings);
        return res.createJSON();
    }

    private Map<String, ITableCellFormatOperation> getITableCellFormatOperationMap() throws Exception {

        Map<String, ITableCellFormatOperation> formOperationsMap = new HashMap<String, ITableCellFormatOperation>();
        createFormatOperations(formOperationsMap);
        return formOperationsMap;
    }

    private void createFormatOperations(Map<String, ITableCellFormatOperation> operationsMap) throws Exception {

        for (BISummaryTarget target : this.getTargets()) {
            ICellFormatSetting setting = new BICellFormatSetting();
            setting.parseJSON(target.getChartSetting().getSettings());
            ITableCellFormatOperation op = new BITableCellNumberFormatOperation(setting);
            operationsMap.put(target.getId(), op);
        }
        for (BIDimension dimension : this.getDimensions()) {
            if (isStringDimension(dimension)) {
                continue;
            }
            ICellFormatSetting setting = new BICellFormatSetting();
            setting.parseJSON(dimension.getChartSetting().getSettings());
            ITableCellFormatOperation op;
            if (dimension.createColumnKey().getFieldType() == DBConstant.COLUMN.NUMBER) {
                op = new BITableCellNumberFormatOperation(setting);
            } else {
                op = new BITableCellDateFormatOperation(dimension.getGroup().getType(), setting);
            }
            operationsMap.put(dimension.getId(), op);
        }
    }

    //todo 简单处理，之后要提个接口
    private boolean isStringDimension(BIDimension dimension) {

        return dimension.createColumnKey().getFieldType() == DBConstant.COLUMN.STRING;
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

    private Map<Integer, List<JSONObject>> createViewMap(BIWidgetConf widgetConf) throws Exception {
        if (widgetConf == null) {
            return widgetConf.getDetailViewMap();
        } else {
            return this.widgetConf.getDetailViewMap();
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
        }
        return new BITableStyle(themeColor);
    }
}