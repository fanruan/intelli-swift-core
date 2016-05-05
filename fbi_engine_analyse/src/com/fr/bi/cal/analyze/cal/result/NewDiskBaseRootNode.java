/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.common.inter.Release;


public class NewDiskBaseRootNode extends Node implements Release {

    /**
     *
     */
    private static final long serialVersionUID = -4908242676423817007L;
    private ColumnNodeList pageNodes = new ColumnNodeList();

    public NewDiskBaseRootNode() {
        this(null);
    }


    public NewDiskBaseRootNode(DimensionCalculator ck) {
        super(ck, null);
    }

    @Override
    public void releaseResource() {
        pageNodes.clear();
    }


    public ColumnNodeList getPageNodes() {
        return pageNodes;
    }

    public void addChild(NewDiskBaseRootNodeChild node) {
        synchronized (pageNodes) {
            pageNodes.add(node);
        }
    }

    @Override
    public NewDiskBaseRootNodeChild getChild(int index) {
        synchronized (pageNodes) {
            if (pageNodes.size() > index) {
                return pageNodes.get(index);
            }
        }
        //TODO 考虑是否这里提交运算请求
        return null;
    }

    public MemNode getMemChild(int index) {
        synchronized (pageNodes) {
            if (pageNodes.size() > index) {
                return pageNodes.getMemNode(index);
            }
        }
        //TODO 考虑是否这里提交运算请求
        return null;
    }

    @Override
    public int getChildLength() {
        return pageNodes.size();
    }

    public int getIndexByValue(Object value) {
        return getMinCompareValue(0, getChildLength() - 1, value);
    }


    /**
     * 找出等于前值或者正好大一点的那个值
     *
     * @param start
     * @param end
     * @param value
     * @return
     */
    private int getMinCompareValue(int start, int end, Object value) {
        if (start > end) {
            return start;
        }
        int index = (start + end) / 2;
        NewDiskBaseRootNodeChild c = pageNodes.get(index);
        int result = getComparator().compare(value, c.getData());
        if (result > 0) {
            return getMinCompareValue(index + 1, end, value);
        } else if (result < 0) {
            return getMinCompareValue(start, index - 1, value);
        } else {
            return index;
        }
    }
}