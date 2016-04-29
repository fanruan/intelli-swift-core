package com.fr.bi.stable.report.result;

import com.fr.bi.stable.report.key.TargetGettingKey;

import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2014/9/30.
 */
public interface BINode extends SummaryContainer, LightNode {

    public Comparable getChildBottomNValueLine(int n);

    public double getChildAVGValue(TargetGettingKey key);

    public <T extends BINode> List<T> getChilds();

    public int getTotalLength();

    public boolean isEmptyChilds();

    public int getSummarySize();

    public Iterator getSummaryValueIterator();
}