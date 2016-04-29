package com.fr.bi.stable.report.result;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by 小灰灰 on 2014/9/30.
 */
public interface BICrossNode extends Serializable, SummaryContainer {
    BICrossNode getLeftFirstChild();

    BICrossNode getBottomSibling();

    BICrossNode getLeftChildByKey(Object ob);

    BICrossNode getLeftChild(int index);

    BICrossNode getLeftParent();

    BINode getLeft();

    int getLeftChildLength();

    Map getSummaryValue();
}