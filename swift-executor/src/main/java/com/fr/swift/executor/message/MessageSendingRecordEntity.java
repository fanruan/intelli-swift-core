package com.fr.swift.executor.message;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author xiqiu
 * @date 2020/6/9
 * @description
 * @since swift 1.1
 */
@Entity
@Table(name = "message_sending_record")
public class MessageSendingRecordEntity implements Serializable {
    private static final long serialVersionUID = 2549266689255345235L;

    @Id
    @Column(name = "messageId")
    private String messageId;

    @Column(name = "appId")
    private String appId;

    @Column(name = "yearMonth")
    private String yearMonth;

    @Column(name = "javaTime")
    private long javaTime;

    @Column(name = "pythonTime")
    private long pythonTime;

    @Column(name = "messageFlag")
    private boolean messageFlag;

    @Column(name = "lateMapFlag")
    private boolean lateMapFlag;

    @Column(name = "javaResultBean")
    private String javaResultBean;

    @Column(name = "pythonResultBean")
    private String pythonResultBean;

    public MessageSendingRecordEntity() {
    }

    public MessageSendingRecordEntity(String messageId, String appId, String yearMonth, long javaTime, long pythonTime,
                                      boolean messageFlag, boolean lateMapFlag, String javaResultBean, String pythonResultBean) {
        this.messageId = messageId;
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.javaTime = javaTime;
        this.pythonTime = pythonTime;
        this.messageFlag = messageFlag;
        this.lateMapFlag = lateMapFlag;
        this.javaResultBean = javaResultBean;
        this.pythonResultBean = pythonResultBean;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public long getJavaTime() {
        return javaTime;
    }

    public void setJavaTime(long javaTime) {
        this.javaTime = javaTime;
    }

    public long getPythonTime() {
        return pythonTime;
    }

    public void setPythonTime(long pythonTime) {
        this.pythonTime = pythonTime;
    }

    public boolean isMessageFlag() {
        return messageFlag;
    }

    public void setMessageFlag(boolean messageFlag) {
        this.messageFlag = messageFlag;
    }

    public boolean isLateMapFlag() {
        return lateMapFlag;
    }

    public void setLateMapFlag(boolean lateMapFlag) {
        this.lateMapFlag = lateMapFlag;
    }

    public String getJavaResultBean() {
        return javaResultBean;
    }

    public void setJavaResultBean(String javaResultBean) {
        this.javaResultBean = javaResultBean;
    }

    public String getPythonResultBean() {
        return pythonResultBean;
    }

    public void setPythonResultBean(String pythonResultBean) {
        this.pythonResultBean = pythonResultBean;
    }
}
