package com.fr.bi.fs;

import com.fr.data.dao.DAOBean;
import com.fr.general.ComparatorUtils;

/**
 * Created by wang on 2016/9/9.
 */
public class BIFineDBConfigLock extends DAOBean {
    private static final long serialVersionUID = -1947786480683007710L;
    public final static String CONFIG_LOCK = "config_lock";
    //锁名称，配置锁暂时只有一个
    private String lockName;
    //    用户ID
    private long userId;
    //访问的sessionId
    private String sessionId;

    private long lockedTime;

    	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!super.equals(obj)){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        BIFineDBConfigLock other = (BIFineDBConfigLock) obj;
        if (lockName == null) {
            if (other.lockName != null){
                return false;
            }
        } else if (!ComparatorUtils.equals(lockName, other.lockName)){
            return false;
        }
        if (userId != other.userId){
            return false;
        }
        if(!sessionId.equals(other.getSessionId())){
            return false;
        }
        return true;
    }

    public BIFineDBConfigLock(){
        this.lockName = CONFIG_LOCK;
    }

    public BIFineDBConfigLock(String sessionId,long userId){
        this.lockName = CONFIG_LOCK;
        this.userId = userId;
        this.sessionId = sessionId;
        this.lockedTime = System.currentTimeMillis();
    }
    public String getLockName() {
        return lockName;
    }

    public long getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getLockedTime() {
        return lockedTime;
    }

    public void updateLockedTime(){
        this.lockedTime = System.currentTimeMillis();
    }

    @Override
    protected int hashCode4Properties() {
        return 0;
    }

    @Override
    public boolean equals4Properties(Object o) {
        return false;
    }
}
