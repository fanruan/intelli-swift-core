package com.fr.swift.jdbc.invoke.emb;

import com.fr.swift.api.rpc.TableService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.jdbc.bean.CreateTableBean;
import com.fr.swift.jdbc.invoke.BaseCreateTableInvoker;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class EmbCreateTableInvoker extends BaseCreateTableInvoker {
    public EmbCreateTableInvoker(CreateTableBean bean) {
        super(bean);
    }

    @Override
    public Integer invoke() throws SQLException {
        TableService service = SwiftContext.get().getBean(TableService.class);
        try {
            return service.createTable(bean.getDatabase(), bean.getTableName(), bean.getColumns());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
