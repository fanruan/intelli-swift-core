package com.fr.bi;

import com.fr.general.IOUtils;
import com.fr.stable.Branch;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Properties;

/**
 * 关于公司产品信息的一些常量
 */
public class ProductConstants {

    private ProductConstants() {
    }

    public static final String DEFAULT_APP_NAME = "FineBI";

    /**
     * 产品名
     */
    public static final String APP_NAME = loadAttribute("APP_NAME", DEFAULT_APP_NAME);

    /**
     * 大的版本号
     */
    public static final String MAIN_VERSION = loadAttribute("Main_Version", "4");

    /**
     * 小的版本号
     */
    public static final String MINOR_VERSION = loadAttribute("Minor_Version", "0");


    public static final String REVISION_VERSION = loadAttribute("Revision_Version", "0");



    /**
     * 产品诞生时间
     */
    public static final String BORN = loadAttribute("BORN", "2001");

    /**
     * 公司名称
     */
    public static final String COMPANY_NAME = loadAttribute("COMPANY_NAME", "Fanruan Software Co. Ltd.");

    /**
     * 公司联系电话
     */
    public static final String COMPARE_TELEPHONE = "86-400-850-5048";

    /**
     * 公司企业QQ
     */
    public static final String SUPPORT_QQ = "http://wpa.b.qq.com/cgi/wpa.php?ln=1&key=XzgwMDA2MDI2OF8xNTM0MDVfODAwMDYwMjY4XzJf";

    /**
     * 公司网站地址
     */
    public static final String WEBSITE_URL = loadAttribute("WEBSITE_URL", "http://www.finebi.com");

    /**
     * 公司技术支持邮箱
     */
    public static final String SUPPORT_EMAIL = loadAttribute("SUPPORT_EMAIL", "support@fanruan.com");

    public static final String BUSINESS_EMAIL = loadAttribute("BUSINESS_EMAIL", "bussiness@fanruan.com");

    public static final String HELP_URL = loadAttribute("HELP_URL", "http://help.finebi.com");

    private static Properties PROP = null;


    /**
     * 正在工作的web工程名
     */
    public static String WEB_APP_NAME = null;

    private static String loadAttribute(String key, String defaultValue) {
        if (PROP == null) {
            PROP = new Properties();
            try {
                PROP.load(IOUtils.readResource("com/fr/bi/build.properties"));
            } catch (Exception e) {
                // do nothing
            }
        }

        String p = PROP.getProperty(key);
        if (StringUtils.isEmpty(p)) {
            p = defaultValue;
        }
        return p;
    }

    public static final Branch BRANCH = Branch.parse(loadAttribute("BRANCH", "stable"), loadAttribute("RELEASE", StringUtils.EMPTY));

    /**
     * 具体的版本号
     */
    public static final String VERSION = StableUtils.join(new String[]{
            ProductConstants.MAIN_VERSION, ProductConstants.MINOR_VERSION
    }, ".");

    /**
     * 详细的产品名字
     */
    public static final String PRODUCT_NAME = StableUtils.join(new String[]{
            APP_NAME, " ", VERSION
    });

    /**
     *
     */
    public static final String RELEASE_VERSION = StableUtils.join(new String[]{
            ProductConstants.MAIN_VERSION, ".", ProductConstants.MINOR_VERSION, ".", ProductConstants.REVISION_VERSION
    });

    public static final String HISTORY = BORN + "-" + (Calendar.getInstance().get(Calendar.YEAR) + 2);


    /**
     * 获取报表设计器的安装目录
     *
     * @return 报表设计器的安装目录
     */
    public static String getEnvHome() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            userHome = System.getProperty("userHome");
        }

        File envHome = new File(userHome + File.separator + "." + ProductConstants.APP_NAME + MAIN_VERSION + MINOR_VERSION);
        if (!envHome.exists()) {
            StableUtils.mkdirs(envHome);
        }

        return envHome.getAbsolutePath();
    }
}