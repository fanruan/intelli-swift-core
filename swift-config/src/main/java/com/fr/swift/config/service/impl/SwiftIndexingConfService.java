package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.entity.SwiftTableAllotConf;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigSessionCreator;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.service.IndexingConfService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/2
 */
@SwiftBean
public class SwiftIndexingConfService implements IndexingConfService {
    private ConfigSessionCreator tx = SwiftContext.get().getBean(ConfigSessionCreator.class);

    private BasicDao<SwiftTableAllotConf> tableConf = new BasicDao<SwiftTableAllotConf>(SwiftTableAllotConf.class);

    private BasicDao<SwiftColumnIndexingConf> columnConf = new BasicDao<SwiftColumnIndexingConf>(SwiftColumnIndexingConf.class);


    @Override
    public SwiftTableAllotConf getTableConf(final SourceKey table) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<SwiftTableAllotConf>(false) {
                @Override
                public SwiftTableAllotConf work(ConfigSession session) throws SQLException {
                    List<SwiftTableAllotConf> list = tableConf.find(session, ConfigWhereImpl.eq("tableId.tableKey", table.getId()));
                    return !list.isEmpty() ? list.get(0) : new SwiftTableAllotConf(new SourceKey(table.getId()), new LineAllotRule(LineAllotRule.STEP));
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return null;
        }
    }

    @Override
    public SwiftColumnIndexingConf getColumnConf(final SourceKey table, final String columnName) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<SwiftColumnIndexingConf>(false) {
                @Override
                public SwiftColumnIndexingConf work(ConfigSession session) throws SQLException {
                    List<SwiftColumnIndexingConf> conf = columnConf.find(session,
                            ConfigWhereImpl.eq("columnId.tableKey", table.getId()),
                            ConfigWhereImpl.eq("columnId.columnName", columnName));
                    return !conf.isEmpty() ? conf.get(0) : new SwiftColumnIndexingConf(new SourceKey(table.getId()), columnName, true, false);
                }

            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return null;
        }
    }

    @Override
    public void setTableConf(final SwiftTableAllotConf conf) {
        setConfig(tableConf, conf);
    }

    @Override
    public void setColumnConf(final SwiftColumnIndexingConf conf) {
        setConfig(columnConf, conf);
    }

    private <T> void setConfig(final SwiftConfigDao<T> dao, final T conf) {
        try {
            tx.doTransactionIfNeed(new BaseTransactionWorker() {
                @Override
                public Object work(ConfigSession session) throws SQLException {
                    dao.saveOrUpdate(session, conf);
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
        }
    }
}