package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.*;

public class CrossExecutor extends BITableExecutor<NewCrossRoot> {

    private BIDimension[] rowDimension;
    private BIDimension[] colDimension;


    public CrossExecutor(TableWidget widget, BIDimension[] usedRows,
                         BIDimension[] usedColumn,
                         Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
        this.rowDimension = usedRows;
        this.colDimension = usedColumn;
    }

    /**
     * 注释
     *
     * @return 注释
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        NewCrossRoot node = getCubeNode();
        if (node == null) {
            return new CBCell[0][0];
        }
        //int page = widget.getVerticalCalculateAllPage();
        int len = usedSumTarget.length;
        Map<String, TargetGettingKey> targetsMap = new HashMap<String, TargetGettingKey>();
        TargetGettingKey[] keys = new TargetGettingKey[len];
        for (int i = 0; i < len; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
            targetsMap.put(usedSumTarget[i].getValue(), keys[i]);
        }

        CBCell[][] cbcells = null;
        int rowPlus = 0;
        if (ExecutorCommonUtils.isAllPage(paging.getOprator())) {
            cbcells = new CBCell[(keys.length == 0 ? node.getTop().getTotalLength() : node.getTop().getTotalLengthWithSummary())
                    * Math.max(1, keys.length) + rowDimension.length + widget.isOrder()]
                    [(keys.length == 0 ? node.getLeft().getTotalLength() : node.getLeft().getTotalLengthWithSummary()) + colDimension.length + 1];
        } else {
            cbcells = new CBCell[(keys.length == 0 ? node.getTop().getTotalLength(expander.getXExpander()) : node.getTop().getTotalLengthWithSummary(expander.getXExpander()))
                    * Math.max(1, keys.length) + rowDimension.length + widget.isOrder()]
                    [(keys.length == 0 ? node.getLeft().getTotalLength(expander.getYExpander()) : node.getLeft().getTotalLengthWithSummary(expander.getYExpander())) + colDimension.length + 1 + rowPlus];
        }
        boolean isColTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(colDimension);
        boolean isRowTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(rowDimension);
        generateTitleCell(cbcells, isColTargetSort, isRowTargetSort);
        if (ExecutorCommonUtils.isAllPage(paging.getOprator())) {
            GroupExecutor.dealWithNode(node.getLeft(), NodeExpander.ALL_EXPANDER, cbcells, colDimension.length + 1, 0, paging.getOprator(), rowDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), rowDimension.length - 1, widget.isOrder(), new BIComplexExecutData(rowDimension));
            HorGroupExecutor.dealWithNode(node.getTop(), NodeExpander.ALL_EXPANDER, cbcells, 0, rowDimension.length + widget.isOrder(), colDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), colDimension.length - 1, true, new IntList(), isRowTargetSort, rowDimension[0], widget, new BIComplexExecutData(colDimension));
            if (hasLeftChild(node)) {
                dealWithNode(node.getLeft(), new NodeAllExpander(rowDimension.length - 1), cbcells, colDimension.length + 1, rowDimension.length, keys, rowDimension.length - 1);
            }
        } else {
            GroupExecutor.dealWithNode(node.getLeft(), expander.getYExpander(), cbcells, colDimension.length + 1, 0, paging.getCurrentPage(), rowDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), rowDimension.length - 1, widget.isOrder(), new BIComplexExecutData(rowDimension));
            HorGroupExecutor.dealWithNode(node.getTop(), expander.getXExpander(), cbcells, 0, rowDimension.length + widget.isOrder(), colDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), colDimension.length - 1, true, new IntList(), isRowTargetSort, rowDimension[0], widget, new BIComplexExecutData(colDimension));
            if (hasLeftChild(node)) {
                dealWithNode(node.getLeft(), expander.getYExpander(), cbcells, colDimension.length + 1, rowDimension.length, keys, rowDimension.length - 1);
            }
        }
        if (widget.isOrder() == 1) {
            createNumberCellTitle(cbcells, colDimension.length);
            if (ExecutorCommonUtils.isAllPage(paging.getOprator())) {
                createAllNumberCellElement(cbcells, 1);
            } else {
                createAllNumberCellElement(cbcells, paging.getCurrentPage());
            }
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
                             int row, int column, TargetGettingKey[] keys, int total) {
        int pos = 0;
        boolean discardSummary = false;
        //Connery:可能被展开，但是子维度被去除 bug：64452
        //所有加上对子节点长度判断
        if (yExpander != null && left.getChildLength() > 0) {
            for (int i = 0; i < left.getChildLength(); i++) {
                pos += dealWithNode((CrossHeader) left.getChild(i), yExpander.getChildExpander(left.getChild(i).getShowValue()), cbcells, row + pos, column, keys, total - 1);
            }
            discardSummary = (!left.needSummary()) || keys.length == 0;
        }
        if (discardSummary) {
            return pos;
        }
        //pos如果不为0说明是汇总的格子
        dealWithCrossNode(left.getValue(), paging.getOprator() < Node.NONE_PAGE_LEVER ? new NodeAllExpander(colDimension.length - 1) : expander.getXExpander(), cbcells, row + pos, column, keys, colDimension.length, pos != 0, total);
        pos++;
        return pos;
    }

    private int dealWithCrossNode(CrossNode node, NodeExpander xExpander, CBCell[][] cbcells, int row, int column, TargetGettingKey[] keys, int xTotal, boolean isYSummary, int yTotal
    ) {
        int pos = 0;
        boolean discardSummary = false;
        if (xExpander != null && node.getTopChildLength() > 0) {
            for (int i = 0; i < node.getTopChildLength(); i++) {
                pos += dealWithCrossNode(node.getTopChild(i), xExpander.getChildExpander(node.getTopChild(i).getHead().getShowValue()), cbcells, row, column + pos * Math.max(keys.length, 1), keys, xTotal - 1, isYSummary, yTotal);
            }
            discardSummary = (!node.getHead().needSummary()) || keys.length == 0;
        }
        if (discardSummary) {
            return pos;
        }
        //pos如果不为0说明是汇总的格子
        if (keys.length == 0) {
            Object v = null;
            CBCell cell = new CBCell(NONEVALUE);
            cell.setColumn(column + widget.isOrder());
            cell.setRow(row);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(

                    pos == 0 ? (isYSummary ?
                            BITableStyle.getInstance().getYTotalCellStyle(v, yTotal)
                            :
                            BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1)
                    ) : BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1));
            List cellList = new ArrayList();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
            cbox.setDimensionJSON(createDimensionValue(node));
            cbox.setName(StringUtils.EMPTY);
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        } else {
            for (int k = 0; k < keys.length; k++) {
                Object v = node.getSummaryValue(keys[k]);
                CBCell cell = new CBCell(v == null ? NONEVALUE : v);
                cell.setColumn(column + (pos * keys.length) + k + widget.isOrder());
                cell.setRow(row);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(
                        pos == 0 ? (isYSummary ?
                                BITableStyle.getInstance().getYTotalCellStyle(v, yTotal)
                                :
                                BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1)
                        ) : BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1));
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
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
        pos++;
        return pos;
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
            CBCell cell = new CBCell();
            cell.setColumn(0);
            cell.setRow(i);
            cell.setColumnSpan(Math.max(1, this.rowDimension.length + widget.isOrder()));
            cell.setRowSpan(1);
            cell.setValue(colDimension[i].getValue());
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(i));
            List cellList = new ArrayList();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(colDimension[i].getValue());
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
            cell.setValue(rowDimension[i].getValue());
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(1));
            List cellList = new ArrayList();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(rowDimension[i].getValue());
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
    public NewCrossRoot getCubeNode() {
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
        int calpage = paging.getOprator();

        NewCrossRoot node = CubeIndexLoader.getInstance(session.getUserId()).loadPageCrossGroup(createTarget4Calculate(), rowDimension, colDimension, allSumTarget, calpage, widget.useRealData(), session, expander, widget);

        clearNullSummary(node.getLeft(), keys);
        clearNullSummary(node.getTop(), keys);


        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return node;
    }

    @Override
    public JSONObject createJSONObject() throws JSONException {
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