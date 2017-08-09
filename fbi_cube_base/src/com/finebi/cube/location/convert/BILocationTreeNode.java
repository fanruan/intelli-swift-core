package com.finebi.cube.location.convert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class BILocationTreeNode<T> implements Serializable {
    protected T locationPath;
    protected T parentPath;
    protected boolean isLeaf;
    protected BILocationTreeNode<T> parentNode;
    protected Set<BILocationTreeNode<T>> childList;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BILocationTreeNode<T> that = (BILocationTreeNode<T>) o;

        return locationPath != null ? locationPath.equals(that.locationPath) : that.locationPath == null;

    }

    @Override
    public int hashCode() {
        return locationPath != null ? locationPath.hashCode() : 0;
    }

    public BILocationTreeNode(T locationPath, boolean isLeaf) {
        this.locationPath = locationPath;
        this.isLeaf = isLeaf;
        if (!isLeaf) {
            initChildList();
        }
    }

    public T getParentPath() {
        return parentPath;
    }

    public void setLocationPath(T locationPath) {
        this.locationPath = locationPath;
    }

    public void setParentPath(T parentPath) {
        this.parentPath = parentPath;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public T getLocationPath() {
        return locationPath;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    /* 插入一个child节点到当前节点中 */
    public void addChildNode(BILocationTreeNode treeNode) {
        initChildList();
        childList.add(treeNode);
        treeNode.setParentNode(this);
    }

    public void initChildList() {
        if (childList == null) {
            childList = new CopyOnWriteArraySet<BILocationTreeNode<T>>();
        }
    }

    /* 返回当前节点的父辈节点集合 */
    public List<BILocationTreeNode> getElders() {
        List<BILocationTreeNode> elderList = new ArrayList<BILocationTreeNode>();
        BILocationTreeNode<T> parentNode = this.getParentNode();
        if (parentNode == null) {
            return elderList;
        } else {
            elderList.add(parentNode);
            elderList.addAll(parentNode.getElders());
            return elderList;
        }
    }

    /* 返回当前节点的晚辈集合 */
    public Set<BILocationTreeNode<T>> getJuniors() {
        Set<BILocationTreeNode<T>> juniorList = new HashSet<BILocationTreeNode<T>>();
        Set<BILocationTreeNode<T>> childList = this.getChildList();
        if (childList == null) {
            return juniorList;
        } else {
            for (BILocationTreeNode<T> childNode : childList) {
                juniorList.add(childNode);
                juniorList.addAll(childNode.getJuniors());
            }
            return juniorList;
        }
    }

    /* 返回当前节点的孩子集合 */
    public Set<BILocationTreeNode<T>> getChildList() {
        return childList;
    }


    public boolean insertJuniorNode(BILocationTreeNode<T> treeNode) {
        T path = treeNode.getParentPath();
        if (this.isLeaf) {
            return false;
        }
        if (this.locationPath.equals(path)) {
            addChildNode(treeNode);
            return true;
        } else {
            boolean insertFlag;
            Set<BILocationTreeNode<T>> childList = this.getChildList();
            for (BILocationTreeNode<T> childNode : childList) {
                insertFlag = childNode.insertJuniorNode(treeNode);
                if (insertFlag == true) {
                    return true;
                }
            }
            return false;
        }
    }

    /* 找到一颗树中某个节点 */
    public BILocationTreeNode<T> findTreeNodeByLocationPath(T nodePath) {
        if (this.locationPath.equals(nodePath)) {
            return this;
        }
        if (this.isLeaf) {
            return null;
        } else {
            Set<BILocationTreeNode<T>> childList = this.getChildList();
            for (BILocationTreeNode<T> childNode : childList) {
                BILocationTreeNode<T> resultNode = childNode.findTreeNodeByLocationPath(nodePath);
                if (resultNode != null) {
                    return resultNode;
                }
            }
            return null;
        }
    }

    /**
     * 按照树路径查找到一系列的叶子节点
     *
     * @param prefixPath
     * @return
     */
    public List<BILocationTreeNode<T>> getAllLeafs(T prefixPath) {
        return getAllLeafs(findTreeNodeByLocationPath(prefixPath));
    }

    /**
     * 查找该节点下的所有叶子节点的集合
     *
     * @param node
     * @return
     */
    private List<BILocationTreeNode<T>> getAllLeafs(BILocationTreeNode<T> node) {
        if (node == null) {
            return new ArrayList<BILocationTreeNode<T>>();
        }
        List<BILocationTreeNode<T>> leafs = new ArrayList<BILocationTreeNode<T>>();
        if (node.isLeaf()) {
            leafs.add(node);
            return leafs;
        } else {
            Set<BILocationTreeNode<T>> childList = node.getChildList();
            for (BILocationTreeNode<T> childNode : childList) {
                leafs.addAll(childNode.getAllLeafs(childNode));
            }
            return leafs;
        }
    }

    /* 遍历一棵树，层次遍历 */
    public void traverse() {
        // TODO: 2017/7/19
    }

    public void removeChild(T nodePath) {
        BILocationTreeNode<T> node = findTreeNodeByLocationPath(nodePath);
        BILocationTreeNode<T> parent = node.getParentNode();
        if (parent.getChildList() != null) {
            BILocationTreeNode<T> delete = new BILocationTreeNode<T>(nodePath, true);
            parent.getChildList().remove(delete);
        }
    }

    public void setChildList(Set<BILocationTreeNode<T>> childList) {
        this.childList = childList;
    }


    private BILocationTreeNode<T> getParentNode() {
        return parentNode;
    }

    public void setParentNode(BILocationTreeNode<T> parentNode) {
        this.parentNode = parentNode;
    }

}  