package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by wuk on 16/7/11.
 */
public abstract class AbstractCubeBuild implements CubeBuild {
    private long userId;
    public AbstractCubeBuild(long userId) {
        this.userId=userId;
    }

    @Override
    public ICubeConfiguration getCubeConfiguration() {
        return  BICubeConfiguration.getTempConf(Long.toString(userId));
    }
    protected Set<List<Set<CubeTableSource>>> calculateTableSource(Set<CubeTableSource> tableSources) {
        Iterator<CubeTableSource> it = tableSources.iterator();
        Set<List<Set<CubeTableSource>>> depends = new HashSet<List<Set<CubeTableSource>>>();
        while (it.hasNext()) {
            CubeTableSource tableSource = it.next();
            depends.add(tableSource.createGenerateTablesList());
        }
        return depends;
    }
    
}
