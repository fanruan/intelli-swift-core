package com.fr.bi.cal.stable.engine;

import com.fr.bi.stable.data.Table;
import com.fr.general.ComparatorUtils;

/**
 * 实时报表cube管理 key值
 *
 * @author guy
 */
public class TempCubeTask {

    private long userId;
    private Table tableKey;

    public TempCubeTask(Table tableKey, long userId) {
        this.userId = userId;
        this.tableKey = tableKey;
    }

    public TempCubeTask(TempCubeTask task) {
        this.userId = task.getUserId();
        this.tableKey = task.getTableKey();
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
        if (tableKey == null) {
            if (key.tableKey != null) {
                return false;
            }
        } else {
            if (key.tableKey == null) {
                return false;
            }
        }
        return ComparatorUtils.equals(tableKey, key.tableKey);

    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (tableKey == null ? 0 : tableKey.hashCode());
        return result;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Table getTableKey() {
        return tableKey;
    }

    public void setTableKey(Table tableKey) {
        this.tableKey = tableKey;
    }
}