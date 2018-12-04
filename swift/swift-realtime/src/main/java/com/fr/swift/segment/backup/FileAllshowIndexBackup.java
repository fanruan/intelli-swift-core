package com.fr.swift.segment.backup;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;

/**
 * This class created on 2018/7/5
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "allShowIndexBackup")
@SwiftScope("prototype")
public class FileAllshowIndexBackup implements AllShowIndexBackup {

    private Segment segment;

    public FileAllshowIndexBackup(Segment segment) {
        this.segment = segment;
    }

    @Override
    public void backupAllShowIndex(ImmutableBitMap allShowIndex) {
        segment.putAllShowIndex(allShowIndex);
        segment.release();
    }
}
