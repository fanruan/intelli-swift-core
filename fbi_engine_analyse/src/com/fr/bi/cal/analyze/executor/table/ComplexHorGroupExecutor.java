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
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexHorGroupExecutor extends AbstractComplexNodeExecutor {

    public ComplexHorGroupExecutor(TableWidget widget, Paging page,
                                   ArrayList<ArrayList<String>> columnArray,
                                   BISession session, ComplexExpander expander) {

        super(widget, page, session, expander);
        rowData = new BIComplexExecutData(columnArray, null);
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
            count += nodes[i].getTotalLengthWithSummary(complexExpander.getXExpander(i));
        }

        return count;
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return super.getSouthEastRectangle();
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

    /**
     * 构建cells
     *
     * @return 构建的cells
     * @throws NoneAccessablePrivilegeException
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        Map<Integer, Node> nodeMap = getCubeNodes();
        if (nodeMap == null || nodeMap.isEmpty()) {
            return new CBCell[][]{new CBCell[0]};
        }
        Iterator<Map.Entry<Integer, Node>> iterator = nodeMap.entrySet().iterator();
        Node[] trees = new Node[nodeMap.size()];
        Integer[] integers = new Integer[nodeMap.size()];
        int count = 0;
        while (iterator.hasNext()) {
            //导出就全部展开吧
            Map.Entry<Integer, Node> entry = iterator.next();
            trees[count] = entry.getValue();
            integers[count] = entry.getKey();
            count++;
        }
        int rowLen = usedSumTarget.length + rowData.getMaxArrayLength();
        //导出就全部展开吧
        int columnLen = paging.getOprator() < Node.NONE_PAGE_LEVER ? getNodesTotalLength(trees) : getNodesTotalLength(trees, complexExpander, integers);

        CBCell[][] cbcells = new CBCell[columnLen + 1][rowLen];

        generateTitle(cbcells, integers[0]);
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
        }

        int startColumn = 1;
        for (int i = 0; i < trees.length; i++) {
            Node tree = trees[i];

            BIDimension[] colDimension = rowData.getDimensionArray(integers[i]);
            NodeExpander nodeExpander = complexExpander.getXExpander(integers[i]);
            if (paging.getOprator() < Node.NONE_PAGE_LEVER) {
                HorGroupExecutor.dealWithNode(tree, cbcells, 0, startColumn, colDimension, usedSumTarget, keys, colDimension.length - 1, rowData);
            } else {
                HorGroupExecutor.dealWithNode(tree, nodeExpander, cbcells, 0, startColumn, colDimension, usedSumTarget, keys, new ArrayList<String>(), colDimension.length - 1, false, new IntList(), false, null, widget, rowData);
            }
            startColumn += paging.getOprator() < Node.NONE_PAGE_LEVER ? tree.getTotalLengthWithSummary() : tree.getTotalLengthWithSummary(nodeExpander);
        }
        geneEmptyCells(cbcells);
        return cbcells;
    }

    /**
     * 创建左边的title 默认展示第一个的
     *
     * @param cbcells 整个cells
     */
    private void generateTitle(CBCell[][] cbcells, int firstRegionIndex) {

        int columnDimensionLength = rowData.getMaxArrayLength();
        BIDimension[] colDimension = rowData.getDimensionArray(firstRegionIndex);
        int colLength = colDimension.length;

        boolean isTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(colDimension);
        int row = 0;
        for (int i = 0; i < colLength; i++) {
            CBCell cell = new CBCell(colDimension[i].getValue());
            cell.setColumn(0);
            cell.setRow(row);
            int rowSpan = rowData.getColumnRowSpan(row, colLength);
            cell.setRowSpan(rowSpan);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, i % 2 == 1));
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITargetAndDimension rowCol = colDimension[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_X);
            if (!isTargetSort) {
                cbox.setSortType(colDimension[i].getSortType());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            row += rowSpan;
        }
        for (int i = 0; i < usedSumTarget.length; i++) {
            CBCell cell = new CBCell(usedSumTarget[i].getValue());
            cell.setColumn(0);
            cell.setRow(columnDimensionLength + i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, cell.getRow() % 2 == 1));
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITargetAndDimension rowCol = usedSumTarget[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.TARGETTITLE_X);
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


    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    @Override
    public Map<Integer, Node> getCubeNodes() {
        if (getSession() == null) {
            return null;
        }
        if (rowData.getDimensionArrayLength() == 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        Map<String, TargetGettingKey> targetsMap = new HashMap<String, TargetGettingKey>();
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int s = 0; s < summaryLength; s++) {
            keys[s] = new TargetGettingKey(usedSumTarget[s].createSummaryCalculator().createTargetKey(), usedSumTarget[s].getValue());
        }
        Map<Integer, Node> nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(true, widget, createTarget4Calculate(), rowData, allDimensions,
                allSumTarget, keys, paging.getOprator(), widget.useRealData(), session, complexExpander, false);


        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
    }
}