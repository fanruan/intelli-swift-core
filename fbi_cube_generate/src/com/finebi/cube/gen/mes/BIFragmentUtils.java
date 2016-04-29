package com.finebi.cube.gen.mes;

import com.finebi.cube.impl.router.fragment.BIFragmentID;
import com.finebi.cube.impl.router.fragment.BIFragmentTag;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentUtils {
    public static IFragmentTag generateFragment(ITopicTag targetTopic, ITableSource tableSource) {
        return generateFragment(targetTopic, tableSource.getSourceID());
    }

    public static IFragmentTag generateFragment(ITopicTag targetTopic, BITableSourceRelationPath relationPath) {
        return generateFragment(targetTopic, relationPath.getSourceID());
    }

    public static IFragmentTag generateFragment(ITopicTag targetTopic, BITableSourceRelation relation) {
        BITableSourceRelationPath relationPath = new BITableSourceRelationPath();
        try {
            relationPath.addRelationAtHead(relation);
            return generateFragment(targetTopic, relationPath.getSourceID());
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    public static IFragmentTag generateFragment(ITopicTag targetTopic, String fragmentID) {
        return new BIFragmentTag(new BIFragmentID(fragmentID), targetTopic);
    }
}
