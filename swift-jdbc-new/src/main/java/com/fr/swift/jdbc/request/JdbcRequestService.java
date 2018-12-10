package com.fr.swift.jdbc.request;

import com.fr.swift.api.request.RequestService;
import com.fr.swift.jdbc.response.JdbcResponse;
import com.fr.swift.jdbc.rpc.JdbcExecutor;

/**
 * @author yee
 * @date 2018-12-07
 */
public interface JdbcRequestService extends RequestService<JdbcResponse, JdbcExecutor> {
    /**
     * execute auth
     *
     * @param sender
     * @param user
     * @param password
     * @return
     */
    JdbcResponse apply(JdbcExecutor sender, String user, String password);

    /**
     * execute auth with retry
     *
     * @param sender
     * @param user
     * @param password
     * @param retryTime
     * @return
     */
    JdbcResponse applyWithRetry(JdbcExecutor sender, String user, String password, int retryTime);
}
