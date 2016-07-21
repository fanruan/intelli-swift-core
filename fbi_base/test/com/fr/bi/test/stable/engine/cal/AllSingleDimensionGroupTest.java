/**
 *
 */
package com.fr.bi.test.stable.engine.cal;

import com.finebi.cube.api.*;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.cal.AllSingleDimensionGroup;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.bi.stable.engine.index.TableIndexAdapter;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.structure.object.CubeValueEntry;
import com.fr.bi.stable.utils.BIServerUtils;
import com.fr.stable.core.UUID;
import edu.emory.mathcs.backport.java.util.Arrays;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Daniel
 */
public class AllSingleDimensionGroupTest extends TestCase {

    public void testSingleDimensionGroup() {
        TI ti = new TI();
        BIKey[] key = new BIKey[12];
        Arrays.fill(key, new IndexKey(""));
        CalDealer dealer = new CalDealer();
        AllSingleDimensionGroup.run(ti.getAllShowIndex(), ti, new IndexKey(""), BIServerUtils.createDimensonDealer(key, dealer));
        assertEquals(dealer.resultMap, ti.resultMap);
    }

    private class CalDealer implements ResultDealer {

        private TreeMap<String, Double> resultMap = new TreeMap<String, Double>();

        @Override
        public void dealWith(final ICubeTableService ti, GroupValueIndex currentIndex) {
            final StringBuffer sb = new StringBuffer();
            final ICubeColumnDetailGetter getter = ti.getColumnDetailReader(new IndexKey(""));
            currentIndex.BrokenableTraversal(new BrokenTraversalAction() {

                @Override
                public boolean actionPerformed(int row) {
                    sb.append((String) getter.getValue(row));
                    return true;
                }
            });
            String key = sb.toString();
            double v = ti.getSUMValue(currentIndex, new IndexKey(""));
            resultMap.put(key, v);
        }


    }


    private class TI extends TableIndexAdapter {
        private int rowCount = 1000000;
        private int groupLength = 1500;
        private String[] values;
        private double[] vv;
        private TreeMap<String, GroupValueIndex> map
                = new TreeMap<String, GroupValueIndex>(ComparatorFacotry.CHINESE_ASC);
        private TreeMap<String, Double> resultMap = new TreeMap<String, Double>();

        TI() {
            String[] group = new String[groupLength];
            for (int i = 0; i < group.length; i++) {
                group[i] = UUID.randomUUID().toString();
            }
            values = new String[rowCount];
            vv = new double[rowCount];
            Map<String, IntList> treeMap = new TreeMap<String, IntList>(ComparatorFacotry.CHINESE_ASC);
            for (int i = 0; i < values.length; i++) {
                values[i] = group[(int) (Math.random() * 15)];
                vv[i] = Math.random() * 10000;
                IntList list = treeMap.get(values[i]);
                if (list == null) {
                    list = new IntList();
                    treeMap.put(values[i], list);
                }
                list.add(i);
            }
            Iterator<Entry<String, IntList>> iter = treeMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, IntList> entry = iter.next();
                map.put(entry.getKey(), GVIFactory.createGroupValueIndexBySimpleIndex(entry.getValue()));
                IntList v = entry.getValue();
                double result = 0;
                Integer[] array = v.toArray();
                for (int i = 0; i < array.length; i++) {
                    result += vv[array[i]];
                }
                resultMap.put(entry.getKey(), result);
                GroupValueIndex gvi = map.get(entry.getKey());
                assertEquals(result, getSUMValue(gvi, new IndexKey("")));
            }
        }
        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public GroupValueIndex getAllShowIndex() {
            return GVIFactory.createAllShowIndexGVI(rowCount);
        }

        @Override
        public ICubeValueEntryGetter getValueEntryGetter(BIKey key, List<BITableSourceRelation> relationList) {
            return new ICubeValueEntryGetter() {
                @Override
                public GroupValueIndex getIndexByRow(int row) {
                    return map.get(values[row]);
                }

                @Override
                public CubeValueEntry getEntryByRow(int row) {
                    return null;
                }

                @Override
                public int getPositionOfGroupByRow(int row) {
                    return 0;
                }

                @Override
                public int getGroupSize() {
                    return 0;
                }
            };
        }



        @Override
        public double getSUMValue(GroupValueIndex gvi, BIKey summaryIndex) {
            SUM s = new SUM();
            gvi.Traversal(s);
            return s.get();
        }

        @Override
        public ICubeColumnDetailGetter getColumnDetailReader(BIKey key) {
            return new ICubeColumnDetailGetter() {
                @Override
                public Object getValue(int row) {
                    return values[row];
                }

                @Override
                public PrimitiveType getPrimitiveType() {
                    return null;
                }

                @Override
                public PrimitiveDetailGetter createPrimitiveDetailGetter() {
                    return null;
                }
            };
        }

        private class SUM implements SingleRowTraversalAction {
            double v = 0;

            @Override
            public void actionPerformed(int data) {
                v += vv[data];
            }

            public double get() {
                return v;
            }

        }

    }

}