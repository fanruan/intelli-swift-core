package com.fr.swift.generate.conf.service;

import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.indexing.ColumnIndexingConf;
import com.fr.swift.config.indexing.TableIndexingConf;
import com.fr.swift.config.service.IndexingConfService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/2
 */
public class SwiftIndexingConfService implements IndexingConfService {
    private HibernateTransactionManager tx = SwiftContext.get().getBean(HibernateTransactionManager.class);

    private SwiftConfigDao<TableIndexingConf> tableConf = new BasicDao<TableIndexingConf>(TableIndexingConf.class);

    private SwiftConfigDao<ColumnIndexingConf> columnConf = new BasicDao<ColumnIndexingConf>(ColumnIndexingConf.class);

    @Override
    public TableIndexingConf getTableConf(final SourceKey table) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<TableIndexingConf>() {
                @Override
                public TableIndexingConf work(Session session) {
                    List<TableIndexingConf> tableConfs = tableConf.find(session,
                            Restrictions.eq("id.tableKey", table.getId())
                    );
                    return tableConfs.isEmpty() ? null : tableConfs.get(0);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public ColumnIndexingConf getColumnConf(final SourceKey table, final String columnName) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<ColumnIndexingConf>() {
                @Override
                public ColumnIndexingConf work(Session session) {
                    List<ColumnIndexingConf> columnConfs = columnConf.find(session,
                            Restrictions.eq("id.tableKey", table.getId()),
                            Restrictions.eq("id.columnName", columnName)
                    );
                    return columnConfs.isEmpty() ? null : columnConfs.get(0);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public void setTableConf(final TableIndexingConf conf) {
        try {
            tx.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work(Session session) throws SQLException {
                    tableConf.saveOrUpdate(session, conf);
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public void setColumnConf(final ColumnIndexingConf conf) {
        try {
            tx.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work(Session session) throws SQLException {
                    columnConf.saveOrUpdate(session, conf);
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static final IndexingConfService INSTANCE = new SwiftIndexingConfService();

    private SwiftIndexingConfService() {
    }

    public static IndexingConfService get() {
        return INSTANCE;
    }
}