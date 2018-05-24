package com.fr.swift.config.conf.session;

import com.fr.decision.base.db.session.DAOSession;
import com.fr.decision.base.db.session.DAOSessionStore;
import com.fr.decision.base.logger.DecisionLogger;
import com.fr.stable.db.DBProvider;
import com.fr.swift.config.conf.dao.MetaDataDAO;
import com.fr.swift.config.conf.dao.SegmentDAO;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftConfigSessionController {
    private MetaDataDAO metaDataDAO = null;
    private SegmentDAO segmentDAO = null;
    private DAOSessionStore sessionStore = null;
    private DecisionLogger logger = null;

    public SwiftConfigSessionController(DBProvider dbProvider, DecisionLogger logger) {
        this.sessionStore = new DAOSessionStore(dbProvider);
        this.logger = logger;
    }

    public DAOSession getDAOSession() {
        return sessionStore.getDAOSession();
    }

    public DecisionLogger getLogger() {
        return logger;
    }

    public DAOSessionStore getSessionStore() {
        return sessionStore;
    }

    public MetaDataDAO getMetaDataDAO() {
        return metaDataDAO;
    }

    public void setMetaDataDAO(MetaDataDAO metaDataDAO) {
        this.metaDataDAO = metaDataDAO;
    }

    public SegmentDAO getSegmentDAO() {
        return segmentDAO;
    }

    public void setSegmentDAO(SegmentDAO segmentDAOl) {
        this.segmentDAO = segmentDAOl;
    }
}
