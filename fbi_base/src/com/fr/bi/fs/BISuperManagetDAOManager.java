package com.fr.bi.fs;

import com.fr.base.FRContext;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.file.XMLFileManager;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.GeneralContext;
import com.fr.privilege.PrivilegeManager;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.Primitive;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/8/4.
 */
public class BISuperManagetDAOManager extends XMLFileManager implements BIReportDAO {
    private static final String XML_TAG = "BISuperManagetDAOManager";
    private static BISuperManagetDAOManager manager = null;
    private EmbeddedTableData sysBITableData;

    private BISuperManagetDAOManager() {
       readXMLFile();
    }

    private Map<Long, BIReportNode> sysBINode_idMap = new Hashtable<Long, BIReportNode>();

    public static BISuperManagetDAOManager getInstance() {
        manager =  BIConstructorUtils.constructObject(BISuperManagetDAOManager.class, manager);
        return manager;
    }


    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BISuperManagetDAOManager.getInstance().envChanged();
            }
        });
    }


    private void initBIMap() {
        synchronized (sysBINode_idMap) {
            if (sysBINode_idMap.isEmpty()) {
                EmbeddedTableData tabledata = getSysBITabledata();
                for (int i = 0, len = tabledata.getRowCount(); i < len; i++) {
                    try {
                        Long id = new Long(tabledata.getValueAt(i, 0).toString());
                        BIReportNode node = new BIReportNode(id.longValue());
                        node.setUserId(PrivilegeManager.SYSADMINID);
                        node.setPath(tabledata.getValueAt(i, 2).toString());
                        node.setParentid(tabledata.getValueAt(i, 1).toString());
                        node.setReportName(tabledata.getValueAt(i, 3).toString());
                        node.setCreatetime(DateUtils.string2Date((String) tabledata.getValueAt(i, 4), true));
                        node.setLastModifyTime(DateUtils.string2Date((String) tabledata.getValueAt(i, 5), true));
                        //中间有一条不再使用的属性，所以跳过
                        Object des = tabledata.getValueAt(i, 7);
                        if (des != Primitive.NULL && des != null) {
                            node.setDescription(des.toString());
                        }
                        sysBINode_idMap.put(id, node);
                        /**
                         * 原先没有保存bid的内容就删了吧，无奈..原先莫有考虑啊
                         */
                    } catch (Exception e) {
                        FRContext.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }
    }


    /**
     * 环境改变
     */
    public void envChanged() {
        sysBINode_idMap = new Hashtable<Long, BIReportNode>();
        readXMLFile();
    }

    @Override
    public void saveOrUpdate(BIReportNode node) throws Exception {
        long id = node.getId() < 0 ? BIDAOUtils.generateID(sysBINode_idMap) : node.getId();
        node.setId(id);
        sysBINode_idMap.put(new Long(id), node);
        saveSysBITableData();
    }

    @Override
    public List<BIReportNode> findByUserID(long l) throws Exception {
        List resList = new ArrayList();
        Iterator iter = sysBINode_idMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            BIReportNode node = (BIReportNode) entry.getValue();
            resList.add(node.clone());
        }
        return resList;
    }

    @Override
    public BIReportNode findByID(long l) throws Exception {
        BIReportNode node = sysBINode_idMap.get(new Long(l));
        if (node == null) {
            return node;
        }
        return (BIReportNode) node.clone();
    }

    @Override
    public BIReportNode findByName(String s) throws Exception {
        Iterator it = sysBINode_idMap.entrySet().iterator();
        BIReportNode res = null;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            BIReportNode node = (BIReportNode) entry.getValue();
            if (ComparatorUtils.equals(s, node.getReportName())) {
                // 有多个相同文件名,则返回null.
                if (res != null) {
                    return null;
                } else {
                    res = (BIReportNode) node.clone();
                }
            }
        }
        return res;
    }

    public List<BIReportNode> findByParentID(String pId) throws Exception {
        Iterator it = sysBINode_idMap.entrySet().iterator();
        List<BIReportNode> res = new ArrayList<BIReportNode>();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            BIReportNode node = (BIReportNode) entry.getValue();
            if (ComparatorUtils.equals(pId, node.getParentid())) {
                res.add(node);
            }
        }
        return res;
    }

    @Override
    public void delete(BIReportNode biReportNode) throws Exception {
        sysBINode_idMap.remove(biReportNode.getId());
        saveSysBITableData();
    }

    @Override
    public boolean deleteByID(long l) throws Exception {
        sysBINode_idMap.remove(l);
        saveSysBITableData();
        return true;
    }

    @Override
    public List<BIReportNode> listAll() throws Exception {
        return findByUserID(UserControl.getInstance().getSuperManagerID());
    }

    @Override
    public String fileName() {
        return "supermanagerdao.xml";
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if ("sysBINode".equals(tagName)) {
                reader.readXMLObject(this.getSysBITabledata());
                initBIMap();
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        if (this.sysBITableData != null) {
            writer.startTAG("sysBINode");
            getSysBITabledata().writeXML(writer);
            writer.end();
        }
        writer.end();
    }

    private void saveSysBITableData() throws Exception {
        synchronized (BISuperManagetDAOManager.class){
            sysBITableData = BIDAOUtils.initEmbeddedTableData();
            Iterator iter = this.sysBINode_idMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                BIReportNode brn = (BIReportNode) entry.getValue();
                List rowList = new ArrayList();
                rowList.add(String.valueOf(brn.getId()));
                rowList.add(String.valueOf(brn.getParentid()));
                rowList.add(brn.getPath());
                rowList.add(brn.getDisplayName());
                Date d = brn.getCreatetime();
                if(d == null){
                    d = new Date();
                }
                rowList.add(String.valueOf(d.getTime()));
                d = brn.getLastModifyTime();
                if(d == null){
                    d = new Date();
                }
                rowList.add(String.valueOf(d.getTime()));
                rowList.add(String.valueOf(-1));
                rowList.add(brn.getDescription());
                this.getSysBITabledata().addRow(rowList);
            }
            FRContext.getCurrentEnv().writeResource(BISuperManagetDAOManager.getInstance());
        }
    }

    private EmbeddedTableData getSysBITabledata() {
        synchronized (BISuperManagetDAOManager.class){
            if (sysBITableData == null) {
                sysBITableData = BIDAOUtils.initEmbeddedTableData();
            }
            return sysBITableData;
        }
    }


}