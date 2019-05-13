package com.fr.swift.boot;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cloud.CloudProperty;
import com.fr.swift.cloud.kafka.MessageConsumer;
import com.fr.swift.cloud.task.CloudTaskType;
import com.fr.swift.cloud.task.TreasureUploadTask;
import com.fr.swift.executor.task.ExecutorTypeContainer;

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
        ExecutorTypeContainer.getInstance().registerClass(CloudTaskType.TREASURE_UPLOAD, TreasureUploadTask.class);
        consumer = new MessageConsumer(CloudProperty.getProperty().getTreasureUploadTopic());
        consumer.start();
    }
}
