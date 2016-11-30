package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeValueEntryGetter;
import com.fr.bi.base.FinalInt;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.stable.collections.array.IntArray;

import java.util.*;

/**
 * Created by 小灰灰 on 2016/11/28.
 */
public class DimensionIteratorCreator {
    /**
     * 根据父节点的过滤条件算出子节点的结构
     * @param getter 子节点所在维度的ICubeValueEntryGetter
     * @param filterGVI 父节点的索引
     * @param asc 升序为true
     * @return 迭代器
     */
    public static Iterator<Map.Entry<Object, GroupValueIndex>> createValueMapIterator(ICubeValueEntryGetter getter, GroupValueIndex filterGVI, boolean asc){
        if (GVIUtils.isAllShowRoaringGroupValueIndex(filterGVI)){
            return getAllShowIterator(getter, asc);
        }
        SortTool tool = SortToolUtils.getSortTool(getter.getGroupSize(), filterGVI.getRowsCountWithData());
        switch (tool) {
            case INT_ARRAY:
                return getArraySortIterator(getter, filterGVI, asc);
            case TREE_MAP_RE_SORT:
                return getTreeMapReSortIterator(getter, filterGVI, asc);
            case INT_ARRAY_RE_SORT:
                return getArrayReSortIterator(getter, filterGVI, asc);
            case DIRECT:
                return getOneKeyIterator(getter, filterGVI);
            case TREE_MAP:
                return getTreeMapSortIterator(getter, filterGVI, asc);
            default:
                return getArraySortIterator(getter, filterGVI, asc);
        }
    }

    private static Iterator getAllShowIterator(final ICubeValueEntryGetter getter, boolean asc) {
        return asc ? getAllShowASCIterator(getter) : getAllShowDESCIterator(getter);
    }

    private static Iterator getAllShowASCIterator(final ICubeValueEntryGetter getter) {
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
                final int groupIndex = index;
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(groupIndex);
                    }

                    @Override
                    public Object getValue() {
                        return getter.getIndexByGroupRow(groupIndex);
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

    private static Iterator getAllShowDESCIterator(final ICubeValueEntryGetter getter) {
        return new Iterator() {
            private int index = getter.getGroupSize() - 1;
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                return index >= 0;
            }

            @Override
            public Object next() {
                final int groupIndex = index;
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(groupIndex);
                    }

                    @Override
                    public Object getValue() {
                        return getter.getIndexByGroupRow(groupIndex);
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
                index--;
                return entry;
            }
        };
    }


    private static Iterator getArraySortIterator(final ICubeValueEntryGetter getter, GroupValueIndex filterGVI, boolean asc) {
        final int[] groupIndex = new int[getter.getGroupSize()];
        Arrays.fill(groupIndex, NIOConstant.INTEGER.NULL_VALUE);
        filterGVI.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                    groupIndex[groupRow] = groupRow;
                }
            }
        });
        return asc ? getArraySortASCIterator(getter, groupIndex) : getArraySortDESCIterator(getter, groupIndex);
    }

    private static Iterator getArraySortDESCIterator(final ICubeValueEntryGetter getter, final int[] groupIndex) {
        return new Iterator() {

            private int index = groupIndex.length - 1;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                while (index >= 0 && groupIndex[index] == NIOConstant.INTEGER.NULL_VALUE) {
                    index--;
                }
                return index >= 0;
            }

            @Override
            public Object next() {
                final int groupRow = index;
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(groupRow);
                    }

                    @Override
                    public Object getValue() {
                        return getter.getIndexByGroupRow(groupRow);
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
                index--;
                return entry;
            }
        };
    }

    private static Iterator getArraySortASCIterator(final ICubeValueEntryGetter getter, final int[] groupIndex) {
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
                final int groupRow = index;
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(groupRow);
                    }

                    @Override
                    public Object getValue() {
                        return getter.getIndexByGroupRow(groupRow);
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

    private static Iterator getOneKeyIterator(final ICubeValueEntryGetter getter, GroupValueIndex filterGVI) {
        final FinalInt i = new FinalInt();
        i.value = NIOConstant.INTEGER.NULL_VALUE;
        filterGVI.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                i.value = row;
            }
        });
        return getOneKeyIterator(getter, i.value);
    }

    private static Iterator getOneKeyIterator(final ICubeValueEntryGetter getter, final Integer row) {
        return new Iterator() {
            int groupRow = getter.getPositionOfGroupByRow(row);
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
                        return GVIFactory.createGroupValueIndexBySimpleIndex(row);
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

    private static Iterator getTreeMapSortIterator(final ICubeValueEntryGetter getter, GroupValueIndex filterGVI, boolean asc) {
        final TreeSet<Integer> set = asc ? new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC) : new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.DESC);
        filterGVI.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                    set.add(groupRow);
                }
            }
        });
        final Iterator<Integer> it = set.iterator();
        return getTreeMapSortIterator(getter, it);
    }

    private static Iterator getTreeMapSortIterator(final ICubeValueEntryGetter getter, final Iterator<Integer> it) {
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
                final int groupRow = it.next();
                Map.Entry entry = new Map.Entry() {
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(groupRow);
                    }

                    @Override
                    public Object getValue() {
                        return getter.getIndexByGroupRow(groupRow);
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

    private static Iterator getTreeMapReSortIterator(final ICubeValueEntryGetter getter, GroupValueIndex filterGVI, boolean asc) {
        final TreeMap<Integer, IntArray> map = asc ? new TreeMap<Integer, IntArray>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC) : new TreeMap<Integer, IntArray>(BIBaseConstant.COMPARATOR.COMPARABLE.DESC);
        filterGVI.Traversal(new SingleRowTraversalAction() {
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

    private static Iterator getTreeMapReSortIterator(final ICubeValueEntryGetter getter, final Iterator<Map.Entry<Integer, IntArray>> it) {
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

    private static Iterator getArrayReSortIterator(final ICubeValueEntryGetter getter, GroupValueIndex filterGVI, boolean asc) {
        final IntArray[] groupArray = new IntArray[getter.getGroupSize()];
        filterGVI.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                    if (groupArray[groupRow] == null){
                        groupArray[groupRow] = new IntArray();
                    }
                    groupArray[groupRow].add(row);
                }
            }
        });
        return asc ? getArrayReSortASCIterator(getter, groupArray) : getArrayReSortDESCIterator(getter, groupArray);
    }

    private static Iterator getArrayReSortDESCIterator(final ICubeValueEntryGetter getter, final IntArray[] groupArray) {
        return new Iterator() {

            private int index = groupArray.length - 1;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                while (index >= 0 && groupArray[index] == null) {
                    index--;
                }
                return index >= 0;
            }

            @Override
            public Object next() {
                Map.Entry entry = new Map.Entry() {
                    int group = index;
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(group);
                    }

                    @Override
                    public Object getValue() {
                        return GVIFactory.createGroupValueIndexBySimpleIndex(groupArray[group]);
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
                index--;
                return entry;
            }
        };
    }

    private static Iterator getArrayReSortASCIterator(final ICubeValueEntryGetter getter, final IntArray[] groupArray) {
        return new Iterator() {

            private int index = 0;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                while (index < groupArray.length && groupArray[index] == null) {
                    index++;
                }
                return index < groupArray.length;
            }

            @Override
            public Object next() {
                Map.Entry entry = new Map.Entry() {
                    int group = index;
                    @Override
                    public Object getKey() {
                        return getter.getGroupValue(group);
                    }

                    @Override
                    public Object getValue() {
                        return GVIFactory.createGroupValueIndexBySimpleIndex(groupArray[group]);
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
}
