package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HorGroupExecutor extends AbstractNodeExecutor {

    public HorGroupExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
    }

    //
    static void dealWithNodeNoChild(Node node, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] colColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys,
                                    TableWidget widget, int tempCol, BIComplexExecutData rowData) {
        int columnLength = colColumn.length;
        int maxColumnLen = rowData.getMaxArrayLength();
        CBCell cell = null;
        int index = row - maxColumnLen;
        if (index == 0) {
            for (int i = 0, len = keys.length; i < len; i++) {
                Object v = node.getSummaryValue(keys[i]);
                cell = new CBCell(v == null ? NONEVALUE : v);
                cell.setRow(maxColumnLen + i);
                cell.setColumn(tempCol);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(i));
                cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
                List<CBCell> cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                //TODO CBBoxElement需要整合减少内存
                CBBoxElement cbox = new CBBoxElement(cellList);
                Node t = node;
                JSONArray ja = new JSONArray();
                int k = columnLength - 1;
                while (k != -1 && t != null) {
                    try {
                        ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                    } catch (Exception e) {
                    }
                    t = t.getParent();
                    k--;
                }
                cbox.setDimensionJSON(ja.toString());
                BITarget sumCol = sumColumn[i];
                cbox.setName(sumCol.getValue());
                TargetStyle style = sumCol.getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
                cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
    }

    static void dealWithNodeIsCross(Node node, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] colColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys,
                                    TableWidget widget, int tempCol, BIComplexExecutData rowData) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        int maxColumnLength = rowData.getMaxArrayLength();
        if (sumColumn.length == 0) {
            cell = new CBCell(StringUtils.EMPTY);
            cell.setRow(maxColumnLength);
            cell.setColumn(tempCol);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(maxColumnLength));
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(StringUtils.EMPTY);
            cbox.setType(CellConstant.CBCELL.SUMARYNAME);
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        } else {
            for (int i = 0; i < sumColumn.length; i++) {
                cell = new CBCell(sumColumn[i].getValue());
                cell.setRow(maxColumnLength);
                cell.setColumn(tempCol + i);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(1));
                List<CBCell> cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                //TODO CBBoxElement需要整合减少内存
                CBBoxElement cbox = new CBBoxElement(cellList);
                BITargetAndDimension sumCol = sumColumn[i];
                cbox.setName(sumCol.getValue());
                cbox.setType(CellConstant.CBCELL.SUMARYNAME);
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
    }

    static void dealWithNodeNoExpander(Node node, CBCell[][] cbcells, int row, int column,
                                       BIDimension[] colColumn,
                                       BITarget[] sumColumn, TargetGettingKey[] keys,
                                       TableWidget widget, int tempCol, BIComplexExecutData rowData) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        for (int i = 0, len = keys.length; i < len; i++) {
            Object v = node.getSummaryValue(keys[i]);
            cell = new CBCell(v == null ? NONEVALUE : v);
            cell.setRow(rowData.getMaxArrayLength() + i);
            cell.setColumn(tempCol);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(cell.getRow()));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITarget sumCol = sumColumn[i];
            TargetStyle style = sumCol.getStyle();
            if (style != null) {
                style.changeCellStyle(cell);
            }
            cbox.setName(sumCol.getValue());
            cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
            Node t = node;
            JSONArray ja = new JSONArray();
            int k = 0;
            while (t.getParent() != null) {
                t = t.getParent();
                k++;
            }
            k--;
            t = node;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                } catch (JSONException e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    static void dealWithNodeNoExpanderIsCross(Node node, CBCell[][] cbcells, int row, int column,
                                              BIDimension[] colColumn,
                                              BITarget[] sumColumn, TargetGettingKey[] keys,
                                              boolean isTargetSort, BIDimension sortDimension,
                                              TableWidget widget, int tempCol, BIComplexExecutData rowData) {
        boolean isSortTitle = row == 0;
        CBCell cell = null;
        int maxColumnLength = rowData.getMaxArrayLength();
        if (sumColumn.length == 0) {
            cell = new CBCell(StringUtils.EMPTY);
            cell.setRow(maxColumnLength);
            cell.setColumn(tempCol);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(maxColumnLength));
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(StringUtils.EMPTY);
            if (isSortTitle) {
                cbox.setType(CellConstant.CBCELL.TARGETTITLE_Y);
                cbox.setSortTargetName(StringUtils.EMPTY);
                cbox.setSortTargetValue("[]");
                if (isTargetSort && ComparatorUtils.equals(sortDimension.getSortTarget(), StringUtils.EMPTY)) {
                    cbox.setSortType(sortDimension.getSortType());
                } else {
                    cbox.setSortType(BIReportConstant.SORT.NONE);
                }
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        } else {
            for (int i = 0; i < sumColumn.length; i++) {
                cell = new CBCell(sumColumn[i].getValue());
                cell.setRow(maxColumnLength);
                cell.setColumn(tempCol + i);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(i));
                List<CBCell> cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                //TODO CBBoxElement需要整合减少内存
                CBBoxElement cbox = new CBBoxElement(cellList);
                BITargetAndDimension sumCol = sumColumn[i];
                cbox.setName(sumCol.getValue());
                if (isSortTitle) {
                    cbox.setType(CellConstant.CBCELL.TARGETTITLE_Y);
                    cbox.setSortTargetName(sumColumn[i].getValue());
                    cbox.setSortTargetValue("[]");
                    if (isTargetSort && ComparatorUtils.equals(sortDimension.getSortTarget(), sumCol.getValue())) {
                        cbox.setSortType(sortDimension.getSortType());
                    } else {
                        cbox.setSortType(BIReportConstant.SORT.NONE);
                    }
                }
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
    }

    static int dealWithNodeExpander(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] colColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys,
                                    final ArrayList<String> indexList, int total, boolean isCross, IntList deepList,
                                    boolean isTargetSort, BIDimension sortDimension, TableWidget widget, int tempCol, BIComplexExecutData rowData) {
        Node tempNode = null;
        CBCell cell = null;
        int columnLength = colColumn.length;
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            @SuppressWarnings("unchecked")
            ArrayList<String> currentIndex = (ArrayList<String>) indexList.clone();
            int dimensionIndex = rowData.getDimensionIndexFromRow(row, columnLength);
            BIDimension rd = colColumn[dimensionIndex];
            String name = rd.toString(tempNode.getData());
            NodeExpander childEx = expander.getChildExpander(name);
            int colSpan = sumColumn.length == 0 ? tempNode.getTotalLength(childEx) : tempNode.getTotalLengthWithSummary(childEx);
            if (isCross) {
                colSpan = colSpan * Math.max(sumColumn.length, 1);
            }
            cell = new CBCell(name);
            currentIndex.add(name == null ? StringUtils.EMPTY : name);
            cell.setRow(row);
            cell.setColumn(tempCol);
            cell.setColumnSpan(colSpan);

            int rowSpan = rowData.getColumnRowSpan(row, columnLength);
            if (childEx == null) {
                rowSpan = rowData.getNoneChildSpan(row, columnLength);
            }
            cell.setRowSpan(rowSpan);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(0));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITargetAndDimension rowCol = colColumn[dimensionIndex];
            cbox.setName(rowCol.getValue());
            if (dimensionIndex != columnLength - 1) {
                cbox.setIndexString_x(trunToIndexString(currentIndex));
                cbox.setExpand(expander.isChildExpand(name));
                cbox.setDimensionRegionIndex(rowData.getDimensionRegionFromDimension(colColumn));
            }
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            Node t = tempNode;
            JSONArray ja = new JSONArray();
            int k = dimensionIndex;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                } catch (Exception e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            IntList list = null;
            try {
                list = (IntList) deepList.clone();
                list.add(i);
            } catch (CloneNotSupportedException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
            }
            dealWithNode(tempNode, expander.getChildExpander(name), cbcells, row + rowSpan, tempCol, colColumn, sumColumn, keys, currentIndex, total - 1, isCross, list, isTargetSort, sortDimension, widget, rowData);
            tempCol += colSpan;
        }
        return tempCol;
    }

    static void dealWithNodeExpanderIsCross(Node node, CBCell[][] cbcells, int row, int column, BIDimension[] colColumn, BITarget[] sumColumn, TargetGettingKey[] keys,
                                            int total, TableWidget widget, int tempCol, BIComplexExecutData columnData) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        boolean isSortTitle = row == 0;
        for (int p = 0; p < sumColumn.length; p++) {
            cell = new CBCell(Inter.getLocText("BI-Summary") + ":" + sumColumn[p].getValue());
            cell.setRow(row);
            cell.setColumn(tempCol + p);
            int dimensionIndex = columnData.getDimensionIndexFromRow(row, columnLength);
            cell.setRowSpan(columnData.getNoneChildRowSpan(row, columnLength) + 1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getXSumStringCellStyle(total));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);//TODO CBBoxElement需要整合减少内存
            cbox.setName(colColumn[dimensionIndex].getValue());
            if (isSortTitle) {
                cbox.setType(CellConstant.CBCELL.TARGETTITLE_Y);
                cbox.setSortTargetName(sumColumn[p].getValue());
                cbox.setSortTargetValue("[]");
                if (widget.getTargetSort() != null && ComparatorUtils.equals(widget.getTargetSort().getName(), sumColumn[p].getValue())) {
                    cbox.setSortType((Integer) widget.getTargetSort().getObject());
                } else {
                    cbox.setSortType(BIReportConstant.SORT.NONE);
                }
            }
            cell.setBoxElement(cbox);
            Node t = node;
            JSONArray ja = new JSONArray();
            int k = dimensionIndex - 1;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                } catch (Exception e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            for (int i = 0, len = keys.length; i < len; i++) {
                Object v = node.getSummaryValue(keys[i]);
                cell = new CBCell(v == null ? NONEVALUE : v);
                cell.setRow(columnData.getMaxArrayLength() + i);
                cell.setColumn(tempCol + p);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getYTotalCellStyle(v, total));
                cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
                cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                cbox = new CBBoxElement(cellList);//TODO CBBoxElement需要整合减少内存
                BITarget sumCol = sumColumn[i];
                TargetStyle style = sumCol.getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
                cbox.setName(sumCol.getValue());
                cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
                t = node;
                ja = new JSONArray();
                k = 0;
                while (t.getParent() != null) {
                    t = t.getParent();
                    k++;
                }
                k--;
                t = node;
                while (k != -1 && t != null) {
                    try {
                        ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                    } catch (JSONException e) {
                    }
                    t = t.getParent();
                    k--;
                }
                cbox.setDimensionJSON(ja.toString());
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
    }

    static void dealWithNodeExpanderIsCrossNo(Node node, CBCell[][] cbcells, int row, int column, BIDimension[] colColumn,
                                              BITarget[] sumColumn, TargetGettingKey[] keys,
                                              int total, TableWidget widget, int tempCol, BIComplexExecutData columnData) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        cell = new CBCell(Inter.getLocText("BI-Summary"));
        cell.setRow(row);
        cell.setColumn(tempCol);
        int dimensionIndex = columnData.getDimensionIndexFromRow(row, columnLength);
        cell.setRowSpan(columnData.getNoneChildRowSpan(row, columnLength));
        cell.setColumnSpan(1);
        cell.setStyle(BITableStyle.getInstance().getXSumStringCellStyle(total));
        cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
        List<CBCell> cellList = new ArrayList<CBCell>();
        cellList.add(cell);
        //TODO CBBoxElement需要整合减少内存
        CBBoxElement cbox = new CBBoxElement(cellList);
        cbox.setName(colColumn[dimensionIndex].getValue());
        cbox.setType(CellConstant.CBCELL.SUMARYNAME);
        cell.setBoxElement(cbox);
        Node t = node;
        JSONArray ja = new JSONArray();
        int k = dimensionIndex - 1;
        while (k != -1 && t != null) {
            try {
                ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
            } catch (Exception e) {
            }
            t = t.getParent();
            k--;
        }
        cbox.setDimensionJSON(ja.toString());
        cbcells[cell.getColumn()][cell.getRow()] = cell;
        for (int i = 0, len = keys.length; i < len; i++) {
            Object v = node.getSummaryValue(keys[i]);
            cell = new CBCell(v == null ? NONEVALUE : v);
            cell.setRow(columnData.getMaxArrayLength() + i);
            cell.setColumn(tempCol);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getXTotalCellStyle(v, total));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            cbox = new CBBoxElement(cellList);
            BITarget sumCol = sumColumn[i];
            TargetStyle style = sumCol.getStyle();
            if (style != null) {
                style.changeCellStyle(cell);
            }
            cbox.setName(sumCol.getValue());
            cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
            t = node;
            ja = new JSONArray();
            k = 0;
            while (t.getParent() != null) {
                t = t.getParent();
                k++;
            }
            k--;
            t = node;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                } catch (JSONException e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    /**
     * @param node
     * @param expander
     * @param cbcells
     * @param row
     * @param column
     * @param colColumn
     * @param sumColumn
     * @param keys
     * @param indexList
     * @param total
     * @param isCross       是否是从交叉表传过来
     * @param deepList
     * @param isTargetSort
     * @param sortDimension
     * @param widget
     * @param rowData
     */
    static void dealWithNode(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column,
                             BIDimension[] colColumn,
                             BITarget[] sumColumn, TargetGettingKey[] keys,
                             final ArrayList<String> indexList, int total, boolean isCross, IntList deepList, boolean isTargetSort, BIDimension sortDimension, TableWidget widget, BIComplexExecutData rowData) {
        int columnLength = colColumn.length;
        Node tempNode = null;
        int tempCol = column;
        CBCell cell = null;
        boolean isSortTitle = row == 0;
        if (isLastDimension(row, rowData)) {
            dealWithNodeNoChild(node, cbcells, row, column, colColumn, sumColumn, keys, widget, tempCol, rowData);
            if (isCross) {
                dealWithNodeIsCross(node, cbcells, row, column, colColumn, sumColumn, keys, widget, tempCol, rowData);
            }
            return;
        }
        //收缩着:
        if (expander == null) {
            dealWithNodeNoExpander(node, cbcells, row, column, colColumn, sumColumn, keys, widget, tempCol, rowData);
            if (isCross) {
                dealWithNodeNoExpanderIsCross(node, cbcells, row, column, colColumn, sumColumn, keys, isTargetSort, sortDimension, widget, tempCol, rowData);
            }
            return;
        }
        //展开情况
        tempCol = dealWithNodeExpander(node, expander, cbcells, row, column, colColumn, sumColumn, keys, indexList, total, isCross, deepList, isTargetSort, sortDimension, widget, tempCol, rowData);
        if (isCross) {
            if (node.needSummary() || (node.getChildLength() == 0)) {
                dealWithNodeExpanderIsCross(node, cbcells, row, column, colColumn, sumColumn, keys, total, widget, tempCol, rowData);
            }
        } else {
            if (node.needSummary()) {
                dealWithNodeExpanderIsCrossNo(node, cbcells, row, column, colColumn, sumColumn, keys, total, widget, tempCol, rowData);
            }
        }
    }

    private static boolean isLastDimension(int row, BIComplexExecutData rowData) {
        return row - rowData.getMaxArrayLength() == 0;
    }

    private static int dealWithNodeNoChildren(Node node, CBCell[][] cbcells, int row, int column,
                                              BIDimension[] colColumn,
                                              BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempCol, BIComplexExecutData rowData) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        int maxColumnLen = rowData.getMaxArrayLength();
        int index = row - maxColumnLen;
        if (index == 0) {
            for (int i = 0, len = keys.length; i < len; i++) {
                Object v = node.getSummaryValue(keys[i]);
                cell = new CBCell(v == null ? NONEVALUE : v);
                cell.setRow(maxColumnLen + i);
                cell.setColumn(tempCol);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1));
                cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
                List<CBCell> cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                //TODO CBBoxElement需要整合减少内存
                CBBoxElement cbox = new CBBoxElement(cellList);
                Node t = node;
                JSONArray ja = new JSONArray();
                int k = columnLength - 1;
                while (k != -1 && t != null) {
                    try {
                        ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                    } catch (Exception e) {
                    }
                    t = t.getParent();
                    k--;
                }
                cbox.setDimensionJSON(ja.toString());
                BITarget sumCol = sumColumn[i];
                cbox.setName(sumCol.getValue());
                TargetStyle style = sumCol.getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
                cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
        return tempCol;
    }

    private static int dealWithNodeChildren(Node node, CBCell[][] cbcells, int row, int column,
                                            BIDimension[] colColumn,
                                            BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempCol, BIComplexExecutData rowData) {
        Node tempNode = null;
        CBCell cell = null;
        int columnLengh = colColumn.length;
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            int colSpan = sumColumn.length == 0 ? tempNode.getTotalLength() : tempNode.getTotalLengthWithSummary();
            int dimensionIndex = rowData.getDimensionIndexFromRow(row, columnLengh);
            BIDimension rd = colColumn[dimensionIndex];
            cell = new CBCell(rd.toString(tempNode.getData()));
            cell.setRow(row);
            cell.setColumn(tempCol);
            cell.setColumnSpan(colSpan);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, cell.getRow() % 2 == 1));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            int rowSpan = rowData.getNormalRowSpan(row, columnLengh);
            cell.setRowSpan(rowSpan);
            BITargetAndDimension rowCol = colColumn[dimensionIndex];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            Node t = tempNode;
            JSONArray ja = new JSONArray();
            int k = dimensionIndex;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                } catch (Exception e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            dealWithNode(tempNode, cbcells, row + rowSpan, tempCol, colColumn, sumColumn, keys, total - 1, rowData);
            tempCol += colSpan;
        }
        return tempCol;
    }

    private static int dealWithNodeChildrenLenDY1(Node node, CBCell[][] cbcells, int row, int column,
                                                  BIDimension[] colColumn,
                                                  BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempCol, BIComplexExecutData rowData) {
        int columnLength = colColumn.length;
        CBCell cell = new CBCell(Inter.getLocText("BI-Summary"));
        cell.setRow(row);
        cell.setColumn(tempCol);
        int dimensionIndex = rowData.getDimensionIndexFromRow(row, columnLength);
        int noChildSpan = rowData.getNoneChildRowSpan(row, columnLength);
        cell.setRowSpan(noChildSpan);
        cell.setColumnSpan(1);
        cell.setStyle(BITableStyle.getInstance().getXSumStringCellStyle(total));
        cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
        List<CBCell> cellList = new ArrayList<CBCell>();
        cellList.add(cell);
        //TODO CBBoxElement需要整合减少内存
        CBBoxElement cbox = new CBBoxElement(cellList);
        cbox.setName(colColumn[dimensionIndex].getValue());
        cbox.setType(CellConstant.CBCELL.SUMARYNAME);
        cell.setBoxElement(cbox);
        Node t = node;
        JSONArray ja = new JSONArray();
        int k = dimensionIndex - 1;
        while (k != -1 && t != null) {
            try {
                ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
            } catch (Exception e) {
            }
            t = t.getParent();
            k--;
        }
        cbox.setDimensionJSON(ja.toString());
        cbcells[cell.getColumn()][cell.getRow()] = cell;
        for (int i = 0, len = keys.length; i < len; i++) {
            Object v = node.getSummaryValue(keys[i]);
            cell = new CBCell(v == null ? NONEVALUE : v);
            cell.setRow(rowData.getMaxArrayLength() + i);
            cell.setColumn(tempCol);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getXTotalCellStyle(v, total));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            cbox = new CBBoxElement(cellList);
            BITarget sumCol = sumColumn[i];
            TargetStyle style = sumCol.getStyle();
            if (style != null) {
                style.changeCellStyle(cell);
            }
            cbox.setName(sumCol.getValue());
            cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
            t = node;
            ja = new JSONArray();
            k = 0;
            while (t.getParent() != null) {
                t = t.getParent();
                k++;
            }
            k--;
            t = node;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(colColumn[k].getValue(), colColumn[k].toFilterObject(t.getData())));
                } catch (JSONException e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
        return tempCol;
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param cbcells   表列
     * @param row       行
     * @param column    列
     * @param colColumn 列
     * @param sumColumn 指标
     * @param keys      key值
     * @param total     总个数
     * @param rowData   复杂表数据，普通表只有一组
     */
    public static void dealWithNode(Node node, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] colColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys, int total, BIComplexExecutData rowData) {
        int columnLength = colColumn.length;
        Node tempNode = null;
        int tempCol = column;
        CBCell cell = null;
        if (isLastDimension(row, rowData)) {
            tempCol = dealWithNodeNoChildren(node, cbcells, row, column, colColumn, sumColumn, keys, total, tempCol, rowData);
            return;
        }
        tempCol = dealWithNodeChildren(node, cbcells, row, column, colColumn, sumColumn, keys, total, tempCol, rowData);
        if (node.getChildLength() > 1) {
            tempCol = dealWithNodeChildrenLenDY1(node, cbcells, row, column, colColumn, sumColumn, keys, total, tempCol, rowData);
        }
    }

    private static String trunToIndexString(ArrayList<String> currentIndex) {
        return new JSONArray(currentIndex).toString();
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    @Override
    public Node getCubeNode() {
        if (getSession() == null) {
            return null;
        }
        long start = System.currentTimeMillis();
        Node tree = CubeIndexLoader.getInstance(session.getUserId()).loadPageGroup(true, widget, createTarget4Calculate(), usedDimensions, allDimensions, allSumTarget, paging.getOprator(), widget.useRealData(), session, expander.getXExpander());
        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }

    /**
     * 创建cell
     *
     * @return cell数组
     * @throws NoneAccessablePrivilegeException
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        Node tree = getCubeNode();
        if (tree == null) {
            return new CBCell[0][0];
        }
        int colLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int rowLength = colLength + summaryLength;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
        }
        int collen = ExecutorCommonUtils.isAllPage(paging.getOprator()) ? tree.getTotalLengthWithSummary() : tree.getTotalLengthWithSummary(expander.getXExpander());

        CBCell[][] cbcells = new CBCell[collen + Math.min(tree.getChildLength(), 1) + widget.isOrder()][rowLength];
        boolean isTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(usedDimensions);

        for (int i = 0; i < colLength; i++) {
            CBCell cell = new CBCell(((BIAbstractTargetAndDimension)usedDimensions[i]).getText());
            cell.setColumn(0);
            cell.setRow(i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(i));
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITargetAndDimension rowCol = usedDimensions[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_X);
            if (!isTargetSort) {
                cbox.setSortType(usedDimensions[i].getSortType());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
        for (int i = 0; i < summaryLength; i++) {
            CBCell cell = new CBCell(usedSumTarget[i].getValue());
            cell.setColumn(0);
            cell.setRow(colLength + i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(i));
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITargetAndDimension rowCol = usedSumTarget[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.TARGETTITLE_X);
            cbox.setSortTargetName(rowCol.getValue());
            cbox.setSortTargetValue("[]");
            if (widget.getTargetSort() != null && ComparatorUtils.equals(widget.getTargetSort().getName(), usedSumTarget[i].getText())) {
                cbox.setSortType((Integer) widget.getTargetSort().getObject());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
        if (ExecutorCommonUtils.isAllPage(paging.getOprator())) {
            dealWithNode(tree, cbcells, 0, 1, usedDimensions, usedSumTarget, keys, usedDimensions.length - 1, new BIComplexExecutData(usedDimensions));
        } else {
            dealWithNode(tree, expander.getXExpander(), cbcells, 0, 1, usedDimensions, usedSumTarget, keys, new ArrayList<String>(), usedDimensions.length - 1, false, new IntList(), false, null, widget, new BIComplexExecutData(usedDimensions));
        }
        return cbcells;
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

}