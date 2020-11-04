package com.fr.swift.executor.task.bean;

/**
 * @author Heng.J
 * @date 2020/11/2
 * @description
 * @since swift-1.2.0
 */
public class MigrateBean {

    private String yearMonth;

    private String target;

    public MigrateBean() {
    }

    public MigrateBean(String yearMonth, String target) {
        this.yearMonth = yearMonth;
        this.target = target;
    }

    public static MigrateBean of(String yearMonth, String target) {
        return new MigrateBean(yearMonth, target);
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "MigrateBean{" +
                "yearMonth='" + getYearMonth() + '\'' +
                ", target='" + getTarget() + '\'' +
                '}';
    }
}
