package com.fr.swift.jdbc.invoke;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.jdbc.bean.InsertBean;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public abstract class BaseInsertInvoker implements SqlInvoker<Integer> {

    protected InsertBean insertBean;

    public BaseInsertInvoker(InsertBean insertBean) {
        this.insertBean = insertBean;
    }

    protected Integer executeInsert(DataMaintenanceService service) throws SQLException {
        try {
            if (null != insertBean.getQueryJson()) {
                return service.insert(insertBean.getSchema(), insertBean.getTableName(), insertBean.getQueryJson());
            } else {
                return service.insert(insertBean.getSchema(), insertBean.getTableName(), insertBean.getColumnNames(), insertBean.getDatas());
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Type getType() {
        return Type.INSERT;
    }
}
