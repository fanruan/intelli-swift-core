package com.finebi.common.resource;

import com.finebi.common.name.Name;
import com.finebi.common.name.NameImp;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ResourceNameImpl implements ResourceName {
    private Name name;

    public ResourceNameImpl(Name name) {
        BINonValueUtils.checkNull(name);
        this.name = name;
    }

    public ResourceNameImpl(String name) {
        this(new NameImp(name));
    }

    @Override
    public String uniqueValue() {
        return this.name.uniqueValue();
    }

    @Override
    public String value() {
        return this.name.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceNameImpl)) {
            return false;
        }

        ResourceNameImpl that = (ResourceNameImpl) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {

        return name.toString();
    }
}
