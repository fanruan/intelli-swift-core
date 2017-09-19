package com.finebi.cube.conf;

import java.util.Set;

/**
 * Created by Lucifer on 2017-5-19.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public interface ICubeGenerateTask {

    long getUserId();

    String getTableSourceId();

    Integer getUpdateType();

    ITaskCalculator getTaskCalculator();

    boolean isOk2Merge();

    ICubeGenerateTask merge(ICubeGenerateTask mergeCubeGenerateTask);

    String getTaskInfo();

    Set<String> getAllsSourceIds();
}
