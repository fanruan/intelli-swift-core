package com.fr.swift.boot;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cloud.kafka.MessageConsumer;
import com.fr.swift.cloud.task.TreasureUploadTask;
import com.fr.swift.executor.task.ExecutorTypeContainer;
import com.fr.swift.executor.type.ExecutorTaskType;

/**
 * This class created on 2019/5/9
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
public class CloudBoot {

    private MessageConsumer consumer;

    public CloudBoot() {
        ExecutorTypeContainer.getInstance().registerClass(ExecutorTaskType.TREASURE_UPLOAD, TreasureUploadTask.class);
        // TODO: 2019/5/10 by lucifer 移动到配置
        consumer = new MessageConsumer("__fine_intelli_treasure_upload__");
        consumer.start();
    }
}
