package com.fr.bi.cal.analyze.executor.detail;

import com.fr.bi.base.FinalInt;
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
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIRowValue;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * TODO 分页机制待优化
 * 不需要每次都计算 索引然后再取位置取值
 *
 * @author Daniel
 */
public class DetailExecutor extends AbstractDetailExecutor {

    private final static int MEMORY_LIMIT = 3000;

    private final static int EXCEL_ROW_MODE_VALUE = ExportConstants.MAX_ROWS_2007 - 1;

    public DetailExecutor(BIDetailWidget widget,
                          //前台传过来的从1开始;
                          Paging paging,
                          BISession session) {
        super(widget, paging, session);

    }



    public DetailCellIterator createCellIterator4Excel() {
        final GroupValueIndex gvi = createDetailViewGvi();
        int count = gvi.getRowsCountWithData();
        paging.setTotalSize(count);
        final DetailCellIterator iter = new DetailCellIterator(widget.getViewDimensions().length, count + 1);
        new Thread() {
            public void run() {
                try {
                    final FinalInt start = new FinalInt();
                    List<CBCell> cells = createCellTitle(CellConstant.CBCELL.TARGETTITLE_Y);
                    Iterator<CBCell> it = cells.iterator();
                    while(it.hasNext()) {
                        iter.getIteratorByPage(start.value).addCell(it.next());
                    }
                    TableRowTraversal action = new TableRowTraversal() {
                        @Override
                        public boolean actionPerformed(BIRowValue row) {
                            int currentRow = (int) row.getRow() + 1;
                            int newRow = currentRow & EXCEL_ROW_MODE_VALUE;
                            if(newRow == 0) {
                                iter.getIteratorByPage(start.value).finish();
                                start.value++;
                            }
                            //row + 1 ? 不然覆盖掉了列名
                            fillOneLine(iter.getIteratorByPage(start.value), newRow, row.getValues(), currentRow);
                            return false;
                        }
                    };
                    travel(action, gvi);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    iter.finish();
                }
            }
        }.start();



        return iter;
    }


    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        return  null;
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