package com.fr.swift.service.transfer;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.RealtimeColumnMemMeter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.space.SpaceUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2019/5/10
 */
public class TransferLimits {
    static final int MIN_PUT_THRESHOLD = LineAllotRule.MEM_STEP / 2;
    /**
     * 所有seg占内存的上限
     */
    static final double ALL_SEG_MEM_LIMIT = getAllSegMemLimit();
    /**
     * 占坑的最多苟过MAX_AGE轮定时transfer
     */
    static final int MAX_AGE = 24;
    /**
     * 苟过MAX_AGE岁的seg足够大才transfer
     */
    static final double MIN_OLD_SEG_MEM = SpaceUnit.MB.toBytes(200);
    /**
     * insert时超过此row count limit就transfer
     */
    private static final Map<SourceKey, Integer> SEG_ROW_COUNT_LIMITS = getSegRowCountLimits();

    public static int getRowCountLimit(SourceKey tableKey) {
        return SEG_ROW_COUNT_LIMITS.containsKey(tableKey) ? SEG_ROW_COUNT_LIMITS.get(tableKey) : LineAllotRule.MEM_STEP;
    }

    private static double getAllSegMemLimit() {
        // 绝对限制：2G
        double absoluteLimit = SpaceUnit.GB.toBytes(2);
        // 相对限制：20% Xmx
        double relativeLimit = Runtime.getRuntime().maxMemory() * 20;
        return Math.min(absoluteLimit, relativeLimit);
    }

    private static Map<SourceKey, Integer> getSegRowCountLimits() {
        Map<String, SwiftMetaData> metas = SwiftContext.get().getBean(SwiftMetaDataService.class).getAllMetaData();

        double segMemPerTable = ALL_SEG_MEM_LIMIT / metas.size();

        HashMap<SourceKey, Integer> segRowCountLimits = new HashMap<SourceKey, Integer>();
        for (Entry<String, SwiftMetaData> entry : metas.entrySet()) {
            int singleRowSize = 0;
            try {
                SwiftMetaData meta = entry.getValue();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    singleRowSize += RealtimeColumnMemMeter.getValueSize(meta.getColumn(i));
                }

                int rowCountLimit = (int) (segMemPerTable / singleRowSize);
                // 5000是insert时单块最小transfer的row count
                int properRowCountLimit = Math.min(Math.max(rowCountLimit, 5000), LineAllotRule.MEM_STEP);
                segRowCountLimits.put(new SourceKey(entry.getKey()), properRowCountLimit);
            } catch (SwiftMetaDataException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }

        return segRowCountLimits;
    }
}