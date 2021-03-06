package com.fr.swift.cloud.query.query.funnel.impl.window;

import com.fr.swift.cloud.query.filter.match.MatchFilter;
import com.fr.swift.cloud.query.info.funnel.filter.TimeFilterInfo;
import com.fr.swift.cloud.query.info.funnel.group.time.TimeGroup;
import com.fr.swift.cloud.query.query.funnel.BaseTimeWindowFilter;
import com.fr.swift.cloud.query.query.funnel.IHead;
import com.fr.swift.cloud.query.query.funnel.IStep;
import com.fr.swift.cloud.query.query.funnel.TimeWindowBean;
import com.fr.swift.cloud.query.query.funnel.impl.head.AHead;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class NoRepeatTimeWindowFilter extends BaseTimeWindowFilter {


    // 对应文档的D类：漏斗关联属性
    // 关联的事件属性值在一行参数中的index，即Object[] row数组中哪一个。这个要在解析的时候算好
    private int firstAssociatedIndex;
    private boolean[] associatedEvents;
    private int associatedColumnSize;
    private boolean[] iterableEvents;

    private IStepContainer[][] lists;
    private boolean[] finished;
    private boolean hasNoHeadBefore = true;

    public NoRepeatTimeWindowFilter(TimeWindowBean timeWindow, TimeGroup timeGroup, MatchFilter timeGroupMatchFilter, TimeFilterInfo info, IStep step,
                                    int firstAssociatedIndex, boolean[] associatedEvents,
                                    DictionaryEncodedColumn associatedPropertyColumn) {
        super(timeWindow, timeGroup, timeGroupMatchFilter, info, step);
        this.firstAssociatedIndex = firstAssociatedIndex;
        this.associatedEvents = associatedEvents;
        this.associatedColumnSize = associatedPropertyColumn == null ? 0 : associatedPropertyColumn.size();
    }

    private void initIterableEvents() {
        if (firstAssociatedIndex == -1) {
            iterableEvents = new boolean[step.size()];
        }
        iterableEvents = new boolean[step.size()];
        for (int i = firstAssociatedIndex + 1; i < step.size(); i++) {
            if (!associatedEvents[i]) {
                iterableEvents[i] = true;
            }
        }
    }

    @Override
    public void init() {
        initIterableEvents();

        this.lists = new IStepContainer[numberOfDates][];
        for (int i = 0; i < numberOfDates; i++) {
            IStepContainer[] containers = new IStepContainer[step.size()];
            for (int j = 0; j < step.size(); j++) {
                if (j < firstAssociatedIndex || firstAssociatedIndex == -1) {
                    containers[j] = new SimpleStepContainer();
                } else {
                    containers[j] = new AStepContainer(associatedColumnSize);
                }
            }
            lists[i] = containers;
        }
        this.finished = new boolean[numberOfDates];
    }

    @Override
    public void setDictSize(int size) {
        this.associatedColumnSize = size;
    }

    @Override
    public void add(int event, long timestamp, int associatedValue, Object groupValue, int row) {
        // 事件有序进入
        // 更新临时对象: 从后往前, 并根据条件适当跳出
        int eventIndex = step.getEventIndex(event);
        if (!step.matches(eventIndex, row)) {
            return;
        }
        if (hasNoHeadBefore && eventIndex != 0) {
            return;
        }
        int dateIndex = getDateIndex(timestamp);
        if (eventIndex == 0) {
            GroupNode node = new GroupNode();
            node.setData(timestamp);
            // 如果符合就加入计算  不符合就不计算
            if (timeGroupMatchFilter.matches(node)) {
                createHead(dateIndex, timestamp, associatedValue, groupValue, lists[dateIndex][0]);
                hasNoHeadBefore = false;
            }
            return;
        }
        int minDay = (int) Math.max(0, dateIndex - dayWindow);
        for (; dateIndex >= minDay; dateIndex--) {
            if (finished[dateIndex]) {
                break;
            }
            IStepContainer[] containers = lists[dateIndex];
            IStepContainer prevHeads = containers[eventIndex - 1];
            IStepContainer currentHeads = containers[eventIndex];
            if (newStep(eventIndex, dateIndex, timestamp, associatedValue, groupValue, prevHeads, currentHeads)) {
                continue;
            }
            updateStep(eventIndex, timestamp, associatedValue, groupValue, currentHeads);
        }
    }

    private void updateStep(int eventIndex, long timestamp, int associatedValue, Object groupValue, IStepContainer currentHeads) {
        if (!iterableEvents[eventIndex]) {
            IHead head = currentHeads.getHead(associatedValue);
            if (head == null) {
                return;
            }
            if (timestamp - head.getTimestamp() > timeWindow) {
                return;
            }
            updateEvent(timestamp, associatedValue, groupValue, head, currentHeads);
        } else {
            currentHeads.initIterator();
            while (true) {
                IHead head = currentHeads.next();
                if (head == null) {
                    break;
                }
                if (timestamp - head.getTimestamp() > timeWindow) {
                    continue;
                }
                updateEvent(timestamp, associatedValue, groupValue, head, currentHeads);
            }
        }
    }

    private boolean newStep(int eventIndex, int dateIndex, long timestamp, int associatedValue, Object groupValue,
                            IStepContainer prevHeads, IStepContainer currentHeads) {
        if (!iterableEvents[eventIndex]) {
            IHead prevHead = prevHeads.getHead(associatedValue);
            if (prevHead == null || timestamp - prevHead.getTimestamp() > timeWindow) {
                return false;
            }
            if (nextEvent(eventIndex, timestamp, prevHead, associatedValue, groupValue, currentHeads)) {
                if (eventIndex == step.size() - 1) {
                    finished[dateIndex] = true;
                }
                // 如果创建了head就不用做下面的更新的了
                return true;
            }
        } else {
            prevHeads.initIterator();
            while (true) {
                IHead head = prevHeads.next();
                if (head == null) {
                    break;
                }
                if (timestamp - head.getTimestamp() > timeWindow) {
                    continue;
                }
                if (nextEvent(eventIndex, timestamp, head, associatedValue, groupValue, currentHeads)) {
                    if (eventIndex == step.size() - 1) {
                        finished[dateIndex] = true;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void updateEvent(long timestamp, int associatedValue, Object groupValue, IHead head, IStepContainer container) {
        // 对应文档A类：漏斗基础规则#优先选择更靠近最终转化目标的事件作为转化事件
        // 相同的属性要取最近的事件
        int currentIndex = head.getSize() - 1;
        if (!associatedEvents[currentIndex]) {
            head.setStep(currentIndex, timestamp, groupValue);
            container.add(head.getAssociatedProperty(), head);
            return;
        }
        if (currentIndex == firstAssociatedIndex) {
            if (associatedValue == head.getAssociatedProperty()) {
                head.setStep(currentIndex, timestamp, groupValue);
                container.add(head.getAssociatedProperty(), head);
                return;
            } else if (!container.contains(associatedValue)) {
                IHead copy = head.copy();
                copy.setAssociatedProperty(associatedValue);
                head.setStep(currentIndex, timestamp, groupValue);
                container.add(associatedValue, copy);
                return;
            }
        }
        if (associatedValue == head.getAssociatedProperty()) {
            head.setStep(currentIndex, timestamp, groupValue);
            container.add(associatedValue, head);
        }
    }

    private boolean nextEvent(int nextEventIndex, long timestamp, IHead head, int associatedValue, Object groupValue, IStepContainer container) {
        // 检查漏斗关联属性
        if (associatedEvents[nextEventIndex]) {
            if (firstAssociatedIndex != nextEventIndex && associatedValue != head.getAssociatedProperty()) {
                // 是下一个事件，但是关联属性不对应，则跳过
                return false;
            }
        }
        // 当前事件为下一个事件
        // 这边一个步骤只存行号，结果过滤和分组，后面根据行号去处理
        IHead copy = head.copy();
        if (associatedEvents[nextEventIndex]) {
            copy.setAssociatedProperty(associatedValue);
        }
        copy.addStep(timestamp, groupValue);
        container.add(copy.getAssociatedProperty(), copy);
        return true;
    }

    private void createHead(int timeIndex, long timeStamp, int associatedValue, Object groupValue, IStepContainer container) {
        // 当前事务没有被使用且属于第一个事件，则新建临时IHead对象
        IHead newHead = new AHead(step.size(), isAllTime() ? "ALL" : timeDetail[timeIndex], associatedValue);
        newHead.addStep(timeStamp, groupValue);
        container.add(associatedValue, newHead);
        // head只能添加在某一天，所以这里要跳出
    }

    private interface IStepContainer {

        void initIterator();

        IHead next();

        void add(int associatedValue, IHead head);

        boolean contains(int associatedValue);

        IHead getHead(int associatedValue);

        void reset();
    }

    private static class Node {
        Node prev;
        Node next;
        IHead head;
    }

    private static class DLink {
        private final Node dummyHead = new Node();
        private final Node dummyTail = new Node();

        public DLink() {
            reset();
        }

        public void reset() {
            dummyHead.next = dummyTail;
            dummyTail.prev = dummyHead;
        }

        public void addLast(Node node) {
            dummyTail.prev.next = node;
            node.prev = dummyTail.prev;
            node.next = dummyTail;
            dummyTail.prev = node;
        }

        public void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private static class AStepContainer implements IStepContainer {

        private DLink link = new DLink();
        private Node[] heads;
        private Node next;

        public AStepContainer(int associatedColumnDictSize) {
            heads = new Node[associatedColumnDictSize];
        }

        @Override
        public void initIterator() {
            next = link.dummyTail.prev;
        }

        @Override
        public IHead next() {
            IHead head = next.head;
            if (head != null) {
                next = next.prev;
                return head;
            }
            return null;
        }

        @Override
        public void add(int associatedValue, IHead head) {
            if (heads[associatedValue] == null) {
                Node node = new Node();
                node.head = head;
                heads[associatedValue] = node;
                link.addLast(node);
            } else {
                Node node = heads[associatedValue];
                node.head = head;
                link.remove(node);
                link.addLast(node);
            }
        }

        @Override
        public boolean contains(int associatedValue) {
            return heads[associatedValue] != null;
        }

        @Override
        public IHead getHead(int associatedValue) {
            return heads[associatedValue] == null ? null : heads[associatedValue].head;
        }

        @Override
        public void reset() {
            link.reset();
            Arrays.fill(heads, null);
        }
    }

    private static class SimpleStepContainer implements IStepContainer {

        private IHead head;
        private IHead next;

        @Override
        public void initIterator() {
            next = head;
        }

        @Override
        public IHead next() {
            IHead ret = next;
            next = null;
            return ret;
        }

        @Override
        public void add(int associatedValue, IHead head) {
            this.head = head;
        }

        @Override
        public boolean contains(int associatedValue) {
            return false;
        }

        @Override
        public IHead getHead(int associatedValue) {
            return head;
        }

        @Override
        public void reset() {
            head = null;
        }
    }

    @Override
    public List<IHead> getResult() {
        List<IHead> result = new ArrayList<IHead>();
        for (IStepContainer[] containers : lists) {
            for (int i = step.size() - 1; i >= 0; i--) {
                IStepContainer container = containers[i];
                container.initIterator();
                IHead head = container.next();
                if (head != null) {
                    result.add(head);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void reset() {
        for (IStepContainer[] list : lists) {
            for (IStepContainer iStepContainer : list) {
                iStepContainer.reset();
            }
        }
        this.finished = new boolean[numberOfDates];
        hasNoHeadBefore = true;
    }
}
