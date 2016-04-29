package com.fr.bi.stable.utils;

/************************************************
 * Created by sheldon on 14-11-18.
 * <p/>
 * MD5 算法的Java Bean
 *
 * @author:Topcat Tuppin
 * Last Modified:10,Mar,2001
 *************************************************/

/************************************************
 MD5 算法的Java Bean
 @author:Topcat Tuppin
 Last Modified:10,Mar,2001
 *************************************************/

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

import java.math.BigInteger;

/**
 * **********************************************
 * md5 类实现了RSA Data Security, Inc.在提交给IETF
 * 的RFC1321中的MD5 message-digest 算法。
 * ***********************************************
 */

public class DecryptBi {
    private String originalPassword;
    private String pwd;//密钥

    public DecryptBi(String originalPassword, String pwd) {
        this.originalPassword = originalPassword;
        this.pwd = pwd;
    }

    public static String decrypt(String originalPassword) {
        return decrypt(originalPassword, "finebi");
    }

    public static String decrypt(String originalPassword, String pwd) {
        if (ComparatorUtils.equals(originalPassword, "")) {
            return "";
        }
        if (StringUtils.isEmpty(pwd)) {
            pwd = "655";
        }
        pwd = escape(pwd);
        if (StringUtils.isEmpty(originalPassword) || originalPassword.length() < 8) {
            BILogger.getLogger().error("A salt value could not be extracted from the encrypted message because it's length is too short. The message cannot be decrypted.");
            return "";
        }
        if (StringUtils.isEmpty(pwd) || pwd.length() <= 0) {
            BILogger.getLogger().error("Please enter a password with which to decrypt the message.");
            return "";
        }
        String prand = "";
        for (int i = 0; i < pwd.length(); i++) {
            prand += (int) (pwd.charAt(i));
        }
        int sPos = (int) Math.floor(prand.length() / 5);
        String mult = "" + prand.charAt(sPos) + prand.charAt(sPos * 2) + prand.charAt(sPos * 3) + prand.charAt(sPos * 4);
        if (sPos * 5 < prand.length()) {
            mult += prand.charAt(sPos * 5);
        }
        int incr = (int) Math.round(pwd.length() / 2.0);
        BigInteger modu = new BigInteger((long) (Math.pow(2, 31) - 1) + "");
        int salt = Integer.parseInt(originalPassword.substring(originalPassword.length() - 8, originalPassword.length()), 16);
        originalPassword = originalPassword.substring(0, originalPassword.length() - 8);
        prand += salt;
        while (prand.length() > 10) {
            prand = (new BigInteger(prand.substring(0, 10)).add(new BigInteger(prand.substring(10, prand.length())))).toString();
        }
        prand = (((new BigInteger(mult).multiply(new BigInteger(prand))).add(new BigInteger(incr + ""))).mod(modu)).toString();
        String enc_chr = "";
        String enc_str = "";
        for (int i = 0; i < originalPassword.length(); i += 2) {
            enc_chr = "" + (Integer.parseInt(originalPassword.substring(i, i + 2), 16) ^ (int) Math.floor((Double.parseDouble(prand) / (modu.doubleValue())) * 255));
            char c = (char) Integer.parseInt(enc_chr);
            enc_str += c;
            prand = (((new BigInteger(mult).multiply(new BigInteger(prand))).add(new BigInteger(incr + ""))).mod(modu)).toString();
        }
        return unescape(enc_str);
    }

    public static String encrypt(String originalPassword) {
        return decrypt(originalPassword, null);
    }

    public static String encrypt(String str, String pwd) {
        if (ComparatorUtils.equals(str, "")) {
            return "";
        }
        str = escape(str);
        if (StringUtils.isEmpty(pwd)) {
            pwd = "655";
        }
        pwd = escape(pwd);
        if (StringUtils.isEmpty(pwd)) {
            BILogger.getLogger().error("A salt value could not be extracted from the encrypted message because it's length is too short. The message cannot be decrypted.");
            return "";
        }
        String prand = "";
        for (int I = 0; I < pwd.length(); I++) {
            prand += (int) (pwd.charAt(I));
        }
        int sPos = (int) Math.floor(prand.length() / 5);
        int mult = Integer.parseInt("" + prand.charAt(sPos) + prand.charAt(sPos * 2) + prand.charAt(sPos * 3) + prand.charAt(sPos * 4) + prand.charAt(sPos * 5));
        int incr = (int) Math.ceil(pwd.length() / 2.0);
        BigInteger modu = new BigInteger((long) (Math.pow(2, 31) - 1) + "");
        if (mult < 2) {
            BILogger.getLogger().error("Please enter a password with which to decrypt the message.");
            return "";
        }
        int salt = (int) Math.round(Math.random() * 1000000000) % 100000000;
        prand += salt;
        while (prand.length() > 10) {
            prand = (new BigInteger(prand.substring(0, 10))).add(new BigInteger(prand.substring(10, prand.length()))).toString();
        }
        prand = (((new BigInteger(mult + "").multiply(new BigInteger(prand))).add(new BigInteger(incr + ""))).mod(modu)).toString();
        String enc_chr = "";
        String enc_str = "";
        for (int I = 0; I < str.length(); I++) {
            enc_chr = (((int) (str.charAt(I))) ^ (int) Math.floor((Double.parseDouble(prand) / (modu.doubleValue())) * 255)) + "";
            BigInteger tempBig = new BigInteger(enc_chr);
            if (tempBig.intValue() < 16) {
                enc_str += "0" + tempBig.toString(16);
            } else {
                enc_str += tempBig.toString(16);
            }
            prand = (((new BigInteger(mult + "").multiply(new BigInteger(prand))).add(new BigInteger(incr + ""))).mod(modu)).toString();
        }
        String str_salt = (new BigInteger(salt + "")).toString(16);
        while (str_salt.length() < 8) {
            str_salt = "0" + str_salt;
        }
        enc_str += str_salt;
        return enc_str;
    }

    private static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            boolean isDigit = Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j);
            if (isDigit) {
                tmp.append(j);
            } else if (j < 256) {
                tmp.append("%");
                if (j < 16) {
                    tmp.append("0");
                }
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    private static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (ComparatorUtils.equals(pos, lastPos)) {
                if (ComparatorUtils.equals(src.charAt(pos + 1), 'u')) {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static void main(String[] args) {
        encrypt("afafa");
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}