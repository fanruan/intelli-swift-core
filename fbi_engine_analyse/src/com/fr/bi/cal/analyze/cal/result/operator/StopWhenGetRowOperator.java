package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.sssecret.GroupConnectionValue;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

/**
 * 当进行到某一行的时候才停止构建操作器
 * <p>
 * 作用有:
 * 1:联动的时候,构建节点构建到需要的行数
 */
public class StopWhenGetRowOperator implements Operator {

    private int counter = 0;

    /**
     * 停止的行数据
     */
    Object[] stopRow;

    boolean shouldEnd = false;

    public StopWhenGetRowOperator(Object[] stopRow) {

        if (stopRow == null) {
            this.stopRow = new Object[0];
        }
        this.stopRow = stopRow;
    }

    @Override
    public void moveIterator(NodeDimensionIterator iterator) {

    }

    // 判断当前页是否结束
    @Override
    public boolean isPageEnd(GroupConnectionValue gc) {
        if (shouldEnd) {
            return true;
        }
        // 没能获取到值得时候表明已经结束了
        GroupConnectionValue p = gc;
        if (gc == null) {
            return true;
        }
        for (Object o : stopRow) {
            GroupConnectionValue c = p.getChild();
            if (!checkEquals(c, o)) {
                return false;
            }
            p = c;
        }
        shouldEnd = true;
        return false;
    }

    //BI-6973 客户工程有可能出现空指针
    private boolean checkEquals(GroupConnectionValue gcv, Object o) {
        Object gcvData = gcv.getData();
        if (gcvData == null && gcvData == null) {
            return true;
        } else if (o == null || gcvData == null) {
            return false;
        } else {
            return gcvData.toString().equals(o);
        }
    }

    @Override
    public void addRow() {

        counter++;
    }

    @Override
    public int getCount() {

        return counter;
    }

    @Override
    public int getMaxRow() {

        return 0;
    }

    @Override
    public Object[] getClickedValue() {

        return new Object[0];
    }

}