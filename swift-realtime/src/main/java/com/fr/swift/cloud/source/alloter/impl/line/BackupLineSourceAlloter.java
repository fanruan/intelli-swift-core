package com.fr.swift.cloud.source.alloter.impl.line;

import com.fr.swift.cloud.source.SourceKey;

/**
 * @author anchore
 * @date 2019/3/12
 * <p>
 * 和{@link RealtimeLineSourceAlloter}分块规则相同，区别只在读备份块
 */
public class BackupLineSourceAlloter extends RealtimeLineSourceAlloter {

    public BackupLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

}