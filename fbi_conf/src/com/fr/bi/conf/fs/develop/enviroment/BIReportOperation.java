package com.fr.bi.conf.fs.develop.enviroment;

import com.fr.bi.conf.fs.develop.DeveloperConfig;
import com.fr.bi.conf.fs.develop.utility.ReportUitlity;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;


/**
 * Created by Connery on 2015/1/2.
 */
public class BIReportOperation {
    //    private static final String LOGON_SITE = "localhost";
//    private static final int LOGON_PORT = 8075;
    HttpClient client;

    public HttpClient login(String username, String userpsw) throws Exception {
        client = new HttpClient();
        client.getHostConfiguration().setHost(DeveloperConfig.getInstance().getHost(), DeveloperConfig.getInstance().getPort());
        PostMethod post = new PostMethod(DeveloperConfig.getInstance().getService() + "?op=fbi&cmd=login");
        NameValuePair name = new NameValuePair("fsusername", username);
        NameValuePair pass = new NameValuePair("fspassword", userpsw);
        NameValuePair fsremember = new NameValuePair("fsremember", "false");
        post.setRequestBody(new NameValuePair[]{
                name, pass, fsremember
        });
        int status = client.executeMethod(post);
//        System.out.println(post.getResponseBodyAsString());
        post.releaseConnection();
        return client;

    }

    public String openReport(String reportID) throws Exception {
        GetMethod get = new GetMethod(DeveloperConfig.getInstance().getService() + "?op=fr_bi&cmd=bi_init&id=" + reportID + "&createby=-999");
//        get.getParams().setParameter("http.protocol.allow-circular-redirects", false);
        get.setFollowRedirects(false);
        client.executeMethod(get);
        String content = get.getResponseBodyAsString();
        get.releaseConnection();
        return ReportUitlity.splitSessionID(content);
    }

    public byte[] generateExcel(String sessionID, String name) throws Exception {
        GetMethod getExcel = new GetMethod(DeveloperConfig.getInstance().getService() + "?op=fr_bi&cmd=bi_export_excel&sessionID=" + sessionID + "&name=" + name);
        client.executeMethod(getExcel);
        byte[] excelContent = getExcel.getResponseBody();
        getExcel.releaseConnection();
        return excelContent;
    }

    public byte[] getWidgetExcel(String reportID, String config, String widgetName) throws Exception {
        String sessionIdD = openReport(reportID);
        biWidgetSetting(sessionIdD, config);
        return generateExcel(sessionIdD, widgetName);
    }

    public byte[] excel() throws Exception {
        GetMethod getExcel = new GetMethod(DeveloperConfig.getInstance().getService() + "?op=fr_bi&cmd=bi_export_excel&sessionID=6198&name=%E7%BB%9F%E8%AE%A1%E7%BB%84%E4%BB%B6");
        client.executeMethod(getExcel);
        byte[] excelContent = getExcel.getResponseBody();
        getExcel.releaseConnection();
        return excelContent;
    }

    public int biWidgetSetting(String sessionID, String config) throws Exception {
        PostMethod post = new PostMethod("/WebReport/ReportServer?op=fr_bi&cmd=bi_widget_setting");
        NameValuePair model = new NameValuePair("__boxModel__", "true");
        NameValuePair session = new NameValuePair("sessionID", sessionID);
        NameValuePair widget = new NameValuePair("config", config);
        post.setRequestBody(new NameValuePair[]{
                model, session, widget
        });
        int status = client.executeMethod(post);
//        System.out.println(post.getResponseBodyAsString());
        post.releaseConnection();
        return status;
    }
}