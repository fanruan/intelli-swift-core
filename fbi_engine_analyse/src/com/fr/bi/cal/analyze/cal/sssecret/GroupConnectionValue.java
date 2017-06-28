package com.fr.bi.cal.analyze.cal.sssecret;

import java.util.ArrayList;
import java.util.List;

public class GroupConnectionValue {

    private GroupConnectionValue parent;

    private GroupConnectionValue child;

    private Object data;

    private NoneDimensionGroup currentValue;

    /**
     * 是否是补全行
     * 年月日等时间维度有补全行这么一选项
     */
    private boolean shouldComplete = false;

    /**
     * 补全的行
     */
    private List<GroupConnectionValue> completeGroupConnectionValues;

    public GroupConnectionValue(Object data, NoneDimensionGroup currentValue) {

        this.data = data;
        this.currentValue = currentValue;
    }

    public Object getData() {

        return data;
    }

    public void setData(Object o) {

        this.data = o;
    }


    public NoneDimensionGroup getCurrentValue() {

        return currentValue;
    }

    public void setCurrentValue(NoneDimensionGroup n) {

        this.currentValue = n;
    }

    public void setChild(GroupConnectionValue c) {

        this.child = c;
    }

    public GroupConnectionValue getChild() {

        return child;
    }

    public GroupConnectionValue getParent() {

        return parent;
    }

    public void setParent(GroupConnectionValue parent) {

        this.parent = parent;
        this.parent.child = this;
    }


    /**
     * 添加补全行
     *
     * @param c
     */
    public void addCompleteGroupConnectionValue(GroupConnectionValue c) {

        if (c != null) {
            if (completeGroupConnectionValues == null) {
                completeGroupConnectionValues = new ArrayList();
                // 标志当前行为补全行
                shouldComplete = true;
            }
            completeGroupConnectionValues.add(c);
        }
    }

    /**
     * 获取补全行
     *
     * @return
     */
    public List<GroupConnectionValue> getCompleteGroupConnectionValues() {

        return completeGroupConnectionValues;
    }

    /**
     * 是否应该补全
     *
     * @return
     */
    public boolean shouldComplete() {

        return shouldComplete;
    }

    /**
     * 是否应该补全
     *
     * @param s
     */
    public void setShouldComplete(boolean s) {

        this.shouldComplete = s;
    }

    /**
     * 获取显示的行
     * 需求:
     * 1:有时间补全行需要补全
     * 2:现在还有一个需求就是维度如果有空值,会设置一个属性判断是否会进行显示......折腾
     * 所以采用
     * 1:迭代器获取到一行的时候其实本不是真正的一行,而是可以映射成多行或者一行都没有
     *
     * @return
     */
    public List<GroupConnectionValue> getDisplayGroupConnectionValue() {

        List<GroupConnectionValue> disp = new ArrayList<GroupConnectionValue>();
        disp.addAll(getCompleteLine());
        disp.add(this);
        return disp;
    }

    private List<GroupConnectionValue> getCompleteLine() {

        List<GroupConnectionValue> r = new ArrayList<GroupConnectionValue>();
        GroupConnectionValue p = this;
        // 如果第一个维度就进行补全
        if (p.shouldComplete) {
            return getCompleteGroupConnectionValues();
        }
        // 父容器
        GroupConnectionValue pContainer = this.cloneWidthNoChild();
        GroupConnectionValue c = p.getChild();
        while (c != null) {
            if (c.shouldComplete) {
                List<GroupConnectionValue> ccs = c.getCompleteGroupConnectionValues();
                for (int i = 0; i < ccs.size(); i++) {
                    GroupConnectionValue t = pContainer.cloneWidthNoChild();
                    t.setChild(ccs.get(i));
                    r.add(t);
                }
                break;
            }
            pContainer.setChild(c.cloneWidthNoChild());
            c = c.getChild();
        }
        return r;
    }


    /**
     * 克隆
     *
     * @return
     */
    public GroupConnectionValue clone() {

        GroupConnectionValue gv = new GroupConnectionValue(data, currentValue);
        if (child != null) {
            gv.setChild(child.clone());
        }
        return gv;
    }

    /**
     * 克隆自己,不克隆儿子
     *
     * @return
     */
    public GroupConnectionValue cloneWidthNoChild() {

        GroupConnectionValue gv = new GroupConnectionValue(data, currentValue);
        return gv;
    }
}