package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexGroupExecutor extends AbstractComplexNodeExecutor {


    public ComplexGroupExecutor(TableWidget widget, Paging page,
                                ArrayList<ArrayList<String>> rowArray,
                                BISession session, ComplexExpander expander) {

        super(widget, page, session, expander);
        rowData = new BIComplexExecutData(rowArray, null);
    }

    /**
     * 获取nodes的个数
     *
     * @param nodes
     * @return
     */
    @Override
    public int getNodesTotalLength(Node[] nodes) {
        int count = 0;

        for (int i = 0; i < nodes.length; i++) {
            count += nodes[i].getTotalLengthWithSummary();
        }

        return count;
    }

    /**
     * 获取node的个数
     */
    @Override
    public int getNodesTotalLength(Node[] nodes, ComplexExpander complexExpander, Integer[] integers) {

        int count = 0;

        for (int i = 0; i < nodes.length; i++) {
            count += nodes[i].getTotalLengthWithSummary(complexExpander.getYExpander(integers[i]));
        }

        return count;
    }

    /**
     * 获取某个nodes
     *
     * @return
     */
    @Override
    public Map<Integer, Node> getCubeNodes() {

        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
        }
        Map<Integer, Node> nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(false, widget, createTarget4Calculate(), rowData, allDimensions,
                allSumTarget, keys, paging.getOprator(), widget.useRealData(), session, complexExpander, true);
        if (nodeMap.isEmpty()) {
            return null;
        }

        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
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


    /**
     * 构建cells
     *
     * @return 构建的cells
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        Map<Integer, Node> nodeMap = getCubeNodes();
        if (nodeMap == null || nodeMap.isEmpty()) {
            return new CBCell[][]{new CBCell[0]};
        }
        int collen = rowData.getMaxArrayLength();
        int columnLen = collen + usedSumTarget.length;

        boolean needAll = paging.getOprator() < Node.NONE_PAGE_LEVER;
        Iterator<Map.Entry<Integer, Node>> iterator = nodeMap.entrySet().iterator();
        Node[] trees = new Node[nodeMap.size()];
        Integer[] integers = new Integer[nodeMap.size()];
        int i = 0;
        while (iterator.hasNext()) {
            //导出就全部展开吧
            Map.Entry<Integer, Node> entry = iterator.next();
            trees[i] = entry.getValue();
            integers[i] = entry.getKey();
            i++;
        }
        //导出就全部展开吧
        int rowLen = needAll ? getNodesTotalLength(trees) : getNodesTotalLength(trees, complexExpander, integers);

        CBCell[][] cbcells = new CBCell[columnLen][rowLen + 1];
        generateTitle(cbcells, integers[0]);
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int s = 0; s < summaryLength; s++) {
            keys[s] = new TargetGettingKey(usedSumTarget[s].createSummaryCalculator().createTargetKey(), usedSumTarget[s].getValue());
        }

        int startRow = 1;

        for (i = 0; i < trees.length; i++) {
            Node node = trees[i];
            BIDimension[] rowDimension = rowData.getDimensionArray(integers[i]);
            NodeExpander nodeExpander = complexExpander.getYExpander(i);

            if (paging.getOprator() < Node.NONE_PAGE_LEVER) {
                GroupExecutor.dealWithNode(node, cbcells, startRow, 0, rowDimension, usedSumTarget, keys, rowDimension.length - 1, 0, rowData);
            } else {
                GroupExecutor.dealWithNode(node, nodeExpander, cbcells, startRow, 0, paging.getCurrentPage(), rowDimension, usedSumTarget, keys, new ArrayList<String>(), rowDimension.length - 1, 0, rowData);
            }
            startRow += needAll ? node.getTotalLengthWithSummary() : node.getTotalLengthWithSummary(nodeExpander);
        }
        geneEmptyCells(cbcells);
        return cbcells;
    }

    /**
     * 创建cells的title, 默认展示第一个cells的title
     *
     * @param cbcells
     */
    private void generateTitle(CBCell[][] cbcells, int firstNodeRegion) {
        BIDimension[] rowDimension = rowData.getDimensionArray(firstNodeRegion);
        boolean useTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(rowDimension);
        int rowLength = rowDimension.length;

        createDimTItle(cbcells, rowDimension, useTargetSort, rowLength);

        //书写title里面指标的cell
        for (int i = 0; i < usedSumTarget.length; i++) {

            CBCell cell = new CBCell(usedSumTarget[i].getValue());
            cell.setColumn(cbcells.length - usedSumTarget.length + i);
            cell.setRow(0);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITarget rowCol = usedSumTarget[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.TARGETTITLE_Y);
            cbox.setSortTargetName(rowCol.getValue());
            cbox.setSortTargetValue("[]");
            if (widget.getTargetSort() != null && ComparatorUtils.equals(widget.getTargetSort().getName(), usedSumTarget[i].getValue())) {
                cbox.setSortType((Integer) widget.getTargetSort().getObject());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    private void createDimTItle(CBCell[][] cbcells, BIDimension[] rowDimension, boolean useTargetSort, int rowLength) {
        for (int i = 0; i < rowDimension.length; i++) {
            CBCell cell = new CBCell(rowDimension[i].getValue());
            cell.setColumn(i);
            cell.setRow(0);
            cell.setRowSpan(1);
            cell.setColumnSpan(rowData.getColumnRowSpan(i, rowLength));
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BIDimension rowCol = rowDimension[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_Y);
            if (!useTargetSort) {
                cbox.setSortType(rowDimension[i].getSortType());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    /**
     * 获取node
     *
     * @return 获取的node
     */
    @Override
    public Node getCubeNode() {
        return null;
    }
}