package com.fr.bi.stable.utils;

import com.fr.bi.stable.utils.file.FtpBusiness;
import junit.framework.TestCase;

/**
 * This class created on 2016/6/14.
 *
 * @author Connery
 * @since 4.0
 */
public class FTPTest extends TestCase {
    public void testFtp() {
        FtpBusiness ftpBusiness = new FtpBusiness();
        ftpBusiness.connectServer("env.finedevelop.com", 21, "administrator", "123", "BI4.0");
        ftpBusiness.download("md5sums", "D:/temp/sum");
    }
}
