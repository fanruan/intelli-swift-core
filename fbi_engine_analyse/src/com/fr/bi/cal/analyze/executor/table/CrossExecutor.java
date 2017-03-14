package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.executor.detail.DetailCellIterator;
import com.fr.bi.cal.analyze.executor.detail.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.dimension.dimension.BIAbstractDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;
import com.fr.stable.StringUtils;

import java.util.*;

public class CrossExecutor extends BITableExecutor<NewCrossRoot> {

    private BIDimension[] rowDimension;
    private BIDimension[] colDimension;
    private int columnIdx = 0;
    private final static int EXCEL_ROW_MODE_VALUE = ExportConstants.MAX_ROWS_2007 - 1;

    public CrossExecutor(TableWidget widget, BIDimension[] usedRows,
                         BIDimension[] usedColumn,
                         Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
        this.rowDimension = usedRows;
        this.colDimension = usedColumn;
    }

    @Override
    public DetailCellIterator createCellIterator4Excel() throws Exception {
        final NewCrossRoot node = getCubeNode();
        if (node == null) {
            return new DetailCellIterator(0, 0);
        }

        int len = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[len];
        boolean isWholeCol = keys.length == 0 || !widget.getChartSetting().showColTotal();
        boolean isWholeRow = keys.length == 0 || !widget.getChartSetting().showRowTotal();
        int columnLen = (isWholeCol ? node.getTop().getTotalLength() :
                node.getTop().getTotalLengthWithSummary()) * Math.max(1, keys.length) + rowDimension.length + widget.isOrder();
        int rowLen = (isWholeRow ? node.getLeft().getTotalLength() :
                node.getLeft().getTotalLengthWithSummary()) + colDimension.length + 1;

        final DetailCellIterator iter = new DetailCellIterator(columnLen, rowLen);
        new Thread() {
            public void run() {
                try {
                    FinalInt start = new FinalInt();
                    int rowIdx = 0;
                    rowIdx++;
                    createCells(iter, start, rowIdx);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    private void createCells(DetailCellIterator iter, FinalInt start, int rowIdx) throws Exception {
        CrossHeader node = getCubeNode().getLeft();
        while (node.getChildLength() != 0) {
            node = (CrossHeader) node.getFirstChild();
        }

        BIDimension[] rowDimensions = widget.getViewDimensions();
        Object[] dimensionNames = new Object[rowDimensions.length];
        while (node != null) {
            columnIdx = 0;
            CrossNode temp = node.getValue();
            int newRow = rowIdx & EXCEL_ROW_MODE_VALUE;
            if (newRow == 0) {
                iter.getIteratorByPage(start.value).finish();
                start.value++;
            }
            StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
            getTopChildren((CrossNode4Calculate) temp, pagedIterator, rowIdx);
            //第一次出现表头时创建cell
            int i = rowDimensions.length;
            while (node.getParent() != null) {
                int rowSpan = node.getTotalLength();
                Object data = node.getData();
                BIDimension dim = rowDimensions[--i];
                Object v = dim.getValueByType(data);
                if (v != dimensionNames[i] || (i == dimensionNames.length - 1)) {
                    CBCell cell = createCell4Dimension(v, rowIdx, rowSpan, i);
                    pagedIterator.addCell(cell);
                    dimensionNames[i] = v;
                }
                node = (CrossHeader) node.getParent();
            }
            rowIdx++;
            node = (CrossHeader) node.getSibling();
        }
    }

    private void getTopChildren(CrossNode4Calculate temp, StreamPagedIterator pagedIterator, int rowIdx) {
        if (temp.getTopFirstChild() != null) {
            int topChildrenLen = temp.getTopChildLength();
            for (int i = 0; i < topChildrenLen; i++) {
                getTopChildren((CrossNode4Calculate) temp.getTopChild(i), pagedIterator, rowIdx);
            }
        } else {
            for (TargetGettingKey key : widget.getTargetsKey()) {
                pagedIterator.addCell(createCell4Target(temp.getSummaryValue(key), columnIdx, rowIdx));
                columnIdx++;
            }
        }
    }

    private CBCell createCell4Target(Object v, int columnIdx, int rowIdx) {
        CBCell cell = new CBCell(v);
        cell.setRow(rowIdx);
        cell.setColumn(columnIdx);
        cell.setRowSpan(1);
        cell.setColumnSpan(1);
        return cell;
    }

    private CBCell createCell4Dimension(Object v, int rowIdx, int rowSpan, int columnIdx) {
        CBCell cell = new CBCell(v);
        cell.setRow(rowIdx);
        cell.setColumn(columnIdx);
        cell.setRowSpan(rowSpan);
        cell.setColumnSpan(1);
        return cell;
    }

    /**
     * 注释
     *
     * @return 注释
     */
    @Override
    public CBCell[][] createCellElement() throws Exception {
        NewCrossRoot node = getCubeNode();
        if (node == null) {
            return new CBCell[0][0];
        }
        //int page = widget.getVerticalCalculateAllPage();
        int len = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[len];

        CBCell[][] cbcells = new CBCell[((keys.length == 0 || !widget.getChartSetting().showColTotal()) ? node.getTop().getTotalLength() : node.getTop().getTotalLengthWithSummary())
                * Math.max(1, keys.length) + rowDimension.length + widget.isOrder()]
                [((keys.length == 0 || !widget.getChartSetting().showRowTotal()) ? node.getLeft().getTotalLength() : node.getLeft().getTotalLengthWithSummary()) + colDimension.length + 1];
        boolean isColTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(colDimension);
        boolean isRowTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(rowDimension);
        GroupExecutor.dealWithNode(node.getLeft(), NodeExpander.ALL_EXPANDER, cbcells, colDimension.length + 1, 0,
                paging.getOperator(), rowDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(),
                rowDimension.length - 1, widget.isOrder(), new BIComplexExecutData(rowDimension), widget.getChartSetting());
        HorGroupExecutor.dealWithNode(node.getTop(), NodeExpander.ALL_EXPANDER, cbcells, 0,
                rowDimension.length + widget.isOrder(), colDimension, usedSumTarget, new TargetGettingKey[]{},
                new ArrayList<String>(), colDimension.length - 1, true, new IntList(), isRowTargetSort,
                rowDimension[0], widget, new BIComplexExecutData(colDimension), widget.getChartSetting());
        if (hasLeftChild(node)) {
            dealWithNode(node.getLeft(), new NodeAllExpander(rowDimension.length - 1), cbcells, colDimension.length + 1, rowDimension.length, keys, rowDimension.length - 1, widget.getChartSetting());
        }
        return cbcells;
    }

    private boolean hasLeftChild(NewCrossRoot node) {
        return node.getLeft().getValue().getLeftChildLength() != 0;
    }


    private BISummaryTarget[] createTarget4Calculate() {
        ArrayList<BITarget> list = new ArrayList<BITarget>();
        for (int i = 0; i < usedSumTarget.length; i++) {
            list.add(usedSumTarget[i]);
        }
        if (widget.getTargetSort() != null) {
            String name = widget.getTargetSort().getName();
            boolean inUsedSumTarget = false;
            for (int i = 0; i < usedSumTarget.length; i++) {
                if (ComparatorUtils.equals(usedSumTarget[i].getValue(), name)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (int i = 0; i < allSumTarget.length; i++) {
                    if (ComparatorUtils.equals(allSumTarget[i].getValue(), name)) {
                        list.add(allSumTarget[i]);
                    }
                }
            }
        }
        Iterator<String> it1 = widget.getTargetFilterMap().keySet().iterator();
        while (it1.hasNext()) {
            String key = it1.next();
            boolean inUsedSumTarget = false;
            for (int i = 0; i < usedSumTarget.length; i++) {
                if (ComparatorUtils.equals(usedSumTarget[i].getValue(), key)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (int i = 0; i < allSumTarget.length; i++) {
                    if (ComparatorUtils.equals(allSumTarget[i].getValue(), key)) {
                        list.add(allSumTarget[i]);
                    }
                }
            }

        }
        return list.toArray(new BISummaryTarget[list.size()]);
    }

    private int dealWithNode(CrossHeader left, NodeExpander yExpander, CBCell[][] cbcells,
                             int row, int column, TargetGettingKey[] keys, int total, DetailChartSetting chartSetting) {
        int pos = 0;
        boolean discardSummary = false;
        //Connery:可能被展开，但是子维度被去除 bug：64452
        //所有加上对子节点长度判断
        if (yExpander != null && left.getChildLength() > 0) {
            for (int i = 0; i < left.getChildLength(); i++) {
                pos += dealWithNode((CrossHeader) left.getChild(i), yExpander.getChildExpander(left.getChild(i).getShowValue()), cbcells, row + pos, column, keys, total - 1, chartSetting);
            }
            discardSummary = !left.needSummary() || keys.length == 0;
        }
        if (isNotChild(left, chartSetting, discardSummary)) {
            return pos;
        }
        //pos如果不为0说明是汇总的格子
        dealWithCrossNode(left.getValue(), paging.getOperator() < Node.NONE_PAGE_LEVER ? new NodeAllExpander(colDimension.length - 1) : expander.getXExpander(), cbcells, row + pos, column, keys, colDimension.length, pos != 0, total, chartSetting);
        pos++;
        return pos;
    }

    private boolean isNotChild(CrossHeader left, DetailChartSetting chartSetting, boolean discardSummary) {
        return (left.getChildLength() > 0 && !chartSetting.showRowTotal()) || discardSummary;
    }

    private int dealWithCrossNode(CrossNode node, NodeExpander xExpander, CBCell[][] cbcells, int row, int column, TargetGettingKey[] keys, int xTotal, boolean isYSummary, int yTotal, DetailChartSetting chartSetting) {
        int pos = 0;
        boolean discardSummary = false;
        if (xExpander != null && node.getTopChildLength() > 0) {
            for (int i = 0; i < node.getTopChildLength(); i++) {
                pos += dealWithCrossNode(node.getTopChild(i), xExpander.getChildExpander(node.getTopChild(i).getHead().getShowValue()), cbcells, row, column + pos * Math.max(keys.length, 1), keys, xTotal - 1, isYSummary, yTotal, chartSetting);
            }
            discardSummary = !node.getHead().needSummary() || keys.length == 0;
        }
        if (discardSummary) {
            return pos;
        }
        //pos如果不为0说明是汇总的格子
        if (keys.length == 0) {
            dealwithSum(node, cbcells, row, column, isYSummary, yTotal, chartSetting);
        } else {
            for (int k = 0; k < keys.length; k++) {
                int numLevel = chartSetting.getNumberLevelByTargetId(keys[k].getTargetName());
                Object v = node.getSummaryValue(keys[k]);
                v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
                CBCell cell = new CBCell(v);
                cell.setColumn(column + (pos * keys.length) + k + widget.isOrder());
                cell.setRow(row);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle((chartSetting.showRowTotal() && isYSummary) ? BITableStyle.getInstance().getYTotalCellStyle(v, yTotal, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT)) : BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT)));
                List cellList = new ArrayList();
                cellList.add(cell);
                CBBoxElement cbox = new CBBoxElement(cellList);
                TargetStyle style = usedSumTarget[k].getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
                cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
                cbox.setDimensionJSON(createDimensionValue(node));
                cbox.setName(usedSumTarget[k].getValue());
                cell.setBoxElement(cbox);
                if (cell.getColumn() < cbcells.length && cell.getRow() < cbcells[cell.getColumn()].length) {
                    cbcells[cell.getColumn()][cell.getRow()] = cell;
                }
            }
        }
        pos++;
        return pos;
    }

    private void dealwithSum(CrossNode node, CBCell[][] cbcells, int row, int column, boolean isYSummary, int yTotal, DetailChartSetting chartSetting) {
        Object v = null;
        CBCell cell = new CBCell(NONEVALUE);
        cell.setColumn(column + widget.isOrder());
        cell.setRow(row);
        cell.setRowSpan(1);
        cell.setColumnSpan(1);
        cell.setStyle((chartSetting.showRowTotal() && isYSummary) ? BITableStyle.getInstance().getYTotalCellStyle(v, yTotal, false) : BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, false));
        List cellList = new ArrayList();
        cellList.add(cell);
        CBBoxElement cbox = new CBBoxElement(cellList);
        cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
        cbox.setDimensionJSON(createDimensionValue(node));
        cbox.setName(StringUtils.EMPTY);
        cell.setBoxElement(cbox);
        cbcells[cell.getColumn()][cell.getRow()] = cell;
    }

    //TODO代码质量
    private String createDimensionValue(CrossNode node) {
        JSONArray ja = new JSONArray();

        Node header = node.getHead();
        Node left = node.getLeft();
        int deep = 0;
        Node temp = header;
        while (temp.getParent() != null) {
            deep++;
            temp = temp.getParent();
        }
        deep--;
        temp = header;
        while (deep != -1 && temp != null) {
            try {
                ja.put(new JSONObject().put(colDimension[deep].getValue(), colDimension[deep].toFilterObject(temp.getData())));
            } catch (JSONException e) {
            }
            temp = temp.getParent();
            deep--;
        }
        deep = 0;
        temp = left;
        while (temp.getParent() != null) {
            deep++;
            temp = temp.getParent();
        }
        deep--;
        temp = left;
        while (deep != -1 && temp != null) {
            try {
                ja.put(new JSONObject().put(rowDimension[deep].getValue(), rowDimension[deep].toFilterObject(temp.getData())));
            } catch (JSONException e) {
            }
            temp = temp.getParent();
            deep--;
        }


        return ja.toString();
    }

    private void generateTitleCell(CBCell[][] cells, boolean isColTargetSort, boolean isRowTargetSort) {
        for (int i = 0; i < colDimension.length; i++) {
            BIAbstractDimension dimension = ((BIAbstractDimension) colDimension[i]);
            CBCell cell = new CBCell();
            cell.setColumn(0);
            cell.setRow(i);
            cell.setColumnSpan(Math.max(1, this.rowDimension.length + widget.isOrder()));
            cell.setRowSpan(1);
            cell.setValue(colDimension[i].getText());
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(i));
            List cellList = new ArrayList();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(colDimension[i].getText());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_X);
            if (!isColTargetSort) {
                cbox.setSortType(colDimension[i].getSortType());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cells[0][i] = cell;
        }

        for (int i = 0; i < rowDimension.length; i++) {
            CBCell cell = new CBCell();
            cell.setColumn(i + widget.isOrder());
            cell.setRow(colDimension.length);
            cell.setColumnSpan(1);
            cell.setRowSpan(1);
            cell.setValue(rowDimension[i].getText());
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(1));
            List cellList = new ArrayList();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(rowDimension[i].getText());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_Y);
            if (!isRowTargetSort) {
                cbox.setSortType(rowDimension[i].getSortType());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cells[cell.getColumn()][colDimension.length] = cell;
        }
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
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
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
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

    private void clearNullSummary(CrossHeader left, TargetGettingKey[] keys) {
        for (TargetGettingKey key : keys) {
            if (left.getSummaryValue(key) == null) {
                left.getValue().setSummaryValue(key, 0);
            }
        }
        for (int i = 0; i < left.getChildLength(); i++) {
            clearNullSummary((CrossHeader) left.getChild(i), keys);
        }
    }

}