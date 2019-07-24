package com.fr.swift.cloud.result.table.downtime;

import com.fr.swift.cloud.util.TimeUtils;
import com.fr.swift.util.Strings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
@Entity
@Table(name = "downtime_result")
public class DowntimeResult {
    @Id
    private String id;

    @Column(name = "appId")
    private String appId;

    @Column(name = "yearMonth")
    private Date yearMonth;

    @Column(name = "node")
    private String node;

    @Column(name = "downTime")
    private long downTime;

    //记录的宕机类型 signalName
    @Column(name = "recordDownType")
    private String recordDownType;

    //预测的宕机类型
    @Column(name = "predictDownType")
    private String predictDownType;

    @Column(name = "sessionNum")
    private int sessionNum;

    @Column(name = "pid")
    private int pid;

    @Column(name = "fullGcTime")
    private long fullGcTime;

    @Column(name = "cpuOverloadRate")
    private long cpuOverloadRate;

    public DowntimeResult(int pid, long downTime, String appId, String yearMonth) {
        this(pid, downTime, Strings.EMPTY, appId, yearMonth);
    }

    public DowntimeResult(int pid, long downTime, String recordDownType, String appId, String yearMonth) {
        this.pid = pid;
        this.downTime = downTime;
        this.recordDownType = recordDownType;
        this.appId = appId;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
        String key = String.valueOf(pid) + downTime + recordDownType + appId;
        this.id = UUID.nameUUIDFromBytes(key.getBytes()).toString();
    }

    public String getId() {
        return id;
    }

    public DowntimeResult setFullGcTime(long fullGcTime) {
        this.fullGcTime = fullGcTime;
        return this;
    }

    public DowntimeResult setCpuOverloadRate(long cpuOverloadRate) {
        this.cpuOverloadRate = cpuOverloadRate;
        return this;
    }

    public DowntimeResult setPredictDownType(String predictDownType) {
        this.predictDownType = predictDownType;
        return this;
    }

    public DowntimeResult setRecordDownType(String recordDownType) {
        this.recordDownType = recordDownType;
        return this;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getPid() {
        return pid;
    }

    public String getAppId() {
        return appId;
    }

    public Date getYearMonth() {
        return yearMonth;
    }

    public String getPredictDownType() {
        return predictDownType;
    }

    /**
     * 暂时swift只判断和输出TERM、CPU、OOM这三种
     */
    public enum SignalName {
        //挂断信号，例如XShell超时导致Tomcat停止
        HUP,
        //外部中断，例如CTRL + C停止Tomcat
        INT,
        //异常终止
        ABRT,
        //用户终止
        TERM,
        //进程停止，从终端读数据时
        TTIN,
        //进程停止，从终端写数据时
        TTIO,
        //CPU超时
        XCPU,
        //虚拟时钟超时
        VTALRM,
        //profile时钟超时
        PROF,
        //（自定义信号量）报表服务器内部执行System.exit(0)方法
        EXIT,
        //（自定义信号量）进程被OOM Killer杀死
        OOM
    }
}
