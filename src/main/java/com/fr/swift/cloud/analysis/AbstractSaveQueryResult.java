package com.fr.swift.cloud.analysis;


import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.log.SwiftLoggers;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public abstract class AbstractSaveQueryResult<T> {

    private static final int SESSION_SIZE = 20;

    //把分析结果写入数据库
    public void saveResult(List<T> queryResult) {
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            for (int i = 0; i < queryResult.size(); i++) {
                session.save(queryResult.get(i));
                if (i % SESSION_SIZE == 0) {
                    session.flush();
                    session.clear();
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            transaction.commit();
            try {
                session.close();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }
}
