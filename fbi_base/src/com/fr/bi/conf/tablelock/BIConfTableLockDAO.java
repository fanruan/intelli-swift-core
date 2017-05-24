package com.fr.bi.conf.tablelock;

import com.fr.bi.fs.BITableMapper;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.data.dao.DatabaseAction;
import com.fr.fs.dao.PlatformDataAccessObject;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Young's on 2016/12/21.
 */
public class BIConfTableLockDAO extends PlatformDataAccessObject {
    private static BIConfTableLockDAO SC = null;

    public static BIConfTableLockDAO getInstance() {
        SC = BIConstructorUtils.constructObject(BIConfTableLockDAO.class, SC);
        return SC;
    }

    public BIConfTableLock getLock(String sessionId, long userId, String tableId) {
        Map<String, Object> fvMap = new HashMap<String, Object>();
        fvMap.put(BITableMapper.BI_CONF_TABLE_LOCK.TABLE_ID, tableId);
        fvMap.put(BITableMapper.BI_CONF_TABLE_LOCK.USER_ID, userId);
        fvMap.put(BITableMapper.BI_CONF_TABLE_LOCK.SESSION_ID, sessionId);
        List<BIConfTableLock> list = createSession().listByFieldValues(BIConfTableLock.class, fvMap);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public BIConfTableLock getLock(String tableId) {
        Map<String, Object> fvMap = new HashMap<String, Object>();
        fvMap.put(BITableMapper.BI_CONF_TABLE_LOCK.TABLE_ID, tableId);
        List<BIConfTableLock> list = createSession().listByFieldValues(BIConfTableLock.class, fvMap);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    private void lock(BIConfTableLock lock) {
        createSession().saveOrUpdate(lock);
    }

    public void release(BIConfTableLock lock) {
        createSession().delete(lock);
    }

    public void clearLocks() {
        createSession().delete(BIConfTableLock.class);
    }


    /**
     * 获取模板被哪些用户session lock
     *
     * @param userId  当前用户id
     * @param tableId
     * @return
     */
    public List<BIConfTableLock> getLock(long userId, String tableId) {
        Map<String, Object> fvMap = new HashMap<String, Object>();
//        fvMap.put(BITableMapper.BI_CONF_TABLE_LOCK.USER_ID, userId);
        fvMap.put(BITableMapper.BI_CONF_TABLE_LOCK.TABLE_ID, tableId);
        return createSession().listByFieldValues(BIConfTableLock.class, fvMap);
    }

    /**
     * @param sessionId
     * @param userId
     * @param tableId
     */
    public boolean lock(String sessionId, long userId, String tableId) {
        if (StringUtils.isEmpty(sessionId)) {
            return false;
        }
        List<BIConfTableLock> lock = getLock(userId, tableId);
        if (lock == null || lock.size() == 0) {
            synchronized (this) {
                lock = getLock(userId, tableId);
                if (lock == null || lock.size() == 0) {
                    BIConfTableLock l = new BIConfTableLock(sessionId, userId, tableId);
                    lock(l);
                    lock = new ArrayList<BIConfTableLock>();
                    lock.add(l);
                }
            }
        }
        for (BIConfTableLock l : lock) {
            if (ComparatorUtils.equals(sessionId, l.getSessionId())) {
                return true;
            }
        }
        return false;

    }

    /**
     * 浏览器异常关闭的情况下应该允许强制lock，需要人工判断
     *
     * @param sessionId
     * @param userId
     * @param tableId
     */
    public void forceLock(String sessionId, long userId, String tableId) {
        boolean isLock = lock(sessionId, userId, tableId);
        if (!isLock) {
            synchronized (this) {
                BIConfTableLock lock = getLock(sessionId, userId, tableId);
                if (lock == null) {
                    lock = new BIConfTableLock(sessionId, userId, tableId);
                    lock(lock);
                }
            }
        }
    }

    public void transfer(BIConfTableLock lock) throws Exception {
        this.createSession(DatabaseAction.TRANSFER).transfer(lock);
    }
}
