package com.fr.bi.cal.generate.timerTask;


import com.fr.bi.conf.manager.update.source.UpdateSettingSource;

import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BICubeTimeTaskCreatorProvider {
    String XML_TAG = "BICubeTimeTaskCreatorProvider";

    List<Timer> reGenerateTimeTasks(long userId, Map<String, UpdateSettingSource> allTimeTaskMap);

    void removeTimeTasks(List<Timer> timerList);
}