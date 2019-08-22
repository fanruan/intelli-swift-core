package com.fr.swift.exception.service;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.util.Strings;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marvin
 * @date 8/16/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
public class SwiftExceptionInfoServiceImpl implements ExceptionInfoService {

    private SwiftHibernateConfigCommandBus<ExceptionInfoBean> commandBus = new SwiftHibernateConfigCommandBus<>(ExceptionInfoBean.class);
    private SwiftHibernateConfigQueryBus<ExceptionInfoBean> queryBus = new SwiftHibernateConfigQueryBus<>(ExceptionInfoBean.class);

    @Override
    public Set<ExceptionInfo> getExceptionInfo(String operateNodeId, ExceptionInfo.State state) {
        return new HashSet<ExceptionInfo>(queryBus.get(SwiftConfigConditionImpl.newInstance()
                .addWhere(ConfigWhereImpl.eq("operateNodeId", operateNodeId))
                .addWhere(ConfigWhereImpl.eq("state", state))));

    }

    @Override
    public Set<ExceptionInfo> getUnsolvedExceptionInfo() {
        return new HashSet<ExceptionInfo>(queryBus.get(SwiftConfigConditionImpl.newInstance()
                .addWhere(ConfigWhereImpl.eq("state", ExceptionInfo.State.UNSOLVED))));

    }

    @Override
    public boolean removeExceptionInfo(String id) {
        if (Strings.isEmpty(id)) {
            return false;
        }
        return commandBus.delete(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("id", id))) >= 0;
    }

    @Override
    public boolean maintain(ExceptionInfo info) {
        return commandBus.merge((ExceptionInfoBean) info) != null;
    }
}
