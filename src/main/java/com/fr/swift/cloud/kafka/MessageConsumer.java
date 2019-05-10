package com.fr.swift.cloud.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.cloud.CloudProperty;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.cloud.task.TreasureUploadTask;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This class created on 2019/5/8
 *
 * @author Lucifer
 * @description
 */
public class MessageConsumer extends ShutdownableThread {
    private final KafkaConsumer<Integer, String> consumer;
    private final String[] topics;
    ObjectMapper jsonMapper;

    public MessageConsumer(String... topics) {
        super("TaskConsumerExample", true);
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CloudProperty.getProperty().getKafkaServerUrl() + ":" + CloudProperty.getProperty().getKafkaServerPort());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CloudProperty.getProperty().getKafkaGroupId());
        // 不启用自动commit，消费完手动commit
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        // 新加入的consumer处理所有历史数据
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // 简单认证
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");

        consumer = new KafkaConsumer<Integer, String>(props);
        this.topics = topics;
        jsonMapper = new ObjectMapper();
        consumer.subscribe(Arrays.asList(this.topics));
    }

    @Override
    public void doWork() {
        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(5));
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>();
        for (ConsumerRecord<Integer, String> record : records) {
            SwiftLoggers.getLogger().info("Received message: ({}, {}) at offset {}", record.key(), record.value(), record.offset());

            switch (record.topic()) {
                // TODO: 2019/5/10 by lucifer 移动到配置
                case "__fine_intelli_treasure_upload__":
                    try {
                        TreasureBean treasureBean = jsonMapper.readValue(record.value(), TreasureBean.class);
                        ExecutorTask executorTask = new TreasureUploadTask(treasureBean);
                        executorTasks.add(executorTask);
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                    break;
                default:
            }
        }

        try {
            boolean produceOk = TaskProducer.produceTasks(executorTasks);
            if (produceOk) {
//                consumer.commitSync();
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
