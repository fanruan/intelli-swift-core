package com.fr.swift.schedule.job;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.impl.SwiftUpdateManager;
import com.finebi.conf.provider.SwiftPackageConfProvider;
import com.finebi.conf.provider.SwiftTableManager;
import com.fr.swift.conf.updateInfo.TableUpdateInfoConfigService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.quartz.Job;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class JobTask implements Job {

    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(JobTask.class);

    protected static final TableUpdateInfoConfigService updateInfoConfigService = TableUpdateInfoConfigService.getService();

    protected SwiftUpdateManager updateManager = StableManager.getContext().getObject("swiftUpdateManager");
    protected SwiftTableManager tableManager = StableManager.getContext().getObject("swiftTableManager");
    protected SwiftPackageConfProvider packageManager = StableManager.getContext().getObject("swiftPackageConfProvider");
}
