package com.fr.swift.config.service.impl;

import com.fr.swift.config.ColumnIndexingConf;
import com.fr.swift.config.TableAllotConf;
import com.fr.swift.config.bean.SwiftColumnIdxConfBean;
import com.fr.swift.config.bean.SwiftTableAllotConfBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.RestrictionFactory;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;
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
public class SwiftIndexingConfService implements IndexingConfService {
    private TransactionManager tx;

    private BasicDao<SwiftTableAllotConfBean> tableConf = new BasicDao<SwiftTableAllotConfBean>(SwiftTableAllotConfBean.TYPE, RestrictionFactoryImpl.INSTANCE);

    private BasicDao<SwiftColumnIdxConfBean> columnConf = new BasicDao<SwiftColumnIdxConfBean>(SwiftColumnIdxConfBean.TYPE, RestrictionFactoryImpl.INSTANCE);

    private RestrictionFactory factory = RestrictionFactoryImpl.INSTANCE;

    @Override
    public TableAllotConf getTableConf(final SourceKey table) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<TableAllotConf>(false) {
                @Override
                public TableAllotConf work(ConfigSession session) throws SQLException {
                    FindList<SwiftTableAllotConfBean> list = tableConf.find(session, factory.eq("tableId.tableKey", table.getId()));
                    SwiftTableAllotConfBean bean = !list.isEmpty() ? list.get(0) : new SwiftTableAllotConfBean(table.getId(), new LineAllotRule(LineAllotRule.STEP));
                    return (TableAllotConf) bean.convert();
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return null;
        }
    }

    @Override
    public ColumnIndexingConf getColumnConf(final SourceKey table, final String columnName) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<ColumnIndexingConf>(false) {
                @Override
                public ColumnIndexingConf work(ConfigSession session) throws SQLException {
                    FindList<SwiftColumnIdxConfBean> conf = columnConf.find(session, factory.eq("columnId.tableKey", table.getId()), factory.eq("columnId.columnName", columnName));
                    SwiftColumnIdxConfBean bean = !conf.isEmpty() ? conf.get(0) : new SwiftColumnIdxConfBean(table.getId(), columnName, true, false);
                    return (ColumnIndexingConf) bean.convert();
                }

            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return null;
        }
    }

    @Override
    public void setTableConf(final TableAllotConf conf) {
        setConfig(tableConf, conf);
    }

    @Override
    public void setColumnConf(final ColumnIndexingConf conf) {
        setConfig(columnConf, conf);
    }

    private <T extends ObjectConverter> void setConfig(final SwiftConfigDao<T> dao, final Object conf) {
        try {
            tx.doTransactionIfNeed(new BaseTransactionWorker() {
                @Override
                public Object work(ConfigSession session) throws SQLException {
                    dao.saveOrUpdate(session, (T) ((ObjectConverter) conf).convert());
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
        }
    }
}