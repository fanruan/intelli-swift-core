package com.fr.bi.fs;

import com.fr.data.dao.DAOBean;
import com.fr.json.JSONObject;

public class BISharedReportNode extends DAOBean {
    private long reportId;
    private long createBy;
    private long shareTo;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(long createBy) {
        this.createBy = createBy;
    }

    public long getShareTo() {
        return shareTo;
    }

    public void setShareTo(long shareTo) {
        this.shareTo = shareTo;
    }

    public BISharedReportNode(long id) {
        this.id = id;
    }

    @Override
    protected int hashCode4Properties() {
        return 0;
    }

    @Override
    public boolean equals4Properties(Object o) {
        return false;
    }

    public BISharedReportNode(){

    }

    public BISharedReportNode(long reportId, long createBy, long shareTo) {
        this.reportId = reportId;
        this.createBy = createBy;
        this.shareTo = shareTo;
    }

    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("reportId")) {
            this.reportId = jo.getLong("reportId");
        }
        if (jo.has("createBy")) {
            this.createBy = jo.getLong("createBy");
        }
        if (jo.has("shareTo")) {
            this.shareTo = jo.getLong("shareTo");
        }
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("reportId", this.reportId);
        jo.put("createBy", this.createBy);
        jo.put("shareTo", this.shareTo);
        return jo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BISharedReportNode that = (BISharedReportNode) o;

        if (reportId != that.reportId) return false;
        if (createBy != that.createBy) return false;
        return shareTo == that.shareTo;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (reportId ^ (reportId >>> 32));
        result = 31 * result + (int) (createBy ^ (createBy >>> 32));
        result = 31 * result + (int) (shareTo ^ (shareTo >>> 32));
        return result;
    }
}