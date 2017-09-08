package com.fr.bi.cal.analyze.cal.result;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.report.report.widget.DetailWidget;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.field.target.detailtarget.field.BIEmptyDetailTarget;
import com.fr.bi.report.result.BIDetailCell;
import com.fr.bi.report.result.BIDetailTableResult;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.connection.DirectTableConnectionFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.TraversalAction;
import com.fr.bi.stable.structure.array.IntList;
import com.fr.bi.stable.structure.array.IntListFactory;
import com.fr.bi.stable.structure.collection.CollectionKey;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.util.BIConfUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by andrew_asa on 2017/8/3.
 */
public class DetailTableResult implements BIDetailTableResult {

    /**
     * 行数
     */
    private int rowSize;

    /**
     * 列数
     */
    private int columnSize;

    /**
     * 拿到第几行
     */
    private int index;

    /**
     * ------以下是业务相关变量------
     */
    private DetailWidget widget;

    private GroupValueIndex gvi;

    private ICubeDataLoader loader;

    private long userId;

    private BusinessTable target;

    private BIDetailTarget[] viewDimension = new BIDetailTarget[0];

    private ArrayList<BIDetailTarget> noneCalculateList = new ArrayList<BIDetailTarget>();

    private ArrayList<BIDetailTarget> calculateList = new ArrayList<BIDetailTarget>();

    private ConnectionRowGetter[] connectionRowGetters;

    private Set<Integer> usedDimensionIndexes;

    private IntList rowList;

    private Map rowMap = new HashMap();

    public DetailTableResult(DetailWidget widget, GroupValueIndex gvi, ICubeDataLoader loader, long userId) {

        this.widget = widget;
        this.loader = loader;
        this.gvi = gvi;
        this.userId = userId;
        init();
    }

    @Override
    public boolean hasNext() {

        return index < rowSize;
    }

    @Override
    public List<BIDetailCell> next() {
        // 获取一行的数据
        List<BIDetailCell> ret = new ArrayList<BIDetailCell>();
        int rowIndex = rowList.get(index);
        Map<String, Object> values = new HashMap<String, Object>();
        Iterator<BIDetailTarget> itT = noneCalculateList.iterator();
        while (itT.hasNext()) {
            BIDetailTarget dimension = itT.next();

            List<BITableSourceRelation> relations = BIConfUtils.convert2TableSourceRelation(dimension.getRelationList(target, userId));

            long row = ((ConnectionRowGetter) (rowMap.get(new CollectionKey<BITableSourceRelation>(relations))))
                    .getConnectedRow(rowIndex);
            values.put(dimension.getValue(), dimension.createDetailValue(
                    row, values, loader, userId));
        }
        executeUntilCallOver(values);
        // 获取一行的值
        Object[] ob = new Object[viewDimension.length];
        for (int i = 0; i < viewDimension.length; i++) {
            ob[i] = values.get(viewDimension[i].getValue());
        }
        // 筛选出使用中的值
        for (int i = 0; i < ob.length; i++) {
            if (usedDimensionIndexes.contains(i)) {
                BIDetailCell cell = new DetailCell();
                cell.setData(BICollectionUtils.cubeValueToWebDisplay(viewDimension[i].createShowValue(ob[i])));
                ret.add(cell);
            }
        }
        index++;
        return ret;
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }

    @Override
    public int rowSize() {

        return rowSize;
    }

    @Override
    public int columnSize() {

        return columnSize;
    }

    public void setColSize(int rowSize) {

        this.rowSize = rowSize;
    }

    public void setColumnSize(int columnSize) {

        this.columnSize = columnSize;
    }

    @Override
    public ResultType getResultType() {

        return ResultType.DETAIL;
    }

    private void init() {

        target = widget.getTargetDimension();
        rowList = IntListFactory.createIntList();
        if (gvi != null) {
            rowSize = gvi.getRowsCountWithData();
            gvi.Traversal(new TraversalAction() {

                @Override
                public void actionPerformed(int[] data) {

                    for (int i : data) {
                        rowList.add(i);
                    }
                }
            });
            index = 0;
        }
        // 初始化飞计算列表和计算列表
        viewDimension = widget.getViewDimensions();
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
        connectionRowGetters = new ConnectionRowGetter[noneCalculateList.size()];
        for (int i = 0; i < noneCalculateList.size(); i++) {
            List<BITableSourceRelation> relations = BIConfUtils.convert2TableSourceRelation(noneCalculateList.get(i).getRelationList(target, userId));
            CollectionKey<BITableSourceRelation> reKey = new CollectionKey<BITableSourceRelation>(relations);
            connectionRowGetters[i] = DirectTableConnectionFactory.createConnectionRow(relations, loader);
            if (rowMap.get(reKey) == null) {
                rowMap.put(reKey, connectionRowGetters[i]);
            }
        }
        usedDimensionIndexes = getUsedDimensionIndexes();
        columnSize = usedDimensionIndexes.size();
    }

    protected void executeUntilCallOver(Map<String, Object> values) {

        HashSet<String> calledTargets = new HashSet<String>();
        while (true) {
            Iterator<BIDetailTarget> it = calculateList.iterator();
            boolean called = false;
            while (it.hasNext()) {
                BIDetailTarget calTarget = it.next();
                if (!calledTargets.contains(calTarget.getValue()) && calTarget.isReady4Calculate(values)) {
                    values.put(calTarget.getValue(), calTarget.createDetailValue(null, values, loader, userId));
                    calledTargets.add(calTarget.getValue());
                    called = true;
                }
            }
            if (!called) {
                break;
            }
        }
    }

    private Set<Integer> getUsedDimensionIndexes() {

        String[] array = widget.getView();
        Set<Integer> usedDimensionIndexes = new HashSet<Integer>();
        for (int i = 0; i < array.length; i++) {
            BIDetailTarget dimension = BITravalUtils.getTargetByName(array[i], viewDimension);
            if (dimension.isUsed()) {
                usedDimensionIndexes.add(i);
            }
        }
        return usedDimensionIndexes;
    }
}
