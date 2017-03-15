package com.fr.bi.fs;

import com.finebi.cube.common.log.BILoggerFactory;
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
 * Created by wang on 2016/9/9.
 */
public class BIFineDBConfigLockDAO extends PlatformDataAccessObject {
    private static BIFineDBConfigLockDAO SC = null;


    public static BIFineDBConfigLockDAO getInstance() {
        SC = BIConstructorUtils.constructObject(BIFineDBConfigLockDAO.class, SC);
        return SC;
    }

    public BIFineDBConfigLock getLock(String sessionId, long userId) {
        Map<String, Object> fvMap = new HashMap<String, Object>();
        fvMap.put(BITableMapper.BI_BUSINESS_PACK_CONFIG_LOCK.LOCK_USERID, userId);
        fvMap.put(BITableMapper.BI_BUSINESS_PACK_CONFIG_LOCK.SESSIONID, sessionId);
        List<BIFineDBConfigLock> list = createSession().listByFieldValues(BIFineDBConfigLock.class, fvMap);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    private void lock(BIFineDBConfigLock lock) {
        BILoggerFactory.getLogger().info("lock " + lock.toString());
        createSession().saveOrUpdate(lock);
    }

    public void updateLock(String sessionId, long userId) {
        BIFineDBConfigLock lock = getLock(sessionId, userId);
        if (lock != null) {
            lock.updateLockedTime();
            createSession().update(lock);
        }
    }

    public void release(BIFineDBConfigLock lock) {
        BILoggerFactory.getLogger().info("release " + lock.toString());
        createSession().delete(lock);
    }

    public void clearLocks() {
        createSession().delete(BIFineDBConfigLock.class);
    }


    /**
     * 获取模板被哪些用户session lock
     *
     * @param userId 当前用户id
     * @return
     */
    public List<BIFineDBConfigLock> getLock(long userId) {
        Map<String, Object> fvMap = new HashMap<String, Object>();
        fvMap.put(BITableMapper.BI_BUSINESS_PACK_CONFIG_LOCK.LOCK_USERID, userId);
        return createSession().listByFieldValues(BIFineDBConfigLock.class, fvMap);
    }

    /**
     * @param sessionId
     * @param userId
     */
    public boolean lock(String sessionId, long userId) {
        if (StringUtils.isEmpty(sessionId)) {
            return false;
        }
        List<BIFineDBConfigLock> lock = getLock(userId);
        if (lock == null || lock.size() == 0) {
            synchronized (this) {
                lock = getLock(userId);
                if (lock == null || lock.size() == 0) {
                    BIFineDBConfigLock l = new BIFineDBConfigLock(sessionId, userId);
                    lock(l);
                    lock = new ArrayList<BIFineDBConfigLock>();
                    lock.add(l);
                }
            }
        }
        for (BIFineDBConfigLock l : lock) {
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
     */
    public void forceLock(String sessionId, long userId) {
        boolean isLock = lock(sessionId, userId);
        if (!isLock) {
            synchronized (this) {
                BIFineDBConfigLock lock = getLock(sessionId, userId);
                if (lock == null) {
                    lock = new BIFineDBConfigLock(sessionId, userId);
                    lock(lock);
                }
            }
        }
    }

    public boolean isLocked(String sessionId, long userId) {
        return getLock(sessionId, userId) != null;
    }

    public void transfer(BIFineDBConfigLock var1) throws Exception {
        this.createSession(DatabaseAction.TRANSFER).transfer(var1);
    }

}

