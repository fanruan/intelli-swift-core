package com.finebi.cube.gen.mes;

import com.finebi.cube.impl.router.topic.BITopicID;
import com.finebi.cube.impl.router.topic.BITopicTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.stable.data.source.CubeTableSource;


/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicUtils {
    public static ITopicTag generateTopicTag(CubeTableSource tableSource) {
        return generateTopicTag(tableSource.getSourceID());
    }

    public static ITopicTag generateTopicTag(String topicTagID) {
        return new BITopicTag(new BITopicID(topicTagID));
    }
}
