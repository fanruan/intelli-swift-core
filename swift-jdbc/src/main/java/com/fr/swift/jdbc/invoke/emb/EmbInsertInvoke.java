package com.fr.swift.jdbc.invoke.emb;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.invoke.BaseInsertInvoke;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class EmbInsertInvoke extends BaseInsertInvoke {
    public EmbInsertInvoke(InsertBean insertBean) {
        super(insertBean);
    }

    @Override
    public Integer invoke() throws SQLException {
        DataMaintenanceService service = SwiftContext.get().getBean(DataMaintenanceService.class);
        if (null != insertBean.getQueryJson()) {
            return service.insert(insertBean.getSchema(), insertBean.getTableName(), insertBean.getQueryJson());
        } else {
            return service.insert(insertBean.getSchema(), insertBean.getTableName(), insertBean.getColumnNames(), insertBean.getDatas());
        }
    }
}
