package com.fr.bi.cal.generate.timerTask;


import java.util.List;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BICubeTimeTaskCreatorProvider {
    String XML_TAG = "BICubeTimeTaskCreatorProvider";

    void reGenerateTimeTasks(long userId, List<TimerTaskSchedule> scheduleList);

    void removeAllTimeTasks(long userId, List<TimerTaskSchedule> scheduleList);
}
