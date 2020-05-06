package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.db.Where;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.service.DeleteService;
import com.fr.swift.source.SourceKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class DeleteJob extends BaseJob<Boolean, FilterBean> {

    private SourceKey sourceKey;
    private Where where;

    /**
     * todo 先改成静态锁表，再考虑改成segment锁，总之也比现在全锁性能要好些
     */
    private transient static Map<SourceKey, Object> TABLE_LOCK = new ConcurrentHashMap<>();

    public DeleteJob(SourceKey sourceKey, Where where) {
        this.sourceKey = sourceKey;
        this.where = where;
    }

    @Override
    public Boolean call() {
        try {
            synchronized (TABLE_LOCK.computeIfAbsent(sourceKey, n -> new Object())) {
                SwiftContext.get().getBean(DeleteService.class).delete(sourceKey, where);
            }
            SwiftLoggers.getLogger().info("Delete from table [{}] with [{}] success!", sourceKey.getId(), JsonBuilder.writeJsonString(where.getFilterBean()));
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public FilterBean serializedTag() {
        return where.getFilterBean();
    }
}
