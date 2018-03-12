package com.fr.swift.result;

import com.fr.swift.cal.Query;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/24
 */

public class SortMultiSegmentDetailResultSet extends DetailResultSet {


    private List<Query<DetailResultSet>> queries;
    private Comparator comparator;
    private ArrayList<Row> sortedDetailList = new ArrayList<Row>();

    public SortMultiSegmentDetailResultSet(List<Query<DetailResultSet>> queries, Comparator comparator) throws SQLException {
        this.queries = queries;
        this.comparator = comparator;
        init();
    }

    @Override
    public void close() {

    }

    @Override
    public SwiftMetaData getMetaData() {
        return null;
    }



    @Override
    public Row getRowData() {

        Row row = sortedDetailList.get(rowCount);
        return row;
    }

    private void sortDetailSegment(ArrayList<Row> sortList) throws SQLException {
        int rows = 0;
        for (Query query : queries) {
            rows += ((SortSegmentDetailResultSet) query.getQueryResult()).getMaxRow();
        }
        mergeSortAllSegment(0, rows - 1, 0, queries.size(), sortList);
    }


    /**
     * @param first    ArrayList的起始索引
     * @param last     ArrayList的结束索引
     * @param nseg     第n块
     * @param segments 有顺序的块数
     * @param sortList
     * @return
     * @throws SQLException
     */
    private void mergeSortAllSegment(int first, int last, int nseg, int segments, ArrayList<Row> sortList) throws SQLException {
        if (first == last || segments == 1) {
            return;
        }
        int mid = first;
        int segment = segments >> 1;
        for (int i = nseg; i < nseg + segment; i++) {
            mid += ((SortSegmentDetailResultSet) queries.get(i).getQueryResult()).getMaxRow();
        }
        mergeSortAllSegment(first, mid, 0, segment, sortList);
        mergeSortAllSegment(mid, last, nseg + segment + 1, segments - segment, sortList);
        mergeSort2Segment(first, mid, last, sortList);
    }


    private void mergeSort2Segment(int first, int mid, int last, ArrayList<Row> sortList) {

        int needMoveFirst = first;
        int needMoveLast = mid;
        int medianPoint = 0;

        while (needMoveFirst <= last && needMoveLast <= last && needMoveFirst < needMoveLast) {
            while (needMoveFirst <= last && comparator.compare(sortList.get(needMoveFirst), sortList.get(needMoveLast)) < 0) {
                needMoveFirst ++;
            }
            medianPoint = needMoveLast;
            while (needMoveLast <= last && comparator.compare(sortList.get(needMoveLast), sortList.get(needMoveFirst)) < 0) {
                needMoveLast ++;
            }
            if (needMoveLast > medianPoint) {
                RotateRight(sortList, needMoveFirst, needMoveLast - 1, needMoveLast - medianPoint);
            }
            needMoveFirst += (needMoveLast - medianPoint + 1);
        }
    }

    private void RotateRight(ArrayList<Row> sortList, int first, int last, int moveCount) {
        int len = last - first + 1;
        moveCount %= len;
        Reverse(sortList, first, last - moveCount);
        Reverse(sortList, last - moveCount + 1, last);
        Reverse(sortList, first, last);
    }

    private void Reverse(ArrayList<Row> sortList, int first, int last) {
        for (; first < last; first++, last--) {
            Row row = sortList.get(first);
            sortList.set(first, sortList.get(last));
            sortList.set(last, row);
        }

    }


    private void init() throws SQLException {
        for (Query query : queries) {
            sortedDetailList.addAll(((SortSegmentDetailResultSet) query.getQueryResult()).getSortedDetailList());
            maxRow += ((SortSegmentDetailResultSet) query.getQueryResult()).getMaxRow();
        }
        sortDetailSegment(sortedDetailList);
    }
}
