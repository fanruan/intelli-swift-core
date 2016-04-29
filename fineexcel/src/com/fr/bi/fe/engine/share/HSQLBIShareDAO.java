package com.fr.bi.fe.engine.share;

import com.fr.fs.dao.FSDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon on 15-1-19.
 */
public class HSQLBIShareDAO extends FSDAO implements BIShareDAO {
    private static HSQLBIShareDAO SC;
    private HSQLBIShareDAO() {

    }

    public static HSQLBIShareDAO getInstance() {
        if( SC == null ) {
            SC = new HSQLBIShareDAO();
        }

        return SC;
    }
    /**
     * 保存或编辑BI内容
     *
     * @param node
     */
    @Override
    public void saveOrUpdate(BIShareOtherNode node) throws Exception {
        createSession().saveOrUpdate( node );
    }

    /**
     * 根据shared_id获取BIReport
     * @param shared_id   分享的唯一值
     * @return
     * @throws Exception
     */
    @Override
    public BIShareOtherNode findByID(String shared_id) throws Exception {
        List list = createSession().listByFieldValue(BIShareOtherNode.class, BIExcelTableMapper.BI_SHARE_OTHER_REPORT_NODE.FIELD_SHARED_OTHER_SHARE_ID, shared_id);
        if( list.size() != 1 ) {
            return null;
        } else {
            return (BIShareOtherNode) list.get(0);
        }
    }

    @Override
	public String findSharedIDByUserAndMode(long user_id, long mode_id) throws Exception {
        BIShareOtherNode node = findSharedNodeByUserAndMode(user_id, mode_id );
        if( node != null ) {
            return node.getShared_id();
        } else {
            return null;
        }
    }

    public BIShareOtherNode findSharedNodeByUserAndMode( long user_id, long mode_id ) throws Exception {
        List biSharedOtherList = listAll();
        BIShareOtherNode currentNode = new BIShareOtherNode( user_id, mode_id );
        String sharedId = null;

        for( int n=0; n<biSharedOtherList.size(); n++ ) {
            if( currentNode.equals4Properties( biSharedOtherList.get(n) ) ) {
                return ( (BIShareOtherNode)biSharedOtherList.get(n) );
            }
        }

        return null;
    }

    /**
     * 找到所有当前用户分享的所有node
     * @param user_id
     * @return
     */
    public List findSharedNodeByUser( long user_id ) {
        List list = createSession().listByFieldValue(BIShareOtherNode.class, BIExcelTableMapper.BI_SHARE_OTHER_REPORT_NODE.FIELD_SHARED_OTHER_USER_ID, user_id);
        if( list != null ) {
            return list;
        } else {
            return new ArrayList();
        }
    }

    /**
     * 获取所有的report
     *
     * @return
     * @throws Exception
     */
    @Override
    public List listAll() throws Exception {
        return createSession().list( BIShareOtherNode.class );
    }
}