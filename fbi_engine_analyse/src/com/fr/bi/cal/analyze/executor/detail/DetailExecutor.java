package com.fr.bi.cal.analyze.executor.detail;

import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.GVIRunner;
import com.fr.bi.cal.analyze.executor.TableRowTraversal;
import com.fr.bi.cal.analyze.executor.detail.execute.DetailAllGVIRunner;
import com.fr.bi.cal.analyze.executor.detail.execute.DetailPartGVIRunner;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIRowValue;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * TODO 分页机制待优化
 * 不需要每次都计算 索引然后再取位置取值
 *
 * @author Daniel
 */
public class DetailExecutor extends AbstractDetailExecutor {

    private final static int MEMORY_LIMIT = 3000;

    private HashSet<Table> tables = new HashSet<Table>();

    public DetailExecutor(BIDetailWidget widget,
                          //前台传过来的从1开始;
                          Paging paging,
                          BISession session) {
        super(widget, paging, session);

    }


    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {

        GroupValueIndex gvi = createDetailViewGvi();
        final CBCell[][] cells = createCells(gvi);
        if (cells == null) {
            return new CBCell[][]{new CBCell[0]};
        }

        TableRowTraversal action = new TableRowTraversal() {
            @Override
            public boolean actionPerformed(BIRowValue row) {
                Boolean x = checkPage(row);
                if (x != null) {
                    return x;
                }
                //row + 1 ? 不然覆盖掉了列名
                fillOneLine(cells, (int) row.getRow() + 1, row.getValues());
                return false;
            }
        };
        travel(action, gvi);
        if (widget.isOrder() == 1) {
            createAllNumberCellElement(cells, paging.getCurrentPage());
        }
        return cells;
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return null;
    }

    @Override
    public JSONObject getCubeNode() throws JSONException {
        GroupValueIndex gvi = createDetailViewGvi();
        paging.setTotalSize(gvi.getRowsCountWithData());
        final JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("value", ja);
        final BIDetailTarget[] dimensions = widget.getViewDimensions();
        TableRowTraversal action = new TableRowTraversal() {
            @Override
            public boolean actionPerformed(BIRowValue row) {
                Boolean x = checkPage(row);
                if (x != null) {
                    return x;
                }
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < row.getValues().length; i++) {
                    jsonArray.put(dimensions[i].createShowValue(row.getValues()[i]));
                }
                ja.put(jsonArray);
                return false;
            }
        };
        travel(action, gvi);
        return jo;
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
                for (int i = 0; i < row.getValues().length; i++){
                    if (viewDimension[i].isUsed()){
                        list.add(row.getValues()[i]);
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
    public JSONObject createJSONObject() throws JSONException {
        return getCubeNode();
    }
}