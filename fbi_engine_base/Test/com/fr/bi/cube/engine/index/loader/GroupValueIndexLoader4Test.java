package com.fr.bi.cube.engine.index.loader;

import com.fr.bi.cube.engine.io.read.GroupValueIndexArrayReader;
import com.fr.bi.cube.engine.store.BIBaseColumnKey;

import java.io.File;

/**
 * Created by Connery on 2014/12/10.
 */
public class GroupValueIndexLoader4Test implements IGroupValueIndexLoader {
    @Override
    public Object deSerializeLinkedIndex(File currentIndexFile, File currentIndexOldFile, int columnIndex, int relation_version, BIBaseColumnKey ck) {
        return new GroupValueIndexArrayReader("", 0);
    }
}