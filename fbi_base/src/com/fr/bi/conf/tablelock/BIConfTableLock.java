package com.fr.bi.conf.tablelock;

import com.fr.data.dao.DAOBean;

/**
 * Created by Young's on 2016/12/21.
 */
public class BIConfTableLock extends DAOBean {

    private long userId;
    private String sessionId;
    private String tableId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @Override
    protected int hashCode4Properties() {
        return 0;
    }

    @Override
    public boolean equals4Properties(Object o) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BIConfTableLock that = (BIConfTableLock) o;

        if (userId != that.userId) return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;
        return tableId != null ? tableId.equals(that.tableId) : that.tableId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (tableId != null ? tableId.hashCode() : 0);
        return result;
    }

    public BIConfTableLock() {

    }

    public BIConfTableLock(String sessionId, long userId, String tableId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.tableId = tableId;
    }
}
