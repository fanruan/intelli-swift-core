package com.fr.swift.quartz.config;

/**
 * @author Heng.J
 * @date 2020/5/17
 * @description
 * @since swift 1.1
 */
public enum ScheduleTaskType {
    PART(SchedulerProperty.get().getExecutorMachineId()), ALL, MIGRATE, COLLATE;

    private String machineId;

    ScheduleTaskType() {
    }

    ScheduleTaskType(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }
}
