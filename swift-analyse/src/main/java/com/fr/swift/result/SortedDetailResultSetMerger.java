package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.queue.SortedListMergingUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/7/20.
 */
class SortedDetailResultSetMerger implements Iterator<List<Row>> {

    private int pageSize;
    private List<DetailResultSet> resultSets;
    private Comparator<Row> comparator;
    private List<Row> remainRows = new ArrayList<Row>(0);
    private Row[] lastRowOfPrevPage;

    SortedDetailResultSetMerger(int pageSize, Comparator<Row> comparator, List<DetailResultSet> resultSets) {
        this.pageSize = pageSize;
        this.comparator = comparator;
        this.resultSets = resultSets;
        this.lastRowOfPrevPage = new Row[resultSets.size()];
    }

    /**
     * remainRows.size() < pageSize情况下，从每个resultSet中取出一页进行合并
     *
     * @return
     */
    private List<Row> updateAll() {
        List<List<Row>> lists = new ArrayList<List<Row>>();
        for (int i = 0; i < resultSets.size(); i++) {
            if (resultSets.get(i).hasNextPage()) {
                List<Row> rows = resultSets.get(i).getPage();
                if (!rows.isEmpty()) {
                    lists.add(rows);
                    lastRowOfPrevPage[i] = rows.get(rows.size() - 1);
                }
            }
        }
        if (!remainRows.isEmpty()) {
            lists.add(remainRows);
        }
        Iterator<Row> iterator = lists.isEmpty() ?
                IteratorUtils.<Row>emptyIterator() : SortedListMergingUtils.merge(lists, comparator);
        return getPage(iterator);
    }

    /**
     * remainRows.size() >= pageSize情况下，更新步骤如下：
     * 1、从remainRows中取出第pageSize行lastRow
     * 2、判断lastRow是否<=resultSet前一页的最后一行，如果是，则不更新该resultSet，否则取这个resultSet的下一页
     * 3、合并
     *
     * @return
     */
    private List<Row> getNext() {
        if (remainRows.size() < pageSize) {
            return updateAll();
        }
        Row lastRow = remainRows.get(pageSize - 1);
        List<List<Row>> newPages = new ArrayList<List<Row>>();
        for (int i = 0; i < resultSets.size(); i++) {
            if (resultSets.get(i).hasNextPage()) {
                if (shouldUpdate(lastRow, lastRowOfPrevPage[i])) {
                    List<Row> rows = resultSets.get(i).getPage();
                    if (!rows.isEmpty()) {
                        newPages.add(rows);
                        lastRowOfPrevPage[i] = rows.get(rows.size() - 1);
                    }
                }
            }
        }
        Iterator<Row> iterator;
        if (!newPages.isEmpty()) {
            newPages.add(remainRows);
            iterator = SortedListMergingUtils.merge(newPages, comparator);
        } else {
            iterator = remainRows.iterator();
        }
        List<Row> page = getPage(iterator);
        if (page.size() < pageSize && remainRows.isEmpty() && hasNext()) {
            // 按照前面的规则更新了，但是不满一页，并且源结果集还有剩余，继续取下一页
            remainRows = page;
            return getNext();
        }
        return page;
    }

    private boolean shouldUpdate(Row remainRow, Row lastRowOfPrevPage) {
        return comparator.compare(remainRow, lastRowOfPrevPage) > 0;
    }

    private List<Row> getPage(Iterator<Row> iterator) {
        List<Row> ret = new ArrayList<Row>();
        remainRows = new ArrayList<Row>();
        int count = pageSize;
        while (iterator.hasNext()) {
            if (count-- > 0) {
                ret.add(iterator.next());
            } else {
                remainRows.add(iterator.next());
            }
        }
        return ret;
    }

    @Override
    public boolean hasNext() {
        if (!remainRows.isEmpty()) {
            return true;
        }
        for (DetailResultSet resultSet : resultSets) {
            if (resultSet.hasNextPage()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Row> next() {
        return getNext();
    }

    @Override
    public void remove() {
    }
}
