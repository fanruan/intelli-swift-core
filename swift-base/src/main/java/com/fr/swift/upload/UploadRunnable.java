package com.fr.swift.upload;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;

/**
 * @author yee
 * @date 2018/8/6
 */
interface UploadRunnable extends Runnable {
    void uploadTable(final DataSource dataSource) throws Exception;

    void uploadRelation(RelationSource relation) throws Exception;

    void doAfterUpload(SwiftRpcEvent event) throws Exception;


}
