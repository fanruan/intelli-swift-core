package com.finebi.cube.gen.mes;

import com.finebi.cube.impl.router.topic.BITopicID;
import com.finebi.cube.impl.router.topic.BITopicTag;
import com.finebi.cube.router.topic.ITopicID;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuildTopicTag extends BITopicTag {
    public static BICubeBuildTopicTag START_BUILD_CUBE = new BICubeBuildTopicTag(new BITopicID("Finebi_Start_Build_Cube"));
    public static BICubeBuildTopicTag FINISH_BUILD_CUBE = new BICubeBuildTopicTag(new BITopicID("Finebi_Finish_Build_Cube"));
    public static BICubeBuildTopicTag STOP_BUILD_CUBE = new BICubeBuildTopicTag(new BITopicID("Finebi_Stop_Build_Cube"));
    public static BICubeBuildTopicTag DATA_SOURCE_TOPIC = new BICubeBuildTopicTag(new BITopicID("Finebi_Data_Source_Table"));
    public static BICubeBuildTopicTag DATA_TRANSPORT_TOPIC = new BICubeBuildTopicTag(new BITopicID("Finebi_Data_Transport"));
    public static BICubeBuildTopicTag DATA_SOURCE_FINISH_TOPIC = new BICubeBuildTopicTag(new BITopicID("Finebi_Data_Source_Finish_Table"));
    public static BICubeBuildTopicTag PATH_TOPIC = new BICubeBuildTopicTag(new BITopicID("Finebi_Path_Topic"));
    public static BICubeBuildTopicTag PATH_FINISH_TOPIC = new BICubeBuildTopicTag(new BITopicID("Finebi_Finish_Path_Topic"));
    public static BICubeBuildTopicTag BI_CUBE_OCCUPIED_TOPIC = new BICubeBuildTopicTag(new BITopicID("Finebi_Occupied_Topic"));

    private BICubeBuildTopicTag(ITopicID topicID) {
        super(topicID);
    }

}
