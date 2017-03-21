package com.finebi.integration.cube.custom.it;

import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-3-20.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomDBTableSource extends DBTableSource {
    public CustomDBTableSource(String dbName, String tableName) {
        super(dbName, tableName);
    }

    @Override
    public Set<ICubeFieldSource> getFacetFields(Set<CubeTableSource> sources) {

        ICubeFieldSource fieldSource = new BICubeFieldSource(null, "a", 0, 0);
        Set<ICubeFieldSource> set = new HashSet<>();
        set.add(fieldSource);
        return set;
    }
}
