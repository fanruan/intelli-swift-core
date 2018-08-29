package com.fr.swift.jdbc.invoke.emb;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.jdbc.bean.CreateTableBean;
import com.fr.swift.jdbc.invoke.BaseCreateTableInvoke;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class EmbCreateTableInvoke extends BaseCreateTableInvoke {
    public EmbCreateTableInvoke(CreateTableBean bean) {
        super(bean);
    }

    @Override
    public Integer invoke() throws SQLException {
        DataMaintenanceService service = SwiftContext.get().getBean(DataMaintenanceService.class);
        return service.createTable(bean.getDatabase(), bean.getTableName(), bean.getColumns());
    }
}
