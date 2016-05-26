package com.fr.bi.stable.conf.cubeconf;

import com.fr.base.FRContext;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.file.XMLFileManager;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.io.File;

/**
 * Created by GUY on 2015/4/1.
 */
public class CubeConfManager extends XMLFileManager {

    private static final String XML_TAG = "CubeConfManger";
    private static CubeConfManager manager;
    private String cubePath;
    private CubeTableSource userInTable;
    private String userInField;

    public CubeTableSource getUserInTable() {
        return userInTable;
    }

    public void setUserInTable(CubeTableSource userInTable) {
        this.userInTable = userInTable;
    }

    public String getUserInField() {
        return userInField;
    }

    public void setUserInField(String userInField) {
        this.userInField = userInField;
    }

    private CubeConfManager() {
        readXMLFile();
    }

    public static CubeConfManager getInstance() {
        synchronized (CubeConfManager.class) {
            if (manager == null) {
                manager = new CubeConfManager();
            }
            return manager;
        }
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                synchronized (CubeConfManager.class) {
                    CubeConfManager.getInstance().envChanged();
                }
            }
        });
    }

    private void envChanged() {
        manager = null;
    }

    public String getCubePath() {
        return cubePath;
    }

    public void setCubePath(String cubePath) {
        synchronized (this) {
            this.cubePath = cubePath;
            try {
                FRContext.getCurrentEnv().writeResource(this);
            } catch (Exception ignore) {

            }
        }
    }

    public String checkCubePath(String cubePath) {
        if (StringUtils.isEmpty(cubePath)) {
            return "";
        }
        if (ComparatorUtils.equals(cubePath, getCubePath())) {
            return getCubePath();
        }
        File file = new File(cubePath);
        if (!file.exists()) {
            try {
                file.mkdirs();
                return file.getAbsolutePath();
            } catch (Exception e) {
                return "";
            } finally {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (!file.isDirectory() || file.list().length > 0) {
            return "";
        }
        return file.getAbsolutePath();
    }

    @Override
    public String fileName() {
        return "bi_cube_conf.xml";
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if (ComparatorUtils.equals(tagName, "cubepath")) {
                cubePath = reader.getAttrAsString("path", StringUtils.EMPTY);
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.startTAG("cubepath").attr("path", cubePath).end();
        writer.end();
    }
}