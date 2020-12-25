package com.fr.swift.executor.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Heng.J
 * @date 2020/12/25
 * @description
 * @since swift-1.2.0
 */
@Entity
@Table(name = "fine_swift_repeat_tasks")
public class SwiftRepeatTaskEntity implements Serializable {

    private static final long serialVersionUID = -2530800144146328693L;

    @Id
    private String id;

    @Column(name = "repeatKey")
    private String repeatKey;

    @Column(name = "taskContent", length = 4000)
    private String taskContent;

    public SwiftRepeatTaskEntity() {
    }

    public SwiftRepeatTaskEntity(String repeatKey, String taskContent) {
        this.id = String.valueOf(System.nanoTime());
        this.repeatKey = repeatKey;
        this.taskContent = taskContent;
    }

    public String getRepeatKey() {
        return repeatKey;
    }

    public String getTaskContent() {
        return taskContent;
    }
}
