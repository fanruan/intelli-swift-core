package com.fr.bi.fe.plate;

import com.fr.bi.fe.engine.share.BIExcelTableMapper;
import com.fr.bi.fe.service.Service4Excel;
import com.fr.bi.platform.web.services.feedback.Service4BIFeedback;
import com.fr.bi.fe.service.noneedlogin.Service4BIFSNoLogin;
import com.fr.data.dao.FieldColumnMapper;
import com.fr.data.dao.ObjectTableMapper;
import com.fr.data.dao.RelationFCMapper;
import com.fr.fs.AbstractFSPlate;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl;
import com.fr.fs.dao.EntryDAO;
import com.fr.stable.fun.Service;

import java.util.List;

/**
 * excel里面的plate，先加进去
 * Created by sheldon on 15-1-22.
 */
public class ExcelPlate extends AbstractFSPlate {
    @Override
    public void initData() {

    }

    @Override
    public Service[] service4Register() {
        return new Service[] {
                //Fine excel
                new Service4Excel(),
                new Service4BIFSNoLogin()
        };
    }

    @Override
    public ObjectTableMapper[] mappers4Register() {
        return new ObjectTableMapper[] {
                BIExcelTableMapper.BI_SHARE_OTHER_REPORT_NODE.TABLE_MAPPER
        };
    }

    @Override
    public Class getRelationClass() {
        return null;
    }

    @Override
    public TableDataDAOControl.ColumnColumn[] getTableDataColumns() {
        return new TableDataDAOControl.ColumnColumn[0];
    }

    @Override
    public FieldColumnMapper[] columnMappers4Company() {
        return new FieldColumnMapper[0];
    }

    @Override
    public FieldColumnMapper[] columnMappers4Custom() {
        return new FieldColumnMapper[0];
    }

    @Override
    public RelationFCMapper getRelationFCMapper4Custom() {
        return null;
    }

    @Override
    public RelationFCMapper getRelationFCMapper4Company() {
        return null;
    }

    @Override
    public Object createPrivilegeObject(long l) {
        return null;
    }

    @Override
    public List getAllPrivilegesID() {
        return null;
    }

    @Override
    public List<EntryDAO> getEntryDaoAccess() {
        return null;
    }

    @Override
    public void refreshManager() {

    }

    @Override
    public void release() {

    }

    @Override
    public boolean isSupport() {
        return false;
    }

    @Override
    public boolean needPrivilege() {
        return false;
    }
}