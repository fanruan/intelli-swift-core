package com.fr.swift.result;

import com.fr.base.FRContext;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.structure.array.IntList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Xiaolei.Liu on 2018/3/6
 */

public class SortSegmentDetailByIndexResultSet extends DetailResultSet {
    private List<Column> columnList;
    private DetailFilter filter;
    private IntList sortIndex;
    private List<SortType> sorts;
    private GroupByResult [] gbr;
    private ImmutableBitMap [] bitmap;


    //已排序的行数
    private int sortedRows;
    //已排序的第几个bitmap
    private int index = 0;
    private int bitmapCount = 1;
    private ArrayList<ImmutableBitMap> sortedDetail = new ArrayList<>();

    public SortSegmentDetailByIndexResultSet(List<Column> columnList, DetailFilter filter, IntList sortIndex, List<SortType> sorts)  {
        this.columnList = columnList;
        this.filter = filter;
        this.sortIndex = sortIndex;
        this.sorts = sorts;
        this.gbr = new GroupByResult[sortIndex.size()];
        this.bitmap = new ImmutableBitMap[sortIndex.size() + 1];
        bitmap[0] = filter.createFilterIndex();
        init();
    }
    private void init() {
        maxRow = filter.createFilterIndex().getCardinality();
        getIndexBitmap(0);
        getSortedData();
    }

    @Override
    public void close() {

    }

    public int getMaxRow() {
        return maxRow;
    }


    @Override
    public Row getRowData(){

        if(bitmapCount > sortedDetail.get(index).getCardinality()) {
            bitmapCount = 1;
            index ++;
        }
        List values = new ArrayList();
        sortedDetail.get(index).breakableTraversal(new BreakTraversalAction() {
            //用来判断是分组中的第几行
            private int count = 1;
            @Override
            public boolean actionPerformed(int row) {
                if(count++ < bitmapCount) {
                    return false;
                }
                for (int i = 0; i < columnList.size(); i++) {
                    Object val = columnList.get(i).getDetailColumn().get(row);
                    if (isNullValue(val) && columnList.get(i).getBitmapIndex().getNullIndex().contains(i)) {
                        continue;
                    }
                    values.add(val);
                }
                return true;
            }
        });
        bitmapCount++;
        return new ListBasedRow(values);
    }


    public void getSortedData()  {
        while(true) {
            ImmutableBitMap filterBitmap = gbr[sortIndex.size() - 1].next().getTraversal().toBitMap();
            sortedRows += filterBitmap.getCardinality();
            sortedDetail.add(filterBitmap);
            if (sortedRows == maxRow) {
                break;
            }
            //下移index游标,计算下一次循环的索引数组
            nextCursor(filterBitmap);
        }
    }

    public void nextCursor(ImmutableBitMap filterBitmap)  {
        int index = 0;
        for (int i = 0; i < sortIndex.size(); i++) {
            bitmap[i] = bitmap [i].getAndNot(filterBitmap);
            if ((bitmap [i].isEmpty())) {
                break;
            }
            index = i;
        }
        getIndexBitmap(index);
    }

    public void getIndexBitmap(int start) {
        for (int i = start; i < sortIndex.size(); i++) {
            gbr[i] = GroupBy.createGroupByResult(columnList.get(sortIndex.get(i)), bitmap[i], sorts.get(sortIndex.get(i)) == SortType.ASC);
            GroupByEntry gbe;
            try {
                if (gbr[i].hasNext()) {
                    gbe = gbr[i].next();
                } else {
                    throw new Exception("At least one column has no data");
                }
                bitmap[i + 1] = gbe.getTraversal().toBitMap();
                gbr[i] = GroupBy.createGroupByResult(columnList.get(sortIndex.get(i)), bitmap[i], sorts.get(sortIndex.get(i)) == SortType.ASC);
                if(i == sortIndex.size() - 1) {
                    gbr[i].hasNext();
                }
            } catch (Exception e) {
                FRContext.getLogger().error(e.getMessage());
            }
        }
    }

    public DetailSortComparator getDetailSortComparator() {
        return new DetailSortComparator();
    }
    protected class DetailSortComparator implements Comparator<Row> {

        @Override
        public int compare(Row o1, Row o2) {

            for (int i = 0; i < sortIndex.size(); i++) {
                int c = 0;
                //比较的列先后顺序
                int realColumn = sortIndex.get(i);
                if (sorts.get(realColumn) == SortType.ASC) {
                    c = columnList.get(realColumn).getDictionaryEncodedColumn().getComparator().compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (sorts.get(realColumn) == SortType.DESC) {
                    c = Comparators.reverse(columnList.get(realColumn).getDictionaryEncodedColumn().getComparator()).compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }


}
