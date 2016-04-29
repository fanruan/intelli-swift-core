package com.fr.bi.conf.fs;

import com.fr.base.FRContext;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.LicUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/8/21.
 */
public class BIUserAuthorAttr implements XMLable {
    public static final String XML_TAG = "UserAuthorAttr";
    public static final int DEFAULT_MOBILE_USER_AUTH_LIMIT = 0;
    private final static int EDIT = 1;
    private final static int VIEW = 2;
    private final static int MOBILE = 3;
    private static long biEditUserLimit = 0;
    private static long biViewUserLimit = 0;
    private static long biMobileUserLimit = 0;
    private static long fsMobileUserLimit = 0;
    //bi编辑用户名单
    private JSONObject biEditUserJo = new JSONObject();
    //bi查看用户名单
    private JSONObject biViewUserJo = new JSONObject();
    //bi移动用户名单
    private JSONObject biMobileUserJo = new JSONObject();


    //从lic中读取保存的bi用户权限
    public void refreshLimitedUser() {
        JSONObject licJo = LicUtils.getJsonFromLic();
//        BI_MOBILE
//        BI_MAKER
//        BI_USER=50
//        0 代码不限制
        if (licJo.has("BI_USER")) {
            biViewUserLimit = Math.max(DEFAULT_MOBILE_USER_AUTH_LIMIT, licJo.optInt("BI_USER"));
        }
        if (licJo.has("BI_MOBILE")) {
            biMobileUserLimit = Math.max(DEFAULT_MOBILE_USER_AUTH_LIMIT, licJo.optInt("BI_MOBILE"));
        }
        if (licJo.has("BI_MAKER")) {
            biEditUserLimit = Math.max(DEFAULT_MOBILE_USER_AUTH_LIMIT, licJo.optInt("BI_MAKER"));
        }
        if (licJo.has(LicUtils.MOBILE_FS_USER)) {
            fsMobileUserLimit = Math.max(DEFAULT_MOBILE_USER_AUTH_LIMIT, licJo.optInt(LicUtils.MOBILE_FS_USER));
        }
    }

    private void readChild(XMLableReader reader) {
        String tagName = reader.getTagName();
        if (ComparatorUtils.equals("biEditAuth", tagName)) {
            reader.readXMLObject(getBiAuthReaderByMode(EDIT));
        } else if (ComparatorUtils.equals("biViewAuth", tagName)) {
            reader.readXMLObject(getBiAuthReaderByMode(VIEW));
        } else if (ComparatorUtils.equals("biMobileAuth", tagName)) {
            reader.readXMLObject(getBiAuthReaderByMode(MOBILE));
        }
    }

    public long getBIAuthUserLimitByMode(int mode) {
        switch (mode) {
            case EDIT:
                return biEditUserLimit;
            case VIEW:
                return biViewUserLimit;
            case MOBILE:
                return biMobileUserLimit;
            default:
                return 0;
        }
    }

    public long getBiEditUserLimit() {
        return biEditUserLimit;
    }

    public long getBiViewUserLimit() {
        return biViewUserLimit;
    }

    public JSONObject getBIAuthUserJoByMode(int mode) {
        switch (mode) {
            case EDIT:
                return biEditUserJo;
            case VIEW:
                return biViewUserJo;
            case MOBILE:
                return biMobileUserJo;
            default:
                return new JSONObject();
        }
    }

    public JSONObject getBIEditUserJo() {
        return biEditUserJo;
    }

    public JSONObject getBIViewUserJo() {
        return biViewUserJo;
    }

    private XMLReadable getBiAuthReaderByMode(final int mode) {
        return new XMLReadable() {
            @Override
            public void readXML(XMLableReader reader) {
                if (reader.isChildNode()) {
                    try {
                        String userName = "";
                        String fullName = "";
                        if (ComparatorUtils.equals("authUser", reader.getTagName())) {
                            userName = reader.getAttrAsString("username", "");
                            fullName = reader.getAttrAsString("fullname", "");
                        }
                        JSONObject userJo = getBIAuthUserJoByMode(mode);
                        if (StringUtils.isNotEmpty(userName)) {
                            //等于0代表不限制
                            if (getBIAuthUserLimitByMode(mode) == 0 || userJo.length() < getBIAuthUserLimitByMode(mode)) {
                                userJo.put(userName, fullName);
                            }
                        }

                    } catch (JSONException e) {
                        FRContext.getLogger().error(e.getMessage());
                    }
                }
            }
        };
    }


    private void writerBIUserAuthXMLByMode(XMLPrintWriter writer, String tagName, int mode) {
        JSONObject userAuthJo = this.getBIAuthUserJoByMode(mode);
        if (userAuthJo.length() > 0) {
            writer.startTAG(tagName);
            try {
                //排序之后保存，便于lic变化时只显示前面一个
                JSONArray authJa = getUserAuthJaByMode(mode, "");
                for (int i = 0; i < authJa.length(); i++) {
                    writer.startTAG("authUser");
                    JSONObject oneAuth = authJa.getJSONObject(i);
                    String username = oneAuth.getString("username");
                    String realname = oneAuth.getString("realname");

                    writer.attr("username", username);
                    writer.attr("fullname", realname);
                    writer.end();
                }
            } catch (JSONException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            writer.end();
        }
    }

    public JSONArray getUserAuthJaByMode(int mode, String keyword) throws JSONException {
        JSONObject userAuthJo = this.getBIAuthUserJoByMode(mode);
        Iterator<?> keyIterator = userAuthJo.keys();
        java.util.List<JSONObject> userList = new ArrayList<JSONObject>();
        long id = 0;
        while (keyIterator.hasNext()) {
            JSONObject user = new JSONObject();
            String userName = (String) keyIterator.next();
            user.put("username", userName);
            user.put("realname", userAuthJo.get(userName));
            if (!isKeywordInUserInfo(user, keyword)) {
                continue;
            }
            user.put("id", ++id);
            userList.add(user);
        }
        Collections.sort(userList, new AuthUserComparator());
        JSONArray ja = new JSONArray();
        for (JSONObject jo : userList) {
            ja.put(jo);
        }

        return ja;
    }

    private boolean isKeywordInUserInfo(JSONObject useJo, String keyword) {
        if (containsNoCase(useJo.optString("username", ""), keyword) || containsNoCase(useJo.optString("realname", ""), keyword)) {
            return true;
        }
        return false;
    }


    private boolean containsNoCase(String str1, String str2) {
        return str1.toUpperCase().contains(str2.toUpperCase());
    }


    /**
     * 读xml方法
     *
     * @param reader
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            readChild(reader);
        }
    }

    /**
     * 写xml方法
     *
     * @param writer
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writerBIUserAuthXMLByMode(writer, "biEditAuth", EDIT);
        writerBIUserAuthXMLByMode(writer, "biViewAuth", VIEW);
        writerBIUserAuthXMLByMode(writer, "biMobileAuth", MOBILE);
        writer.end();
    }



    /**
     * 用于按用户名排序的Comparator
     */
    class AuthUserComparator implements Comparator<JSONObject> {

        @Override
        public int compare(JSONObject a, JSONObject b) {
            try {
                String valA = a.getString("username");
                String valB = b.getString("username");
                return valA.compareTo(valB);
            } catch (Exception ignore) {
                //忽略比较错误
            }
            return 0;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}