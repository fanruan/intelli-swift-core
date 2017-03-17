package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.result.NodeExpander;

public interface NodeDimensionIterator {

    void setExpander(NodeExpander expander);

    void setRoot(IRootDimensionGroup root);

    IRootDimensionGroup getRoot();

    int[] getStartIndex();

    void moveNext();

    GroupConnectionValue next();

    void pageEnd();

    boolean hasPrevious();

    boolean hasNext();

    int getPageIndex();

    void moveToShrinkStartValue(Object[] value);

    void moveLastPage();

    NodeDimensionIterator createClonedIterator();
}