package com.fr.swift.cloud.segment.operator.update;

import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.operator.Updater;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractUpdater implements Updater {

    protected Segment segment;

    public AbstractUpdater(Segment segment) {
        this.segment = segment;
    }
}
