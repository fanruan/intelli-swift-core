package com.finebi.cube.gen.mes;

import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.impl.router.fragment.BIFragmentID;
import com.finebi.cube.impl.router.fragment.BIFragmentTag;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentUtils {
    public static IFragmentTag generateFragment(ITopicTag targetTopic, CubeTableSource tableSource) {
        return generateFragment(targetTopic, tableSource.getSourceID());
    }

    public static IFragmentTag generateFragment(ITopicTag targetTopic, BITableSourceRelationPath relationPath) {
        return generateFragment(targetTopic, getPathID(relationPath));
    }
    private static String getPathID(BITableSourceRelationPath relationPath){
      return   BIRelationIDUtils.calculatePathID(relationPath);
    }

    private static String getRelationID(BITableSourceRelation relation){
        return   BIRelationIDUtils.calculateRelationID(relation);
    }
    public static IFragmentTag generateFragment(ITopicTag targetTopic, BITableSourceRelation relation) {
            return generateFragment(targetTopic,getRelationID( relation));
    }

    public static IFragmentTag generateFragment(ITopicTag targetTopic, String fragmentID) {
        return new BIFragmentTag(new BIFragmentID(fragmentID), targetTopic);
    }
}
