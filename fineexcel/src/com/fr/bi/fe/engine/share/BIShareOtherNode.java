package com.fr.bi.fe.engine.share;

import com.fr.data.dao.DAOBean;

import java.util.UUID;

/**
 * Created by sheldon on 15-1-19.
 */
public class BIShareOtherNode extends DAOBean {

    private long user_id;         //分享的登录用户id
    private long mode_id;         //分享的模板id
    private boolean isShared = true;     //当前模板状态是否分享的
    private String shared_id;

    public BIShareOtherNode(){
        shared_id = UUID.randomUUID().toString();
    }

    public BIShareOtherNode(long user_id, long mode_id, boolean shared) {
        this.user_id = user_id;
        this.mode_id = mode_id;
        this.isShared = shared;

        shared_id = UUID.randomUUID().toString();
    }

    public BIShareOtherNode(long user_id, long mode_id) {
        this.user_id = user_id;
        this.mode_id = mode_id;
        this.isShared = true;

        shared_id = UUID.randomUUID().toString();
    }

    public String getShared_id(){
        return shared_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public long getMode_id() {
        return mode_id;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setMode_id(long mode_id) {
        this.mode_id = mode_id;
    }

    public void setShared(boolean isShared) {
        this.isShared = isShared;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BIShareOtherNode that = (BIShareOtherNode) o;

        if (mode_id != that.mode_id) {
            return false;
        }
        if (user_id != that.user_id) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals4Properties(Object obj) {
        if ( !(obj instanceof BIShareOtherNode)) {
            return false;
        }
        BIShareOtherNode arn = (BIShareOtherNode) obj;
        return this.getUser_id() == arn.getUser_id()
                && this.getMode_id() == arn.getMode_id();

    }

    @Override
    public int hashCode() {
        int result = (int) (user_id ^ (user_id >>> 32));
        result = 31 * result + (int) (mode_id ^ (mode_id >>> 32));
        return result;
    }

    @Override
    protected int hashCode4Properties() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (getUser_id() ^ (getUser_id() >>> 32));
        result = prime * result + (int) (getMode_id() ^ (getMode_id() >>> 32));
        return result;
    }
}