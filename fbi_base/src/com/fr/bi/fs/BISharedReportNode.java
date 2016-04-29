package com.fr.bi.fs;

import com.fr.data.dao.DAOBean;

public class BISharedReportNode extends DAOBean {
    private long bid = -1;
    private long userid = -1;

    public BISharedReportNode() {

    }

    public BISharedReportNode(long bid, long userId) {
        this.setBid(bid);
        this.userid = userId;
    }

    /**
     * 返回报表ID
     *
     * @return
     */
    public long getBid() {
        return bid;
    }

    /**
     * 设置报表ID
     *
     * @param bid
     */
    public void setBid(long bid) {
        this.bid = bid;
    }

    /**
     * 返回用户ID
     *
     * @return
     */
    public long getUserId() {
        return userid;
    }

    /**
     * 设置用户ID
     *
     * @param userId
     */
    public void setUserId(long userId) {
        this.userid = userId;
    }

    /* (non-Javadoc)
     * @see com.fr.data.dao.DAOBean#equals4Properties(java.lang.Object)
     */
    @Override
    public boolean equals4Properties(Object obj) {
        if (!(obj instanceof BISharedReportNode)) {
            return false;
        }
        return this.getBid() == ((BISharedReportNode) obj).getBid()
                && this.userid == ((BISharedReportNode) obj).userid;
    }

    /* (non-Javadoc)
     * @see com.fr.data.dao.DAOBean#hashCode4Properties()
     */
    @Override
    protected int hashCode4Properties() {
        final int prime = 31;
        int result = (int) (getBid() ^ (getBid() >>> 32));
        result = prime * result + (int) (userid ^ (userid >>> 32));
        return result;
    }
}