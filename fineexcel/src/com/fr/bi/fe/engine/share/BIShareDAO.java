package com.fr.bi.fe.engine.share;

import java.util.List;

/**
 * Created by sheldon on 15-1-19.
 */
public interface BIShareDAO {
    /**
     * 保存或编辑BI内容
     * @param node
     */
    public void saveOrUpdate(BIShareOtherNode node) throws Exception;

    /**
     * 根据id获取BIReport
     * @param shared_id
     * @return
     * @throws Exception
     */
    public BIShareOtherNode findByID(String shared_id) throws Exception ;

    /**
     * 通过用户id和模板id获取到分享的唯一id
     * @param user_id
     * @param mode_id
     * @return
     */
    public String findSharedIDByUserAndMode(long user_id, long mode_id) throws Exception;

    /**
     * 获取所有的report
     * @return
     * @throws Exception
     */
    public List listAll() throws Exception ;
}