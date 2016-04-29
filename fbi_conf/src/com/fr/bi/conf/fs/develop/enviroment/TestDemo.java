package com.fr.bi.conf.fs.develop.enviroment;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Connery on 2015/1/3.
 */
public class TestDemo {
    static final String LOGON_SITE = "localhost";

    static final int LOGON_PORT = 8075;

    public static void main(String[] args) throws Exception {

        HttpClient client = new HttpClient();

        client.getHostConfiguration().setHost(LOGON_SITE, LOGON_PORT);
        PostMethod post = new PostMethod("/WebReport/ReportServer?op=fbi&cmd=login");
        NameValuePair name = new NameValuePair("fsusername", "1");
        NameValuePair pass = new NameValuePair("fspassword", "1");
        NameValuePair fsremember = new NameValuePair("fsremember", "false");
        post.setRequestBody(new NameValuePair[]{
                name, pass, fsremember
        });
        int status = client.executeMethod(post);
        System.out.println(post.getResponseBodyAsString());
        post.releaseConnection();

        //查看cookie信息

        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] cookies = cookiespec.match(LOGON_SITE, LOGON_PORT, "/", false, client.getState().getCookies());
        if (cookies.length == 0) {
            System.out.println("None");
        } else {
            for (int i = 0; i < cookies.length; i++) {
                System.out.println(cookies[i].toString());
            }
        }
        GetMethod get = new GetMethod("/WebReport/ReportServer?op=fr_bi&cmd=bi_init&id=22&createby=-999");
        client.executeMethod(get);
        String content = get.getResponseBodyAsString();
        get.releaseConnection();

    }
}