package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2017/7/10.
 */
public class GroupNodeCreator implements NodeCreator {
    @Override
    public Node createNode(int sumLen) {
        return new Node(sumLen);
    }

    @Override
    public Node createNode(Object data, int sumLength) {
        return new Node(data, sumLength);
    }

    @Override
    public List<TargetAndKey> createTargetAndKeyList(TargetAndKey targetAndKey) {
        List<TargetAndKey> list = new ArrayList<TargetAndKey>();
        list.add(targetAndKey);
        return list;
    }
}
