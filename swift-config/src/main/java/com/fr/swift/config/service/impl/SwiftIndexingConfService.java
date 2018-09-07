package com.fr.swift.config.service.impl;

import com.fr.swift.config.convert.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.convert.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.entity.SwiftTableIndexingConf;
import com.fr.swift.config.entity.key.ColumnId;
import com.fr.swift.config.entity.key.TableId;
import com.fr.swift.config.indexing.ColumnIndexingConf;
import com.fr.swift.config.indexing.TableIndexingConf;
import com.fr.swift.config.service.IndexingConfService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/7/2
 */
@Service
public class SwiftIndexingConfService implements IndexingConfService {
    @Autowired
    private HibernateTransactionManager tx;

    private SwiftConfigDao<SwiftTableIndexingConf> tableConf = new BasicDao<SwiftTableIndexingConf>(SwiftTableIndexingConf.class);

    private SwiftConfigDao<SwiftColumnIndexingConf> columnConf = new BasicDao<SwiftColumnIndexingConf>(SwiftColumnIndexingConf.class);

    @Override
    public TableIndexingConf getTableConf(final SourceKey table) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<TableIndexingConf>() {
                @Override
                public TableIndexingConf work(Session session) throws SQLException {
                    SwiftTableIndexingConf conf = tableConf.select(session, new TableId(table));
                    return conf != null ? conf : new SwiftTableIndexingConf(table, new LineAllotRule());
                }

                @Override
                public boolean needTransaction() {
                    return false;
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
                public ColumnIndexingConf work(Session session) throws SQLException {
                    SwiftColumnIndexingConf conf = columnConf.select(session, new ColumnId(table, columnName));
                    return conf != null ? conf : new SwiftColumnIndexingConf(table, columnName, true, false);
                }

                @Override
                public boolean needTransaction() {
                    return false;
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
                    tableConf.saveOrUpdate(session, (SwiftTableIndexingConf) conf);
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
                    columnConf.saveOrUpdate(session, (SwiftColumnIndexingConf) conf);
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}