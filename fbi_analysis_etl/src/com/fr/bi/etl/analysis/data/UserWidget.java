package com.fr.bi.etl.analysis.data;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.result.ComplexAllExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.detail.DetailExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.imp.DetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.imp.SummaryWidget;
import com.fr.bi.cal.analyze.report.report.widget.imp.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.cluster.utils.ClusterLockObject;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.stable.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2016/4/12.
 */
public class UserWidget implements Serializable {

    private static final long serialVersionUID = 2202469219214676310L;
    private int maxRow = Integer.MAX_VALUE;
    @BICoreField
    private BIWidget widget;
    @BICoreField
    private long userId;
    @BIIgnoreField
    private ClusterLockObject lock = new ClusterLockObject();

    @BIIgnoreField
    private transient UserSession session;
    @BIIgnoreField
    private /*transient*/ Map<Integer, List> tempValue = new ConcurrentHashMap<Integer, List>();

    public UserWidget(BIWidget widget, long userId) {
        this.widget = widget;
        this.userId = userId;
        this.session = new UserSession();
    }


    public List<List> createData(int start, int end) {
        end = Math.min(end, maxRow);
        if (!contains(start, end)) {
            synchronized (lock) {
                if (!contains(start, end)) {
                    if (widget.getType() == WidgetType.DETAIL) {
                        createDetailData(start, end);
                    } else {
                        createTableData(end);
                    }
                }
            }
        }
        end = Math.min(end, maxRow);
        return getDate(start, end);
    }



    private List<List> getDate(int start, int end) {
        List<List> values = new ArrayList<List>();
        for (int i = start; i < end; i++) {
            if (!tempValue.containsKey(i)) {
                break;
            }
            values.add(tempValue.get(i));
        }
        return values;
    }

    private boolean contains(int start, int end) {
        for (int i = start; i < end; i++) {
            if (!tempValue.containsKey(i)) {
                return false;
            }
        }
        return true;
    }

    private int getPageSize() {
        return ((SummaryWidget) widget).getMaxRow();
    }

    private void createTableData(int end) {
        List<List> v = new ArrayList<List>();
        int rowCount = tempValue.size();
        while (rowCount < end) {
            v.clear();
            if (rowCount == 0) {
                v = getNextValue(session, BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH);
            } else {
                if (((TableWidget) widget).hasVerticalNextPage()) {
                    v = getNextValue(session, BIReportConstant.TABLE_PAGE_OPERATOR.ROW_NEXT);
                }
            }
            for (int i = 0; i < v.size(); i++) {
                tempValue.put(rowCount, v.get(i));
                rowCount++;
            }

            if (!((TableWidget) widget).hasVerticalNextPage()) {
                maxRow = rowCount;
                break;
            }
        }
    }

    private List<List> getNextValue(UserSession session, int op) {
        List<List> values = new ArrayList<List>();
        try {
            ((TableWidget) widget).setComplexExpander(new ComplexAllExpander());
            ((TableWidget) widget).setOperator(op);
            Node n = (Node) ((TableWidget) widget).getExecutor(session).getCubeNode();
            if (n.getFirstChild() == null) {
                return values;
            }
            while (n.getFirstChild() != null) {
                n = n.getFirstChild();
            }
            BIDimension[] rows = ((TableWidget) widget).getViewDimensions();
            while (n != null) {
                List rowList = new ArrayList();
                Node temp = n;
                for (TargetGettingKey key : ((TableWidget) widget).getTargetsKey()) {
                    rowList.add(temp.getSummaryValue(key));
                }
                int i = rows.length;
                while (temp.getParent() != null) {
                    Object data = temp.getData();
                    BIDimension dim = rows[--i];
                    Object v = dim.getValueByType(data);
                    rowList.add(0, v);
                    temp = temp.getParent();
                }
                if (!rowList.isEmpty()) {
                    values.add(rowList);
                }
                n = n.getSibling();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return values;
    }

    private Iterator<List<List>> createTableDataIterator() {
        return new Iterator<List<List>>() {
            List<List> v = getNextValue(session, BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH);
            @Override
            public void remove() {

            }

            @Override
            public boolean hasNext() {
                return v != null;
            }

            @Override
            public List<List> next() {
                List<List> temp = v;
                if (((TableWidget) widget).hasVerticalNextPage()){
                    v = getNextValue(session, BIReportConstant.TABLE_PAGE_OPERATOR.ROW_NEXT);
                } else {
                    v = null;
                }
                return temp;
            }
        };
    }

    private void createDetailData(int start, int end) {
        int step = end - start;
        Paging paging = PagingFactory.createPaging(step);
        paging.setPageSize(step);
        int page = start / step;
        paging.setCurrentPage(page + 1);
        DetailExecutor exe = new DetailExecutor((DetailWidget) widget, paging, session);
        List<List> data = exe.getData();
        int row = page * step;
        for (int i = 0; i < data.size(); i++) {
            tempValue.put(i + row, data.get(i));
        }
        maxRow = (int) paging.getTotalSize();
        paging.setCurrentPage(page + 2);
        exe = new DetailExecutor((DetailWidget) widget, paging, session);
        data = exe.getData();
        row = (page + 1) * step;
        for (int i = 0; i < data.size(); i++) {
            tempValue.put(i + row, data.get(i));
        }
    }

    private static final int STEP = 1000;
    private Iterator<List<List>> createDetailDataIterator() {
        return new Iterator<List<List>>() {
            int page = 0;
            int step = STEP;
            List<List> data = get();
            @Override
            public void remove() {

            }

            private List<List> get(){
                return getDetailData(step * page, step *(++page));
            }

            @Override
            public boolean hasNext() {
                return data != null && !data.isEmpty();
            }

            @Override
            public List<List> next() {
                List<List> temp = data;
                data = get();
                return temp;
            }
        };
    }

    private List<List> getDetailData(int start, int end) {
        int step = end - start;
        Paging paging = PagingFactory.createPaging(step);
        paging.setPageSize(step);
        int page = start / step;
        paging.setCurrentPage(page + 1);
        DetailExecutor exe = new DetailExecutor((DetailWidget) widget, paging, session);
        List<List> data =  exe.getData();
        if (paging.getTotalSize() > start){
            return data;
        }
        return null;
    }


    public void clear() {
        synchronized (lock) {
            maxRow = Integer.MAX_VALUE;
            tempValue.clear();
            session.clearCachedMaps();
            session = new UserSession();
        }

    }

    public Iterator<List<List>> getDataIterator() {
        if (widget.getType() == WidgetType.DETAIL) {
            return createDetailDataIterator();
        } else {
            return createTableDataIterator();
        }
    }

    private class UserSession extends BISession {

        private static final long serialVersionUID = -6365288173994213351L;

        public UserSession() {
            super(StringUtils.EMPTY, new BIWeblet(), userId);
        }
    }
}
