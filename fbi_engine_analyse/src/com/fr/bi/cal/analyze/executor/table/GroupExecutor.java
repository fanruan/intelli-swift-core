package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCache;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCacheKey;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;
import com.fr.bi.cal.analyze.cal.sssecret.PageIteratorGroup;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BICounterTarget;
import com.fr.bi.field.target.target.BINumberTarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;
import com.fr.stable.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class GroupExecutor extends AbstractTableWidgetExecutor<Node> {

    private Rectangle rectangle;

    private BIDimension[] usedDimensions;

    private CrossExpander expander;

    /**
     * 节点返回之前的处理管道
     */
    private HashMap<Object, ResultNodeDealWithPipeline> nodeDealWidthPipelines = new HashMap<Object, ResultNodeDealWithPipeline>();

    public GroupExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {

        super(widget, paging, session);
        usedDimensions = widget.getViewDimensions();
        this.expander = expander;
    }

    public TableCellIterator createCellIterator4Excel() throws Exception {

        final Node tree = getCubeNode();
        if (tree == null) {
            return new TableCellIterator(0, 0);
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;

        //显示不显示汇总行
        int rowLen = tree.getTotalLengthWithSummary();
        //        final boolean useTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(usedDimensions);
        rectangle = new Rectangle(rowLength + widget.isOrder(), 1, columnLen + widget.isOrder() - 1, rowLen);
        final TableCellIterator iter = new TableCellIterator(columnLen + widget.isOrder(), rowLen + 1);
        new Thread() {

            public void run() {

                try {
                    FinalInt start = new FinalInt();
                    generateHeader(widget, usedDimensions, usedSumTarget, iter.getIteratorByPage(start.value), usedDimensions.length);
                    generateCells(tree, widget, widget.getViewDimensions(), iter, start, new FinalInt(), usedDimensions.length);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    /**
     * @param usedDimensions ComplexGroupExecutor复用时需要的参数
     * @param usedSumTarget  ComplexGroupExecutor复用时需要的参数
     * @param pagedIterator
     * @throws Exception
     */
    public static void generateHeader(TableWidget widget, BIDimension[] usedDimensions, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator, int maxRowDimensionsLength) throws Exception {

        int columnIdx = 0;
//        if (widget.isOrder() == 1) {
//            CBCell cell = ExecutorUtils.createCBCell(Inter.getLocText("BI-Number_Index"), 0, 1, columnIdx++, 1);
//            pagedIterator.addCell(cell);
//        }
        for (int i = 0; i < usedDimensions.length; i++) {
            //复杂表 行表头 区域1有2两个维度、区域2有3个维度，区域1里最后一个维度的columnSpan需要特殊处理
            int columnSpanOffSet = i == usedDimensions.length - 1 ? maxRowDimensionsLength - usedDimensions.length : 0;
            CBCell cell = ExecutorUtils.createCBCell(usedDimensions[i].getText(), 0, 1, columnIdx++, columnSpanOffSet + 1, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
        }
        for (BISummaryTarget anUsedSumTarget : usedSumTarget) {
            DetailChartSetting setting = widget.getChartSetting();
            int numLevel = setting.getNumberLevelByTargetId(anUsedSumTarget.getId());
            String unit = setting.getUnitByTargetId(anUsedSumTarget.getId());
            String levelAndUnit = ExecutorUtils.formatLevelAndUnit(numLevel, unit);

            String dimensionUnit = StringUtils.isEmpty(levelAndUnit) ? "" : "(" + levelAndUnit + ")";

            CBCell cell = ExecutorUtils.createCBCell(anUsedSumTarget.getText() + dimensionUnit, 0, 1, columnIdx++, 1, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
        }
    }

    /**
     * @param n                      ComplexGroupExecutor复用时需要的参数
     * @param widget                 ComplexGroupExecutor复用时需要的参数
     * @param rowDimensions          ComplexGroupExecutor复用时需要的参数
     * @param iter
     * @param start                  ComplexGroupExecutor复用时需要的参数
     * @param rowIdx                 ComplexGroupExecutor复用时需要的参数
     * @param maxRowDimensionsLength ComplexGroupExecutor复用时需要的参数,
     */
    public static void generateCells(Node n, TableWidget widget, BIDimension[] rowDimensions, TableCellIterator iter, FinalInt start, FinalInt rowIdx, int maxRowDimensionsLength) {

        while (n.getFirstChild() != null) {
            n = n.getFirstChild();
        }
        int[] oddEven = new int[rowDimensions.length];
        Object[] dimensionNames = new Object[rowDimensions.length];
        while (n != null) {
            if (checkNull(n, rowDimensions.length)) {
                Node temp = n;
                rowIdx.value++;
                int newRow = rowIdx.value & ExportConstants.MAX_ROWS_2007 - 1;
                if (newRow == 0) {
                    iter.getIteratorByPage(start.value).finish();
                    start.value++;
                }
                StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                //分组表维度需要合并单元格
                generateDimNames(temp, widget, rowDimensions, dimensionNames, oddEven, pagedIterator, rowIdx.value, maxRowDimensionsLength);
                generateTargetCells(temp, widget, pagedIterator, rowIdx.value, maxRowDimensionsLength, rowIdx.value % 2 == 1 ? widget.getTableStyle().getOddRowStyle(Style.getInstance()) : widget.getTableStyle().getEvenRowStyle(Style.getInstance()));
                if (widget.showRowToTal()) {
                    int columnSpanOffSet = maxRowDimensionsLength - rowDimensions.length + 1;
                    generateSumCells(temp, widget, pagedIterator, rowIdx, maxRowDimensionsLength - columnSpanOffSet, maxRowDimensionsLength);
                }
            }
            n = n.getSibling();
            while (n != null && n.getFirstChild() != null) {
                n = n.getFirstChild();
            }
        }
    }

    private static void generateSumCells(Node temp, TableWidget widget, StreamPagedIterator pagedIterator, FinalInt rowIdx, int columnIdx, int maxRowDimensionsLength) {
        //isLastSum 是否是最后一行会总行
        if ((widget.getViewTargets().length != 0) && checkIfGenerateSumCell(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                rowIdx.value++;
                boolean isLastSumRow = temp.getSibling() == null && columnIdx == 0;
                Style style = isLastSumRow ? widget.getTableStyle().getHeaderStyle(Style.getInstance()) : widget.getTableStyle().getSumRowStyle(Style.getInstance());
                CBCell cell = ExecutorUtils.createCBCell(Inter.getLocText("BI-Summary_Values"), rowIdx.value, 1, columnIdx, maxRowDimensionsLength - columnIdx, style);
                pagedIterator.addCell(cell);
                generateTargetCells(temp.getParent(), widget, pagedIterator, rowIdx.value, maxRowDimensionsLength, style);
            }
            //开辟新内存，不对temp进行修改
            Node parent = temp.getParent();
            generateSumCells(parent, widget, pagedIterator, rowIdx, columnIdx - 1, maxRowDimensionsLength);
        }
    }

    private static boolean checkNull(Node n, int length) {

        Node temp = n;
        for (int i = 0; i < length; i++) {
            if (temp.getParent() == null) {
                return false;
            }
            temp = temp.getParent();
        }
        return true;
    }

    private static void generateTargetCells(Node temp, TableWidget widget, StreamPagedIterator pagedIterator, int rowIdx, int dimensionsLength, Style style) {

        int targetsKeyIndex = 0;
        for (TargetGettingKey key : widget.getTargetsKey()) {
            int columnIdx = targetsKeyIndex + dimensionsLength;
            Object data = temp.getSummaryValue(key);
            CBCell cell = formatTargetCell(data, widget.getChartSetting(), key, rowIdx, columnIdx, style);
            pagedIterator.addCell(cell);
            targetsKeyIndex++;
        }
    }

    private static void generateDimNames(Node temp, TableWidget widget, BIDimension[] rowDimensions, Object[] dimensionNames, int[] oddEven, StreamPagedIterator pagedIterator, int rowIdx, int maxRowDimensionsLength) {
        //维度第一次出现即addCell
        int i = rowDimensions.length;
        while (temp.getParent() != null) {
            int rowSpan = widget.showRowToTal() ? temp.getTotalLengthWithSummary() : temp.getTotalLength();
            BIDimension dim = rowDimensions[--i];
            //年月日字段格式化
            Object v = ExecutorUtils.formatDateGroup(dim.getGroup().getType(), dim.toString(temp.getData()));
            if (!ComparatorUtils.equals(dimensionNames[i], v) || (i == rowDimensions.length - 1) || temp.getParent().getTotalLength() == 1) {
                oddEven[i]++;
                int columnSpanOffSet = i == rowDimensions.length - 1 ? maxRowDimensionsLength - rowDimensions.length : 0;
                Style style = rowIdx % 2 == 1 ? widget.getTableStyle().getOddRowStyle(Style.getInstance()) : widget.getTableStyle().getEvenRowStyle(Style.getInstance());
                CBCell cell = ExecutorUtils.createCBCell(v, rowIdx, rowSpan, i, 1 + columnSpanOffSet, style);
                pagedIterator.addCell(cell);
                dimensionNames[i] = v;
            }
            temp = temp.getParent();
        }
    }


    protected WidgetCacheKey createWidgetCacheKey() {
        PageIteratorGroup iteratorGroup = getPageIterator();
        Operator rowOp = PagingFactory.createRowOperator(paging.getOperator(), widget);
        return WidgetCacheKey.createKey(widget.fetchObjectCore(), expander.getXExpander(), expander.getXExpander(),
                rowOp, getStartIndex(rowOp, iteratorGroup == null ? null : iteratorGroup.getRowIterator(), usedDimensions.length),
                null, null, widget.getAuthFilter(session.getUserId()));
    }

    @Override
    public Node getCubeNode() throws Exception {

        if (session == null) {
            return null;
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        if (columnLen == 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        int calpage = paging.getOperator();
        CubeIndexLoader cubeIndexLoader = CubeIndexLoader.getInstance(session.getUserId());
        Node tree = cubeIndexLoader.loadPageGroup(false, widget, createTarget4Calculate(), usedDimensions,
                allDimensions, allSumTarget, calpage, widget.isRealData(), session, expander.getYExpander());
        if (tree == null) {
            tree = new Node(allSumTarget.length);
        }
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");

        // 如果不是实时数据 表格展示的时候,看到的汇总数据由各子节点的汇总数据求得
        // 尚待确定的功能
        //if (!widget.isRealData()) {
        //    addResultNodeDealWithPipline(new SummaryValueKeepSameAsViewPipeline());
        //}
        // 结果返回前的处理
        //dealWithResultNode(tree);
        return tree;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        WidgetCacheKey key = createWidgetCacheKey();
        WidgetCache<JSONObject> widgetCache = getWidgetCache(key);
        if (widgetCache != null){
            updateByCache(widgetCache);
            return widgetCache.getData();
        }
        JSONObject jo =  getCubeNode().toJSONObject(usedDimensions, widget.getTargetsKey(), -1);
        PageIteratorGroup pg = session.getPageIteratorGroup(true, widget.getWidgetId());
        NodeDimensionIterator rowIter = pg.getRowIterator().createClonedIterator();
        rowIter.setRoot(null);
        updateCache(key, new WidgetCache(jo, rowIter, null, widget.getPageSpinner()));
        return jo;
    }

    private void updateByCache(WidgetCache widgetCache) {
        widget.setPageSpinner(widgetCache.getPageSpinner());
        PageIteratorGroup pg = session.getPageIteratorGroup(true, widget.getWidgetId());
        if (pg == null){
            pg = new PageIteratorGroup();
            pg.setRowIterator(widgetCache.getRowIterator());
            session.setPageIteratorGroup(true, widget.getWidgetId(), pg);
        } else {
            NodeDimensionIterator rowIterator = widgetCache.getRowIterator().createClonedIterator();
            rowIterator.setRoot(pg.getRowIterator().getRoot());
            pg.setRowIterator(rowIterator);
        }
    }

    public Node getStopOnRowNode(Object[] stopRow) throws Exception {

        if (session == null) {
            return null;
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        if (columnLen == 0) {
            return null;
        }
        int calPage = paging.getOperator();
        CubeIndexLoader cubeIndexLoader = CubeIndexLoader.getInstance(session.getUserId());
        Node n = cubeIndexLoader.getStopWhenGetRowNode(stopRow, widget, createTarget4Calculate(), usedDimensions,
                allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
        return n;
    }

    private String getRandWidgetName() {

        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public Rectangle getSouthEastRectangle() {

        return rectangle;
    }

    /**
     * 初始化工作
     */
    private void init() {
        // 注册节点返回管道
        registerResultNodeDealWithPipline();
    }

    /**
     * 注册节点返回之前的操作管道
     */
    private void registerResultNodeDealWithPipline() {

    }

    /**
     * 添加处理管道
     *
     * @param pipeline
     */
    public void addResultNodeDealWithPipline(ResultNodeDealWithPipeline pipeline) {

        if (pipeline != null) {
            nodeDealWidthPipelines.put(pipeline.getPipelineName(), pipeline);
        }
    }

    /**
     * 删除处理管道
     *
     * @param pipelineName
     * @return
     */
    public ResultNodeDealWithPipeline removeResultNodeDealWithPipline(Object pipelineName) {

        if (pipelineName != null) {
            return nodeDealWidthPipelines.remove(pipelineName);
        }
        return null;
    }

    /**
     * 处理结果返回管道
     */
    private Node dealWithResultNode(Node n) {

        Node result = n;
        for (ResultNodeDealWithPipeline pipeline : nodeDealWidthPipelines.values()) {
            result = pipeline.dealWidthNode(result);
        }
        return result;
    }


    /**
     * 返回结果之前进行对node进行操作的管道
     * 1: 现在有一个需求是在设置界面的时候,分组汇总值并不和显示值一样,所以需要进行添加一层处理
     * 2: 不想以后多一个需求在另添加一个方法,所以定义一个接口,到时候有什么需要对节点进行特殊处理的只需要实现一个接口然后进行注册就行了.
     */
    public interface ResultNodeDealWithPipeline {

        /**
         * 处理节点
         */
        Node dealWidthNode(Node n);

        /**
         * 管道的签名,作为hashmap的key值
         *
         * @return
         */
        Object getPipelineName();
    }

    /**
     * 详细设置的时候,让分组的汇总值改为由显示出来的节点求得.因为详细设置只是显示部分节点
     */
    private class SummaryValueKeepSameAsViewPipeline implements ResultNodeDealWithPipeline {

        @Override
        public Node dealWidthNode(Node n) {

            if (n == null) {
                return n;
            }
            dealwidth(n);
            return n;
        }

        private void dealwidth(Node parent) {
            // 叶子节点,或者没有child的节点
            if (null == parent || parent.getChilds().size() == 0) {
                return;
            }
            List<Node> childs = parent.getChilds();
            for (Node child : childs) {
                dealwidth(child);
            }
            setSummaryValue(parent, childs);
        }

        private void setSummaryValue(Node parent, List<Node> childs) {

            int summaryLen = parent.getSummaryValue().length;
            Number[] summary = parent.getSummaryValue();
            for (int i = 0; i < summaryLen; i++) {
                BISummaryTarget target = widget.getTargets()[i];
                // 该指标使用的时候才需要进行操心这些事情
                if (target.isUsed()) {
                    List<Number> childSummaryList = new ArrayList<Number>();
                    for (Node child : childs) {
                        childSummaryList.add(child.getSummaryValue()[i]);
                    }
                    summary[i] = getNodeIndexSummary(target, summary[i], childSummaryList);
                }
            }
            parent.setSummaryValue(summary);
        }

        private Number getNodeIndexSummary(BISummaryTarget target, Number parentSum, List<Number> childSummaryList) {
            // FIXME 来一个更好的方法...
            Number r = 0;
            // 计数或数值求和
            if (target instanceof BICounterTarget || (target instanceof BINumberTarget && BIReportConstant.SUMMARY_TYPE.SUM == target.getSummaryType())) {
                // 求和
                r = getSum(childSummaryList);
            } else if (target instanceof BINumberTarget && BIReportConstant.SUMMARY_TYPE.MAX == target.getSummaryType()) {
                // 最大
                r = getMax(childSummaryList);
            } else if (target instanceof BINumberTarget && BIReportConstant.SUMMARY_TYPE.MIN == target.getSummaryType()) {
                // 最小
                r = getMin(childSummaryList);
            } else if (target instanceof BINumberTarget && BIReportConstant.SUMMARY_TYPE.AVG == target.getSummaryType()) {
                // 平均
                r = getSum(childSummaryList).doubleValue() / childSummaryList.size();
            } else {
                // 其它情况返回原值
                r = parentSum;
            }
            return r;
        }

        private Number getSum(List<Number> childSummaryList) {

            double r = NIOConstant.DOUBLE.NULL_VALUE;
            // 求和
            for (Number n : childSummaryList) {
                if (BICollectionUtils.isNotCubeNullKey(n)) {
                    if (BICollectionUtils.isCubeNullKey(r)) {
                        r = n.doubleValue();
                    } else {
                        r += n.doubleValue();
                    }
                }
            }
            return r;
        }

        private Number getMax(List<Number> numberList) {

            double max = NIOConstant.DOUBLE.NULL_VALUE;
            if (numberList.size() == 0) {
                return max;
            }
            for (Number n : numberList) {
                if (BICollectionUtils.isNotCubeNullKey(n)) {
                    double t = n.doubleValue();
                    max = (max > t) ? max : t;
                }
            }
            return max;
        }

        private Number getMin(List<Number> numberList) {

            double min = NIOConstant.DOUBLE.NULL_VALUE;
            if (numberList.size() == 0) {
                return min;
            }
            for (Number n : numberList) {
                if (BICollectionUtils.isNotCubeNullKey(n)) {
                    double t = n.doubleValue();
                    if (BICollectionUtils.isCubeNullKey(min)) {
                        min = t;
                    } else {
                        min = (min < t) ? min : t;
                    }
                }
            }
            return min;
        }

        @Override
        public Object getPipelineName() {

            return "SummaryValueKeepTheSameAsViewPipeline";
        }
    }

    public GroupValueIndex getClickGvi(Map<String, JSONArray> clicked, BusinessTable targetKey) {

        GroupValueIndex linkGvi = null;
        try {
            String target = getClieckTarget(clicked);
            // 连联动计算指标都没有就没有所谓的联动了,直接返回
            if (target == null) {
                return null;
            }
            BISummaryTarget summaryTarget = widget.getBITargetByID(target);
            BusinessTable linkTargetTable = summaryTarget.createTableKey();
            if (!targetKey.equals(linkTargetTable)) {
                return null;
            }
            List<Object> rowData = getLinkRowData(clicked, target, false);
            Node linkNode = getStopOnRowNode(rowData.toArray(), widget.getViewDimensions());
            // 总汇总值
            if (rowData == null || rowData.size() == 0) {
                for (String key : clicked.keySet()) {
                    linkGvi = GVIUtils.AND(linkGvi, getTargetIndex(key, linkNode));
                }
                return linkGvi;
            }
            linkGvi = GVIUtils.AND(linkGvi, getLinkNodeFilter(linkNode, target, rowData));
        } catch (Exception e) {
            BILoggerFactory.getLogger(GroupExecutor.class).info("error in get link filter", e);
        }
        return linkGvi;
    }
}