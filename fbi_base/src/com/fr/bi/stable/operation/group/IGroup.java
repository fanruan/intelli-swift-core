package com.fr.bi.stable.operation.group;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.json.JSONTransform;
import com.fr.stable.xml.XMLable;

/**
 * Created by GUY on 2015/2/14.
 */
public interface IGroup extends JSONTransform, Cloneable, XMLable {

    int getType();

    ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap);

    boolean isNullGroup();
}