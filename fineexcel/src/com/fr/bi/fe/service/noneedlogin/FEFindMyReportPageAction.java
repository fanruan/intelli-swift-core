package com.fr.bi.fe.service.noneedlogin;

import com.fr.bi.fe.engine.share.BIShareOtherNode;
import com.fr.bi.fe.engine.share.HSQLBIShareDAO;
import com.fr.bi.fs.BIReportNode;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.CodeUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 获取用户自己保存的biReport
 *
 */
public class FEFindMyReportPageAction extends ActionNoSessionCMD {

	@Override
	public String getCMD() {
		return "fe_my_bi_saved";
	}


	private void sortByModifyTime(List nodeList) {
		Collections.sort(nodeList, new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				if(o1 instanceof BIReportNode && o2 instanceof BIReportNode){
					return 0 - ComparatorUtils.compare(((BIReportNode)o1).getLastModifyTime(), ((BIReportNode)o2).getLastModifyTime());
				}
				return 0;
			}
			
		});
	}

    private BIShareOtherNode getShareOtherNode( BIReportNode node, List shareList ) {
        if( shareList== null ) {
            return null;
        }
        long node_id = node.getId();
        for( int i=0; i<shareList.size(); i++ ) {

            if( ((BIShareOtherNode)shareList.get(i)).getMode_id() == node_id ){
                return (BIShareOtherNode) shareList.get(i);
            }
        }

        return null;
    }


	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String isMobile = WebUtils.getHTTPRequestParameter(req, "isMobile");
		long userId = ServiceUtils.getCurrentUserID(req);
		List nodeList = UserControl.getInstance().getBIReportNodes(userId);
		boolean isShareList = Boolean.valueOf(WebUtils.getHTTPRequestParameter(req, "isShareList"));
		if(nodeList == null){
			nodeList = new ArrayList();
		}
		sortByModifyTime(nodeList);
		JSONArray ja = new JSONArray();
		Iterator iter = nodeList.iterator();
		List list = HSQLBIShareDAO.getInstance().findSharedNodeByUser(userId);

		while(iter.hasNext()){
			BIReportNode node = (BIReportNode) iter.next();
			JSONObject nodeJo = node.createJSONConfig()
					.put("available", true).put("createTime", node.getCreateTime().getTime()).put("description", "fine_excel");
			//只有要求获取分享列表的时候，才获取是否分享
			if( isShareList ) {

				BIShareOtherNode shareNode = getShareOtherNode(node, list);
				//只展示已经分享的模板
                if( shareNode != null && shareNode.isShared()  ) {
                    nodeJo.put("isShared", true );
                } else {
                    continue;
				}
			}
			ja.put( nodeJo );

		}

		for(int i = 0; i < ja.length(); i++){
			JSONObject jo = ja.getJSONObject(i);
			String buildurl = jo.getString("buildurl");
			String url = buildurl.replaceAll("bi_init", "fe_bi_init");
			jo.put("buildurl", url);
			ja.put(i, jo);
		}

		if(isMobile != null) {
			WebUtils.printAsJSON(res, ja);
			return;
		}

		JSONObject jo = new JSONObject();
		jo.put("nodes", ja);
		Map parmeterMap = new HashMap();
		parmeterMap.put("bilist", CodeUtils.javascriptEncode(jo.toString()));
		WebUtils.printAsJSON(res, jo);
	}
}