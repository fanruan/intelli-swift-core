/**
 *
 */
package com.fr.bi.fe.service;

import com.fr.base.ConfigManager;
import com.fr.base.TemplateUtils;
import com.fr.bi.conf.aconfig.BIBusiPackManagerInterface;
import com.fr.bi.conf.aconfig.BIInterfaceAdapter;
import com.fr.bi.conf.aconfig.singleuser.SingleUserBIBusiPackManager;
import com.fr.bi.fe.fs.data.FineExcelUserModel;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.base.entity.User;
import com.fr.fs.base.entity.UserInfo;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.FSLoadLoginAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.Constants;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;


/**
 * @author young
 *
 */
public class BIFELoginAction extends FSLoadLoginAction {
    private static final String HOSTURL = "http://hihidata.com";
    @Override
    protected String getRenderUrl() {
        return "${servletURL}?op=fbi&_=";
    }

    @Override
    public String getCMD() {
        return "login_fe";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        HttpSession session = req.getSession(true);
        String userFrom = WebUtils.getHTTPRequestParameter(req, "user_from");
        long userId = ServiceUtils.getCurrentUserID(req);
        if(userFrom != null){
//            String taobaoCode = WebUtils.getHTTPRequestParameter(req, "taobaoCode");
            //这个是淘宝部分，待用，暂时由于各种限制，先使用明道
//            JSONObject nick = FineExcelUserService.getInstance().getTaobaoUserNick(taobaoCode);
//            -------------MingDao------
//            JSONObject mingDaoUserTemp = FineExcelUserService.getInstance().getMingDaoUser(taobaoCode);
//            JSONObject mingDaoUser = mingDaoUserTemp.optJSONObject("user");
//            String username = mingDaoUser.getString("name");
//            String password = mingDaoUser.getString("id");
//            String email = mingDaoUser.getString("email");

            String username = "";
            String password = "";
            String email = "";
            String realname = "";
            //sina weibo
            if(userFrom.equals("sina")){
                String code = WebUtils.getHTTPRequestParameter(req, "code");
                JSONObject jo = FineExcelUserService.getInstance().getSinaWebUser(code);
                username = "sina_" + jo.getString("id");
                password = "sina_" + jo.getString("id");
                realname = jo.getString("username");
                //首先去数据库查看是否存在该用户, 如果还没有存在到数据库，那么在平台 和 数据库都有插入相关的用户信息
                boolean checkSinaWeibo = FineExcelUserService.getInstance().checkSinaWebUser(username);
                if(!checkSinaWeibo){
                    //insert FS
                    User uSina = new User();
                    uSina.setUsername(username);
                    uSina.setPassword(username);
                    uSina.setRealname(realname);
                    UserControl.getInstance().addUser(uSina);
                    //insert DB
                    long id = UserControl.getInstance().getUser(username, username).getId();
                    FineExcelUserService.getInstance().insertWeiboUser2DB(id, username);
                }else{
                    int uid = FineExcelUserService.getInstance().getUserIdByWeiboId(jo.getString("id"));
                    User u = UserControl.getInstance().getUser(Long.parseLong(String.valueOf(uid)));
                    username = u.getUsername();
                    password = u.getPassword();
                    realname = u.getRealname();
                }

            }else if(userFrom.equals("qq")){
                String code = WebUtils.getHTTPRequestParameter(req, "code");
                JSONObject jo = FineExcelUserService.getInstance().getQQUserbyCode(code);
                username = jo.getString("openid");
                password = jo.getString("openid");
                realname = jo.getString("username");
                //首先去数据库查看是否存在该用户, 如果还没有存在到数据库，那么在平台 和 数据库都有插入相关的用户信息
                boolean checkSinaWeibo = FineExcelUserService.getInstance().checkQQUser(username);
                if(!checkSinaWeibo){
                    //insert FS
                    User uSina = new User();
                    uSina.setUsername(username);
                    uSina.setPassword(username);
                    uSina.setRealname(realname);
                    UserControl.getInstance().addUser(uSina);
                    //insert DB
                    long id = UserControl.getInstance().getUser(username, username).getId();
                    FineExcelUserService.getInstance().insertQQUser2DB(id, username);
                }else{
                    int uid = FineExcelUserService.getInstance().getUserIdByQQId(jo.getString("openid"));
                    User u = UserControl.getInstance().getUser(Long.parseLong(String.valueOf(uid)));
                    username = u.getUsername();
                    password = u.getPassword();
                    realname = u.getRealname();
                }
            }

            User user = new User();
            user = UserControl.getInstance().getUser(username, password);
            boolean isTemplate = ComparatorUtils.equals(true, session.getAttribute("isTemplate"));
            PrintWriter writer = WebUtils.createPrintWriter(res);
            if (dealLoginInfo(req, res, username, password, isTemplate, false)) {
                UserInfo ui = new UserInfo(username, password, Boolean.valueOf(
                        WebUtils.getHTTPRequestParameter(req, Constants.FS.REMEMBER)));
                ui.dealBrowserCookies(res);
                Object oo = session.getAttribute(isTemplate ? Constants.PF.TEMPLATE_ORIGINAL_URL : Constants.FS.ORIGINAL_URL);
                String url = (oo == null) ? TemplateUtils
                        .render(getRenderUrl()) : oo.toString()
                        + "&_=" + System.currentTimeMillis();
                signOnSuccess(req, res, writer, url);
                FineExcelUserService.getInstance().saveUserLoginRecord(user.getId(), username);
            } else {
                JSONObject jo = new JSONObject();
                if(user != null){
                    jo.put("result", "exsit");
                }else if(user != null){
                    jo.put("email", user.getEmail());
                    jo.put("result", "nouse");
                }else{

                }
                WebUtils.printAsJSON(res, jo);
            }
            writer.flush();
            writer.close();
            BIBusiPackManagerInterface mgr = BIInterfaceAdapter.getBIBusiPackAdapter();
            SingleUserBIBusiPackManager singleUserBIBusiPackManager = (SingleUserBIBusiPackManager) mgr.getBusiPackageManager(user.getId());
            if(!singleUserBIBusiPackManager.hasAvailableAnalysisPacks()){
                singleUserBIBusiPackManager.addAnEmptyPack("Excel数据集", req.getLocale());
                singleUserBIBusiPackManager.getLastestedPackage()[0].setName("Excel数据集");
            }
        }else{
            String username = WebUtils.getHTTPRequestParameter(req, Constants.FS.FSUSERNAME);
            if (StringUtils.isEmpty(username)) {
                USERNAME = Constants.FS.USERNAME;
                username = WebUtils.getHTTPRequestParameter(req, Constants.FS.USERNAME);
            }
            String password = WebUtils.getHTTPRequestParameter(req, Constants.FS.FSPASSWORD);
            if (StringUtils.isEmpty(password)) {
                PASSWORD = Constants.FS.PASSWORD;
                password = WebUtils.getHTTPRequestParameter(req, Constants.FS.PASSWORD);
            }
            //是否是过来验证模板是否有权限的
            boolean isTemplate = ComparatorUtils.equals(true, session.getAttribute("isTemplate"));
            PrintWriter writer = WebUtils.createPrintWriter(res);
            User user = UserControl.getInstance().getByUserName(username);
            boolean status = false;
            if(user != null){
        	    status = FineExcelUserService.getInstance().getUserStatusById(user.getId());
            }else{
                //使用邮箱登陆
                FineExcelUserModel fineExcelUserModel = FineExcelUserService.getInstance().getFEUserByEmail(username);
                if(fineExcelUserModel != null){
                    user = UserControl.getInstance().getUser(fineExcelUserModel.getId());
                    if(user != null){
                        status = FineExcelUserService.getInstance().getUserStatusById(user.getId());
                        username = user.getUsername();
                    }
                }
            }
            if(status){
                if (dealLoginInfo(req, res, username, password, isTemplate, false)){
                    String remember = WebUtils.getHTTPRequestParameter(req, "phone");
                    boolean rememberPWD = remember.equals("true") ? true : false;
                    UserInfo ui = new UserInfo(username, password, rememberPWD);
                    ui.dealBrowserCookies(res);
                    Object oo = session.getAttribute(isTemplate ? Constants.PF.TEMPLATE_ORIGINAL_URL : Constants.FS.ORIGINAL_URL);
                    String url = (oo == null) ? TemplateUtils
                            .render(getRenderUrl()) : oo.toString()
                            + "&_=" + System.currentTimeMillis();
                    signOnSuccess(req, res, writer, url);
                    //待修改，应该要另起线程，保存用户登录记录
                    FineExcelUserService.getInstance().saveUserLoginRecord(user.getId(), username);
                }
            }else {
                JSONObject jo = new JSONObject();
                if(user == null){
                    jo.put("result", "noexsit");
                }else if(user != null && status == true){
                    jo.put("result", "exsit");
                }else if(user != null && status == false){
                    jo.put("email", user.getEmail());
                    jo.put("result", "nouse");
                    //未激活， 现在就发送邮件
                    //邮件的内容
                    JSONArray je = new JSONArray();
                    je.put(user.getEmail());

                    JSONArray userName = new JSONArray();
                    userName.put(user.getEmail());
                    JSONArray link = new JSONArray();
                    link.put(HOSTURL + "/?op=fbi_no_need_login&cmd=email_validate&email=" + user.getEmail() + "&validateCode="+FineExcelUserService.encode2hex(user.getEmail()));
                    JSONObject sub = new JSONObject();
                    sub.put("%username%", userName);
                    sub.put("%hihidatalink%", link);
                    String subject = "Hihidata注册邮件";
                    String from = "service@hihidata.com";
                    //发送邮件
                    FineExcelUserService.sendEmailBySendCould(je, sub, subject, from, 1);
                }else{

                }
                WebUtils.printAsJSON(res, jo);
            }
            BIBusiPackManagerInterface mgr = BIInterfaceAdapter.getBIBusiPackAdapter();
            SingleUserBIBusiPackManager singleUserBIBusiPackManager = (SingleUserBIBusiPackManager) mgr.getBusiPackageManager(user.getId());
            if(!singleUserBIBusiPackManager.hasAvailableAnalysisPacks()){
                singleUserBIBusiPackManager.addAnEmptyPack("Excel数据集", req.getLocale());
                singleUserBIBusiPackManager.getLastestedPackage()[0].setName("Excel数据集");
            }
            writer.flush();
            writer.close();


        }

    }

}