package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 小灰灰 on 14-2-28.
 */
public class RootNode extends Node {

    public static final int ALL_PAGE = -1;
    public static final int CONSTRUCT_NODE = 0;
    /**
     *
     */
    private static final long serialVersionUID = -7756734178296131122L;
    private volatile boolean constructed = false;

    private volatile boolean lock = false;

    private ArrayList<RootNodeChild> pageNodes = new ArrayList<RootNodeChild>();


    public RootNode(DimensionCalculator key, Object data) {
        super(key, data);

    }

    public void lock() {
        lock = true;
    }

    public void releaseLock() {
        lock = false;
    }

    public boolean isLock() {
        return lock;
    }

    public RootNodeChild getNodeByPage(int page) {
        int pos = page - 1;
        if (pageNodes.size() > pos) {
            return pageNodes.get(pos);
        }
        while (true) {
            synchronized (this) {
                if (pageNodes.size() > pos) {
                    return pageNodes.get(pos);
                }
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
            if (pageNodes.size() > pos) {
                return pageNodes.get(pos);
            }
        }
    }

    public Node getNewNodeByPage(int page) {
        if (page == ALL_PAGE) {
            while (!this.constructed) {
                synchronized (this) {
                    if (this.constructed) {
                        break;
                    }
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
            Node child = getNodeByPage(1);
            Node n = new Node(key, child.getData());
            Node tmp = child.getFirstChild();
            if (tmp == null) {
                return n;
            }
            n.addChild(tmp.createCloneNodeWithoutChild());
            while (tmp.getSibling() != null) {
                tmp = tmp.getSibling();
                n.addChild(tmp.createCloneNodeWithoutChild());
            }
            return n;
        } else {
            Node child = getNodeByPage(page);
            Node n = new Node(key, child.getData());
            int len = child.getChildLength();
            for (int i = 0; i < len; i++) {
                n.addChild(child.getChild(i).createCloneNodeWithoutChild());
            }
            return n;
        }
    }

    public RootNodeChild getNode(int index, int length) {
        RootNodeChild n = new RootNodeChild(key, this.getData(), this.getGroupValueIndex());
        n.setParent(this);
        Node c = getChild(index);
        if (c == null || length == 0) {
            return n;
        }
        n.addChild(c);
        if (length == ALL_PAGE) {
            while (!this.constructed) {
                synchronized (this) {
                    if (this.constructed) {
                        break;
                    }
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
            while (true) {
                c = c.getSibling();
                if (c == null) {
                    break;
                }
                n.addChild(c);
            }
        } else {
            while (length-- > 1) {
                while (c.getSibling() == null) {
                    synchronized (this) {
                        if (c.getSibling() != null) {
                            break;
                        }
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                        }
                    }
                }
                c = c.getSibling();
                n.addChild(c);
            }
        }
        return n;
    }

    public void setSibing(int page) {
        if (page != 1) {
            CubeReadingUtils.setSibing(pageNodes.get(page - 2), pageNodes.get(page - 1));
        }
        if (page != pageNodes.size()) {
            CubeReadingUtils.setSibing(pageNodes.get(page - 1), pageNodes.get(page));
        }
    }

    @Override
    public Node getChild(int index) {
        return getNodeByPage(index / BIBaseConstant.X64_PAGESPLITROWSTEP + 1).getChild(index % BIBaseConstant.X64_PAGESPLITROWSTEP);
    }

    public boolean isConstructed() {
        return constructed;
    }

    public void addNode(RootNodeChild node) {
        node.setParent(this);
        pageNodes.add(node);
        int size = pageNodes.size();
        if (size > 1) {
            CubeReadingUtils.setSibing(pageNodes.get(size - 2), pageNodes.get(size - 1));
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void finishConstruction() {
        constructed = true;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public boolean isPageCalculated(int page) {
        return getNodeByPage(page).isCalculated();
    }

    public void setPageNotCalculated() {
        Iterator<RootNodeChild> it = pageNodes.iterator();
        while (it.hasNext()) {
            it.next().prepareReCalculate();
        }
    }

    public ArrayList<Integer> getPages(int index, int length) {
        ArrayList<Integer> al = new ArrayList<Integer>();
        if (length == ALL_PAGE) {
            while (!this.constructed) {
                synchronized (this) {
                    if (this.constructed) {
                        break;
                    }
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
            for (int i = 0; i < pageNodes.size(); i++) {
                al.add(i + 1);
            }
        } else {
            al.add(index / BIBaseConstant.X64_PAGESPLITROWSTEP + 1);
            al.add((index + length - 1) / BIBaseConstant.X64_PAGESPLITROWSTEP + 1);
        }
        return al;
    }

    public void clearChilds() {
        pageNodes = new ArrayList<RootNodeChild>();
    }

    @Override
    public int getChildLength() {
        int len = 0;
        Iterator<RootNodeChild> it = pageNodes.iterator();
        while (it.hasNext()) {
            len += it.next().getChildLength();
        }
        return len;
    }

    public int getPageCount() {
        return pageNodes.size();
    }
}