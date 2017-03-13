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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (reportId ^ (reportId >>> 32));
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
		return result;
	}

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
		BIReportNodeLock other = (BIReportNodeLock) obj;
		if (reportId != other.reportId){
			return false;
		}
		if (sessionId == null) {
			if (other.sessionId != null){
				return false;
			}
		} else if (!ComparatorUtils.equals(sessionId, other.sessionId)){
			return false;
		}
		if (userId != other.userId){
			return false;
		}
		return true;
	}
	
	public BIReportNodeLock(){
	}
	
	public BIReportNodeLock(String sessionId, long userId, long reportId){
		this.sessionId = sessionId;
		this.userId = userId;
		this.reportId = reportId;
		this.lockedTime = System.currentTimeMillis();
	}
	//
	//访问的sessionId
	private String sessionId;
	//创建者
	private long userId;
	//报表id
	private long reportId;

	//锁占用时间
	private long lockedTime = System.currentTimeMillis();
	public String getSessionId(){
		return sessionId;
	}

	public void updateLockedTime(){
		this.lockedTime = System.currentTimeMillis();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -1947786480683007710L;

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

	@Override
	public String toString() {
		return "BIReportNodeLock{" +
				"sessionId='" + sessionId + '\'' +
				", userId=" + userId +
				", reportId=" + reportId +
				", lockedTime=" + new Date(lockedTime).toString() +
				'}';
	}
}