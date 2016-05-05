/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.io.io.write.SerializableCacheReadWriter;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.collection.map.lru.LRUWithKHashMap;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.stable.utils.code.BILogger;


public class DiskBaseRootNode extends Node implements Release {

    private final static int CACHE_PAGE_SIZE = 16;

    /**
     *
     */
    private static final long serialVersionUID = 4888156727013632352L;


    private volatile boolean pageCalculated = false;


    private SerializableCacheReadWriter<DiskBaseRootNodeChild> pageNodes = new SerializableCacheReadWriter<DiskBaseRootNodeChild>();

    private LRUWithKHashMap<Integer, RootNodeChild> pageTempMap = new LRUWithKHashMap<Integer, RootNodeChild>(CACHE_PAGE_SIZE);

    public DiskBaseRootNode(DimensionCalculator key, Object data) {
        super(key, data);
    }

    /**
     * 计算指标
     */
    public void finishPageCalculate() {
        pageCalculated = true;
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * 是否计算完
     *
     * @return 完成计算了
     */
    public boolean isFinishPageCalculated() {
        return pageCalculated;
    }

    public RootNodeChild getNodeByPage(final int page) {

        return pageTempMap.get(page, new ValueCreator<RootNodeChild>() {

            @Override
            public RootNodeChild createNewObject() {
                int pos = page - 1;
                if (pageNodes.size() > pos) {
                    DiskBaseRootNodeChild pageChild = pageNodes.get(pos);
                    RootNodeChild rc = pageChild.createRootNodeChild();
                    rc.setParent(DiskBaseRootNode.this);
                    return rc;
                }
                while (true) {
                    synchronized (DiskBaseRootNode.this) {
                        if (pageNodes.size() > pos) {
                            DiskBaseRootNodeChild pageChild = pageNodes.get(pos);
                            RootNodeChild rc = pageChild.createRootNodeChild();
                            rc.setParent(DiskBaseRootNode.this);
                            return rc;
                        }
                        try {
                            DiskBaseRootNode.this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    if (pageNodes.size() > pos) {
                        DiskBaseRootNodeChild pageChild = pageNodes.get(pos);
                        RootNodeChild rc = pageChild.createRootNodeChild();
                        rc.setParent(DiskBaseRootNode.this);
                        return rc;
                    }
                }
            }
        });

    }

    public Node getNewNodeByPage(int page) {
        if (page == RootNode.ALL_PAGE) {
            while (!this.pageCalculated) {
                synchronized (this) {
                    if (this.pageCalculated) {
                        break;
                    }
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                                BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
            int size = this.pageNodes.size();
            Node n = new Node(null, null);
            for (int i = 0; i < size; i++) {
                Node child = getNodeByPage(i + 1);
                for (int j = 0; j < child.getChildLength(); j++) {
                    n.addChild(child.getChild(j).createCloneNodeWithoutChild());
                }
            }
            return n;
        } else {
            Node child = getNodeByPage(page);
            Node n = new Node(child.key, child.getData());
            int len = child.getChildLength();
            for (int i = 0; i < len; i++) {
                n.addChild(child.getChild(i).createCloneNodeWithoutChild());
            }
            return n;
        }
    }

//    public RootNodeChild getNode(int index, int length) {

    /**
     * 获取第page也，便宜
     *
     * @param page
     * @param shift
     * @return
     */
    public Node getNewNodeByPage(int page, int shift) {
        if (page == RootNode.ALL_PAGE) {
            while (!this.pageCalculated) {
                synchronized (this) {
                    if (this.pageCalculated) {
                        break;
                    }
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                                BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
            int size = this.pageNodes.size();
            Node n = new Node(null, null);
            for (int i = 0; i < size; i++) {
                Node child = getNodeByPage(i + 1);
                for (int j = 0; j < child.getChildLength(); j++) {
                    n.addChild(child.getChild(j).createCloneNodeWithoutChild());
                }
            }
            return n;
        } else {
            Node child = getNodeByPage(page);
            Node n = new Node(child.key, child.getData());
            int len = child.getChildLength();
            for (int i = shift; i < len; i++) {
                n.addChild(child.getChild(i).createCloneNodeWithoutChild());
            }
            if (this.getPageCount() > page) {
                Node child2 = getNodeByPage(page + 1);
                len = Math.min(shift, child2.getChildLength());
                for (int i = 0; i < shift; i++) {
                    n.addChild(child2.getChild(i).createCloneNodeWithoutChild());
                }
            }

            return n;
        }
    }

    /**
     * 获取前面count个的Node
     *
     * @param count 个数
     * @return 新的node
     */
    @Override
    public Node createBeforeCountNode(int count) {
        Node n = new Node(key, this.getData());
        int len = Math.min(this.getChildLength(), count);
        for (int i = 0; i < len; i++) {
            n.addChild(this.getChild(i).createCloneNodeWithoutChild());
        }
        return n;
    }
//    }

    @Override
    public Node getChild(int index) {
        return getNodeByPage(index / BIBaseConstant.X64_PAGESPLITROWSTEP + 1).getChild(index % BIBaseConstant.X64_PAGESPLITROWSTEP);
    }


    /**
     * 添加一个node
     *
     * @param node 新的
     */
    public void addNode(DiskBaseRootNodeChild node) {
        pageNodes.add(node);
        synchronized (this) {
            this.notifyAll();
        }
    }


    public int getPageCount() {
        return pageNodes.size();
    }

    /**
     * 释放
     */
    @Override
    public void releaseResource() {
        pageNodes.releaseResource();
    }

}