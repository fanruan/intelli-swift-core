package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;

import java.util.*;

public class CrossExecutor extends AbstractTableWidgetExecutor<NewCrossRoot> {

    private BIDimension[] rowDimension;
    private BIDimension[] colDimension;
    private CrossExpander expander;

    public CrossExecutor(TableWidget widget, BIDimension[] usedRows,
                         BIDimension[] usedColumn,
                         Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session);
        this.rowDimension = usedRows;
        this.colDimension = usedColumn;
        this.expander = expander;
    }

    @Override
    public TableCellIterator createCellIterator4Excel() throws Exception {
        NewCrossRoot node = getCubeNode();
        if (node == null) {
            return new TableCellIterator(0, 0);
        }

        int len = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[len];
        boolean isWholeCol = keys.length == 0 || !widget.getChartSetting().showColTotal();
        boolean isWholeRow = keys.length == 0 || !widget.getChartSetting().showRowTotal();
        int columnLen = (isWholeCol ? node.getTop().getTotalLength() :
                node.getTop().getTotalLengthWithSummary()) * Math.max(1, keys.length) + rowDimension.length + widget.isOrder();
        int rowLen = (isWholeRow ? node.getLeft().getTotalLength() :
                node.getLeft().getTotalLengthWithSummary()) + colDimension.length + 1;

        final TableCellIterator iter = new TableCellIterator(columnLen, rowLen);
        new Thread() {
            public void run() {
                try {
                    FinalInt start = new FinalInt();
                    FinalInt rowIdx = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    NewCrossRoot[] newCrossRoots = new NewCrossRoot[1];
                    newCrossRoots[0] = getCubeNode();
                    generateTitle(newCrossRoots, widget, colDimension, rowDimension, usedSumTarget, pagedIterator, rowIdx);
                    rowIdx.value++;
                    generateCells(newCrossRoots, widget, rowDimension, rowDimension.length, iter, start, rowIdx, 0);
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
     * @param roots         ComplexCrossExecutor复用此方法时需要的参数
     * @param widget        ComplexCrossExecutor复用此方法时需要的参数
     * @param colDimension  ComplexCrossExecutor复用此方法时需要的参数
     * @param rowDimension  ComplexCrossExecutor复用此方法时需要的参数
     * @param usedSumTarget ComplexCrossExecutor复用此方法时需要的参数
     * @param pagedIterator
     * @param rowIdx
     * @throws Exception
     */
    public static void generateTitle(NewCrossRoot[] roots, TableWidget widget, BIDimension[] colDimension, BIDimension[] rowDimension,
                                     BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator, FinalInt rowIdx) throws Exception {
        int rootsLen = roots.length;
        CrossHeader[] tops = new CrossHeader[rootsLen];
        for (int i = 0; i < rootsLen; i++) {
            tops[i] = roots[i].getTop();
        }
        Style style = BITableStyle.getInstance().getTitleDimensionCellStyle(0);
        if (widget.isOrder() == 1) {
            CBCell cell = ExecutorUtils.createCell(Inter.getLocText("BI-Number_Index"), 0, colDimension.length + 1, 0, 1, style);
            pagedIterator.addCell(cell);
        }

        int colDimLen = 0;
        while (colDimLen < colDimension.length) {
            CBCell cell = ExecutorUtils.createCell(colDimension[colDimLen].getText(), rowIdx.value, 1, widget.isOrder(), rowDimension.length, style);
            pagedIterator.addCell(cell);
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = rowDimension.length + widget.isOrder();
            for (int i = 0; i < rootsLen; i++) {
                tops[i] = (CrossHeader) tops[i].getFirstChild();
                //列表头
                getColDimensionsTitle(widget, colDimension, usedSumTarget, pagedIterator, tops[i], rowIdx.value, columnIdx, style);
            }
            rowIdx.value++;
            colDimLen++;
        }

        for (int i = 0; i < rowDimension.length; i++) {
            CBCell cell = ExecutorUtils.createCell(rowDimension[i].getText(), rowIdx.value, 1, i + widget.isOrder(), 1, style);
            pagedIterator.addCell(cell);
        }
        if (widget.getViewTargets().length > 1) {
            FinalInt targetsTitleColumnIdx = new FinalInt();
            targetsTitleColumnIdx.value = rowDimension.length + widget.isOrder();
            for (int i = 0; i < rootsLen; i++) {
                getTargetsTitle(usedSumTarget, pagedIterator, tops[i], rowIdx.value, targetsTitleColumnIdx, style);
            }
        }
    }

    private static void getColDimensionsTitle(TableWidget widget, BIDimension[] colDimension,
                                              BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator,
                                              CrossHeader top, int rowIdx, FinalInt columnIdx, Style style) {
        int targetNum = widget.getViewTargets().length;

        CrossHeader temp = top;
        while (temp != null) {
            int columnSpan = temp.getTotalLength() * targetNum;
            Object data = temp.getData();
            BIDimension dim = widget.getViewDimensions()[rowIdx];
            Object v = dim.getValueByType(data);
            CBCell cell = ExecutorUtils.createCell(v, rowIdx, colDimension.length + (usedSumTarget.length == 1 ? 1 : 0), columnIdx.value, columnSpan, style);
            pagedIterator.addCell(cell);
            columnIdx.value += columnSpan;
            temp = (CrossHeader) temp.getSibling();
        }
    }

    private static void getTargetsTitle(BISummaryTarget[] usedSumTarget,
                                        StreamPagedIterator pagedIterator, CrossHeader top, int rowIdx, FinalInt columnIdx, Style style) {
        CrossHeader temp = top;
        while (temp != null) {
            for (int i = 0; i < top.getTotalLength(); i++) {
                for (int j = 0; j < usedSumTarget.length; j++) {
                    CBCell cell = ExecutorUtils.createCell(usedSumTarget[j].getText(), rowIdx, 1, columnIdx.value++, 1, style);
                    pagedIterator.addCell(cell);
                }
            }
            temp = (CrossHeader) temp.getSibling();
        }
    }

    /**
     * @param roots        ComplexCrossExecutor复用此方法时需要的参数
     * @param widget       ComplexCrossExecutor复用此方法时需要的参数
     * @param rowDimension ComplexCrossExecutor复用此方法时需要的参数
     * @param maxDimLen    ComplexCrossExecutor复用此方法时需要的参数 列表头中维度最多区域中维度的长度
     * @param iter
     * @param start
     * @param rowIdx       ComplexCrossExecutor复用此方法时需要的参数 记录行数
     * @param order        ComplexCrossExecutor复用此方法时需要的参数 记录序号
     * @throws Exception
     */
    public static void generateCells(NewCrossRoot[] roots, TableWidget widget, BIDimension[] rowDimension, int maxDimLen,
                                     TableCellIterator iter, FinalInt start, FinalInt rowIdx, int order) throws Exception {
        //判断奇偶行需要用到标题的行数
        int titleRowSpan = rowIdx.value;
        CrossHeader[] crossNodes = new CrossHeader[roots.length];
        for (int i = 0, j = roots.length; i < j; i++) {
            CrossHeader node = roots[i].getLeft();
            while (node.getChildLength() != 0) {
                node = (CrossHeader) node.getFirstChild();
            }
            crossNodes[i] = node;
        }

        int[] oddEven = new int[rowDimension.length];
        oddEven[0] = order;
        Object[] dimensionNames = new Object[rowDimension.length];
        while (crossNodes[0] != null) {
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = rowDimension.length + widget.isOrder();

            int newRow = rowIdx.value & ExportConstants.MAX_ROWS_2007 - 1;
            if (newRow == 0) {
                iter.getIteratorByPage(start.value).finish();
                start.value++;
            }
            StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);

            for (int i = 0, j = crossNodes.length; i < j; i++) {
                CrossNode temp = crossNodes[i].getValue();
                //第一次出现表头时创建cell
                CrossHeader parent = crossNodes[i];
                if (i == 0) {
                    generateDimensionName(parent, rowDimension, pagedIterator, dimensionNames, oddEven, rowIdx, columnIdx,
                            widget.isOrder(), maxDimLen);
                }
                generateTopChildren(widget, temp, pagedIterator, rowIdx.value, columnIdx, titleRowSpan);
                crossNodes[i] = (CrossHeader) crossNodes[i].getSibling();
            }
            rowIdx.value++;
        }
    }

    private static void generateDimensionName(CrossHeader parent, BIDimension[] rowDimension, StreamPagedIterator pagedIterator,
                                              Object[] dimensionNames, int[] oddEven, FinalInt rowIdx, FinalInt columnIdx,
                                              int isOrder, int maxDimLen) {
        int i = rowDimension.length;
        while (parent.getParent() != null) {
            int rowSpan = parent.getTotalLength();
            Object data = parent.getData();
            BIDimension dim = rowDimension[--i];
            Object v = dim.getValueByType(data);
            if (v != dimensionNames[i] || (i == dimensionNames.length - 1)) {
                oddEven[i]++;
                //不应该加一 为了和前台展示统一 奇偶行的颜色互换
                Style style = BITableStyle.getInstance().getDimensionCellStyle(false, (oddEven[i] + 1) % 2 == 0);
                CBCell cell = ExecutorUtils.createCell(v, rowIdx.value, rowSpan, i + isOrder, 1, style);
                pagedIterator.addCell(cell);
                //复杂表两个区域的维度的情况下 需要设置最后一个维度单元格columnSpan
                if (i == dimensionNames.length - 1) {
                    int diff = maxDimLen - rowDimension.length;
                    cell.setColumnSpan(diff + 1);
                    //后面指标的位置需要向右偏移
                    columnIdx.value += diff;
                }
                //创建序号并和顶级维度共享rowSpan
                if (i == 0 && isOrder == 1) {
                    CBCell orderCell = ExecutorUtils.createCell(oddEven[0], rowIdx.value, rowSpan, 0, 1, style);
                    pagedIterator.addCell(orderCell);
                }
                dimensionNames[i] = v;
            }
            parent = (CrossHeader) parent.getParent();
        }
    }

    private static void generateTopChildren(TableWidget widget, CrossNode temp, StreamPagedIterator pagedIterator,
                                            int rowIdx, FinalInt columnIdx, int titleRowSpan) {
        if (temp.getTopFirstChild() != null) {
            int topChildrenLen = temp.getTopChildLength();
            for (int i = 0; i < topChildrenLen; i++) {
                generateTopChildren(widget, temp.getTopChild(i), pagedIterator, rowIdx, columnIdx, titleRowSpan);
            }
        } else {
            for (TargetGettingKey key : widget.getTargetsKey()) {
                Object v = temp.getSummaryValue(key);
                boolean isPercent = widget.getChartSetting().getNumberLevelByTargetId(key.getTargetName()) == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT;
                Style style = BITableStyle.getInstance().getNumberCellStyle(v, (rowIdx - titleRowSpan + 1) % 2 == 1, isPercent);
                CBCell cell = ExecutorUtils.createCell(v, rowIdx, 1, columnIdx.value, 1, style);
                pagedIterator.addCell(cell);
                columnIdx.value++;
            }
        }
    }

    @Override
    public NewCrossRoot getCubeNode() throws Exception {
        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int len = usedSumTarget.length;
        Map<String, TargetGettingKey> targetsMap = new HashMap<String, TargetGettingKey>();
        TargetGettingKey[] keys = new TargetGettingKey[len];
        for (int i = 0; i < len; i++) {
            keys[i] = usedSumTarget[i].createTargetGettingKey();
            targetsMap.put(usedSumTarget[i].getValue(), keys[i]);
        }
        int calpage = paging.getOperator();

        NewCrossRoot node = CubeIndexLoader.getInstance(session.getUserId()).loadPageCrossGroup(createTarget4Calculate(), rowDimension, colDimension, allSumTarget, calpage, widget.useRealData(), session, expander, widget);

        if (widget.useTargetSort()) {
            node = node.createSortedNode(widget.getTargetSort(), targetsMap);
        }
        clearNullSummary(node.getLeft(), keys);
        clearNullSummary(node.getTop(), keys);
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return node;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        return getCubeNode().toJSONObject(rowDimension, colDimension, widget.getTargetsKey());
    }

    @Override
    public List<MetricGroupInfo> getLinkedWidgetFilterGVIList() throws Exception {
        if (getSession() == null) {
            return null;
        }
        int calPage = paging.getOperator();
        List<NodeAndPageInfo> infoList = CubeIndexLoader.getInstance(session.getUserId()).getPageCrossGroupInfoList(createTarget4Calculate(), rowDimension, colDimension, allSumTarget, calPage, widget.useRealData(), session, expander, widget);
        ArrayList<MetricGroupInfo> gviList = new ArrayList<MetricGroupInfo>();
        for (NodeAndPageInfo info : infoList) {
            gviList.addAll(info.getIterator().getRoot().getMetricGroupInfoList());
        }
        return gviList;

    }

    private void clearNullSummary(CrossHeader left, TargetGettingKey[] keys) {
        for (TargetGettingKey key : keys) {
            if (left.getSummaryValue(key) == null) {
                left.getValue().setSummaryValue(key, null);
            }
        }
        for (int i = 0; i < left.getChildLength(); i++) {
            clearNullSummary((CrossHeader) left.getChild(i), keys);
        }
    }

}