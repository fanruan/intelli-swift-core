package com.fr.bi.mongodbsource;

import com.fr.base.Parameter;
import com.fr.base.TableData;
import com.fr.base.TemplateUtils;
import com.fr.data.AbstractParameterTableData;
import com.fr.data.core.DataCoreXmlUtils;
import com.fr.data.impl.Connection;
import com.fr.file.DatasourceManager;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;
import com.fr.stable.ArrayUtils;
import com.fr.stable.ParameterProvider;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by richie on 16/1/22.
 */
public class MongoTableData extends AbstractParameterTableData {

    private static final String ATTR_TAG = "MongoTableDataAttr";
    private static final long serialVersionUID = 7983857586281020916L;

    private Connection connection;
    private String dbName;

    private String tableName;

    private String query;

    private String filter;

    private String sort;

    private boolean showUniqueID;

    public MongoTableData(Connection connection,String dbName,String tableName, String query) {
        this.connection =  connection;
        this.query = query;
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }


    public void setParameters(ParameterProvider[] providers) {
        super.setDefaultParameters(providers);
    }

    @Override
    public DataModel createDataModel(Calculator calculator) {
        return createDataModel(calculator, TableData.RESULT_ALL);
    }

    @Override
    public DataModel createDataModel(Calculator calculator, int rowCount) {
        Parameter[] ps = Parameter.providers2Parameter(Calculator.processParameters(calculator, parameters));
            if (connection != null) {
                return new MongoTableDataModel((MongoDatabaseConnection) connection,
                        dbName,
                        tableName,
                        calculateQuery(query, ps),
                        calculateQuery(filter, ps),
                        calculateQuery(sort, ps),
                        rowCount);
            }
        return null;
    }

    private String calculateQuery(String query, Parameter[] ps) {
        if (ArrayUtils.isEmpty(ps)) {
            return query;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (Parameter p : ps) {
            map.put(p.getName(), p.getValue());
        }
        try {
            return TemplateUtils.renderParameter4Tpl(query, map);
        } catch (Exception e) {
            return query;
        }
    }

    public void readXML(XMLableReader reader) {
        super.readXML(reader);

        if (reader.isChildNode()) {
            String tmpName = reader.getTagName();
            String tmpVal;

            if (Connection.XML_TAG.equals(tmpName)) {
                if (reader.getAttrAsString("class", null) != null) {
                    Connection con = DataCoreXmlUtils.readXMLConnection(reader);
                    this.setConnection(con);
                }
            } else if (ATTR_TAG.equals(tmpName)) {
                dbName = reader.getAttrAsString("dbName", StringUtils.EMPTY);
                tableName = reader.getAttrAsString("tableName", StringUtils.EMPTY);
                showUniqueID = reader.getAttrAsBoolean("showUniqueID", false);
            } else if ("Query".equals(tmpName)) {
                if ((tmpVal = reader.getElementValue()) != null) {
                    this.setQuery(tmpVal);
                }
            } else if ("Filter".equals(tmpName)) {
                if ((tmpVal = reader.getElementValue()) != null) {
                    this.setFilter(tmpVal);
                }
            } else if ("Sort".equals(tmpName)) {
                if ((tmpVal = reader.getElementValue()) != null) {
                    this.setSort(tmpVal);
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        super.writeXML(writer);
        if (this.connection != null) {
            DataCoreXmlUtils.writeXMLConnection(writer, this.connection);
        }
        writer.startTAG(ATTR_TAG);
        writer.attr("dbName", dbName);
        writer.attr("tableName", tableName);
        if (showUniqueID) {
            writer.attr("showUniqueID", true);
        }
        writer.end();
        writer.startTAG("Query").textNode(getQuery()).end();
        writer.startTAG("Filter").textNode(getFilter()).end();
        writer.startTAG("Sort").textNode(getSort()).end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MongoTableData cloned = (MongoTableData) super.clone();
        cloned.connection = connection;
        cloned.dbName = dbName;
        cloned.tableName = tableName;
        cloned.showUniqueID = showUniqueID;
        cloned.query = query;
        cloned.filter = filter;
        cloned.sort = sort;
        return cloned;
    }
}