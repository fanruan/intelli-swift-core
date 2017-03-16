package com.finebi.cube.tools;

import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/6/2.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMemDataSourceDependent extends BIMemDataSourceTestToolCube {
    public CubeTableSource parent;

    public BIMemDataSourceDependent(List<CubeTableSource> parents) {
        super(null, parents);
        this.parent = parents.get(0);
    }

    @Override
    public boolean isIndependent() {
        return false;
    }

    @Override
    public Set<ICubeFieldSource> getParentFields(Set<CubeTableSource> sources) {
        return parent.getFacetFields(sources);
    }

    @Override
    public Set<ICubeFieldSource> getSelfFields(Set<CubeTableSource> sources) {
        return super.getSelfFields(sources);
    }

    @Override
    public Set<ICubeFieldSource> getFacetFields(Set<CubeTableSource> sources) {
        Set<ICubeFieldSource> fields = new HashSet<ICubeFieldSource>(super.getFacetFields(sources));
        fields.addAll(getParentFields(sources));
        return fields;
    }

    @Override
    public String getSourceID() {
        return "BIMemDataSourceDependent";
    }
}
