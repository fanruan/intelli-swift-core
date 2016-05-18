package com.fr.bi.etl.analysis.data;

import com.fr.bi.cal.analyze.cal.result.ComplexAllExpalder;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.detail.DetailExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONException;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/4/12.
 */
public class UserWidget {

    private BIWidget widget;

    private long userId;

    public UserWidget(BIWidget widget, long userId) {
        this.widget = widget;
        this.userId = userId;
    }


    public List<List> createData(int start, int end) {
        if (widget.getType() == BIReportConstant.WIDGET.DETAIL){
            return createDetailData(start, end);
        } else {
            return createTableData(start, end); 
        }
    }

    private List<List> createTableData(int start, int end) {
        UserSession session = new UserSession();
        List<List> values = new ArrayList<List>();
        List<List> v = getNextValue(session, BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH);
        int rowCount = 0;
        for (int i = 0; i < v.size(); i++){
            if (rowCount >= start && rowCount < end){
                values.add(v.get(i));
            }
            rowCount ++;
        }
        while (v.size() == PagingFactory.PAGE_PER_GROUP_20 && rowCount < end){
            v = getNextValue(session, BIReportConstant.TABLE_PAGE_OPERATOR.COLUMN_NEXT);
            for (int i = 0; i < v.size(); i++){
                if (rowCount >= start && rowCount < end){
                    values.add(v.get(i));
                }
                rowCount ++;
            }
        }
        return values;
    }

    private List<List> getNextValue(UserSession session, int op){
        List<List> values = new ArrayList<List>();
        try {
            ((TableWidget)widget).setComplexExpander(new ComplexAllExpalder());
            ((TableWidget)widget).setOperator(op);
            Node n = (Node) ((TableWidget)widget).getExecutor(session).getCubeNode();
            while (n.getFirstChild() != null) {
                n = n.getFirstChild();
            }
            BIDimension[] rows = ((TableWidget) widget).getViewDimensions();
            while (n != null) {
                List rowList = new ArrayList();
                Node temp = n;
                for (TargetGettingKey key : ((TableWidget) widget).getTargetsKey()){
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
                values.add(rowList);
                n = n.getSibling();
            }
        } catch (JSONException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return values;
    }

    private List<List> createDetailData(int start, int end) {
        List<List> values = new ArrayList<List>();
        int step = end - start;
        Paging paging = PagingFactory.createPaging(step);
        int page = start / step;
        paging.setCurrentPage(page);
        DetailExecutor exe = new DetailExecutor((BIDetailWidget)widget, paging, new UserSession());
        List<List> data =  exe.getData();
        for (int i = start - page * step; i < data.size(); i++){
            List d = data.get(i);
            List l = new ArrayList();
            for (int k = 0; k < d.size(); k++){
                l.add(d.get(k));
            }
            values.add(l);
        }
        paging.setCurrentPage(page + 1);
        int leftCount = end - values.size();
        exe = new DetailExecutor((BIDetailWidget)widget, paging, new UserSession());
        data =  exe.getData();
        for (int i =0; i < data.size() && i < leftCount; i++){
            List d = data.get(i);
            List l = new ArrayList();
            for (int k = 0; k < d.size(); k++){
                l.add(d.get(k));
            }
            values.add(l);
        }
        return values;
    }

    private class UserSession extends BISession{

        public UserSession() {
            super(StringUtils.EMPTY, new BIWeblet(), userId);
        }
    }
}
