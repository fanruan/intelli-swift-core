package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.UploadService;

import java.util.Collections;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class DownloadJob extends BaseJob<Boolean> {

    private SegmentKey downloadSegKey;

    private boolean downloadWholeSeg;

    private boolean replace;

    public DownloadJob(SegmentKey downloadSegKey, boolean downloadWholeSeg, boolean replace) {
        this.downloadSegKey = downloadSegKey;
        this.downloadWholeSeg = downloadWholeSeg;
        this.replace = replace;
    }

    @Override
    public Boolean call() {
        try {
            UploadService uploadService = SwiftContext.get().getBean(UploadService.class);
            if (downloadWholeSeg) {
                uploadService.download(Collections.singleton(downloadSegKey), replace);
            } else {
                uploadService.downloadAllShow(Collections.singleton(downloadSegKey));
            }
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}
