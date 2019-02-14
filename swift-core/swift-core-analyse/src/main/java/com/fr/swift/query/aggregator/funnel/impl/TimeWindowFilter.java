package com.fr.swift.query.aggregator.funnel.impl;

import com.fr.swift.query.aggregator.funnel.IHead;
import com.fr.swift.query.aggregator.funnel.IStep;
import com.fr.swift.query.aggregator.funnel.ITimeWindowFilter;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class TimeWindowFilter implements ITimeWindowFilter {

    private static final int DAY_SECONDS = 86400;
    private final int dayWindow;

    private int timeWindow;
    private int dateStart;
    private int numberOfDates;

    // 漏斗定义的顺序步骤
    private IStep step;
    private boolean hasAnotherStep0;

    // 对应文档的D类：漏斗关联属性
    // 关联的事件属性值在一行参数中的index，即Object[] row数组中哪一个。这个要在解析的时候算好
    private int firstAssociatedIndex;
    private boolean[] associatedEvents;
    private int associatedColumnSize;

    private List<IHead> temp = new ArrayList<IHead>();
    private List<List<IHead>> lists;
    private boolean[] finished;
    private boolean hasNoHeadBefore = true;

    public TimeWindowFilter(int timeWindow, int dateStart, int numberOfDates, IStep step,
                            int firstAssociatedIndex, boolean[] associatedEvents,
                            DictionaryEncodedColumn associatedPropertyColumn) {
        this.timeWindow = timeWindow;
        this.dayWindow = timeWindow / DAY_SECONDS + 1;
        this.dateStart = dateStart;
        this.numberOfDates = numberOfDates;
        this.step = step;
        this.firstAssociatedIndex = firstAssociatedIndex;
        this.associatedEvents = associatedEvents;
//        this.associatedColumnSize = associatedPropertyColumn == null ? 0 : associatedPropertyColumn.size();
        this.hasAnotherStep0 = step.isHeadRepeated();
    }

    @Override
    public void init() {
        this.lists = new ArrayList<List<IHead>>();
        for (int i = 0; i < numberOfDates; i++) {
            lists.add(new ArrayList<IHead>());
        }
        this.finished = new boolean[numberOfDates];
    }

    @Override
    public void setDictSize(int size) {
        this.associatedColumnSize = size;
    }

    @Override
    public void add(int event, int timestamp, int dateDictIndex, int associatedValue, Object groupValue) {
        // 事件有序进入
        // 更新临时对象: 从后往前, 并根据条件适当跳出
        if (hasNoHeadBefore && !step.isEqual(0, event)) {
            return;
        }
        int dateIndex = dateDictIndex - dateStart;
        int minDay = Math.max(0, dateIndex - dayWindow);
        for (; dateIndex >= minDay; dateIndex--) {
            if (finished[dateIndex]) {
                break;
            }
            temp = lists.get(dateIndex);
            if (step.isEqual(0, event)) {
                hasNoHeadBefore = false;
                createHead(dateDictIndex, timestamp, associatedValue, groupValue);
                //重复事件是head
                if (!hasAnotherStep0) {
                    break;
                }
            }
            boolean copied = false;
            for (int i = temp.size() - 1; i >= 0; --i) {
                IHead head = temp.get(i);
                if ((timestamp - head.getTimestamp()) > timeWindow) {
                    // 当前事件的时间戳减去flag[0]超过时间窗口不合法, 跳出
                    break;
                }
                int nextEventIndex = head.getSize();
                if (nextEventIndex >= step.size()) {
                    finished[dateIndex] = true;
                    break;
                } else if (step.isEqual(nextEventIndex, event)) {
                    nextEvent(nextEventIndex, timestamp, associatedValue, groupValue, head);
                } else if (head.getSize() > 1 && step.isEqual(head.getSize() - 1, event)) {
                    if (updateEvent(i, timestamp, associatedValue, groupValue, head, copied)) {
                        copied = true;
                    }
                } else {
                    if (!copied) {
                        if (copyHead(i, event, timestamp, associatedValue, groupValue, head)) {
                            copied = true;
                        }
                    }
                }
            }
        }
    }

    private void nextEvent(int nextEventIndex, int timestamp, int associatedValue, Object groupValue, IHead head) {
        // 检查漏斗关联属性
        if (associatedEvents[nextEventIndex]) {
            if (firstAssociatedIndex == nextEventIndex) {
                // 第一个要关联的步骤
                head.setAssociatedProperty(associatedValue);
            } else if (associatedValue != head.getAssociatedProperty()) {
                // 是下一个事件，但是关联属性不对应，则跳过
                return;
            }
        }
        // 当前事件为下一个事件
        // 这边一个步骤只存行号，结果过滤和分组，后面根据行号去处理
        head.addStep(timestamp, groupValue);
    }

    private boolean updateEvent(int index, int timestamp, int associatedValue, Object groupValue, IHead head, boolean copied) {
        // 对应文档A类：漏斗基础规则#优先选择更靠近最终转化目标的事件作为转化事件
        // 相同的属性要取最近的事件
        int currentIndex = head.getSize() - 1;
        if (!associatedEvents[currentIndex]) {
            // 选靠近结束的事件
            head.setStep(currentIndex, timestamp, groupValue);
            return false;
        }
        // 检查漏斗关联属性
        if (associatedValue == head.getAssociatedProperty()) {
            // 选靠近结束的事件
            head.setStep(currentIndex, timestamp, groupValue);
        } else if (!copied && firstAssociatedIndex == currentIndex && !head.containsAssociatedEvents(associatedValue)) {
            IHead copy = head.copy();
            copy.setStep(currentIndex, timestamp, groupValue);
            copy.setAssociatedProperty(associatedValue);
            temp.add(index + 1, copy);
            return true;
        }
        return false;
    }

    private boolean copyHead(int i, int event, int timestamp, int associatedValue, Object groupValue, IHead head) {
        // 有结果分组的情况下。 1,2,3,2,3,4这种情况，出现第二个2的时候要做一次拷贝
        int index = 0;
        for (int j = 1; j < head.getSize() - 1; j++) {
            if (step.isEqual(j, event)) {
                index = j;
                break;
            }
        }
        if (index == 0) {
            return false;
        }
        // 检查漏斗关联属性
        if (associatedEvents[index]) {
            if (firstAssociatedIndex != index) {
                if (associatedValue != head.getAssociatedProperty()) {
                    // 既不是第一要关联的步骤，关联属性也不相等，则不拷贝
                    return false;
                }
            }
        }
        IHead copy = head.copy();
        copy.setStep(index, timestamp, groupValue);
        if (index <= firstAssociatedIndex) {
            copy.newEventSet();
            copy.setAssociatedProperty(-1);
        }
        if (associatedEvents[index]) {
            copy.setAssociatedProperty(associatedValue);
        }
        copy.reset(index + 1);
        temp.add(i + 1, copy);
        return true;
    }

    private void createHead(int date, int timestamp, int associatedValue, Object groupValue) {
        // 当前事务没有被使用且属于第一个事件，则新建临时IHead对象
        IHead newHead = new Head(step.size(), date, associatedValue, associatedColumnSize);
        newHead.addStep(timestamp, groupValue);
        temp.add(newHead);
    }

    @Override
    public List<IHead> getResult() {
        List<IHead> result = new ArrayList<IHead>();
        for (List<IHead> list : lists) {
            if (list.isEmpty()) {
                continue;
            }
            IHead ret = list.get(list.size() - 1);
            for (int i = list.size() - 2; i >= 0; i--) {
                IHead head = list.get(i);
                if (ret.getSize() < head.getSize()) {
                    ret = head;
                } else if (head.getSize() == step.size() && isRightHead(head.getTimestamps(), ret.getTimestamps())) {
                    ret = head;
                } else if (head.getSize() == ret.getSize() && head.getSize() < step.size()) {
                    int size = head.getSize();
                    int[] h = head.getTimestamps();
                    int[] r = ret.getTimestamps();
                    for (int j = size - 1; j >= 0; j--) {
                        if (h[j] > r[j]) {
                            ret = head;
                            break;
                        } else if (h[j] != r[j]) {
                            break;
                        }
                    }
                }
            }
            result.add(ret);
        }
        return result;
    }

    private static boolean isRightHead(int[] a, int[] b) {
        long at = a[a.length - 1];
        long bt = b[b.length - 1];
        if (at < bt) {
            return true;
        }
        if (at != bt) {
            return false;
        }
        for (int i = a.length - 2; i >= 0; i--) {
            if (a[i] > b[i]) {
                return true;
            } else if (a[i] < b[i]) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        for (int i = 0; i < numberOfDates; i++) {
            lists.get(i).clear();
        }
        Arrays.fill(finished, false);
        hasNoHeadBefore = true;
    }
}
