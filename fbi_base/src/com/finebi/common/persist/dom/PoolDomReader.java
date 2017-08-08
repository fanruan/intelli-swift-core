package com.finebi.common.persist.dom;

import com.finebi.common.persist.PoolPersistentReader;
import com.finebi.common.resource.ResourcePool;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class PoolDomReader<Pool extends ResourcePool> implements PoolPersistentReader<Pool> {
    protected String targetPath;
    protected Document doc;
    protected DomSpecifiedReader<Pool> reader;
    protected DomReaderFactory<Pool> factory = new DomReaderFactory<Pool>();
    protected String xmlPatternVersion;

    public PoolDomReader(String targetPath) {
        this.targetPath = targetPath;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            doc = dbBuilder.parse(targetPath);
            xmlPatternVersion = readVersion();
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    public void register(String xmlPatternVersion, DomSpecifiedReader<Pool> reader) {
        factory.register(xmlPatternVersion, reader);
    }

    private String readVersion() {
        return doc.getDocumentElement().getAttribute(DomConstants.PERSISTENT_VERSION);
    }

    @Override
    public Pool read() {
        reader = factory.getDomReader(xmlPatternVersion);
        return reader.read(doc);
    }
    //    @Override
//    public ResourcePool read() {
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
//        return null;
//    }
}
