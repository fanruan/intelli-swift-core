package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SwiftColumnIdxConfBean;
import com.fr.swift.config.bean.SwiftTableAllotConfBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.service.IndexingConfService;
import com.fr.swift.converter.FindList;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/7/2
 */
@SwiftBean
public class SwiftIndexingConfService implements IndexingConfService {
    private TransactionManager tx = SwiftContext.get().getBean(TransactionManager.class);

    private BasicDao<SwiftTableAllotConfBean> tableConf = new BasicDao<SwiftTableAllotConfBean>(SwiftTableAllotConfBean.TYPE);

    private BasicDao<SwiftColumnIdxConfBean> columnConf = new BasicDao<SwiftColumnIdxConfBean>(SwiftColumnIdxConfBean.TYPE);


    @Override
    public SwiftTableAllotConfBean getTableConf(final SourceKey table) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<SwiftTableAllotConfBean>(false) {
                @Override
                public SwiftTableAllotConfBean work(ConfigSession session) throws SQLException {
                    FindList<SwiftTableAllotConfBean> list = tableConf.find(session, ConfigWhereImpl.eq("tableId.tableKey", table.getId()));
                    return !list.isEmpty() ? list.get(0) : new SwiftTableAllotConfBean(table.getId(), new LineAllotRule(LineAllotRule.STEP));
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return null;
        }
    }

    @Override
    public SwiftColumnIdxConfBean getColumnConf(final SourceKey table, final String columnName) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<SwiftColumnIdxConfBean>(false) {
                @Override
                public SwiftColumnIdxConfBean work(ConfigSession session) throws SQLException {
                    FindList<SwiftColumnIdxConfBean> conf = columnConf.find(session,
                            ConfigWhereImpl.eq("columnId.tableKey", table.getId()),
                            ConfigWhereImpl.eq("columnId.columnName", columnName));
                    return !conf.isEmpty() ? conf.get(0) : new SwiftColumnIdxConfBean(table.getId(), columnName, true, false);
                }

            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return null;
        }
    }

    @Override
    public void setTableConf(final SwiftTableAllotConfBean conf) {
        setConfig(tableConf, conf);
    }

    @Override
    public void setColumnConf(final SwiftColumnIdxConfBean conf) {
        setConfig(columnConf, conf);
    }

    private <T extends ObjectConverter> void setConfig(final SwiftConfigDao<T> dao, final T conf) {
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