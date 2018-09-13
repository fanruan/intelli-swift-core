package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.source.SwiftResultSet;


/**
 * @author pony
 * @date 2017/10/12
 * 分析服务
 */
@SwiftService(name = "analyse")
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService {
    private static final long serialVersionUID = 841582089735823794L;

    private transient SessionFactory sessionFactory;

    private SwiftAnalyseService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start = super.start();
        QueryRunnerProvider.getInstance().registerRunner(this);
        this.sessionFactory = SwiftContext.get().getBean("swiftQuerySessionFactory", SessionFactory.class);
        return start;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        sessionFactory = null;
        QueryRunnerProvider.getInstance().registerRunner(null);
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public SwiftResultSet getQueryResult(QueryBean info) throws Exception {
        SwiftLoggers.getLogger().debug(QueryInfoBeanFactory.queryBean2String(info));
        return sessionFactory.openSession(info.getQueryId()).executeQuery(info);
    }
}
