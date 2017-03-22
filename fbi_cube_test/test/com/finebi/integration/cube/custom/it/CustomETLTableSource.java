package com.finebi.integration.cube.custom.it;

import com.fr.bi.conf.data.source.SingleOperatorETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lucifer on 2017-3-20.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomETLTableSource extends SingleOperatorETLTableSource {
    public CustomETLTableSource(IETLOperator operators, List<CubeTableSource> parents) {
        super(parents, operators);
    }

    @Override
    public Set<ICubeFieldSource> getFacetFields(Set<CubeTableSource> sources) {
        return new HashSet<>();
    }

}
