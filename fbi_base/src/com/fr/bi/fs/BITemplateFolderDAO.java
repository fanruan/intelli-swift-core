package com.fr.bi.fs;

import java.util.List;

public interface   BITemplateFolderDAO {

    public void saveOrUpdate(BITemplateFolderNode biTemplateFolderNode) throws Exception;

    public List<BITemplateFolderNode> findFolderByUserID(long userId) throws Exception;

    public boolean deleteByFolderID(String folderId) throws Exception;

    public BITemplateFolderNode findFolderByID(long id) throws Exception;

    public BITemplateFolderNode findFolderByFolderId(String folderId, long UserId) throws Exception;

    public List<BITemplateFolderNode> findTemplateFolderByParentId (String parentId) throws Exception;

    public List<BIReportNode> findTemplateByParentId (String parentId) throws Exception;

    public List<BITemplateFolderNode> findByName(String name) throws Exception;
}