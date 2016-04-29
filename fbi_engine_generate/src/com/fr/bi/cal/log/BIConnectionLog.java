package com.fr.bi.cal.log;

import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

public abstract class BIConnectionLog implements JSONCreator {

    protected long userId;
    private RelationColumnKey ck;

    BIConnectionLog() {
    }

    public BIConnectionLog(long userId) {
        this.userId = userId;
    }

    public BIConnectionLog(RelationColumnKey ck, long userId) {
        this(userId);
        this.setColumnFieldKey(ck);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public RelationColumnKey getColumnFieldKey() {
        return ck;
    }

    public void setColumnFieldKey(RelationColumnKey ck) {
        this.ck = ck;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return JSON对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (ck != null) {
            jo = ck.createJSON();
        }
        return jo;
    }

    /**
     * 正在运行
     *
     * @return 是否正在运行
     */
    public boolean isRunning() {
        return false;
    }


    public abstract long getTime();
}