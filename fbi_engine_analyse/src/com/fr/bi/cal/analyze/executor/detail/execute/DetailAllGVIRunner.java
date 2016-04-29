package com.fr.bi.cal.analyze.executor.detail.execute;

import com.fr.bi.cal.analyze.executor.TableRowTraversal;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.data.db.BIRowValue;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.TraversalAction;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.collection.CollectionKey;
import com.fr.bi.util.BIConfUtils;

import java.util.*;

/**
 * Created by GUY on 2015/4/21.
 */
public class DetailAllGVIRunner extends AbstractGVIRunner {

    public DetailAllGVIRunner(GroupValueIndex gvi, BIDetailWidget widget, ICubeDataLoader loader, long userId) {
        super(gvi, widget, loader, userId);
    }

    @Override
    public void Traversal(TableRowTraversal action) {
        final DetailParas paras = new DetailParas(widget, gvi, loader);
        final TreeSet<BIRowValue> set = new TreeSet<BIRowValue>(new DetailSortCompactor());
        gvi.Traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int[] rowIndexs) {
                for (int j = 0; j < rowIndexs.length; j++) {
                    int rowIndex = rowIndexs[j];
                    Map<String, Object> values = new HashMap<String, Object>();
                    Iterator<BIDetailTarget> itT = paras.getNoneCalculateList().iterator();
                    while (itT.hasNext()) {
                        BIDetailTarget dimension = itT.next();

                        List<BITableSourceRelation> relations = BIConfUtils.convertToMD5RelationFromSimpleRelation(dimension.getRelationList(target, biUser.getUserId()), biUser);

                        long row = ((ConnectionRowGetter) (paras.getRowMap().get(new CollectionKey<BITableSourceRelation>(relations))))
                                .getConnectedRow(rowIndex);
                        values.put(dimension.getValue(), dimension.createDetailValue(
                                row, values, loader, biUser.getUserId()));
                    }

                    HashSet<String> calledTargets = new HashSet<String>();
                    executeUntilCallOver(paras, values, calledTargets);
                    Object[] ob = new Object[viewDimension.length];
                    for (int i = 0; i < viewDimension.length; i++) {
                        ob[i] = viewDimension[i].createShowValue(values.get(viewDimension[i].getValue()));
                    }
                    set.add(new BIRowValue(rowIndex, ob));
                }
            }
        });
        actionPerform(action, set);
    }

    private void actionPerform(TableRowTraversal action, TreeSet<BIRowValue> set) {
        Iterator<BIRowValue> it = set.iterator();
        int index = -1;
        while (it.hasNext()) {
            index++;
            BIRowValue value = it.next();
            value.setRow(index);
            if (action.actionPerformed(value)) {
                break;
            }
        }
    }
}