package com.fr.bi.report.result;

import com.fr.bi.report.key.TargetGettingKey;
import com.fr.general.NameObject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Hiram on 2015/1/26.
 */
public interface BINode extends SummaryContainer, BIGroupNode {

    //Object getData();

    //int getChildLength();

    BINode getChild(int i);

    BINode getFirstChild();

    BINode getSibling();

    void setSibling(BINode sibling);

    BINode getParent();

    void setParent(BINode parent);

    BINode getChild(Object key);

    void addChild(BINode child);

    void setData(Object data);

    String getShowValue();

    void setShowValue(String showValue);

    Comparable getChildTOPNValueLine(int N);

    Comparable getChildBottomNValueLine(int n);

    Comparator getComparator();

    BINode getLastChild();

    BINode createSortedNode(NameObject targetSort,
                            Map<String, TargetGettingKey> targetsMap);

    double getChildTOPNValueLine(TargetGettingKey targetKey, int n);

    double getChildAVGValue(TargetGettingKey key);

    <T extends BINode> List<T> getChilds();

    int getTotalLength();

    int getTotalLengthWithSummary();

    int getDeep();

    /**
     * 获得根node的整体的deep,
     * attention：这个deep==使用的维度数，只有根node能获得。
     *
     * @return
     */
    int getFrameDeep();

    int setFrameDeep(int deep);
}