package com.fr.bi.stable.relation;

import com.fr.bi.stable.data.BIField;
import com.fr.json.JSONTransform;

/**
 * Created by GUY on 2015/3/6.
 */
public interface Relation extends  JSONTransform, Cloneable {
    BIField getPrimaryKey();

    BIField getForeignKey();
}