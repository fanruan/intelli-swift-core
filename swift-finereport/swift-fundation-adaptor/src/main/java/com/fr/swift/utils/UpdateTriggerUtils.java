package com.fr.swift.utils;

import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.constants.UpdateConstants;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.source.DataSource;

/**
 * This class created on 2018/5/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class UpdateTriggerUtils {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(UpdateTriggerUtils.class);

    /**
     * 判断每个更新类型是否符合条件
     * ALL始终执行
     * INCREMENT:判断cube是否存在，存在的话，每次都取增量更新，否则做全量
     * NEVER:判断cube是否存在，存在的话，每次都不执行，否则做全量
     *
     * @param tableUpdateInfo
     * @param fineBusinessTable
     * @return
     */
    public static TableUpdateInfo checkUpdateInfo(TableUpdateInfo tableUpdateInfo, FineBusinessTable fineBusinessTable) {
        switch (tableUpdateInfo.getUpdateType()) {
            case UpdateConstants.TableUpdateType.ALL:
                return tableUpdateInfo;
            case UpdateConstants.TableUpdateType.INCREMENT:
                DataSource incrementDataSource = DataSourceFactory.getDataSourceInCache(fineBusinessTable);
                if (SwiftContext.get().getBean(LocalSegmentProvider.class).isSegmentsExist(incrementDataSource.getSourceKey())) {
                    return tableUpdateInfo;
                } else {
                    TableUpdateInfo result = new TableUpdateInfo();
                    result.setUpdateType(UpdateConstants.TableUpdateType.ALL);
                    return result;
                }
            case UpdateConstants.TableUpdateType.NEVER:
                DataSource neverDataSource = DataSourceFactory.getDataSourceInCache(fineBusinessTable);
                if (!SwiftContext.get().getBean(LocalSegmentProvider.class).isSegmentsExist(neverDataSource.getSourceKey())) {
                    return tableUpdateInfo;
                } else {
                    return null;
                }
            default:
                LOGGER.error("Update type '" + tableUpdateInfo.getUpdateType() + "' is not valid");
                return null;
        }
    }
}
