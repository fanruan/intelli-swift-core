package com.fr.bi.web.base.operation;

import com.fr.json.JSONArray;

/**
 * Created by Connery on 14-11-14.
 */
public class BIOperationStore implements Runnable {


    private static Object lock = new Object();
    private String reportName;
    private long userId;
    private long id;
    private String content;
    private JSONArray interAction;

    /**
     * 初始化
     *
     * @param userId
     * @param reportId
     * @param reportName
     * @param content
     */
    public BIOperationStore(long userId, long reportId, String reportName, String content, JSONArray interActions) {
        this.reportName = reportName;
        this.userId = userId;
        this.id = reportId;
        this.content = content;
        this.interAction = interActions;
    }

    /**
     * 开始线程记录
     */
    public void startRecording() {
        Thread thread = new Thread(this);
        thread.start();
    }


    @Override
    /**
     * 开始线程
     */
    public void run() {
        try {

            synchronized (lock) {
                BIOperationRecord record = new BIOperationRecord(userId, id, reportName);
                record.save(content);
            }
        } catch (Exception ex) {
            return;
        }

    }


}