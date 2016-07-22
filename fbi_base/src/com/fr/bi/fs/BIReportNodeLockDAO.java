/**
 * 
 */
package com.fr.bi.fs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.fs.dao.PlatformDataAccessObject;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

/**
 * @author Daniel
 *
 */
public class BIReportNodeLockDAO extends PlatformDataAccessObject {
	
	private static BIReportNodeLockDAO SC = null;
	
	public static BIReportNodeLockDAO getInstance(){
		SC = BIConstructorUtils.constructObject(BIReportNodeLockDAO.class, SC);
		SC.clearLocks();
		return SC;
	}
	
	public BIReportNodeLock getLock(String sessionId, long userId, long reportId){
		Map<String, Object> fvMap = new HashMap<String, Object>();
		fvMap.put(BITableMapper.BI_REPORT_NODE_LOCK.REPORT_ID, reportId);
		fvMap.put(BITableMapper.BI_REPORT_NODE_LOCK.FIELD_USERID, userId);
		fvMap.put(BITableMapper.BI_REPORT_NODE_LOCK.SESSIONID, sessionId);
		List<BIReportNodeLock> list = createSession().listByFieldValues(BIReportNodeLock.class, fvMap);
		if(list.isEmpty()){
			return null;
		} else {
			return list.get(0);
		}
	}
	
	private void lock(BIReportNodeLock lock){
		createSession().saveOrUpdate(lock);
	}
	
	public void release(BIReportNodeLock lock){
		createSession().delete(lock);
	}
	
	public void clearLocks(){
		createSession().delete(BIReportNodeLock.class);
	}


	/**
	 * 获取模板被哪些用户session lock
	 * @param reportId
	 * @return
	 */
	public List<BIReportNodeLock> getLock(long reportId) {
		Map<String, Object> fvMap = new HashMap<String, Object>();
		fvMap.put(BITableMapper.BI_REPORT_NODE_LOCK.REPORT_ID, reportId);
		return createSession().listByFieldValues(BIReportNodeLock.class, fvMap);
	}


	/**
	 * 获取模板被哪些用户session lock
	 * @param userId 当前用户id
	 * @param reportId
	 * @return
     */
	public List<BIReportNodeLock> getLock(long userId, long reportId) {
		Map<String, Object> fvMap = new HashMap<String, Object>();
		fvMap.put(BITableMapper.BI_REPORT_NODE_LOCK.FIELD_USERID, userId);
		fvMap.put(BITableMapper.BI_REPORT_NODE_LOCK.REPORT_ID, reportId);
		return createSession().listByFieldValues(BIReportNodeLock.class, fvMap);
	}

	/**
	 * @param sessionId
	 * @param userId
	 * @param reportId
	 */
	public boolean lock(String sessionId, long userId, long reportId) {
		if(StringUtils.isEmpty(sessionId)){
			return false;
		}
		List<BIReportNodeLock> lock = getLock(userId, reportId);
		if(lock == null || lock.size() == 0){
			synchronized (this) {
				lock =  getLock(userId, reportId);
				if(lock == null || lock.size() == 0) {
					BIReportNodeLock l = new BIReportNodeLock(sessionId, userId, reportId);
					lock(l);
					lock = new ArrayList<BIReportNodeLock>();
					lock.add(l);
				}
			}
		}
		for(BIReportNodeLock l : lock) {
			if(ComparatorUtils.equals(sessionId, l.getSessionId())){
				return true;
			}
		}
		return false;
		
	}

	/**
	 * 浏览器异常关闭的情况下应该允许强制lock，需要人工判断
	 * @param sessionId
	 * @param userId
	 * @param reportId
     */
	public void forceLock(String sessionId, long userId, long reportId) {
		boolean isLock = lock(sessionId, userId, reportId);
		if(!isLock){
			synchronized (this) {
				BIReportNodeLock lock = getLock(sessionId, userId, reportId);
				if(lock == null) {
					lock = new BIReportNodeLock(sessionId, userId, reportId);
					lock(lock);
				}
			}
		}
	}

}