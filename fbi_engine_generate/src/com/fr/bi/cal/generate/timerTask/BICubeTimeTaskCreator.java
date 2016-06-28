package com.fr.bi.cal.generate.timerTask;


import java.util.List;
import java.util.Timer;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BICubeTimeTaskCreator {
    String XML_TAG = "BICubeTimeTaskCreator";

    List<Timer> reGenerateTimeTasks(long userId, Map<String, UpdateSettingSource> allTimeTaskMa);

    void removeTimeTasks(List<Timer> timerList);
}