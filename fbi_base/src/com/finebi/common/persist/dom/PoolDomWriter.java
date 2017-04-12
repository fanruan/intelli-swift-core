package com.finebi.common.persist.dom;

import com.finebi.common.persist.PoolPersistentWriter;
import com.finebi.common.resource.ResourcePool;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class PoolDomWriter<Pool extends ResourcePool> implements PoolPersistentWriter<Pool> {
    protected String targetPath;
    protected Document document;

    public PoolDomWriter(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public void write(Pool pool) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.newDocument();
            writeFile(buildDocument(pool));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract Document buildDocument(Pool pool);

    protected void writeFile(Document document) {
        try {
            File file = new File(targetPath);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.transform(source, result);
        } catch (Exception e) {

        }
    }
}
