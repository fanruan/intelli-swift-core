package com.fr.bi.cal.analyze.cal.store;

import com.fr.bi.cal.analyze.cal.result.DiskBaseRootNode;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.cache.list.IntList;

import java.util.Comparator;

/**
 * Created by 小灰灰 on 2014/10/10.
 */
public class NodePageTraveller {
    private int index;

    private transient Node child;

    private DiskBaseRootNode root;

    private IntList pageValues = new IntList();

    private Comparator comparator;

    public NodePageTraveller(DiskBaseRootNode node) {
        index = 0;
        this.root = node;
        this.comparator = node.getComparator();
        child = node.getNodeByPage(1).getFirstChild();
    }

    public DiskBaseRootNode getRoot() {
        return root;
    }

    public Object getValue() {
        if (child == null) {
            return null;
        }
        return child.getData();
    }

    public void writePageIndex() {
        pageValues.add(index);
        synchronized (this) {
            this.notifyAll();
        }
    }

    public Comparator getCompactor() {
        return comparator;
    }

    public Node getRootNode(Node child) {
        Node temp = child;
        while (temp != null && temp.getParent() != null) {
            temp = temp.getParent();
        }
        return temp;
    }

    public void compareValue(Object value) {
        if (child == null) {
            return;
        }
        if (comparator.compare(child.getData(), value) <= 0) {
            while (child == null && (!root.isFinishPageCalculated())) {
                synchronized (root) {
                    if (child == null || root.isFinishPageCalculated()) {
                        break;
                    }
                    try {
                        root.wait();
                    } catch (InterruptedException e) {
                    }
                    child = root.getChild(index);
                }

            }
            if (root.isFinishPageCalculated() && root.getPageCount() < (index + 1) / BIBaseConstant.X64_PAGESPLITROWSTEP + 1) {
                child = null;
                return;
            }
            index++;
            child = root.getChild(index);
        }
    }

    public int getPageSize() {
        return pageValues.size();
    }

    /**
     * @param i
     * @return
     */
    public int get(int i) {
        if (pageValues.size() > i) {
            return pageValues.get(i);
        }
        while (true) {
            synchronized (this) {
                if (pageValues.size() > i) {
                    return pageValues.get(i);
                }
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
            if (pageValues.size() > i) {
                return pageValues.get(i);
            }
        }
    }
}