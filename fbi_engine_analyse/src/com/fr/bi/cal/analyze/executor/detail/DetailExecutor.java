package com.fr.bi.cal.analyze.executor.detail;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.result.DetailCell;
import com.fr.bi.cal.analyze.cal.result.DetailTableResult;
import com.fr.bi.cal.analyze.executor.GVIRunner;
import com.fr.bi.cal.analyze.executor.TableRowTraversal;
import com.fr.bi.cal.analyze.executor.detail.execute.DetailAllGVIRunner;
import com.fr.bi.cal.analyze.executor.detail.execute.DetailPartGVIRunner;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.DetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.field.target.detailtarget.field.BIDateDetailTarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.result.BIDetailCell;
import com.fr.bi.report.result.BIDetailTableResult;
import com.fr.bi.stable.data.db.BIRowValue;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO 分页机制待优化
 * 不需要每次都计算 索引然后再取位置取值
 *
 * @author Daniel
 */
public class DetailExecutor extends AbstractDetailExecutor {
    private static BILogger LOGGER = BILoggerFactory.getLogger(DetailExecutor.class);
    private final static int MEMORY_LIMIT = 3000;

    public DetailExecutor(DetailWidget widget,
                          //前台传过来的从1开始;
                          Paging paging,
                          BISession session) {

        super(widget, paging, session);

    }

