package com.fr.swift.query.group.by;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static com.fr.swift.cube.io.IOConstant.NULL_INT;

/**
 * @author 小灰灰
 * @date 2016/11/28
 */
public class GroupBy {
    private static final GroupByResult EMPTY = new GroupByResult() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public GroupByEntry next() {
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    /**
     * 根据父节点的过滤条件算出子节点的结构
     *
     * @param column            一列
     * @param filteredTraversal 过滤后的行
     * @param asc               升序为true
     * @return 迭代器
     */
    public static GroupByResult createGroupByResult(Column column, RowTraversal filteredTraversal, boolean asc) {
        return createGroupByResult(column, filteredTraversal, 0, asc);
    }

    public static GroupByResult createGroupByResult(Column column, RowTraversal filteredTraversal, Object start, boolean asc) {
        int index = asc ? column.getDictionaryEncodedColumn().getIndex(start) : column.getDictionaryEncodedColumn().size() - column.getDictionaryEncodedColumn().getIndex(start) - 1;
        return createGroupByResult(column, filteredTraversal, index, asc);
    }

    /**
     * 根据父节点的过滤条件算出子节点的结构
     *
     * @param column            一列
     * @param filteredTraversal 过滤后的行
     * @param asc               升序为true
     * @param startIndex        偏移量
     * @return 迭代器
     */
    public static GroupByResult createGroupByResult(Column column, RowTraversal filteredTraversal, int startIndex, boolean asc) {
        DictionaryEncodedColumn dictionaryEncodedColumn = column.getDictionaryEncodedColumn();
        BitmapIndexedColumn bitMapColumn = column.getBitmapIndex();
        if (filteredTraversal.isFull()) {
            return getAllShowResult(dictionaryEncodedColumn, bitMapColumn, startIndex, asc);
        }
        SortTool tool = SortToolUtils.getSortTool(dictionaryEncodedColumn.size(), filteredTraversal.getCardinality());
        switch (tool) {
            case INT_ARRAY:
                return getArraySortResult(dictionaryEncodedColumn, bitMapColumn, filteredTraversal, startIndex, asc);
            case TREE_MAP_RESORT:
                return getTreeMapResortResult(dictionaryEncodedColumn, filteredTraversal, startIndex, asc);
            case INT_ARRAY_RESORT:
                return getArrayResortResult(dictionaryEncodedColumn, filteredTraversal, startIndex, asc);
            case DIRECT:
                return getOneKeyResult(dictionaryEncodedColumn, filteredTraversal);
            case EMPTY:
                return EMPTY;
            case TREE_MAP:
                return getTreeMapSortResult(dictionaryEncodedColumn, bitMapColumn, filteredTraversal, startIndex, asc);
            default:
                return getArraySortResult(dictionaryEncodedColumn, bitMapColumn, filteredTraversal, startIndex, asc);
        }
    }

    private static GroupByResult getAllShowResult(DictionaryEncodedColumn dictionaryEncodedColumn, BitmapIndexedColumn bitMapColumn, int startIndex, boolean asc) {
        return asc ? getAllShowAscResult(dictionaryEncodedColumn, bitMapColumn, startIndex) :
                getAllShowDescResult(dictionaryEncodedColumn, bitMapColumn, startIndex);
    }

    private static GroupByResult getAllShowAscResult(final DictionaryEncodedColumn dictionaryEncodedColumn, final BitmapIndexedColumn bitMapColumn, final int startIndex) {
        return new GroupByResult() {
            // 如果null值没有对应的指标，就从1开始
            private int index = startIndex == 0 && bitMapColumn.getBitMapIndex(0).isEmpty() ? 1 : startIndex;
            private int groupSize = dictionaryEncodedColumn.size();

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                return index < groupSize;
            }

            @Override
            public GroupByEntry next() {
                GroupByEntry entry = new AllShowBitMapGroupByEntry(index, bitMapColumn);
                index++;
                return entry;
            }
        };
    }

