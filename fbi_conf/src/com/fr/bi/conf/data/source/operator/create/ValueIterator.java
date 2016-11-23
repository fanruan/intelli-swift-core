package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.FinalInt;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.SortTool;
import com.fr.bi.stable.engine.SortToolUtils;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.structure.collection.CubeIndexGetterWithNullValue;
import com.fr.bi.stable.structure.object.CubeValueEntry;
import com.fr.stable.collections.array.IntArray;

import java.util.*;

/**
 * Created by 小灰灰 on 2016/11/23.
 */
class ValueIterator {
    private ICubeValueEntryGetter[] getters;
    private ICubeColumnIndexReader[] mapGetters;
    private ValuesAndGVI next;
    private GroupValueIndex allShowIndex;
    private Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators;
    private ValuesAndGVI[] valuesAndGVIs;
    private ICubeTableService ti;
    private BIKey[] keys;
    private IGroup[] groups;

    public ValueIterator(ICubeTableService ti, BIKey[] keys) {
       this(ti, keys, null);
    }

    public ValueIterator(ICubeTableService ti, BIKey[] keys, IGroup[] groups) {
        this.ti = ti;
        this.keys = keys;
        allShowIndex = ti.getAllShowIndex();
        getters = new ICubeValueEntryGetter[keys.length];
        iterators = new Iterator[keys.length];
        valuesAndGVIs = new ValuesAndGVI[keys.length + 1];
        if (groups == null || groups.length != keys.length){
            this.groups = new IGroup[keys.length];
        } else {
            this.groups = groups;
            this.mapGetters = new ICubeColumnIndexReader[keys.length];
        }
        valuesAndGVIs[0] = new ValuesAndGVI(new Object[0], allShowIndex);
        for (int i = 0; i < keys.length; i++) {
            getters[i] = ti.getValueEntryGetter(this.keys[i], new ArrayList<BITableSourceRelation>());
        }
        iterators[0] = getIter(0, allShowIndex);
        move(0);
    }

    public boolean hasNext() {
        return next != null;
    }

    public ValuesAndGVI next() {
        ValuesAndGVI temp = next;
        moveNext();
        return temp;
    }

    private void moveNext() {
        for (int i = iterators.length - 1; i >= 0; i--){
            if (iterators[i].hasNext()){
                move(i);
                return;
            }
        }
        next = null;
    }
    private void move(int index){
        for (int i = index; i < iterators.length; i ++){
            if (i != index){
                iterators[i] = getIter(i, valuesAndGVIs[i].gvi);
            }
            if (!iterators[i].hasNext() && i != 0){
                move(i - 1);
                return;
            }
            Map.Entry<Object, GroupValueIndex> entry = iterators[i].next();
            Object[] values = new Object[i + 1];
            System.arraycopy(valuesAndGVIs[i].values, 0, values, 0, values.length - 1);
            values[values.length - 1] = entry.getKey();
            valuesAndGVIs[i + 1] = new ValuesAndGVI(values, entry.getValue().AND(valuesAndGVIs[i].gvi));
        }
        next = valuesAndGVIs[valuesAndGVIs.length - 1];
    }

    private Iterator getIter(int index, GroupValueIndex gvi) {
        if (isCustomGroup(groups[index])){
            return createMapIterator(index);
        }
        ICubeValueEntryGetter getter = getters[index];
        if (GVIUtils.isAllShowRoaringGroupValueIndex(gvi)){
            return getAllShowIterator(getter);
        }
        SortTool tool = SortToolUtils.getSortTool(getter.getGroupSize(), gvi.getRowsCountWithData());
        switch (tool) {
            case INT_ARRAY:
                return getArraySortIterator(getter, gvi);
            case RE_SORT:
                return getTreeMapReSortIterator(getter, gvi);
            case DIRECT:
                return getOneKeyIterator(getter, gvi);
            case TREE_MAP:
                return getTreeMapSortIterator(getter, gvi);
            default:
                return getArraySortIterator(getter, gvi);
        }
    }

    private Iterator createMapIterator(int index) {
        if (mapGetters[index] == null){
            ICubeColumnIndexReader baseGroupMap = ti.loadGroup(keys[index], new ArrayList<BITableSourceRelation>());
            GroupValueIndex nullIndex =  ti.getNullGroupValueIndex(keys[index]);
            if (!nullIndex.isAllEmpty()){
                baseGroupMap =  new CubeIndexGetterWithNullValue(baseGroupMap, nullIndex);
            }
            mapGetters[index] = groups[index].createGroupedMap(baseGroupMap);
        }
        return mapGetters[index].iterator();
    }

