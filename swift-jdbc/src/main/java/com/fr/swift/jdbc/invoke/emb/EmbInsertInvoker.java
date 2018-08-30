package com.fr.swift.jdbc.invoke.emb;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.invoke.BaseInsertInvoker;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class EmbInsertInvoker extends BaseInsertInvoker {
    public EmbInsertInvoker(InsertBean insertBean) {
        super(insertBean);
    }

    @Override
    public Integer invoke() throws SQLException {
        DataMaintenanceService service = SwiftContext.get().getBean(DataMaintenanceService.class);
        return executeInsert(service);
    }


}
