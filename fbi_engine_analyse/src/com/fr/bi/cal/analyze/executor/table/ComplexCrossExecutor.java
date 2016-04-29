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
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.*;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexCrossExecutor extends BIComplexExecutor<NewCrossRoot> {

    private BIComplexExecutData rowData;
    private BIComplexExecutData columnData;

    public ComplexCrossExecutor(TableWidget widget, Paging page,
                                ArrayList<ArrayList<String>> rowArray, ArrayList<ArrayList<String>> columnArray,
                                BISession session, ComplexExpander expander) {

        super(widget, page, session, expander);
        rowData = new BIComplexExecutData(rowArray, null);
        columnData = new BIComplexExecutData(columnArray, null);
    }


    /**
     * 获取nodes 复杂列表获取的是一个nodes
     *
     * @return 获取的nodes
     */
    @Override
    public Map<Integer, NewCrossRoot> getCubeNodes() throws InterruptedException {
        return null;
    }

    /**
     * 获取nodes的个数
     *
     * @param nodes
     * @return
     */
    @Override
    public int getNodesTotalLength(Node[] nodes) {
        return 0;
    }

    /**
     * 获取node的个数
     *
     * @param nodes
     * @param complexExpander
     * @param ints
     */
    @Override
    public int getNodesTotalLength(Node[] nodes, ComplexExpander complexExpander, Integer[] ints) {
        return 0;
    }


    /**
     * 获取node
     *
     * @return 获取的node
     */
    @Override
    public NewCrossRoot getCubeNode() {
        return null;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        Iterator<Map.Entry<Integer, NewCrossRoot[]>> it = getCubeCrossNodes().entrySet().iterator();
        JSONObject jo = new JSONObject();
        while (it.hasNext()){
            Map.Entry<Integer, NewCrossRoot[]> entry = it.next();
            JSONArray ja = new JSONArray();
            for (int i = 0; i < entry.getValue().length; i ++){
                ja.put(entry.getValue()[i].toJSONObject(rowData.getDimensionArray(entry.getKey()), columnData.getDimensionArray(i), widget.getTargetsKey()));
            }
            jo.put(String.valueOf(entry.getKey()), ja);
        }
        return jo;
    }

    /**
     * 注释
     *
     * @return 注释
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        Map<Integer, NewCrossRoot[]> nodesMap = getCubeCrossNodes();
        if (nodesMap.isEmpty() || nodesMap == null) {
            return new CBCell[0][0];
        }
        int len = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[len];
        for (int i = 0; i < len; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
        }
        boolean hasTarget = keys.length != 0;
        CBCell[][] cbcells = null;
        int rowPlus = 0;
        ArrayList<NewCrossRoot> nodes = new ArrayList<NewCrossRoot>();
        Iterator<Map.Entry<Integer, NewCrossRoot[]>> iterator = nodesMap.entrySet().iterator();
        ArrayList<Integer> integers = new ArrayList<Integer>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, NewCrossRoot[]> entry = iterator.next();
            integers.add(entry.getKey());
            NewCrossRoot[] roots = entry.getValue();
            for (int i = 0; i < roots.length; i++) {
                nodes.add(roots[i]);
            }
        }
        boolean needAllPage = paging.getOprator() < Node.NONE_PAGE_LEVER;
        if (needAllPage) {
            cbcells = new CBCell[getTotalNodeColumnLength(nodes, hasTarget) * (Math.max(1, keys.length)) + rowData.getMaxArrayLength()][
                    getTotalNodeRowLength(nodes, hasTarget, integers) + columnData.getMaxArrayLength() + 1];
        } else {

            cbcells = new CBCell[getTotalNodeColumnLength(nodes, hasTarget, complexExpander) * Math.max(1, keys.length) + rowData.getMaxArrayLength()][
                    getTotalNodeRowLength(nodes, hasTarget, complexExpander, integers) + columnData.getMaxArrayLength() + 1 + rowPlus];
        }

        generateTitle(cbcells, integers.get(0));
        dealWithCorssTrees(hasTarget, integers, nodes, cbcells, needAllPage, keys);
        geneEmptyCells(cbcells);
        return cbcells;
    }

    //生成表头的表头
    private void generateTitle(CBCell[][] cells, int firstNode) {
        BIDimension[] colDimension = columnData.getDimensionArray(0);
        BIDimension[] rowDimension = rowData.getDimensionArray(firstNode);
        boolean isColTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(colDimension);
        boolean isRowTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(rowDimension);
        geneColTitle(cells, colDimension, isColTargetSort);
        for (int i = 0; i < rowDimension.length; i++) {
            CBCell cell = new CBCell();
            cell.setColumn(i);
            cell.setRow(columnData.getMaxArrayLength());
            cell.setColumnSpan(rowData.getColumnRowSpan(i, rowDimension.length));
            cell.setRowSpan(1);
            cell.setValue(rowDimension[i].getValue());
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, colDimension.length % 2 == 1));
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
            cells[i][columnData.getMaxArrayLength()] = cell;
        }
    }

    private void geneColTitle(CBCell[][] cells, BIDimension[] colDimension, boolean isColTargetSort) {
        int row = 0;
        for (int i = 0; i < colDimension.length; i++) {
            CBCell cell = new CBCell();
            cell.setColumn(0);
            cell.setRow(row);
            cell.setColumnSpan(Math.max(1, rowData.getMaxArrayLength()));
            int rowSpan = columnData.getColumnRowSpan(i, colDimension.length);
            cell.setRowSpan(columnData.getColumnRowSpan(i, colDimension.length));
            cell.setValue(colDimension[i].getValue());
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, i % 2 == 1));
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
            cells[0][row] = cell;
            row += rowSpan;
        }
    }

    private void dealWithCorssTrees(boolean hasTarget, ArrayList<Integer> integers, ArrayList<NewCrossRoot> nodes, CBCell[][] cbcells, boolean needAllPage, TargetGettingKey[] keys) {
        int maxRowDimensionLen = rowData.getMaxArrayLength();
        int maxColumnDimensionLen = columnData.getMaxArrayLength();
        int colRegionLength = columnData.getDimensionArrayLength();
        int startRow = maxColumnDimensionLen + 1;
        for (int i = 0; i < integers.size(); i++) {
            int rowRegionIndex = integers.get(i);
            BIDimension[] rowDimension = rowData.getDimensionArray(rowRegionIndex);
            int startColumn = maxRowDimensionLen;
            for (int n = i * colRegionLength; n < (i + 1) * colRegionLength; n++) {
                NewCrossRoot node = nodes.get(n);
                BIDimension[] colDimension = columnData.getDimensionArray(n - i * colRegionLength);
                boolean isColTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(colDimension);
                boolean isRowTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(rowDimension);
                if (needAllPage) {
                    if (n == i * colRegionLength) {
                        GroupExecutor.dealWithNode(node.getLeft(), NodeExpander.ALL_EXPANDER, cbcells, startRow, 0, paging.getCurrentPage(), rowDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), maxRowDimensionLen - 1, 0, rowData);
                    }
                    if (i == 0) {
                        HorGroupExecutor.dealWithNode(node.getTop(), NodeExpander.ALL_EXPANDER, cbcells, 0, startColumn, colDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), maxColumnDimensionLen - 1, true, new IntList(), isRowTargetSort, rowDimension[0], widget, columnData);
                    }
                    CrossExpander expander = CrossExpander.ALL_EXPANDER;
                    dealWithNode(node.getLeft(), expander, expander.getYExpander(), cbcells, rowDimension, colDimension, startRow, startColumn, keys, maxRowDimensionLen - 1);
                    startColumn += !hasTarget ? node.getTop().getTotalLength() : node.getTop().getTotalLengthWithSummary();
                } else {
                    CrossExpander expander = complexExpander.createCrossNode(rowRegionIndex, n - i * colRegionLength);
                    if (n == i * colRegionLength) {
                        GroupExecutor.dealWithNode(node.getLeft(), expander.getYExpander(), cbcells, startRow, 0, paging.getCurrentPage(), rowDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), maxRowDimensionLen - 1, 0, rowData);
                    }
                    if (i == 0) {
                        HorGroupExecutor.dealWithNode(node.getTop(), expander.getXExpander(), cbcells, 0, startColumn, colDimension, usedSumTarget, new TargetGettingKey[]{}, new ArrayList<String>(), maxColumnDimensionLen - 1, true, new IntList(), isRowTargetSort, rowDimension[0], widget, columnData);
                    }
                    dealWithNode(node.getLeft(), expander, expander.getYExpander(), cbcells, rowDimension, colDimension, startRow, startColumn, keys, rowDimension.length - 1);
                    startColumn += (!hasTarget ? node.getTop().getTotalLength(expander.getXExpander()) : node.getTop().getTotalLengthWithSummary(expander.getXExpander())) * (Math.max(1, keys.length));
                }

            }
            if (needAllPage) {
                startRow += !hasTarget ? nodes.get(i).getLeft().getTotalLength() : nodes.get(i).getTop().getTotalLengthWithSummary();
            } else {
                startRow += !hasTarget ? nodes.get(i).getLeft().getTotalLength(complexExpander.getYExpander(rowRegionIndex)) : nodes.get(i).getLeft().getTotalLengthWithSummary(complexExpander.getYExpander(rowRegionIndex));
            }
        }
    }

    private int getTotalNodeRowLength(ArrayList<NewCrossRoot> roots, boolean hasTarget, ArrayList<Integer> integers) {
        int count = 0;
        int columnRegionLength = this.columnData.getDimensionArrayLength();

        for (int i = 0; i < integers.size(); i++) {
            if (hasTarget) {

                count += roots.get(i * columnRegionLength).getLeft().getTotalLengthWithSummary();
            } else {

                count += roots.get(i * columnRegionLength).getLeft().getTotalLength();
            }
        }

        return count;

    }

    private int getTotalNodeRowLength(ArrayList<NewCrossRoot> roots, boolean hasTarget, ComplexExpander complexExpander, ArrayList<Integer> integers) {
        int count = 0;
        int columnRegionLength = this.columnData.getDimensionArrayLength();

        for (int i = 0; i < integers.size(); i++) {
            NodeExpander nodeExpander = complexExpander.getYExpander(integers.get(i));
            if (hasTarget) {

                count += roots.get(i * columnRegionLength).getLeft().getTotalLengthWithSummary(nodeExpander);
            } else {

                count += roots.get(i * columnRegionLength).getLeft().getTotalLength(nodeExpander);
            }
        }
        return count;
    }

    private int getTotalNodeColumnLength(ArrayList<NewCrossRoot> roots, boolean hasTarget) {
        int count = 0;
        int columnRegionLength = this.columnData.getDimensionArrayLength();

        for (int i = 0; i < columnRegionLength; i++) {
            if (hasTarget) {

                count += roots.get(i).getTop().getTotalLengthWithSummary();
            } else {

                count += roots.get(i).getTop().getTotalLength();
            }
        }

        return count;

    }

    private int getTotalNodeColumnLength(ArrayList<NewCrossRoot> roots, boolean hasTarget, ComplexExpander complexExpander) {
        int count = 0;
        int columnRegionLength = this.columnData.getDimensionArrayLength();

        for (int i = 0; i < columnRegionLength; i++) {
            NodeExpander nodeExpander = complexExpander.getXExpander(i);
            if (hasTarget) {

                count += roots.get(i).getTop().getTotalLengthWithSummary(nodeExpander);
            } else {

                count += roots.get(i).getTop().getTotalLength(nodeExpander);
            }
        }
        return count;
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

    private int dealWithNode(CrossHeader left, CrossExpander expander, NodeExpander yExpander, CBCell[][] cbcells, BIDimension[] rowDimension, BIDimension[] colDimension,
                             int row, int column, TargetGettingKey[] keys, int total) {
        int pos = 0;
        boolean discardSummary = false;
        if (yExpander != null) {
            for (int i = 0; i < left.getChildLength(); i++) {
                pos += dealWithNode((CrossHeader) left.getChild(i), expander, yExpander.getChildExpander(left.getChild(i).getShowValue()), cbcells, rowDimension, colDimension, row + pos, column, keys, total - 1);
            }
            discardSummary = (!left.needSummary()) || keys.length == 0;
        }
        if (discardSummary) {
            return pos;
        }
        //pos如果不为0说明是汇总的格子
        dealWithCrossNode(left.getValue(), paging.getOprator() < Node.NONE_PAGE_LEVER ? new NodeAllExpander(colDimension.length - 1) : expander.getXExpander(), cbcells,
                rowDimension, colDimension, row + pos, column, keys, colDimension.length, pos != 0, total);
        pos++;
        return pos;
    }

    private int dealWithCrossNode(CrossNode node, NodeExpander xExpander, CBCell[][] cbcells, BIDimension[] rowDimension,
                                  BIDimension[] colDimension, int row, int column, TargetGettingKey[] keys, int xTotal, boolean isYSummary, int yTotal) {
        int pos = 0;
        boolean discardSummary = false;
        if (xExpander != null) {
            for (int i = 0; i < node.getTopChildLength(); i++) {
                pos += dealWithCrossNode(node.getTopChild(i), xExpander.getChildExpander(node.getTopChild(i).getHead().getShowValue()), cbcells, rowDimension, colDimension,
                        row, column + pos * Math.max(keys.length, 1), keys, xTotal - 1, isYSummary, yTotal);
            }
            discardSummary = (!node.getHead().needSummary()) || keys.length == 0;
        }
        if (discardSummary) {
            return pos;
        }
        //pos如果不为0说明是汇总的格子
        if (keys.length == 0) {
            dealWithSumNode(node, cbcells, rowDimension, colDimension, row, column, xTotal, isYSummary, yTotal, pos);
        } else {
            for (int k = 0; k < keys.length; k++) {
                Object v = node.getSummaryValue(keys[k]);
                CBCell cell = new CBCell(v == null ? NONEVALUE : v);
                cell.setColumn(column + (pos * keys.length) + k);
                cell.setRow(row);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(
                        pos == 0 ? (isYSummary ?
                                BITableStyle.getInstance().getYTotalCellStyle(v, yTotal)
                                :
                                BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1)
                            ) : BITableStyle.getInstance().getXTotalCellStyle(v, xTotal));
                List cellList = new ArrayList();
                cellList.add(cell);
                CBBoxElement cbox = new CBBoxElement(cellList);
                TargetStyle style = usedSumTarget[k].getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
                cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
                cbox.setDimensionJSON(createDimensionValue(node, rowDimension, colDimension));
                cbox.setName(usedSumTarget[k].getValue());
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
        pos++;
        return pos;
    }

    private void dealWithSumNode(CrossNode node, CBCell[][] cbcells, BIDimension[] rowDimension, BIDimension[] colDimension, int row, int column, int xTotal, boolean isYSummary, int yTotal, int pos) {
        Object v = null;
        CBCell cell = new CBCell(NONEVALUE);
        cell.setColumn(column);
        cell.setRow(row);
        cell.setRowSpan(1);
        cell.setColumnSpan(1);
        cell.setStyle(
                pos == 0 ? (isYSummary ?
                        BITableStyle.getInstance().getYTotalCellStyle(v, yTotal)
                        :
                        BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1)
                ) : BITableStyle.getInstance().getXTotalCellStyle(v, xTotal));
        List cellList = new ArrayList();
        cellList.add(cell);
        CBBoxElement cbox = new CBBoxElement(cellList);
        cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
        cbox.setDimensionJSON(createDimensionValue(node, rowDimension, colDimension));
        cbox.setName(StringUtils.EMPTY);
        cell.setBoxElement(cbox);
        cbcells[cell.getColumn()][cell.getRow()] = cell;
    }

    //TODO代码质量
    private String createDimensionValue(CrossNode node, BIDimension[] rowDimension, BIDimension[] colDimension) {
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

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    private Map<Integer, NewCrossRoot[]> getCubeCrossNodes() {
        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int usedSumLen = usedSumTarget.length;
        Map<String, TargetCalculator> targetsMap = new HashMap<String, TargetCalculator>();
        for (int i = 0; i < usedSumLen; i++) {
            targetsMap.put(usedSumTarget[i].getValue(), usedSumTarget[i].createSummaryCalculator());
        }

        Map<Integer, NewCrossRoot[]> nodeMap = getCubeCrossNodesFromProvide(targetsMap);

        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
    }

    //通过交叉表的provide，创建nodeMap
    private Map<Integer, NewCrossRoot[]> getCubeCrossNodesFromProvide(Map<String, TargetCalculator> targetsMap) {
        BISummaryTarget[] usedTarget = createTarget4Calculate();
        Map<Integer, NewCrossRoot[]> nodeMap = new HashMap<Integer, NewCrossRoot[]>();
        int columnRegionLen = columnData.getDimensionArrayLength();

        for (int i = 0; i < rowData.getRegionIndex(); i++) {

            BIDimension[] rowDimension = rowData.getDimensionArray(i);
            NewCrossRoot[] nodes = new NewCrossRoot[columnRegionLen];

            for (int j = 0; j < columnRegionLen; j++) {

                CrossExpander expander = complexExpander.createCrossNode(i, j);
                BIDimension[] colDimension = columnData.getDimensionArray(j);
                NewCrossRoot node = null;
                node = CubeIndexLoader.getInstance(session.getUserId()).loadPageCrossGroup(usedTarget, rowDimension, colDimension, allSumTarget, -1, widget.useRealData(), session, expander, widget);
                node = node.createResultFilterNodeWithTopValue(rowDimension, colDimension, targetsMap);
                nodes[j] = node;
            }
            nodeMap.put(i, nodes);
        }
        return nodeMap;
    }
}