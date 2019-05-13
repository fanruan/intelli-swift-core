package com.fr.swift.cloud.kafka;

import com.fr.swift.cloud.CloudProperty;
import com.fr.swift.log.SwiftLoggers;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * This class created on 2019/5/8
 *
 * @author Lucifer
 * @description
 */
public class MessageProducer {
    private KafkaProducer<Integer, String> producer;

    public MessageProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CloudProperty.getProperty().getKafkaServerUrl() + ":" + CloudProperty.getProperty().getKafkaServerPort());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "uploadTask");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 简单认证
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        producer = new KafkaProducer<Integer, String>(props);
    }

    public void produce(String topic, String message) throws ExecutionException, InterruptedException {
        producer.send(new ProducerRecord<Integer, String>(topic, message)).get();
        SwiftLoggers.getLogger().info("Sent message: [" + message + "]");
    }
}