    private static GroupByResult getAllShowDescResult(final DictionaryEncodedColumn dictionaryEncodedColumn, final BitmapIndexedColumn bitMapColumn, final int startIndex) {
        return new GroupByResult() {
            private int index = dictionaryEncodedColumn.size() - 1 - startIndex;
            // 如果null值没有对应的指标，index减到1结束
            private int endIndex = startIndex == 0 && bitMapColumn.getBitMapIndex(0).isEmpty() ? 1 : 0;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                return index >= endIndex;
            }

            @Override
            public GroupByEntry next() {
                GroupByEntry entry = new AllShowBitMapGroupByEntry(index, bitMapColumn);
                index--;
                return entry;
            }
        };
    }

    private static GroupByResult getArraySortResult(final DictionaryEncodedColumn dictionaryEncodedColumn, BitmapIndexedColumn bitMapColumn, RowTraversal filteredTraversal, int startIndex, boolean asc) {
        //改成boolean小一点,反正这边只是标记一下
        final boolean[] groupIndex = new boolean[dictionaryEncodedColumn.size()];
        filteredTraversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = dictionaryEncodedColumn.getIndexByRow(row);
                if (groupRow != NULL_INT) {
                    groupIndex[groupRow] = true;
                }
            }
        });
        return asc ? getArraySortAscResult(bitMapColumn, groupIndex, startIndex, filteredTraversal) :
                getArraySortDescResult(bitMapColumn, groupIndex, startIndex, filteredTraversal);
    }

    private static GroupByResult getArraySortAscResult(final BitmapIndexedColumn bitMapColumn, final boolean[] groupIndex, final int startIndex, final RowTraversal rowTraversal) {
        return new GroupByResult() {
            private int index = startIndex;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                while (index < groupIndex.length && groupIndex[index] == false) {
                    index++;
                }
                return index < groupIndex.length;
            }

            @Override
            public GroupByEntry next() {
                GroupByEntry entry = new BitMapGroupByEntry(index, bitMapColumn, rowTraversal.toBitMap());
                index++;
                return entry;
            }
        };
    }

    private static GroupByResult getArraySortDescResult(final BitmapIndexedColumn bitMapColumn, final boolean[] groupIndex, final int startIndex, final RowTraversal rowTraversal) {
        return new GroupByResult() {
            private int index = groupIndex.length - 1 - startIndex;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                while (index >= 0 && groupIndex[index] == false) {
                    index--;
                }
                return index >= 0;
            }

            @Override
            public GroupByEntry next() {
                GroupByEntry entry = new BitMapGroupByEntry(index, bitMapColumn, rowTraversal.toBitMap());
                index--;
                return entry;
            }
        };
    }

    private static GroupByResult getOneKeyResult(DictionaryEncodedColumn dictionaryEncodedColumn, RowTraversal filteredTraversal) {
        final AtomicInteger value = new AtomicInteger(NULL_INT);
        filteredTraversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                value.set(row);
            }
        });
        return getOneKeyResult(dictionaryEncodedColumn, value.get());
    }

    private static GroupByResult getOneKeyResult(final DictionaryEncodedColumn dictionaryEncodedColumn, final Integer row) {
        return new GroupByResult() {
            int groupRow = dictionaryEncodedColumn.getIndexByRow(row);

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            @Override
            public boolean hasNext() {
                return groupRow != NULL_INT;
            }

            @Override
            public GroupByEntry next() {
                IntList list = IntListFactory.createHeapIntList(1);
                list.add(row);
                GroupByEntry entry = new IntListGroupByEntry(groupRow, new IntListRowTraversal(list));
                groupRow = NULL_INT;
                return entry;
            }
        };
    }

    private static GroupByResult getTreeMapSortResult(final DictionaryEncodedColumn dictionaryEncodedColumn, BitmapIndexedColumn bitMapColumn, RowTraversal filteredTraversal, final int startIndex, final boolean asc) {
        final TreeSet<Integer> set = new TreeSet<Integer>(asc ? Comparators.<Integer>asc() : Comparators.<Integer>desc());
        filteredTraversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = dictionaryEncodedColumn.getIndexByRow(row);
                if (match(startIndex, asc, groupRow)) {
                    set.add(groupRow);
                }
            }
        });
        final Iterator<Integer> it = set.iterator();
        return getTreeMapSortResult(bitMapColumn, it, filteredTraversal);
    }

    private static GroupByResult getTreeMapSortResult(final BitmapIndexedColumn bitMapColumn, final Iterator<Integer> it, final RowTraversal rowTraversal) {
        return new GroupByResult() {
            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public GroupByEntry next() {
                return new BitMapGroupByEntry(it.next(), bitMapColumn, rowTraversal.toBitMap());
            }
        };
    }

    private static GroupByResult getTreeMapResortResult(final DictionaryEncodedColumn dictionaryEncodedColumn, RowTraversal filteredTraversal, final int startIndex, final boolean asc) {
        final TreeMap<Integer, IntList> map = new TreeMap<Integer, IntList>(asc ? Comparators.<Integer>asc() : Comparators.<Integer>desc());
        filteredTraversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = dictionaryEncodedColumn.getIndexByRow(row);
                if (match(startIndex, asc, groupRow)) {
                    IntList array = map.get(groupRow);
                    if (array == null) {
                        array = IntListFactory.createHeapIntList();
                        map.put(groupRow, array);
                    }
                    array.add(row);
                }
            }
        });
        return getTreeMapResortResult(map);
    }

    private static boolean match(int startIndex, boolean asc, int groupRow) {
        if (groupRow == NULL_INT) {
            return false;
        }
        if (startIndex == 0) {
            return true;
        }
        return asc ? groupRow >= startIndex : groupRow <= startIndex;
    }

    private static GroupByResult getTreeMapResortResult(Map<Integer, IntList> map) {
        return new TreeMapResortResult(map);
    }

    private static GroupByResult getArrayResortResult(final DictionaryEncodedColumn dictionaryEncodedColumn, RowTraversal filteredTraversal, int startIndex, boolean asc) {
        final IntList[] groupArray = new IntList[dictionaryEncodedColumn.size()];
        filteredTraversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = dictionaryEncodedColumn.getIndexByRow(row);
                if (groupRow != NULL_INT) {
                    if (groupArray[groupRow] == null) {
                        groupArray[groupRow] = IntListFactory.createHeapIntList();
                    }
                    groupArray[groupRow].add(row);
                }
            }
        });
        return asc ? getArrayResortAscResult(groupArray, startIndex) : getArrayResortDescResult(groupArray, startIndex);
    }

    private static GroupByResult getArrayResortAscResult(final IntList[] groupArray, final int startIndex) {
        return new GroupByResult() {
            private int index = startIndex;

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
            public GroupByEntry next() {
                GroupByEntry entry = new IntListGroupByEntry(index, new IntListRowTraversal(groupArray[index]));
                index++;
                return entry;
            }
        };
    }

    private static GroupByResult getArrayResortDescResult(final IntList[] groupArray, final int startIndex) {
        return new GroupByResult() {
            private int index = groupArray.length - 1 - startIndex;

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
            public GroupByEntry next() {
                GroupByEntry entry = new IntListGroupByEntry(index, new IntListRowTraversal(groupArray[index]));
                index--;
                return entry;
            }
        };
    }
}
