package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
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
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BIAbstractTarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.*;

public class HorGroupExecutor extends GroupExecutor {

    private BIDimension[] colDimension;

    public HorGroupExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
        usedDimensions = widget.getViewTopDimensions();
        colDimension = usedDimensions;
    }

    public DetailCellIterator createCellIterator4Excel() throws Exception {
        final Node node = getCubeNode();
        int rowLength = colDimension.length + usedSumTarget.length;
        int columnLength = node.getTotalLength() + widget.isOrder() + 1;
        final DetailCellIterator iter = new DetailCellIterator(columnLength, rowLength);
        new Thread() {
            public void run() {
                try {
                    FinalInt start = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    generateTitle(node, widget, colDimension, pagedIterator);
                    generateCells(node, widget, colDimension, usedSumTarget, pagedIterator);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    private void generateTitle(Node node, TableWidget widget, BIDimension[] colDimension, StreamPagedIterator pagedIterator) {
        Style style = BITableStyle.getInstance().getTitleDimensionCellStyle(0);
        if (widget.isOrder() == 1) {
            CBCell cell = ExecutorUtils.createCell(Inter.getLocText("BI-Number_Index"), 0, colDimension.length, 0, 1, style);
            pagedIterator.addCell(cell);
        }
        int colDimIdx = 0;
        while (colDimIdx < colDimension.length) {
            CBCell cell = ExecutorUtils.createCell(colDimension[colDimIdx].getText(), colDimIdx, 1, widget.isOrder(), 1, style);
            pagedIterator.addCell(cell);
            node = node.getFirstChild();
            Node temp = node;
            int columnIdx = widget.isOrder() + 1;
            BIDimension dim = colDimension[colDimIdx];
            while (temp != null) {
                Object data = temp.getData();
                Object v = dim.getValueByType(data);
                CBCell dimCell = ExecutorUtils.createCell(v, colDimIdx, 1, columnIdx, temp.getTotalLength(), style);
                pagedIterator.addCell(dimCell);
                columnIdx += temp.getTotalLength();
                temp = temp.getSibling();
            }
            colDimIdx++;
        }
    }

    private void generateCells(Node node, TableWidget widget, BIDimension[] colDimension, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator) {
        int rowIdx = colDimension.length;
        TargetGettingKey[] keys = widget.getTargetsKey();
        while (node.getFirstChild() != null) {
            node = node.getFirstChild();
        }

        for (int i = 0; i < usedSumTarget.length; i++) {
            int columnIdx = widget.isOrder() + 1;
            Style headStyle = BITableStyle.getInstance().getDimensionCellStyle(false, (i + 1) % 2 == 1);
            if (widget.isOrder() == 1) {
                CBCell orderCell = ExecutorUtils.createCell(i + 1, rowIdx + i, 1, 0, 1, headStyle);
                pagedIterator.addCell(orderCell);
            }
            Object targetName = usedSumTarget[i].getText();
            CBCell targetNameCell = ExecutorUtils.createCell(targetName, rowIdx + i, 1, widget.isOrder(), 1, headStyle);
            pagedIterator.addCell(targetNameCell);
            Node temp = node;
            while (temp != null) {
                Object data = temp.getSummaryValue(keys[i]);
                boolean isPercent = widget.getChartSetting().getNumberLevelByTargetId(keys[i].getTargetName()) == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT;
                Style style = BITableStyle.getInstance().getNumberCellStyle(data, (i + 1) % 2 == 1, isPercent);
                CBCell cell = ExecutorUtils.createCell(data, rowIdx + i, 1, columnIdx++, 1, style);
                pagedIterator.addCell(cell);
                temp = temp.getSibling();
            }
        }
    }

    static void dealWithNodeNoChild(Node node, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] colColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys,
                                    TableWidget widget, int tempCol, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int columnLength = colColumn.length;
        int maxColumnLen = rowData.getMaxArrayLength();
        CBCell cell = null;
        int index = row - maxColumnLen;
        if (index == 0) {
            for (int i = 0, len = keys.length; i < len; i++) {
                Object v = node.getSummaryValue(keys[i]);
                v = ExecutorUtils.formatExtremeSumValue(v, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()));
                cell = new CBCell(v);
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
                cell = new CBCell(((BIAbstractTarget) sumColumn[i]).getText());
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
                cbox.setName(((BIAbstractTarget) sumColumn[i]).getText());
                cbox.setType(CellConstant.CBCELL.SUMARYNAME);
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
    }

    static void dealWithNodeNoExpander(Node node, CBCell[][] cbcells, int row, int column,
                                       BIDimension[] colColumn,
                                       BITarget[] sumColumn, TargetGettingKey[] keys,
                                       TableWidget widget, int tempCol, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        for (int i = 0, len = keys.length; i < len; i++) {
            Object v = node.getSummaryValue(keys[i]);
            v = ExecutorUtils.formatExtremeSumValue(v, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()));
            cell = new CBCell(v);
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

    static private void createCellWhileNoSumColumnLength(int maxColumnLength, int tempCol, boolean isSortTitle, boolean isTargetSort, BIDimension sortDimension, CBCell[][] cbcells) {
        CBCell cell = new CBCell(StringUtils.EMPTY);
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
            if (isTargetSort && sortDimension != null && ComparatorUtils.equals(sortDimension.getSortTarget(), StringUtils.EMPTY)) {
                cbox.setSortType(sortDimension.getSortType());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
        }
        cell.setBoxElement(cbox);
        cbcells[cell.getColumn()][cell.getRow()] = cell;
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
            createCellWhileNoSumColumnLength(maxColumnLength, tempCol, isSortTitle, isTargetSort, sortDimension, cbcells);
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
                    if (isTargetSort && sortDimension != null && ComparatorUtils.equals(sortDimension.getSortTarget(), sumCol.getValue())) {
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

    static private void createCellForNodeExpander(String name, int row, int tempCol, int rowSpan, int colSpan,
                                                  int columnLength, ArrayList<String> currentIndex, BIDimension[] colColumn,
                                                  int dimensionIndex, NodeExpander expander, BIComplexExecutData rowData,
                                                  Node tempNode, CBCell[][] cbcells) {
        CBCell cell = new CBCell(name);
        currentIndex.add(name == null ? StringUtils.EMPTY : name);
        cell.setRow(row);
        cell.setColumn(tempCol);
        cell.setColumnSpan(colSpan);
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
    }

    static int dealWithNodeExpander(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] colColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys,
                                    final ArrayList<String> indexList, int total, boolean isCross, IntList deepList,
                                    boolean isTargetSort, BIDimension sortDimension, TableWidget widget, int tempCol, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
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
            if (rd.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(name) != null) {
                name = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(name).longValue()));
            }
            NodeExpander childEx = expander.getChildExpander(name);
            int colSpan = (sumColumn.length == 0 || !chartSetting.showColTotal()) ? tempNode.getTotalLength(childEx) : tempNode.getTotalLengthWithSummary(childEx);
            if (isCross) {
                colSpan = colSpan * Math.max(sumColumn.length, 1);
            }
            int rowSpan = rowData.getColumnRowSpan(row, columnLength);
            if (childEx == null) {
                rowSpan = rowData.getNoneChildSpan(row, columnLength);
            }
            createCellForNodeExpander(name, row, tempCol, rowSpan, colSpan,
                    columnLength, currentIndex, colColumn, dimensionIndex, expander, rowData, tempNode, cbcells);
            IntList list = null;
            try {
                list = (IntList) deepList.clone();
                list.add(i);
            } catch (CloneNotSupportedException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            dealWithNode(tempNode, expander.getChildExpander(name), cbcells, row + rowSpan, tempCol, colColumn, sumColumn, keys, currentIndex, total - 1, isCross, list, isTargetSort, sortDimension, widget, rowData, chartSetting);
            tempCol += colSpan;
        }
        return tempCol;
    }

    static private void addCellToCellList(BITarget[] sumColumn, int p, DetailChartSetting chartSetting, CBCell cell, int row,
                                          int tempCol, BIComplexExecutData columnData, int columnLength, int total, BIDimension[] colColumn,
                                          boolean isSortTitle, TableWidget widget, Node node, CBCell[][] cbcells) {
        BIAbstractTarget target = (BIAbstractTarget) sumColumn[p];
        String dimensionName = target.getText();
        String dId = target.getValue();
        String levelAndUnit = ExecutorUtils.formatLevelAndUnit(chartSetting.getNumberLevelByTargetId(dId), chartSetting.getUnitByTargetId(dId));
        if (!ComparatorUtils.equals(levelAndUnit, StringUtils.EMPTY)) {
            dimensionName = dimensionName + "(" + levelAndUnit + ")";
        }
        cell = new CBCell(Inter.getLocText("BI-Summary_Values") + ":" + dimensionName);
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
    }

    static void dealWithNodeExpanderIsCross(Node node, CBCell[][] cbcells, int row, int column, BIDimension[] colColumn, BITarget[] sumColumn, TargetGettingKey[] keys, int total, TableWidget widget, int tempCol, BIComplexExecutData columnData, DetailChartSetting chartSetting) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        boolean isSortTitle = row == 0;
        for (int p = 0; p < sumColumn.length; p++) {
            addCellToCellList(sumColumn, p, chartSetting, cell, row,
                    tempCol, columnData, columnLength, total, colColumn,
                    isSortTitle, widget, node, cbcells);
            for (int i = 0, len = keys.length; i < len; i++) {
                int numLevel = chartSetting.getNumberLevelByTargetId(keys[i].getTargetName());
                Object v = node.getSummaryValue(keys[i]);
                v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
                Style dataStyle = BITableStyle.getInstance().getYTotalCellStyle(v, total, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT));
                cell = ExecutorUtils.createCell(v, columnData.getMaxArrayLength() + i, 1, tempCol + p, 1, dataStyle);
                List<CBCell> cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                CBBoxElement cbox = new CBBoxElement(cellList);//TODO CBBoxElement需要整合减少内存
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
    }

    static private void addCellToCBCell(TargetGettingKey[] keys, DetailChartSetting chartSetting, Node node, CBCell cell, BIComplexExecutData columnData,
                                        int tempCol, int total, List<CBCell> cellList, CBBoxElement cbox, BITarget[] sumColumn, BIDimension[] colColumn,
                                        CBCell[][] cbcells) {
        for (int i = 0, len = keys.length; i < len; i++) {
            int numLevel = chartSetting.getNumberLevelByTargetId(keys[i].getTargetName());
            Object v = node.getSummaryValue(keys[i]);
            v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
            cell = new CBCell(v);
            cell.setRow(columnData.getMaxArrayLength() + i);
            cell.setColumn(tempCol);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getXTotalCellStyle(v, total, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT)));
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

    static void dealWithNodeExpanderIsCrossNo(Node node, CBCell[][] cbcells, int row, int column, BIDimension[] colColumn,
                                              BITarget[] sumColumn, TargetGettingKey[] keys,
                                              int total, TableWidget widget, int tempCol, BIComplexExecutData columnData, DetailChartSetting chartSetting) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        cell = new CBCell(Inter.getLocText("BI-Summary_Values"));
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
        addCellToCBCell(keys, chartSetting, node, cell, columnData, tempCol, total, cellList, cbox, sumColumn, colColumn, cbcells);
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
                             final ArrayList<String> indexList, int total, boolean isCross, IntList deepList, boolean isTargetSort, BIDimension sortDimension, TableWidget widget, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int columnLength = colColumn.length;
        Node tempNode = null;
        int tempCol = column;
        CBCell cell = null;
        boolean isSortTitle = row == 0;
        if (isLastDimension(row, rowData)) {
            dealWithNodeNoChild(node, cbcells, row, column, colColumn, sumColumn, keys, widget, tempCol, rowData, chartSetting);
            if (isCross) {
                dealWithNodeIsCross(node, cbcells, row, column, colColumn, sumColumn, keys, widget, tempCol, rowData);
            }
            return;
        }
        //收缩着:
        if (expander == null) {
            dealWithNodeNoExpander(node, cbcells, row, column, colColumn, sumColumn, keys, widget, tempCol, rowData, chartSetting);
            if (isCross) {
                dealWithNodeNoExpanderIsCross(node, cbcells, row, column, colColumn, sumColumn, keys, isTargetSort, sortDimension, widget, tempCol, rowData);
            }
            return;
        }
        //展开情况
        tempCol = dealWithNodeExpander(node, expander, cbcells, row, column, colColumn, sumColumn, keys, indexList, total, isCross, deepList, isTargetSort, sortDimension, widget, tempCol, rowData, chartSetting);
        if (isCross) {
            boolean nodeInfo = node.needSummary() || (node.getChildLength() == 0);
            if (chartSetting.showColTotal() && nodeInfo) {
                dealWithNodeExpanderIsCross(node, cbcells, row, column, colColumn, sumColumn, keys, total, widget, tempCol, rowData, chartSetting);
            }
        } else {
            if (chartSetting.showColTotal() && node.needSummary()) {
                dealWithNodeExpanderIsCrossNo(node, cbcells, row, column, colColumn, sumColumn, keys, total, widget, tempCol, rowData, chartSetting);
            }
        }
    }

    private static boolean isLastDimension(int row, BIComplexExecutData rowData) {
        return row - rowData.getMaxArrayLength() == 0;
    }

    private static int dealWithNodeNoChildren(Node node, CBCell[][] cbcells, int row, int column,
                                              BIDimension[] colColumn,
                                              BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempCol, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int columnLength = colColumn.length;
        CBCell cell = null;
        int maxColumnLen = rowData.getMaxArrayLength();
        int index = row - maxColumnLen;
        if (index == 0) {
            for (int i = 0, len = keys.length; i < len; i++) {
                Object v = node.getSummaryValue(keys[i]);
                v = ExecutorUtils.formatExtremeSumValue(v, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()));
                cell = new CBCell(v);
                cell.setRow(maxColumnLen + i);
                cell.setColumn(tempCol);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()) == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT));
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
                                            BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempCol, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        Node tempNode = null;
        CBCell cell = null;
        int columnLengh = colColumn.length;
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            int colSpan = (sumColumn.length == 0 || !chartSetting.showColTotal()) ? tempNode.getTotalLength() : tempNode.getTotalLengthWithSummary();
            int dimensionIndex = rowData.getDimensionIndexFromRow(row, columnLengh);
            BIDimension rd = colColumn[dimensionIndex];
            String text = rd.toString(tempNode.getData());
            if (rd.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(text) != null) {
                text = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(text).longValue()));
            }
            cell = new CBCell(text);
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
            dealWithNode(tempNode, cbcells, row + rowSpan, tempCol, colColumn, sumColumn, keys, total - 1, rowData, chartSetting);
            tempCol += colSpan;
        }
        return tempCol;
    }

    private static void prepare4dealWithNodeChildrenLenDY1(BIDimension[] colColumn, int row, int tempCol, BIComplexExecutData rowData,
                                                           int total, Node node, CBCell[][] cbcells) {
        int columnLength = colColumn.length;
        CBCell cell = new CBCell(Inter.getLocText("BI-Summary_Values"));
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
    }

    private static int dealWithNodeChildrenLenDY1(Node node, CBCell[][] cbcells, int row, int column,
                                                  BIDimension[] colColumn,
                                                  BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempCol, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        prepare4dealWithNodeChildrenLenDY1(colColumn, row, tempCol, rowData, total, node, cbcells);
        for (int i = 0, len = keys.length; i < len; i++) {
            int numLevel = chartSetting.getNumberLevelByTargetId(keys[i].getTargetName());
            Object v = node.getSummaryValue(keys[i]);
            v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
            CBCell cell = new CBCell(v);
            cell.setRow(rowData.getMaxArrayLength() + i);
            cell.setColumn(tempCol);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getXTotalCellStyle(v, total, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT)));
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
                                    BITarget[] sumColumn, TargetGettingKey[] keys, int total, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int columnLength = colColumn.length;
        Node tempNode = null;
        int tempCol = column;
        CBCell cell = null;
        if (isLastDimension(row, rowData)) {
            tempCol = dealWithNodeNoChildren(node, cbcells, row, column, colColumn, sumColumn, keys, total, tempCol, rowData, chartSetting);
            return;
        }
        tempCol = dealWithNodeChildren(node, cbcells, row, column, colColumn, sumColumn, keys, total, tempCol, rowData, chartSetting);
        if (node.getChildLength() > 1) {
            tempCol = dealWithNodeChildrenLenDY1(node, cbcells, row, column, colColumn, sumColumn, keys, total, tempCol, rowData, chartSetting);
        }
    }

    private static String trunToIndexString(ArrayList<String> currentIndex) {
        return new JSONArray(currentIndex).toString();
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
        Node tree = cubeIndexLoader.loadPageGroup(true, widget, createTarget4Calculate(), usedDimensions, allDimensions, allSumTarget, calpage, widget.isRealData(), session, expander.getXExpander());
        if (tree == null) {
            tree = new Node(null, null);
        }
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }

    private CBCell[][] createCBCells(NewCrossRoot tree, int rowLength, int colLength, int summaryLength) {
        int collen = tree.getTop().getTotalLengthWithSummary();
        CBCell[][] cbcells = new CBCell[collen + Math.min(tree.getTop().getChildLength(), 1) + widget.isOrder()][rowLength];
        boolean isTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(colDimension);
        for (int i = 0; i < colLength; i++) {
            CBCell cell = new CBCell(((BIAbstractTargetAndDimension) colDimension[i]).getText());
            cell.setColumn(0);
            cell.setRow(i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(i));
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
        }
        for (int i = 0; i < summaryLength; i++) {
            CBCell cell = new CBCell(usedSumTarget[i].getText());
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
        return cbcells;
    }

    /**
     * 创建cell
     *
     * @return cell数组
     * @throws NoneAccessablePrivilegeException
     */
    @Override
    public CBCell[][] createCellElement() throws Exception {
        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int len = usedSumTarget.length;
        Map<String, TargetGettingKey> targetsMap = new HashMap<String, TargetGettingKey>();
        TargetGettingKey[] usedSumTargetKeys = new TargetGettingKey[len];
        for (int i = 0; i < len; i++) {
            usedSumTargetKeys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
            targetsMap.put(usedSumTarget[i].getValue(), usedSumTargetKeys[i]);
        }
        int calpage = paging.getOperator();

        NewCrossRoot tree = CubeIndexLoader.getInstance(session.getUserId()).loadPageCrossGroup(createTarget4Calculate(), new BIDimension[0], colDimension, allSumTarget, calpage, widget.useRealData(), session, expander, widget);

        clearNullSummary(tree.getLeft(), usedSumTargetKeys);
        clearNullSummary(tree.getTop(), usedSumTargetKeys);

        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        if (tree == null) {
            return new CBCell[0][0];
        }
        int colLength = colDimension.length;
        int summaryLength = usedSumTarget.length;
        int rowLength = colLength + summaryLength;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
        }
        CBCell[][] cbcells = createCBCells(tree, rowLength, colLength, summaryLength);
        if (ExecutorCommonUtils.isAllPage(paging.getOperator())) {
            dealWithNode(tree.getTop(), cbcells, 0, 1, colDimension, usedSumTarget, keys, colDimension.length - 1, new BIComplexExecutData(colDimension), widget.getChartSetting());
        } else {
            dealWithNode(tree.getTop(), expander.getXExpander(), cbcells, 0, 1, colDimension, usedSumTarget, keys, new ArrayList<String>(), colDimension.length - 1, false, new IntList(), false, null, widget, new BIComplexExecutData(colDimension), widget.getChartSetting());
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