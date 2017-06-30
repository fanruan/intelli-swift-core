package com.fr.bi.stable.data.tableSource;

import com.finebi.common.resource.ResourceItem;
import com.fr.bi.stable.data.db.FineCubeField;
import com.fr.bi.stable.data.source.CubeTableSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Roy on 2017/6/16.
 */
public interface FineCubeTable extends ResourceItem, CubeTableSource {
    Element buildTableElement(Document document);

    void initFields(List<FineCubeField> fieldList);

}
