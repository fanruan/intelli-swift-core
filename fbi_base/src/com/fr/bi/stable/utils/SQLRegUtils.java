package com.fr.bi.stable.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kary on 16/7/15.
 */
public class SQLRegUtils {
    /**
     * 逗号
     */
    private static final String Comma = ",";

    /**
     * 四个空格tab
     */
    private static final String FourSpace = "    ";

    /**
     * 是否单行显示字段，表，条件的标识量
     */
    private static boolean isSingleLine=true;

    /**
     * 待解析的SQL语句
     */
    private String sql;

    /**
     * SQL中选择的列
     */
    private String cols;

    /**
     * SQL中查找的表
     */
    private String tables;

    /**
     * 查找条件
     */
    private String conditions;

    /**
     * Group By的字段
     */
    private String groupCols;

    /**
     * Order by的字段
     */
    private String orderCols;

    /**
     * 构造函数
     * 功能：传入构造函数，解析成字段，表，条件等
     * @param sql：传入的SQL语句
     */
    public SQLRegUtils(String sql){
        this.sql=sql.trim();

        parseCols();
        parseTables();
        parseConditions();
        parseGroupCols();
        parseOrderCols();
    }

    /**
     * 解析选择的列
     *
     */
    private void parseCols(){
        String regex="(select)(.+)(from)";
        cols=getMatchedString(regex,sql);
    }

    /**
     * 解析选择的表
     *
     */
    private void parseTables(){
        String regex="";

        if(isContains(sql,"\\s+where\\s+")){
            regex="(from)(.+)(where)";
        }
        else{
            regex="(from)(.+)($)";
        }

        tables=getMatchedString(regex,sql);
    }

    /**
     * 解析查找条件
     *
     */
    private void parseConditions(){
        String regex="";

        if(isContains(sql,"\\s+where\\s+")){
            // 包括Where，有条件

            if(isContains(sql,"group\\s+by")){
                // 条件在where和group by之间
                regex="(where)(.+)(group\\s+by)";
            }
            else if(isContains(sql,"order\\s+by")){
                // 条件在where和order by之间
                regex="(where)(.+)(order\\s+by)";
            }
            else{
                // 条件在where到字符串末尾
                regex="(where)(.+)($)";
            }
        }
        else{
            // 不包括where则条件无从谈起，返回即可
            return;
        }

        conditions=getMatchedString(regex,sql);
    }

    /**
     * 解析GroupBy的字段
     *
     */
    private void parseGroupCols(){
        String regex="";

        if(isContains(sql,"group\\s+by")){
            // 包括GroupBy，有分组字段

            if(isContains(sql,"order\\s+by")){
                // group by 后有order by
                regex="(group\\s+by)(.+)(order\\s+by)";
            }
            else{
                // group by 后无order by
                regex="(group\\s+by)(.+)($)";
            }
        }
        else{
            // 不包括GroupBy则分组字段无从谈起，返回即可
            return;
        }

        groupCols=getMatchedString(regex,sql);
    }

    /**
     * 解析OrderBy的字段
     *
     */
    private void parseOrderCols(){
        String regex="";

        if(isContains(sql,"order\\s+by")){
            // 包括GroupBy，有分组字段
            regex="(order\\s+by)(.+)($)";
        }
        else{
            // 不包括GroupBy则分组字段无从谈起，返回即可
            return;
        }

        orderCols=getMatchedString(regex,sql);
    }


    /**
     * 从文本text中找到regex首次匹配的字符串，不区分大小写
     * @param regex： 正则表达式
     * @param text：欲查找的字符串
     * @return regex首次匹配的字符串，如未匹配返回空
     */
    private static String getMatchedString(String regex,String text){
        Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);

        Matcher matcher=pattern.matcher(text);

        while(matcher.find()){
            return matcher.group(2);
        }

        return null;
    }

    /**
     * 看word是否在lineText中存在，支持正则表达式
     * @param lineText
     * @param word
     * @return
     */
    private static boolean isContains(String lineText,String word){
        Pattern pattern=Pattern.compile(word,Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(lineText);
        return matcher.find();
    }


    public String toString(){
        // 无法解析则原样返回
        if(cols==null && tables==null && conditions==null && groupCols==null && orderCols==null ){
            return sql;
        }

        StringBuffer sb=new StringBuffer();

        for(String str:getParsedSqlList()){
            sb.append(str);
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * 在分隔符后加上回车
     * @param str
     * @param splitStr
     * @return
     */
    private static String getAddEnterStr(String str,String splitStr){
        Pattern p = Pattern.compile(splitStr,Pattern.CASE_INSENSITIVE);

        // 用Pattern类的matcher()方法生成一个Matcher对象
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();

        // 使用find()方法查找第一个匹配的对象
        boolean result = m.find();

        // 使用循环找出模式匹配的内容替换之,再将内容加到sb里
        while (result) {
            m.appendReplacement(sb, m.group(0) + "\n     ");
            result = m.find();
        }
        // 最后调用appendTail()方法将最后一次匹配后的剩余字符串加到sb里；
        m.appendTail(sb);

        return FourSpace+sb.toString();
    }

    /**
     * 取得解析的SQL字符串列表
     * @return
     */
    public List<String> getParsedSqlList(){
        List<String> sqlList=new ArrayList<String>();

        // 无法解析则原样返回
        if (cols == null || tables == null) {
            return sqlList;
        }

        if(cols!=null){
            sqlList.add("select\n");
            if(isSingleLine){
                sqlList.add(getAddEnterStr(cols,Comma));
            }
            else{
                sqlList.add(FourSpace+cols);
            }
        }

        if(tables!=null){
            sqlList.add(" \nfrom\n");

            if(isSingleLine){
                sqlList.add(getAddEnterStr(tables,Comma));
            }
            else{
                sqlList.add(FourSpace+tables);
            }
        }

        if(conditions!=null){
            sqlList.add(" \nwhere\n");

            if(isSingleLine){
                sqlList.add(getAddEnterStr(conditions,"(and|or)"));
            }
            else{
                sqlList.add(FourSpace+conditions);
            }
        }

        if(groupCols!=null){
            sqlList.add(" \ngroup by\n");

            if(isSingleLine){
                sqlList.add(getAddEnterStr(groupCols,Comma));
            }
            else{
                sqlList.add(FourSpace+groupCols);
            }
        }

        if(orderCols!=null){
            sqlList.add(" \norder by\n");

            if(isSingleLine){
                sqlList.add(getAddEnterStr(orderCols,Comma));
            }
            else{
                sqlList.add(FourSpace+orderCols);
            }
        }

        return sqlList;
    }

    public boolean isSql() {
        return cols != null && tables != null;
    }
    /**
     * 设置是否单行显示表，字段，条件等
     * @param isSingleLine
     */
    public static void setSingleLine(boolean isSingleLine) {
        SQLRegUtils.isSingleLine = isSingleLine;
    }

    public static String getComma() {
        return Comma;
    }

    public static String getFourSpace() {
        return FourSpace;
    }

    public static boolean isSingleLine() {
        return isSingleLine;
    }

    public String getSql() {
        return sql;
    }

    public String getCols() {
        return cols;
    }

    public String getTables() {
        return tables;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getGroupCols() {
        return groupCols;
    }

    public String getOrderCols() {
        return orderCols;
    }
}
