package com.fr.swift.config.conf.context;

import com.fr.decision.base.db.transaction.DataOperatorMethodHooks;
import com.fr.decision.base.db.transaction.TransactionProvider;
import com.fr.decision.base.logger.impl.DecisionLoggerImpl;
import com.fr.decision.base.util.ProxyUtil;
import com.fr.log.FineLoggerProvider;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.DBProvider;
import com.fr.stable.db.context.ContextOption;
import com.fr.swift.config.conf.controller.MetaDataController;
import com.fr.swift.config.conf.controller.SegmentController;
import com.fr.swift.config.conf.controller.impl.MetaDataControllerImpl;
import com.fr.swift.config.conf.controller.impl.SegmentControllerImpl;
import com.fr.swift.config.conf.dao.MetaDataDAO;
import com.fr.swift.config.conf.dao.SegmentDAO;
import com.fr.swift.config.conf.entity.MetaDataEntity;
import com.fr.swift.config.conf.entity.SegmentEntity;
import com.fr.swift.config.conf.session.SwiftConfigSessionController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftConfigContext {
    private static SwiftConfigContext self = new SwiftConfigContext();
    private boolean initialized = false;

    private MetaDataController metaDataController = null;
    private SegmentController segmentController = null;
    private Map<Class, Object> unWrapMap = new HashMap<Class, Object>();

    private SwiftConfigContext() {
    }

    public static SwiftConfigContext getInstance() {
        return self;
    }

    public SwiftConfigContext init(ContextOption option) throws Exception {
        synchronized (this) {
            if (initialized) {
                throw new IllegalAccessException("SwiftConfigContext has already initialized");
            }
            FineLoggerProvider l = option.getLogger();
            if (l == null) {
                l = FineLoggerProvider.DEFAULT;
            }
            DBProvider context = option.getDBProvider();
            DecisionLoggerImpl logger = new DecisionLoggerImpl(l);
            //在controller中注册DAO
            SwiftConfigSessionController controller = new SwiftConfigSessionController(context, logger);
            controller.setMetaDataDAO(new MetaDataDAO(controller.getDAOSession()));
            controller.setSegmentDAO(new SegmentDAO(controller.getDAOSession()));

            //代理controller
            ProxyUtil.MethodHooks controllerMethodHooks = new DataOperatorMethodHooks(controller.getSessionStore(), logger);
            MetaDataController mc = new MetaDataControllerImpl(controller);
            SegmentController sc = new SegmentControllerImpl(controller);
            unWrapMap.put(MetaDataController.class, mc);
            unWrapMap.put(SegmentController.class, sc);
            metaDataController = ProxyUtil.createProxy(mc, MetaDataController.class, controllerMethodHooks);
            segmentController = ProxyUtil.createProxy(sc, SegmentController.class, controllerMethodHooks);

            unWrapMap.put(TransactionProvider.class, controller.getSessionStore());

            this.initialized = true;
        }

        return this;
    }

    @SuppressWarnings("all")
    public <T> T unWrap(Class<T> cls) {
        if (cls == null) {
            return null;
        }
        Object o = unWrapMap.get(cls);
        if (cls.isInstance(o)) {
            return (T) o;
        }

        return null;
    }

    public MetaDataController getMetaDataController() {
        return metaDataController;
    }

    public void setMetaDataController(MetaDataController packRowFilterController) {
        this.metaDataController = packRowFilterController;
    }

    public SegmentController getSegmentController() {
        return segmentController;
    }

    public void setSegmentController(SegmentController segmentController) {
        this.segmentController = segmentController;
    }
}
