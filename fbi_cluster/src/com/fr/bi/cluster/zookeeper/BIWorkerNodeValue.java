package com.fr.bi.cluster.zookeeper;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by Connery on 2015/3/27.
 */
public class BIWorkerNodeValue {
    public static final int TASK_BEGIN = 100;
    public static final int TASK_LOAD_DB = 101;
    public static final int TASK_BASIC_INDEX = 102;
    public static final int TASK_FIRST_INDEX = 103;
    public static final int TASK_IN_USE_LINK_INDEX = 104;
    public static final int TASK_REPLACE = 105;
    public static final int TASK_END = 200;
    //当前worker正常处理分配的任务
    public static int STATUS_ACCEPT = 1;
    //改任务已经完成
    public static int STATUS_FINISH = 2;
    //新任务
    public static int NEW = 3;
    private long id;
    private int taskName;
    private String taskContent;
    private int status;
    private long userId;

    private String basePath = "";

    private String tmpPath = "";

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMissionID() {
        return id;
    }

    public int getTaskName() {
        return taskName;
    }

    public void setTaskName(int taskName) {
        this.taskName = taskName;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void init(byte[] bytes) {
        if (bytes.length != 0) {
            String content = new String(bytes);
            JSONObject jsonContent;
            try {
                jsonContent = new JSONObject(content);
                this.taskContent = jsonContent.has("taskContent") ? jsonContent.getString("taskContent") : "";
                this.taskName = jsonContent.getInt("taskName");
                this.id = jsonContent.getInt("id");
                this.status = jsonContent.getInt("status");
                this.userId = jsonContent.getLong("userId");
                this.basePath = jsonContent.getString("basePath");
                this.tmpPath = jsonContent.getString("tmpPath");
            } catch (JSONException ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String toString() {
        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("taskName", taskName);
            jsonContent.put("taskContent", taskContent);
            jsonContent.put("status", status);
            jsonContent.put("id", id);
            jsonContent.put("userId", userId);
            jsonContent.put("basePath", basePath);
            jsonContent.put("tmpPath", tmpPath);
        } catch (JSONException ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        }

        return jsonContent.toString();
    }

    public void setDefault() {
        id = 100;
        status = NEW;
        taskName = 0;
        taskContent = "defaultContent";
        userId = -999;
//        basePath = BIConfUtils.createUserTotalPath(userId);
//        tmpPath = BIConfUtils.createUserTotalTempPath(userId);
    }

    public byte[] toByte() {
        return toString().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIWorkerNodeValue)) {
            return false;
        }

        BIWorkerNodeValue value = (BIWorkerNodeValue) o;

        if (id != value.id) {
            return false;
        }
        if (status != value.status) {
            return false;
        }
        if (taskName != value.taskName) {
            return false;
        }
        if (userId != value.userId) {
            return false;
        }
        if (!ComparatorUtils.equals(taskContent, value.taskContent)) {
            return false;
        }
        if (!ComparatorUtils.equals(basePath, value.basePath)) {
            return false;
        }
        if (!ComparatorUtils.equals(tmpPath, value.tmpPath)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + taskName;
        result = 31 * result + taskContent.hashCode();
        result = 31 * result + basePath.hashCode();
        result = 31 * result + tmpPath.hashCode();
        result = 31 * result + status;
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }
}