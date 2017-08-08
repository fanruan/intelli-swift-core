package com.finebi.common.persist.dom;

import com.finebi.common.persist.PoolPersistentWriter;
import com.finebi.common.resource.ResourcePool;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
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
    private static final BILogger LOGGER = BILoggerFactory.getLogger(PoolDomWriter.class);
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
            Element documentElement = document.createElement("Pool");
            document.appendChild(documentElement);
            writeVersion(document);
            writeFile(buildDocument(pool,documentElement));
            renameFile();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void writeVersion(Document document) {
        document.getDocumentElement().setAttribute(DomConstants.PERSISTENT_VERSION, DomConstants.CURRENT_VERSION);
    }

    public abstract Document buildDocument(Pool pool, Element documentElement);

    protected void writeFile(Document document) {
        try {
            File file = new File(targetPath + ".tmp");
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(source, result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

        }
    }

    private void renameFile(){
        File dest = new File(targetPath);
        if(dest.exists()){
            dest.delete();
        }
        new File(targetPath+".tmp").renameTo(dest);
    }
}