    @Override
    public JSONObject getCubeNode() throws Exception {

        long start = System.currentTimeMillis();
        GroupValueIndex gvi = createDetailViewGvi();
        paging.setTotalSize(gvi.getRowsCountWithData());
        final JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("value", ja);
        //返回前台的时候再去掉不使用的字段
        final BIDetailTarget[] dimensions = widget.getViewDimensions();
        final Set<Integer> usedDimensionIndexes = getUsedDimensionIndexes();
        TableRowTraversal action = new TableRowTraversal() {

            @Override
            public boolean actionPerformed(BIRowValue row) {

                Boolean x = checkPage(row);
                if (x != null) {
                    return x;
                }
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < row.getValues().length; i++) {
                    if (usedDimensionIndexes.contains(i)) {
                        jsonArray.put(BICollectionUtils.cubeValueToWebDisplay(dimensions[i].createShowValue(row.getValues()[i])));
                    }
                }
                ja.put(jsonArray);
                return false;
            }
        };
        travel(action, gvi);
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return jo;
    }

    private Set<Integer> getUsedDimensionIndexes() {

        final BIDetailTarget[] dimensions = widget.getViewDimensions();
        String[] array = widget.getView();
        final Set<Integer> usedDimensionIndexes = new HashSet<Integer>();
        for (int i = 0; i < array.length; i++) {
            BIDetailTarget dimension = BITravalUtils.getTargetByName(array[i], dimensions);
            if (dimension.isUsed()) {
                usedDimensionIndexes.add(i);
            }
        }
        return usedDimensionIndexes;
    }

    protected GroupValueIndex getLinkFilter(GroupValueIndex gvi) {

        try {
            if (widget.getLinkWidget() != null && widget.getLinkWidget() instanceof TableWidget) {
                // 判断两个表格的基础表是否相同
                BusinessTable widgetTargetTable = widget.getTargetDimension();
                TableWidget linkWidget = widget.getLinkWidget();
                Map<String, JSONArray> clicked = widget.getClicked();
                BISummaryTarget summaryTarget = null;
                String[] ids = clicked.keySet().toArray(new String[]{});
                for (String linkTarget : ids) {
                    try {
                        summaryTarget = linkWidget.getBITargetByID(linkTarget);
                        break;
                    } catch (Exception e) {
                        BILoggerFactory.getLogger(TableWidget.class).warn("Target id " + linkTarget + " is absent in linked widget " + linkWidget.getWidgetName());
                    }
                }
                if (summaryTarget != null) {
                    BusinessTable linkTargetTable = summaryTarget.createTableKey();
                    // 基础表相同的时候才有联动的意义
                    if (widgetTargetTable.equals(linkTargetTable)) {
                        // 其联动组件的父联动gvi
                        GroupValueIndex pLinkGvi = linkWidget.createLinkedFilterGVI(widgetTargetTable, session);
                        // 其联动组件的点击过滤gvi
                        GroupValueIndex linkGvi = linkWidget.getLinkFilter(linkWidget, widgetTargetTable, clicked, session);
                        gvi = GVIUtils.AND(gvi, GVIUtils.AND(pLinkGvi, linkGvi));
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return gvi;
    }

    public List<List> getData() {

        if (target == null) {
            return new ArrayList<List>();
        }
        GroupValueIndex gvi = createDetailViewGvi();
        paging.setTotalSize(gvi.getRowsCountWithData());
        final List<List> data = new ArrayList<List>();
        TableRowTraversal action = new TableRowTraversal() {

            @Override
            public boolean actionPerformed(BIRowValue row) {

                Boolean x = checkPage(row);
                if (x != null) {
                    return x;
                }
                List list = new ArrayList();
                for (int i = 0; i < row.getValues().length; i++) {
                    if (viewDimension[i].isUsed()) {
                        if (viewDimension[i] instanceof BIDateDetailTarget) {
                            if (BICollectionUtils.isCubeNullKey(row.getValues()[i])) {
                                list.add(null);
                            } else {
                                list.add(Long.valueOf(String.valueOf(row.getValues()[i])));
                            }
                        } else {
                            list.add(row.getValues()[i]);
                        }
                    }
                }
                data.add(list);
                return false;
            }
        };
        travel(action, gvi);
        return data;
    }

    private Boolean checkPage(BIRowValue row) {

        if (paging.getStartRow() > row.getRow()) {
            return false;
        }
        if (paging.getEndRow() <= row.getRow()) {
            return true;
        }
        return null;
    }

    private void travel(TableRowTraversal action, GroupValueIndex gvi) {

        if (gvi.getRowsCountWithData() < MEMORY_LIMIT) {
            GVIRunner runner = new DetailAllGVIRunner(gvi, widget, getLoader(), userId);
            runner.Traversal(action);
        } else {
            GVIRunner runner = new DetailPartGVIRunner(gvi, session, widget, paging, getLoader());
            runner.Traversal(action);
        }
    }

    @Override
    public JSONObject createJSONObject() throws Exception {

        return getCubeNode();
    }



    /**
     * 返回导出excel的数据结构
     *
     * @return
     * @throws Exception
     */
    public BIDetailTableResult getResult() {

        long start = System.currentTimeMillis();
        GroupValueIndex gvi = createDetailViewGvi();
        BIDetailTableResult r = null;


        final List<List<BIDetailCell>> result = new ArrayList<List<BIDetailCell>>();
        //返回前台的时候再去掉不使用的字段
        final BIDetailTarget[] dimensions = widget.getViewDimensions();
        final Set<Integer> usedDimensionIndexes = getUsedDimensionIndexes();
        TableRowTraversal action = new TableRowTraversal() {

            @Override
            public boolean actionPerformed(BIRowValue row) {

                List<BIDetailCell> rowData = new ArrayList<BIDetailCell>();
                for (int i = 0; i < row.getValues().length; i++) {
                    if (usedDimensionIndexes.contains(i)) {
                        BIDetailCell cell = new DetailCell();
                        cell.setData(BICollectionUtils.cubeValueToWebDisplay(dimensions[i].createShowValue(row.getValues()[i])));
                        rowData.add(cell);
                    }
                }
                result.add(rowData);
                return false;
            }
        };
        GVIRunner runner = new DetailAllGVIRunner(gvi, widget, getLoader(), userId);
        runner.Traversal(action);
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        r = new DetailTableResult(result);
        return r;
    }
}