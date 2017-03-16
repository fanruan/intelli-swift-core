package com.fr.bi.fs;

import com.fr.data.dao.DAOBean;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Date;

public class BITemplateFolderNode extends DAOBean {

    private static final long serialVersionUID = -3662026856074945968L;
    private String folderId;
    private String parentId;
    private long userId;
    private String folderName;
    private Date modifyTime;

    /**
     * 默认构造函数
     */
    public BITemplateFolderNode() {
    }

    /**
     * 构造
     *
     * @param folderId
     * @param parentId
     * @param userId
     * @param folderName
     */
    public BITemplateFolderNode(String folderId, String parentId, long userId, String folderName) {
        this.folderId = folderId;
        this.parentId = parentId;
        this.userId = userId;
        this.folderName = folderName;
        updateLastModifyTime();
    }

    /**
     * 构造
     *
     * @param folderId
     */
    public BITemplateFolderNode(String folderId, long userId, String folderName) {
        this.folderId = folderId;
        this.userId = userId;
        this.folderName = folderName;
        updateLastModifyTime();
    }

    public long getUserId () {
        return userId;
    }

    public void setUserId (long userId) {
        this.userId = userId;
    }
    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void updateLastModifyTime() {
        this.modifyTime = new Date();
    }
    /**
     * 获取最后修改时间
     *
     * @return
     */
    public Date getLastModifyTime() {
        return modifyTime;
    }

    /**
     * 设置最后修改时间
     *
     * @param date
     */
    public void setLastModifyTime(Date date) {
        this.modifyTime = date;
    }
    @Override
    public boolean equals4Properties(Object obj) {
        if (!(obj instanceof BITemplateFolderNode)) {
            return false;
        }
        return this.folderId == ((BITemplateFolderNode) obj).folderId
                && this.userId == ((BITemplateFolderNode) obj).userId
                && this.parentId == ((BITemplateFolderNode) obj).parentId
                && this.folderName == ((BITemplateFolderNode) obj).folderName;
    }

    public JSONObject createJSONConfig() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("id", folderId);
        jo.put("pId", this.parentId);
        jo.put("text", this.folderName);
        jo.put("lastModify", modifyTime.getTime());
        jo.put("value", folderId);
        return jo;
    }

    @Override
    protected int hashCode4Properties() {
        return 0;
    }
}