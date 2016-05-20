package com.fr.bi.conf.data.source;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.conf.data.source.operator.OperatorFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by GUY on 2015/2/28.
 */
public class ETLTableSource extends AbstractETLTableSource<IETLOperator, ICubeTableSource> {

    public static final String XML_TAG = "ETLTableSource";
    /**
     *
     */
    private static final long serialVersionUID = -4709748792691267870L;

    public ETLTableSource() {
    }

    public ETLTableSource(List<IETLOperator> oprators, List<ICubeTableSource> parents) {
        super(oprators, parents);
    }

    @Override
    public JSONObject createJSON() throws Exception {

        JSONObject jo = super.createJSON();
        jo.put("connection_name", DBConstant.CONNECTION.ETL_CONNECTION);
        JSONArray tables = new JSONArray();
        for (int i = 0; i < parents.size(); i++) {
            tables.put(parents.get(i).createJSON());
        }

        jo.put("tables", tables);
        OperatorFactory.createJSONByOperators(jo, oprators);
        return jo;
    }

    public void  parseJSON(JSONObject jo, long userId) throws Exception {
        this.oprators = OperatorFactory.createOperatorsByJSON(jo, userId);
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        this.fields.clear();
        this.parents.clear();
        reader.readXMLObject(new XMLReadable() {
            @Override
            public void readXML(XMLableReader reader) {
                if (reader.isChildNode()) {
                    if (reader.getTagName().equals(BIField.XML_TAG)) {
                        BICubeFieldSource field = BICubeFieldSource.getBiEmptyField();
                        reader.readXMLObject(field);
                        fields.put(field.getFieldName(), field);
                    } else if (reader.getTagName().equals(ETLTableSource.XML_TAG)) {
                        ETLTableSource table = new ETLTableSource();
                        reader.readXMLObject(table);
                        parents.add(table);
                    } else if (reader.getTagName().equals(ExcelTableSource.XML_TAG)) {
                        ExcelTableSource table = new ExcelTableSource();
                        reader.readXMLObject(table);
                        parents.add(table);
                    } else if (reader.getTagName().equals(SQLTableSource.XML_TAG)) {
                        SQLTableSource table = new SQLTableSource();
                        reader.readXMLObject(table);
                        parents.add(table);
                    } else if (reader.getTagName().equals(DBTableSource.XML_TAG)) {
                        DBTableSource table = new DBTableSource();
                        reader.readXMLObject(table);
                        parents.add(table);
                    } else if (reader.getTagName().equals(ServerTableSource.XML_TAG)) {
                        ServerTableSource table = new ServerTableSource();
                        reader.readXMLObject(table);
                        parents.add(table);
                    } else {
                        IETLOperator oprator = OperatorFactory.createOperatorByXMLTagName(reader.getTagName());
                        if (oprator != null) {
                            reader.readXMLObject(oprator);
                            oprators.add(oprator);
                        }
                    }

                }
            }
        });
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        Iterator<BICubeFieldSource> fIter = fields.values().iterator();
        while (fIter.hasNext()) {
            fIter.next().writeXML(writer);
        }
        Iterator<IETLOperator> oIter = oprators.iterator();
        while (oIter.hasNext()) {
            oIter.next().writeXML(writer);
        }
        Iterator<ICubeTableSource> pIter = parents.iterator();
        while (pIter.hasNext()) {
            pIter.next().writeXML(writer);
        }
        writer.end();
    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLETYPE.ETL;
    }

    @Override
    /**
     * FIXME 需要实现
     */
    public long read(final Traversal<BIDataValue> travel, BICubeFieldSource[] fields, ICubeDataLoader loader) {
        Iterator<IETLOperator> it = oprators.iterator();
        long index = 0;
        while (it.hasNext()) {
            IETLOperator op = it.next();
            index = op.writeSimpleIndex(travel, parents, loader);
        }
        return index;
    }

    @Override
	protected Set<ICubeTableSource> createSourceSet() {
        Set<ICubeTableSource> set = new HashSet<ICubeTableSource>();
        if (oprators != null){
            for (IETLOperator op : oprators){
                set.add(new SingleOperatorETLTableSource(parents, op));
            }
        } else {
            set.add(this);
        }
        return set;
    }

}