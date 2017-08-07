package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.cache.list.IntList;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 小灰灰 on 2017/2/6.
 * 类似n位进制不定的整数的加法，每次next就加1。
 * 比如有三个维度，数组初始位置是{-1, -1, -1}, 从末尾依次往上加1，一旦越界，比如加到了{0, 0, 6}越界了，就进位加1，变成{0, 1, 0}再继续。
 * 初始化为{-1, -1, -1}而不是{0, 0, -1}是因为可能没有展开倒最后一个维度，要是没有展开的情况会直接略过分组的第一个值。
 */
public class TreeIterator implements NodeDimensionIterator {
    private int[] index;
    private int[] tempIndex;
    private NodeExpander expander;
    private IRootDimensionGroup root;
    private int size;

    /**
     * TODO 先放内存看看再说
     */
    private List<int[]> pageIndex = new ArrayList<int[]>();

    private TreeIterator() {

    }

    public TreeIterator(int size) {
        this.size = size;
        this.index = new int[size];
        Arrays.fill(this.index, -1);
        pageEnd();
    }

    @Override
    public void setExpander(NodeExpander expander) {
        this.expander = expander;
    }

    @Override
    public void setRoot(IRootDimensionGroup root) {
        this.root = root;
    }

    @Override
    public IRootDimensionGroup getRoot() {
        return root;
    }

    @Override
    public int[] getStartIndex() {
        return index;
    }

    @Override
    public GroupConnectionValue next() {
        return seek(index);
    }

    private GroupConnectionValue seek(int[] index) {
        GroupConnectionValue gv = new GroupConnectionValue(null, root.getRoot());
        IntList list = new IntList();
        int indexCopy[] = Arrays.copyOf(index, index.length);
        if (ReturnStatus.GroupEnd == root.getNext(gv, indexCopy, 0, expander, list)) {
            this.tempIndex = null;
            return null;
        }
        //没有展开的情况list的size会小于维度的数量，要补齐。主要是怕越界。。。
        for (int i = list.size(); i < size; i++) {
            list.add(-1);
        }
        this.tempIndex = list.toArray();
        return gv;
    }

    @Override
    public boolean hasPrevious() {
        return pageIndex.size() > 2;
    }

    @Override
    public boolean hasNext() {
        return next() != null && index != null && index.length > 0;
    }

    @Override
    public void moveNext() {
        if (this.tempIndex != null) {
            this.index = tempIndex.clone();
        }
    }

    @Override
    public int getPageIndex() {
        return pageIndex.size() - 1;
    }

    @Override
    public void pageEnd() {
        pageIndex.add(this.index.clone());
    }


    /**
     * TODO 这里可以改成可以前后移动的游标提高性能先这样
     */
    @Override
    public void moveToShrinkStartValue(Object[] value) {
        if (value != null) {
            int[] shrinkPos = root.getValueStartRow(value);
            travelToPositionPage(shrinkPos);
        } else {
            moveCurrentStart();
        }
    }

    private void moveCurrentStart() {
        int pos = pageIndex.size() - 2;
        if (pos < 0){
            return;
        }
        this.index = pageIndex.get(pos);
        this.pageIndex = this.pageIndex.subList(0, pos + 1);
    }


    private void travelToPositionPage(int[] shrinkPos) {
        int position = findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
        if (position - 1 >= 0) {
            this.index = pageIndex.get(position - 1);
        }
        if (position < pageIndex.size()) {
            pageIndex = pageIndex.subList(0, position);
        }
    }

    private int findPageIndexDichotomy(int[] shrinkPos, List<int[]> pageIndex, int start, int end) throws ArrayIndexOutOfBoundsException {
        //判断数组是否为空
        if (pageIndex == null) {
            throw new NullPointerException();
        }
        if (start < 0 || end < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (end > start) {
            int middle = (start + end) / 2;
            int[] middleIndex = pageIndex.get(middle);
            //中间值小于当前值
            if (compare(shrinkPos, middleIndex) >= 0) {
                //中间值小于当前值，同时下一个值大于等于当前值（end>=middle+1）,则middle为最小的大于值
                if (compare(shrinkPos, pageIndex.get(middle + 1)) < 0) {
                    return middle + 1;
                } else {
                    //中间值小于当前值，但是下一个值仍然小于，则结果应该在（middle+1,end）中间
                    return findPageIndexDichotomy(shrinkPos, pageIndex, middle + 1, end);
                }
            } else {
                //中间值大于当前值
                return findPageIndexDichotomy(shrinkPos, pageIndex, start, middle);
            }
        } else if (start == end) {
            return start;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private int compare(int[] p1, int[] p2) {
        if (p1 == p2) {
            return 0;
        }
        if (p1 == null) {
            return -1;
        }
        if (p2 == null) {
            return 1;
        }
        int len1 = p1.length;
        int len2 = p2.length;
        int lim = Math.min(len1, len2);
        int k = 0;
        while (k < lim) {
            int c1 = p1[k];
            int c2 = p2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }


    @Override
    public void moveLastPage() {
        int pos = pageIndex.size() - 3;
        this.index = pageIndex.get(pos);
        this.pageIndex = this.pageIndex.subList(0, pos + 1);
    }

    @Override
    public NodeDimensionIterator createClonedIterator() {
        TreeIterator iterator = new TreeIterator();
        iterator.size = size;
        if (index != null){
            iterator.index = index.clone();
        }
        if (tempIndex != null){
            iterator.tempIndex = tempIndex.clone();
        }
        iterator.pageIndex.addAll(pageIndex);
        if (root != null){
            iterator.root = root.createClonedRoot();
        }
        return iterator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TreeIterator that = (TreeIterator) o;

        if (size != that.size) {
            return false;
        }
        if (!ComparatorUtils.equals(index, that.index)) {
            return false;
        }
        if (!ComparatorUtils.equals(tempIndex, that.tempIndex)) {
            return false;
        }
        return pageIndex != null ? ComparatorUtils.equals(pageIndex, that.pageIndex) : that.pageIndex == null;

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(index);
        result = 31 * result + Arrays.hashCode(tempIndex);
        result = 31 * result + size;
        result = 31 * result + (pageIndex != null ? pageIndex.hashCode() : 0);
        return result;
    }
}