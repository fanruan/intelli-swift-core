package com.fr.bi.cal.analyze.executor.detail.execute;

import com.fr.bi.cal.analyze.executor.detail.key.DetailSortKey;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.field.target.detailtarget.field.BIEmptyDetailTarget;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.connection.DirectTableConnectionFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.collection.CollectionKey;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.util.BIConfUtils;

import java.util.*;

/**
 * Created by GUY on 2015/4/21.
 */
public class DetailParas {

    private ICubeDataLoader loader;
    private transient Table target;
    private transient BIDetailTarget[] viewDimension;
    private transient String[] sortTargets;
    protected BIUser biUser;
    private transient DetailSortKey sortKey;

    private ICubeColumnIndexReader[] getters;
    private ArrayList<BIDetailTarget> noneCalculateList = new ArrayList<BIDetailTarget>();
    private ArrayList<BIDetailTarget> calculateList = new ArrayList<BIDetailTarget>();
    private Map rowMap = new HashMap();
    private boolean[] asc;
    private GroupValueIndex gvi;
    public DetailParas(BIDetailWidget widget, GroupValueIndex gvi, ICubeDataLoader loader) {
        this.loader = loader;

        this.target = widget.getTargetDimension();
        this.viewDimension = widget.getViewDimensions();
        this.sortTargets = widget.getSortTargets();
        this.gvi = gvi;
        biUser = new BIUser(loader.getUserId());
        init();
    }
    public ArrayList<BIDetailTarget> getNoneCalculateList() {
        return noneCalculateList;
    }

    public DetailSortKey getSortKey() {
        return sortKey;
    }

    public ArrayList<BIDetailTarget> getCalculateList() {
        return calculateList;
    }

    public Map getRowMap() {
        return rowMap;
    }


    public boolean[] getAsc() {
        return asc;
    }

    public ICubeColumnIndexReader[] getCubeIndexGetters() {
        return getters;
    }

    private void initCalList() {

        for (int i = 0; i < viewDimension.length; i++) {
            if (viewDimension[i] == null) {
                viewDimension[i] = new BIEmptyDetailTarget("--");
            }
            if (viewDimension[i].isCalculateTarget()) {
                calculateList.add(viewDimension[i]);
            } else {
                noneCalculateList.add(viewDimension[i]);
            }
        }
    }

    private void init() {
        initCalList();
        for (int i = 0; i < noneCalculateList.size(); i++) {

            List<BITableSourceRelation> relations = BIConfUtils.convertToMD5RelationFromSimpleRelation(noneCalculateList.get(i).getRelationList(target, biUser.getUserId()),biUser);
            CollectionKey<BITableSourceRelation> reKey = new CollectionKey<BITableSourceRelation>(relations);
            if (rowMap.get(reKey) == null) {
                rowMap.put(reKey, DirectTableConnectionFactory.createConnectionRow(relations, loader));
            }
        }
        List<BIDetailTarget> sortList = getTargetSortMap();
        sortKey = new DetailSortKey(gvi, target, sortList);
        asc = new boolean[sortList.size()];
        getters = new ICubeColumnIndexReader[sortList.size()];
        for (int i = 0; i < sortList.size(); i ++){
            getters[i] = sortList.get(i).createGroupValueMapGetter(target, loader, biUser.getUserId());
            asc[i] = sortList.get(i).getSort().getSortType() == BIReportConstant.SORT.ASC ? true : false;
            i++;
        }
    }

    /**
     * 获取sortTargets对应的排序
     *
     * @return
     */
    private List<BIDetailTarget> getTargetSortMap() {
        List<BIDetailTarget> sortList = new ArrayList<BIDetailTarget>();
        for (int i = 0; i < sortTargets.length; i++) {
            BIDetailTarget target = BITravalUtils.getTargetByName(sortTargets[i], viewDimension);
            if (target != null && target.getSort().getSortType() != BIReportConstant.SORT.NONE) {
                sortList.add(target);
            }
        }
        return sortList;
    }
}