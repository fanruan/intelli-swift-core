package com.fr.swift.source.alloter.impl.time;

import com.fr.swift.source.SourceKey;

/**
 * @author Marvin
 * @date 7/22/2019
 * @description
 * @since swift 1.1
 */
public class RealtimeTimeSourceAlloter extends HistoryTimeSourceAlloter {

    protected RealtimeTimeSourceAlloter(SourceKey tableKey, TimeAllotRule rule) {
        super(tableKey, rule);
    }
}
