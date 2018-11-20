package com.fr.swift.jdbc.sql;

import com.fr.swift.jdbc.JdbcProperty;
import com.fr.swift.jdbc.exception.Exceptions;

import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * @author yee
 * @date 2018/11/19
 */
public class SwiftStatementHolder {
    private final Semaphore idleSemaphore;
    private final int maxIdle;
    private Map<String, SwiftStatement> using;
    private Queue<SwiftStatement> idle;
    private int timeout = JdbcProperty.get().getConnectionTimeout().intValue();

    public SwiftStatementHolder(int idleCount) {
        this.maxIdle = idleCount;
        this.idleSemaphore = new Semaphore(idleCount);
        this.using = new ConcurrentHashMap<String, SwiftStatement>(idleCount);
        this.idle = new ConcurrentLinkedQueue<SwiftStatement>();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    synchronized
    public void using(SwiftStatement obj) {
        try {
            idleSemaphore.acquire(timeout);
        } catch (InterruptedException e) {
            throw Exceptions.timeout(String.format("Too many open statement. Limit is %d.", maxIdle), e);
        }
        using.put(obj.getObjId(), obj);
    }

    synchronized
    public void idle(SwiftStatement obj) {
        SwiftStatement remove = using.remove(obj.getObjId());
        if (null != remove) {
            idle.add(remove);
            idleSemaphore.release();
        }
    }

    synchronized
    public SwiftStatement getIdle() {
        return idle.poll();
    }

    synchronized
    public SwiftStatement getPreparedIdle(String id) {
        SwiftStatement statement;
        while (null != (statement = idle.peek())) {
            if (statement.getObjId().equals(id) && statement instanceof PreparedStatement) {
                idle.remove(statement);
                return statement;
            }
        }
        return null;
    }
}
