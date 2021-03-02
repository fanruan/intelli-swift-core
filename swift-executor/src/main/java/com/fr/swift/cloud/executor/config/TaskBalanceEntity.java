package com.fr.swift.cloud.executor.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/3
 */
@Entity
@Table(name = "fine_swift_task_balance")
public class TaskBalanceEntity implements Serializable {

    @Id
    private String id;

    @Column(name = "clusterId")
    private String clusterId;

    @Column(name = "taskType")
    private String taskType;

    @Column(name = "weight")
    private double weight;

    private TaskBalanceEntity() {
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getTaskType() {
        return taskType;
    }

    public double getWeight() {
        return weight;
    }
}
