package com.finebi.cube.gen.mes;

import com.finebi.cube.impl.router.fragment.BIFragmentID;
import com.finebi.cube.impl.router.fragment.BIFragmentTag;
import com.finebi.cube.router.fragment.IFragmentID;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuildFragmentTag extends BIFragmentTag {
    public static BICubeBuildFragmentTag getCubeOccupiedFragment(ITopicTag topicTag) {
        return new BICubeBuildFragmentTag(new BIFragmentID("Finebi_Occupied_Fragment"), topicTag);

    }

    public BICubeBuildFragmentTag(IFragmentID fragmentID, ITopicTag topicTag) {
        super(fragmentID, topicTag);
    }
}
