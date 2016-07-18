package com.fr.bi.cal.stable.engine;

import com.fr.general.ComparatorUtils;

/**
 * 实时报表cube管理 key值
 *
 * @author guy
 */
public class TempCubeTask {

    private long userId;
    private String md5;
    private String tableId;

    public TempCubeTask(String md5, String tableId, long userId) {
        this.userId = userId;
        this.md5 = md5;
        this.tableId = tableId;
    }

    public TempCubeTask(TempCubeTask task) {
        this.userId = task.getUserId();
        this.md5 = task.getMd5();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TempCubeTask)) {
            return false;
        }

        TempCubeTask key = (TempCubeTask) o;

        if (userId != key.userId) {
            return false;
        }
        if (md5 == null) {
            if (key.md5 != null) {
                return false;
            }
        } else if (key.md5 == null) {
            return false;
        }
        return ComparatorUtils.equals(md5, key.md5);

    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (md5 == null ? 0 : md5.hashCode());
        return result;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMd5() {
        return md5;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}