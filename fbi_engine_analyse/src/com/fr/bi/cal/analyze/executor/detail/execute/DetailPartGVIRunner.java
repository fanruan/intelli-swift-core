package com.fr.bi.cal.analyze.executor.detail.execute;

import com.fr.bi.base.FinalBoolean;
import com.fr.bi.cal.analyze.executor.TableRowTraversal;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.data.db.BIRowValue;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.collection.CollectionKey;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;

import java.util.*;

/**
 * Created by GUY on 2015/4/21.
 */
public class DetailPartGVIRunner extends AbstractGVIRunner {

    private DetailParas paras;
    private DetailSortGviIndex index;
    private Paging paging;
    private BISession session;
    //索引起始行数
    private transient int row;
    //临时变量，当前分组已经遍历的行数
    private transient int currentIndex = -1;


    public DetailPartGVIRunner(GroupValueIndex gvi,BISession session, BIDetailWidget widget, Paging paging, ICubeDataLoader loader) {
        super(gvi, widget, loader, session.getUserId());
        this.paging = paging;
        this.session = session;
        paras = new DetailParas(widget, gvi, loader);
        index = new DetailSortGviIndex(session.getDetailLastValue(paras.getSortKey(), paging.getCurrentPage()), paras.getCubeIndexGetters(), paras.getAsc());
        row = paging.getStartRow() - session.getDetailLastIndex(paras.getSortKey(), paging.getCurrentPage());;
        row--;
    }

    @Override
    public void Traversal(TableRowTraversal action) {
        while (true) {
            GroupValueIndex sortGvi = index.createSortedGVI(gvi, paging.getEndRow() - row);
            if (sortGvi == null) {
                break;
            }
            if (row >= paging.getEndRow()){
                break;
            }
            if (index.isAllSort()){
                if (setSortedCells(sortGvi, action)){
                    break;
                }
            } else {
                if (setNoneSortedCells(sortGvi, action)){
                    break;
                }
            }
            currentIndex = -1;
        }
    }

    private boolean setSortedCells(GroupValueIndex sortedGVI, final TableRowTraversal action) {
        final FinalBoolean ended = new FinalBoolean();
        ended.flag = false;
        sortedGVI.BrokenableTraversal(new BrokenTraversalAction() {

            @Override
            public boolean actionPerformed(int rowIndex) {
                checkAndSetSession();
                Boolean x = checkPage();
                if (x != null){
                    return x;
                }
                Object[] ob = getRowValue(rowIndex);
                boolean end =  action.actionPerformed(new BIRowValue(row, ob));
                if (end){
                    ended.flag = true;
                }
                return end;
            }
        });
        return ended.flag;
    }

    private Object[] getRowValue(int rowIndex) {
        Map resMap = new HashMap();
        Iterator iter = paras.getRowMap().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ConnectionRowGetter c = (ConnectionRowGetter) entry.getValue();
            resMap.put(entry.getKey(), new Long(c.getConnectedRow(rowIndex)));
        }
        Map<String, Object> values = new HashMap<String, Object>();
        Iterator<BIDetailTarget> iterator = paras.getNoneCalculateList().iterator();
        while (iterator.hasNext()) {
            BIDetailTarget dimension = iterator.next();
            CollectionKey<BITableSourceRelation> key = new CollectionKey<BITableSourceRelation>(BIConfUtils.convertToMD5RelationFromSimpleRelation(dimension.getRelationList(target, biUser.getUserId()),biUser));
            Long row = (Long) resMap.get(key);
            values.put(dimension.getValue(), dimension.createDetailValue(row, values, loader, biUser.getUserId()));
        }
        HashSet<String> calledTargets = new HashSet<String>();
        executeUntilCallOver(paras, values, calledTargets);
        Object[] ob = new Object[viewDimension.length];
        for (int i = 0; i < viewDimension.length; i++) {
            ob[i] = values.get(viewDimension[i].getValue());
        }
        return ob;
    }

    private boolean setNoneSortedCells(GroupValueIndex sortedGVI, TableRowTraversal action) {
        final TreeSet<BIRowValue> set = new TreeSet<BIRowValue>(new DetailSortCompactor());
        sortedGVI.BrokenableTraversal(new BrokenTraversalAction() {
            @Override
            public boolean actionPerformed(int rowIndex) {
                checkAndSetSession();
                Boolean x = checkPage();
                if (x != null){
                    return x;
                }
                set.add(new BIRowValue(row, getRowValue(rowIndex)));
                return false;
            }
        });
        Iterator<BIRowValue> it1 = set.iterator();
        while (it1.hasNext()) {
            BIRowValue value = it1.next();
            if (action.actionPerformed(value)) {
                return true;
            }
        }
        return false;
    }

    private Boolean checkPage() {
        if (paging.getStartRow() > row) {
            return false;
        }
        if (paging.getEndRow() <= row){
            return true;
        }
        return null;
    }

    private void checkAndSetSession(){
        currentIndex++;
        row++;
        if ((row) % paging.getPageSize() == 0) {
            try {
                Object[] values = index.getValue().clone();
                int page = row / paging.getPageSize() + 1;
                session.setDetailIndexMap(paras.getSortKey(), page , currentIndex);
                session.setDetailValueMap(paras.getSortKey(), page, values);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage() ,e);
            }
        }
    }

}