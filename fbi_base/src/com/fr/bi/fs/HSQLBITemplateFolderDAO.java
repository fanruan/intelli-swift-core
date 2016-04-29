package com.fr.bi.fs;

import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.data.dao.DataAccessObjectSession;
import com.fr.fs.dao.PlatformDataAccessObject;

import java.util.HashMap;
import java.util.List;

/**
 * 模板文件夹操作类
 * Created by Baron 2015/12/28
 */
public class HSQLBITemplateFolderDAO extends PlatformDataAccessObject implements BITemplateFolderDAO {

    private static HSQLBITemplateFolderDAO dao = null;

    public static HSQLBITemplateFolderDAO getInstance() {
        dao = BIConstructorUtils.constructObject(HSQLBITemplateFolderDAO.class, dao);
        return dao;
    }

    @Override
    public void saveOrUpdate(BITemplateFolderNode biTemplateFolderNode) throws Exception {
        createSession().saveOrUpdate(biTemplateFolderNode);
    }

    @Override
    public List<BITemplateFolderNode> findFolderByUserID(long userId) throws Exception {
        return createSession().listByFieldValue(BITemplateFolderNode.class, BITableMapper.BI_CREATED_TEMPLATE_FOLDER.FIELD_USERID, new Long(userId));
    }

    @Override
    public BITemplateFolderNode findFolderByID(long id) throws Exception {
        return (BITemplateFolderNode) createSession().load(BITemplateFolderNode.class, id);
    }

    @Override
    public BITemplateFolderNode findFolderByFolderId(String folderId, long userId) throws Exception {
        HashMap map = new HashMap();
        map.put(BITableMapper.BI_CREATED_TEMPLATE_FOLDER.FIELD_FOLDER_ID, folderId);
        List<BITemplateFolderNode> nodes = createSession().listByFieldValues(BITemplateFolderNode.class, map);
        if(nodes != null && nodes.size() == 1){
            return nodes.get(0);
        }
        return null;
    }

    @Override
    public boolean deleteByFolderID(String folderId) throws Exception {
        DataAccessObjectSession session = null;
        try {
            session = createSession();
            session.beginTransaction();
            HashMap map = new HashMap();
            map.put(BITableMapper.BI_CREATED_TEMPLATE_FOLDER.FIELD_FOLDER_ID, folderId);
            boolean success = session.deleteByFields(BITemplateFolderNode.class, map);
            session.commit();
            return success;
        } catch (Exception e) {
            rollbackSession(session);
            throw e;
        } finally {
            closeSession(session);
        }
    }



    @Override
    public List<BIReportNode> findTemplateByParentId(String parentId) throws Exception {
        return createSession().listByFieldValue(BIReportNode.class, BITableMapper.BI_REPORT_NODE.FIELD_PARENTID, parentId);
    }

    @Override
    public List<BITemplateFolderNode> findTemplateFolderByParentId(String parentId) throws Exception {
        return createSession().listByFieldValue(BITemplateFolderNode.class, BITableMapper.BI_CREATED_TEMPLATE_FOLDER.FIELD_FOLDER_PARENTID, parentId);
    }
    @Override
    public List<BITemplateFolderNode> findByName(String name) throws Exception{
        return createSession().listByFieldValue(BITemplateFolderNode.class, BITableMapper.BI_CREATED_TEMPLATE_FOLDER.FIELD_FOLDER_NAME, name);
    }
}