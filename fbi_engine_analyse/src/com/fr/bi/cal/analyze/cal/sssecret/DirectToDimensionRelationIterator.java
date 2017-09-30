package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.stable.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/12/30.
 */
public class DirectToDimensionRelationIterator implements DimensionIterator {
    private Iterator<Map.Entry<Object, GroupValueIndex>> iterator;
    private ICubeColumnIndexReader dimensionGetter;
    private ICubeColumnIndexReader primaryTableGetter;
    protected DirectToDimensionRelationIterator(Iterator<Map.Entry<Object, GroupValueIndex>> iterator, DimensionCalculator calculator, ICubeDataLoader loader) {
        this.iterator = iterator;
        //默认第一个位置放的是主表
        CubeTableSource primaryTableSource = calculator.getDirectToDimensionRelationList().get(0).getPrimaryTable();
        ICubeFieldSource primaryFieldSource = calculator.getDirectToDimensionRelationList().get(0).getPrimaryField();
        this.dimensionGetter = loader.getTableIndex(calculator.getField().getTableBelongTo().getTableSource()).loadGroup(calculator.createKey());
        this.primaryTableGetter = loader.getTableIndex(primaryTableSource).loadGroup(new IndexKey(primaryFieldSource.getFieldName()), calculator.getDirectToDimensionRelationList());

    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Map.Entry<Object, GroupValueIndex> next() {
        Map.Entry<Object, GroupValueIndex> entry = iterator.next();
        Object keyValue = entry.getKey();
        final GroupValueIndex gvi = entry.getValue();
        final Set keyValueSet = new LinkedHashSet();
       primaryTableGetter.getGroupIndex(new Object[]{keyValue})[0].Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                Object ob = dimensionGetter.getOriginalValue(row);
                if (ob != null){
                    keyValueSet.add(ob);
                } else {
                    keyValueSet.add(StringUtils.EMPTY);
                }
            }
        });
        StringBuilder sb = new StringBuilder();
        Iterator it = keyValueSet.iterator();
        while (it.hasNext()){
            sb.append(it.next());
            sb.append(",");
        }
        final int size = keyValueSet.size();
        if (size > 0){
            sb.deleteCharAt(sb.length() - 1);
        }

        final String finalKeyValueString = sb.toString();
        return new Map.Entry<Object, GroupValueIndex>() {
            @Override
            public Object getKey() {
                return finalKeyValueString;
            }

            @Override
            public GroupValueIndex getValue() {
                            /*
            * BI-10179
            * */
                return size > 0 ? gvi : GVIFactory.createAllEmptyIndexGVI();

            }

            @Override
            public GroupValueIndex setValue(GroupValueIndex value) {
                return null;
            }
        };
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public int getCurrentGroup() {
        return 0;
    }

    @Override
    public boolean canReGainGroupValueIndex(){
        return false;
    }

    @Override
    public boolean isReturnFinalGroupValueIndex() {
        return false;
    }

    @Override
    public GroupValueIndex getGroupValueIndexByGroupIndex(int groupIndex) {
        return null;
    }
}
