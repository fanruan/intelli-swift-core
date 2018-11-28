package com.fr.swift.test.external;

import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.LocalClassHelperDao;
import com.fr.config.dao.impl.LocalEntityDao;
import com.fr.config.dao.impl.LocalXmlEntityDao;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.util.FileUtil;
import com.fr.workspace.WorkContext;
import org.junit.rules.ExternalResource;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/10/25
 */
public class ConfDbResource extends ExternalResource {

    @Override
    protected void before() {
        prepareConfDb();
    }

    private static void prepareConfDb() {
        FileUtil.delete(WorkContext.getCurrent().getPath() + "/embed");

        clearConfTable();

        DaoContext.setEntityDao(new LocalEntityDao());
        DaoContext.setClassHelperDao(new LocalClassHelperDao());
        DaoContext.setXmlEntityDao(new LocalXmlEntityDao());
    }

    private static void clearConfTable() {
        try {
            SwiftContext.get().getBean(HibernateTransactionManager.class).doTransactionIfNeed(new BaseTransactionWorker<Void>() {
                @Override
                public Void work(ConfigSession session) {
                    for (Class<?> entity : SwiftConfigConstants.ENTITIES) {
                        session.createQuery(String.format("delete from %s", entity.getName())).executeUpdate();
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}