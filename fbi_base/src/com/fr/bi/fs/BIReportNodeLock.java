/**
 * 
 */
package com.fr.bi.fs;

import com.fr.data.dao.DAOBean;
import com.fr.general.ComparatorUtils;

import java.util.Date;

/**
 * @author Daniel
 *
 */
public class BIReportNodeLock extends DAOBean {

	private static final long serialVersionUID = -1947786480683007710L;

	//
	//访问的sessionId
	private String sessionId;
	//创建者
	private long userId;
	//报表id
	private long reportId;
	//	当前编辑用户的id
	private long currentEditUserId;

	//锁占用时间
	private long lockedTime = System.currentTimeMillis();
	
	public BIReportNodeLock(){
	}
	public BIReportNodeLock(String sessionId, long userId, long reportId,long currentEditUserId){
		this.sessionId = sessionId;
		this.userId = userId;
		this.reportId = reportId;
		this.currentEditUserId = currentEditUserId;
		this.lockedTime = System.currentTimeMillis();
	}

	public String getSessionId(){
		return sessionId;
	}

	public void updateLockedTime(){
		this.lockedTime = System.currentTimeMillis();
	}


	/* (non-Javadoc)
	 * @see com.fr.data.dao.DAOBean#hashCode4Properties()
	 */
	@Override
	protected int hashCode4Properties() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.fr.data.dao.DAOBean#equals4Properties(java.lang.Object)
	 */
	@Override
	public boolean equals4Properties(Object obj) {
		return true;
	}

	public long getUserId() {
		return userId;
	}

	public long getReportId() {
		return reportId;
	}

	public long getLockedTime() {
		return lockedTime;
	}

	public long getCurrentEditUserId() {
		return currentEditUserId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()){
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		BIReportNodeLock that = (BIReportNodeLock) o;
		if (userId != that.userId) {
			return false;
		}
		if (reportId != that.reportId){
			return false;
		}
		if (currentEditUserId != that.currentEditUserId) {
			return false;
		}
		if (lockedTime != that.lockedTime){
			return false;
		}
		if (sessionId == null) {
			if (that.sessionId != null){
				return false;
			}
		}
		else if (!ComparatorUtils.equals(sessionId, that.sessionId)){
			return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (sessionId != null ? sessionId.hashCode() : 0);
		result = prime * result + (int) (userId ^ (userId >>> 32));
		result = prime * result + (int) (reportId ^ (reportId >>> 32));
		result = prime * result + (int) (currentEditUserId ^ (currentEditUserId >>> 32));
		result = prime * result + (int) (lockedTime ^ (lockedTime >>> 32));
		return result;
	}


	@Override
	public String toString() {
		return "BIReportNodeLock{" +
				"sessionId='" + sessionId + '\'' +
				", userId=" + userId +
				", reportId=" + reportId +
				", currentEditUserId=" + currentEditUserId +
				", lockedTime=" + lockedTime +
				'}';
	}
}