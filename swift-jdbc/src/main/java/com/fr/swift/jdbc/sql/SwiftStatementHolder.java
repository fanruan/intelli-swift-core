package com.fr.swift.jdbc.sql;

import com.fr.swift.jdbc.JdbcProperty;
import com.fr.swift.jdbc.exception.Exceptions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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

    SwiftStatementHolder(int idleCount) {
        this.maxIdle = idleCount;
        this.idleSemaphore = new Semaphore(idleCount);
        this.using = new ConcurrentHashMap<String, SwiftStatement>(idleCount);
        this.idle = new ConcurrentLinkedQueue<SwiftStatement>();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    synchronized void using(SwiftStatement obj) {
        try {
            if (idleSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
                using.put(obj.getObjId(), obj);
            } else {
                throw Exceptions.timeout(String.format("Too many open statement. Limit is %d.", maxIdle));
            }
        } catch (InterruptedException e) {
            throw Exceptions.timeout(String.format("Too many open statement. Limit is %d.", maxIdle), e);
        }

    }

    synchronized void idle(SwiftStatement obj) {
        SwiftStatement remove = using.remove(obj.getObjId());
        if (null != remove) {
            idle.add(remove);
            idleSemaphore.release();
        }
    }

    synchronized SwiftStatement getIdle() {
        return idle.poll();
    }

    synchronized SwiftStatement getPreparedIdle(String id) {
        SwiftStatement statement;
        while (null != (statement = idle.peek())) {
            if (statement.getObjId().equals(id) && statement instanceof PreparedStatement) {
                idle.remove(statement);
                return statement;
            }
        }
        return null;
    }

    synchronized void closeAll() throws SQLException {
        SwiftStatement statement = null;
        while (null != (statement = idle.poll())) {
            statement.close();
        }
        for (SwiftStatement value : using.values()) {
            if (null != value) {
                value.close();
            }
        }
        using.clear();
    }
}
