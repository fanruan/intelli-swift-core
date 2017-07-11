package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;

import java.util.List;

/**
 * Created by 小灰灰 on 2017/7/7.
 */
public interface NodeCreator {
    Node createNode(int sumLen);

    Node createNode(Object data, int sumLength);

    List<TargetAndKey> createTargetAndKeyList(TargetAndKey targetAndKey);
}
