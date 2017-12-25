package com.finebi.base.common.resource;


import com.finebi.common.name.BlankName;
import com.finebi.common.name.Name;
import com.finebi.common.name.NameImp;
import com.fr.bi.stable.utils.program.BIStringUtils;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ResourceNameImpl implements ResourceName {
    private static Name BLANK_TYPE = new BlankName();
    private Name name;
    private Name type;

    public ResourceNameImpl(Name name, Name type) {
        this.name = name;
        this.type = type;
    }

    public ResourceNameImpl(Name name, String type){
        this(name, new NameImp(type));
    }

    public ResourceNameImpl(String name, String type){
        this(new NameImp(name), new NameImp(type));
    }

    public ResourceNameImpl(String name) {
        this(new NameImp(name), BLANK_TYPE);
    }

    public ResourceNameImpl(Name name){
        this(name, BLANK_TYPE);
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
        StringBuffer sb = new StringBuffer("[");
        if (type == BLANK_TYPE) {
            sb.append(name.toString()).append("]");
        } else {
            sb.append(BIStringUtils.append(type.toString(), ":", name.toString())).append("]");
        }
        return sb.toString();
    }
}
