package com.fr.bi.cal.analyze.executor.paging;

/**
 * Created by GUY on 2015/4/17.
 */
public class Paging {

    private int oprator = 0;//刷新的操作
    private int current = 1;//当前页
    private long totalSize;//总行数
    private int pageSize;//每页行数

    protected Paging() {

    }

    public int getCurrentPage() {
        return current;
    }

    public void setCurrentPage(int current) {
        this.current = current <= 0 ? 1 : current;
    }

    public int getPages() {
        return ((int) totalSize - 1) / pageSize + 1;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取当前页的行数
     *
     * @return
     */
    public int getCurrentSize() {
        return current == getPages() ? ((int) totalSize - getPageSize() * (current - 1)) : getPageSize();
    }

    /**
     * 开始行数,从0开始
     *
     * @return
     */
    public int getStartRow() {
        return getPageSize() * (current - 1);
    }

    /**
     * 结束行数
     */
    public int getEndRow() {
        return getStartRow() + getCurrentSize();
    }

    public int getOprator() {
        return oprator;
    }

    public void setOprator(int oprator) {
        this.oprator = oprator;
    }

    public boolean isAllPage() {
        return oprator == -1;
    }
}