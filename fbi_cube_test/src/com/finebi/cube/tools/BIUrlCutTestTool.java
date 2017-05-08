package com.finebi.cube.tools;

/**
 * Created by Lucifer on 2017-2-15.
 *
 * @author Lucifer
 * @since 4.0
 */
public class BIUrlCutTestTool {

    /**
     * 仅限没有重复的url
     * @param key
     * @param url
     * @return
     */
    public static String[] cutUrl(String key, String url) {
        String[] strs = url.split(key);
        return strs;
    }

    public static String joinUrl(String[] strs,String key,String newStr){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(strs[0]).append(key).append(newStr).append(strs[1]);
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        String str = "aaaaabbbbbcccccddddd";

        System.out.print(joinUrl(cutUrl("bbbbb",str),"bbbbb","hhh"));
    }
}
