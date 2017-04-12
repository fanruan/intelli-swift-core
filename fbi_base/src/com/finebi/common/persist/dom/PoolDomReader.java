package com.finebi.common.persist.dom;

import com.finebi.common.persist.PoolPersistentReader;
import com.finebi.common.resource.ResourcePool;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class PoolDomReader implements PoolPersistentReader {
    private String targetPath;

    @Override
    public ResourcePool read() {
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        File file = new File("D:\\Connery\\Code\\conneryDom.xml");
//        Document document = db.newDocument();
//        Element element = document.createElement("node");
//        document.appendChild(element);
//        element.appendChild(document.createElement("table"));
//        TransformerFactory tfactory = TransformerFactory.newInstance();
//        Transformer transformer = tfactory.newTransformer();
//        DOMSource source = new DOMSource(document);
//        StreamResult result = new StreamResult(file);
//        transformer.setOutputProperty("encoding", "UTF-8");
//        transformer.transform(source, result);
        return null;
    }
}