    private boolean isCustomGroup(IGroup group) {
        if (group == null){
            return false;
        }
        int groupType = group.getType();
        return groupType == BIReportConstant.GROUP.CUSTOM_GROUP
                || groupType == BIReportConstant.GROUP.CUSTOM_NUMBER_GROUP
                || groupType == BIReportConstant.GROUP.AUTO_GROUP;
    }

    private Iterator getArraySortIterator(final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
        final int[] groupIndex = new int[getter.getGroupSize()];
        Arrays.fill(groupIndex, NIOConstant.INTEGER.NULL_VALUE);
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                    groupIndex[groupRow] = groupRow;
                }
            }
        });
        return new Iterator() {

            private int index = 0;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
            @Override
            public boolean hasNext() {
                while (index < groupIndex.length && groupIndex[index] == NIOConstant.INTEGER.NULL_VALUE) {
                    index++;
                }
                return index < groupIndex.length;
            }

            @Override
            public Object next() {
                final CubeValueEntry gve = getter.getEntryByGroupRow(index);
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return gve.getT();
                    }

                    @Override
                    public Object getValue() {
                        return gve.getGvi();
                    }

                    @Override
                    public Object setValue(Object value) {
                        return null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        return false;
                    }

                    @Override
                    public int hashCode() {
                        return 0;
                    }
                };
                index++;
                return entry;
            }
        };
    }

    private Iterator getTreeMapReSortIterator(final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
        final TreeMap<Integer, IntArray> map = new TreeMap<Integer, IntArray>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                    IntArray array = map.get(groupRow);
                    if (array == null){
                        array = new IntArray();
                        map.put(groupRow, array);
                    }
                    array.add(row);
                }
            }
        });
        final Iterator<Map.Entry<Integer, IntArray>> it = map.entrySet().iterator();
        return getTreeMapReSortIterator(getter, it);
    }

    private Iterator getTreeMapReSortIterator(final ICubeValueEntryGetter getter, final Iterator<Map.Entry<Integer, IntArray>> it) {
        return new Iterator() {
            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Object next() {
                final Map.Entry<Integer, IntArray> fEntry = it.next();
                final Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(fEntry.getKey());
                    }

                    @Override
                    public Object getValue() {
                        return GVIFactory.createGroupValueIndexBySimpleIndex(fEntry.getValue());
                    }

                    @Override
                    public Object setValue(Object value) {
                        return null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        return false;
                    }

                    @Override
                    public int hashCode() {
                        return 0;
                    }
                };
                return entry;
            }
        };
    }


    private Iterator getAllShowIterator(final ICubeValueEntryGetter getter) {
        return new Iterator() {
            private int index = 0;
            private int groupSize = getter.getGroupSize();
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                return index < groupSize;
            }

            @Override
            public Object next() {
                final CubeValueEntry gve = getter.getEntryByGroupRow(index);
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return gve.getT();
                    }

                    @Override
                    public Object getValue() {
                        return gve.getGvi();
                    }

                    @Override
                    public Object setValue(Object value) {
                        return null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        return false;
                    }

                    @Override
                    public int hashCode() {
                        return 0;
                    }
                };
                index++;
                return entry;
            }
        };
    }


    private Iterator getOneKeyIterator(final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
        final FinalInt i = new FinalInt();
        i.value = NIOConstant.INTEGER.NULL_VALUE;
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                i.value = row;
            }
        });
        return new Iterator() {
            int groupRow = getter.getPositionOfGroupByRow(i.value);
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                return groupRow != NIOConstant.INTEGER.NULL_VALUE;
            }

            @Override
            public Object next() {
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        Object value = getter.getGroupValue(groupRow);
                        groupRow = NIOConstant.INTEGER.NULL_VALUE;
                        return value;
                    }

                    @Override
                    public Object getValue() {
                        return GVIFactory.createGroupValueIndexBySimpleIndex(i.value);
                    }

                    @Override
                    public Object setValue(Object value) {
                        return null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        return false;
                    }

                    @Override
                    public int hashCode() {
                        return 0;
                    }
                };
                return entry;
            }
        };
    }

    private Iterator getTreeMapSortIterator(final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
        final TreeSet<Integer> set = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                    set.add(groupRow);
                }
            }
        });
        final Iterator<Integer> it = set.iterator();
        return new Iterator() {
            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Object next() {
                final CubeValueEntry gve = getter.getEntryByGroupRow(it.next());
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return gve.getT();
                    }

                    @Override
                    public Object getValue() {
                        return gve.getGvi();
                    }

                    @Override
                    public Object setValue(Object value) {
                        return null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        return false;
                    }

                    @Override
                    public int hashCode() {
                        return 0;
                    }
                };
                return entry;
            }
        };
    }

}
