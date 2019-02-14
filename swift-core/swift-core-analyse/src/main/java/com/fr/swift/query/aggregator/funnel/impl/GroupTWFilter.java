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
public class GroupTWFilter implements ITimeWindowFilter {

    private static final int DAY_SECONDS = 86400;
    private final int dayWindow;

    private int timeWindow;
    private int dateStart;
    private int numberOfDates;

    // 漏斗定义的顺序步骤
    private IStep step;

    // 对应文档的D类：漏斗关联属性
    // 关联的事件属性值在一行参数中的index，即Object[] row数组中哪一个。这个要在解析的时候算好
    private int firstAssociatedIndex;
    private boolean[] associatedEvents;
    private int associatedColumnSize;
    private boolean[] iterableEvents;

    private List<List<IStepContainer>> lists;
    private boolean[] finished;
    private boolean hasNoHeadBefore = true;

    public GroupTWFilter(int timeWindow, int dateStart, int numberOfDates, IStep step,
                         int firstAssociatedIndex, boolean[] associatedEvents,
                         DictionaryEncodedColumn associatedPropertyColumn) {
        this.timeWindow = timeWindow;
        this.dayWindow = timeWindow / DAY_SECONDS + 1;
        this.dateStart = dateStart;
        this.numberOfDates = numberOfDates;
        this.step = step;
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

        this.lists = new ArrayList<List<IStepContainer>>();
        for (int i = 0; i < numberOfDates; i++) {
            List<IStepContainer> containers = new ArrayList<IStepContainer>();
            for (int j = 0; j < step.size(); j++) {
                if (j < firstAssociatedIndex || firstAssociatedIndex == -1) {
                    containers.add(new SimpleStepContainer());
                } else {
                    containers.add(new AStepContainer(associatedColumnSize));
                }
            }
            lists.add(containers);
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
        int eventIndex = step.getEventIndex(event);
        if (hasNoHeadBefore && eventIndex != 0) {
            return;
        }
        int dateIndex = dateDictIndex - dateStart;
        if (eventIndex == 0) {
            createHead(dateDictIndex, timestamp, associatedValue, groupValue, lists.get(dateIndex).get(0));
            hasNoHeadBefore = false;
            return;
        }
        int minDay = Math.max(0, dateIndex - dayWindow);
        for (; dateIndex >= minDay; dateIndex--) {
            if (finished[dateIndex]) {
                break;
            }
            List<IStepContainer> containers = lists.get(dateIndex);
            IStepContainer prevHeads = containers.get(eventIndex - 1);
            IStepContainer currentHeads = containers.get(eventIndex);
            if (newStep(eventIndex, dateIndex, timestamp, associatedValue, groupValue, prevHeads, currentHeads)) {
                continue;
            }
            updateStep(eventIndex, timestamp, associatedValue, groupValue, currentHeads);
        }
    }

    private void updateStep(int eventIndex, int timestamp, int associatedValue, Object groupValue, IStepContainer currentHeads) {
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

    private boolean newStep(int eventIndex, int dateIndex, int timestamp, int associatedValue, Object groupValue,
                            IStepContainer prevHeads, IStepContainer currentHeads) {
        if (!iterableEvents[eventIndex]) {
            IHead prevHead;
            if (associatedEvents[eventIndex]) {
                prevHead = prevHeads.getHead(associatedValue);
            } else {
                prevHead = prevHeads.getHead(associatedValue);
            }
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

    private void updateEvent(int timestamp, int associatedValue, Object groupValue, IHead head, IStepContainer container) {
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

    private boolean nextEvent(int nextEventIndex, int timestamp, IHead head, int associatedValue, Object groupValue, IStepContainer container) {
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

    private void createHead(int date, int timestamp, int associatedValue, Object groupValue, IStepContainer container) {
        // 当前事务没有被使用且属于第一个事件，则新建临时IHead对象
        IHead newHead = new AHead(step.size(), date, associatedValue);
        newHead.addStep(timestamp, groupValue);
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
        for (List<IStepContainer> containers : lists) {
            for (int i = step.size() - 1; i >= 0; i--) {
                IStepContainer container = containers.get(i);
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
        for (int i = 0; i < numberOfDates; i++) {
            List<IStepContainer> containers = lists.get(i);
            for (int j = 0; j < step.size(); j++) {
                containers.get(j).reset();
            }
        }
        this.finished = new boolean[numberOfDates];
        hasNoHeadBefore = true;
    }
}
