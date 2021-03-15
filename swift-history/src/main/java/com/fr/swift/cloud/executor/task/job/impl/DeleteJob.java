package com.fr.swift.cloud.executor.task.job.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.base.json.JsonBuilder;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.executor.task.job.BaseJob;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.query.FilterBean;
import com.fr.swift.cloud.service.DeleteService;
import com.fr.swift.cloud.source.SourceKey;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class DeleteJob extends BaseJob<Boolean, FilterBean> {

    private SourceKey sourceKey;
    private Where where;

    public DeleteJob(SourceKey sourceKey, Where where) {
        this.sourceKey = sourceKey;
        this.where = where;
    }

    @Override
    public Boolean call() {
        try {
            SwiftContext.get().getBean(DeleteService.class).delete(sourceKey, where);
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
