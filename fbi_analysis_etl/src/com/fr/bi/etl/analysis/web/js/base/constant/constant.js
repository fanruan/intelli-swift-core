ETLCst.ENTERBUTTON = {};
ETLCst.ENTERBUTTON.GAP = 5;
ETLCst.ENTERBUTTON.HEIGHT = BI.DataStyleTab.prototype.constants.DETAIL_TAB_HEIGHT - (ETLCst.ENTERBUTTON.GAP*2);
ETLCst.ENTERBUTTON.WIDTH = 150;
ETLCst.ENTERBUTTON.POINTERWIDTH = 10;
ETLCst.ANALYSIS_LAYER="analysis_layer";
ETLCst.ANALYSIS_MERGE_LAYER="analysis_merge_layer";
ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER="analysis_popup_float_box";
ETLCst.ETL_UNION_STYLE = 5;

ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP = {
    CHOOSE_FIELD : {
        text:BI.i18nText("BI-choose_field"),
        value: ETLCst.ETL_TYPE.SELECT_DATA,
        operatorType:ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA
    },
    CHOOSE_NONE_FIELD : {
        text:BI.i18nText("BI-choose_field"),
        value: ETLCst.ETL_TYPE.SELECT_NONE_DATA,
        operatorType:ETLCst.ANALYSIS_ETL_PAGES.SELECT_NONE_DATA
    },
    FILTER : {
        text:BI.i18nText("BI-Filter"),
        value:ETLCst.ETL_TYPE.FILTER,
        operatorType:ETLCst.ANALYSIS_ETL_PAGES.FILTER
    },
    GROUP : {
        text:BI.i18nText("BI-group_summary"),
        value:ETLCst.ETL_TYPE.GROUP_SUMMARY,
        operatorType:ETLCst.ANALYSIS_ETL_PAGES.GROUP_SUMMARY
    },
    ADD_COLUMN : {
        text:BI.i18nText("BI-Add_Column"),
        value:ETLCst.ETL_TYPE.ADD_COLUMN,
        operatorType:ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN
    },
    USE_PART_FIELDS :  {
        text: BI.i18nText("BI-Use_Part_Of_Fields"),
        value:ETLCst.ETL_TYPE.USE_PART_FIELDS,
        operatorType:ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS
    },
    MERGED_TABLE : {
        text:BI.i18nText("BI-Merge_Table"),
        value:ETLCst.ETL_TYPE.MERGE_SHEET,
        operatorType:ETLCst.ANALYSIS_ETL_PAGES.MERGE_SHEET
    }
}

ETLCst.ANALYSIS_TABLE_PANE ="_pane";

ETLCst.ANALYSIS_TABLE_TITLE ="_title";

ETLCst.ANALYSIS_TABLE_OPERATOR_PREVIEW_HEADER="bi.analysis_etl_p_t_h_cell_";

ETLCst.ANALYSIS_DRAG_CLASS="bi-analysis-table-drag-button";

ETLCst.ANALYSIS_TABLE_OPERATOR = [
    ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER,
    ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.GROUP,
    ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.ADD_COLUMN,
    ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.USE_PART_FIELDS
]

ETLCst.ANALYSIS_TABLE_OPERATOR_KEY = {};
BI.each(ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP, function(idx, item){
    ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[item["value"]] = item;
});
ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL = null;

ETLCst.ANALYSIS_ADD_COLUMN_TYPE = [[{
    text:BI.i18nText("BI-Formula"),
    value:BICst.ETL_ADD_COLUMN_TYPE.FORMULA
}],[{
    el:{
        text:BI.i18nText("BI-Date_Dealer"),
        value:BI.i18nText("BI-Date_Dealer")
    },
    children:[{
        text:BI.i18nText("BI-Date_Diff"),
        value:BICst.ETL_ADD_COLUMN_TYPE.DATE_DIFF
    },{
        text:BI.i18nText("BI-Date_Year"),
        value:BICst.ETL_ADD_COLUMN_TYPE.DATE_YEAR
    },{
        text:BI.i18nText("BI-Date_Season"),
        value:BICst.ETL_ADD_COLUMN_TYPE.DATE_SEASON
    },{
        text:BI.i18nText("BI-Date_Month"),
        value:BICst.ETL_ADD_COLUMN_TYPE.DATE_MONTH
    }]
}],[{
    el:{
        text:BI.i18nText("BI-Expr_Data"),
        value:BI.i18nText("BI-Expr_Data"),
    },
    children:[{
        text:BI.i18nText("BI-Year_On_Value"),
        value:BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP
    },{
        text:BI.i18nText("BI-Month_On_Month_Value"),
        value:BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP
    },{
        text:BI.i18nText("BI-Year_On_Year_Rate"),
        value:BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP_PERCENT
    },{
        text:BI.i18nText("BI-Month_On_Month_Rate"),
        value:BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP_PERCENT
    },{
        text:BI.i18nText("BI-All_Values") +"/" + BI.i18nText("BI-All_Values_In_Group"),
        value:BICst.ETL_ADD_COLUMN_TYPE.EXPR_SUM
    },{
        text:BI.i18nText("BI-Cumulative_Value") +"/" + BI.i18nText("BI-Cumulative_Value_In_Group"),
        value:BICst.ETL_ADD_COLUMN_TYPE.EXPR_ACC
    },{
        text:BI.i18nText("BI-Ranging") +"/" + BI.i18nText("BI-Group_Ranking"),
        value:BICst.ETL_ADD_COLUMN_TYPE.EXPR_RANK
    }]
}],[{
    el:{
        text:BI.i18nText("BI-Tag_Value"),
        value:BI.i18nText("BI-Tag_Value")
    },
    children:[{
        text:BI.i18nText("BI-Group_Value"),
        value:BICst.ETL_ADD_COLUMN_TYPE.GROUP
    },{
        text:BI.i18nText("BI-Setted_Value"),
        value:BICst.ETL_ADD_COLUMN_TYPE.SINGLE_VALUE
    }]
}], [{
    text:BI.i18nText("BI-Convert"),
    value:BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT
}]]
ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE = {};
ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.YEAR = 0;
ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.SEASON = 1;
ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.MONTH = 2;
ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.DAY = 4;
ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE_ITEMS = [{
    text:BI.i18nText("BI-Year"),
    value:ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.YEAR
},{
    text:BI.i18nText("BI-Quarter"),
    value:ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.SEASON
},{
    text:BI.i18nText("BI-Month"),
    value:ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.MONTH
},{
    text:BI.i18nText("BI-Day"),
    value:ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.DAY
}]
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS = {};
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.FORMULA] = [BICst.COLUMN.STRING, BICst.COLUMN.NUMBER, BICst.COLUMN.DATE];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.DATE_DIFF] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.DATE_YEAR] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.DATE_SEASON] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.DATE_MONTH] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP_PERCENT] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP_PERCENT] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.EXPR_SUM] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.EXPR_ACC] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.EXPR_RANK] = [BICst.COLUMN.NUMBER];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.GROUP] = [BICst.COLUMN.STRING, BICst.COLUMN.NUMBER, BICst.COLUMN.DATE];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.SINGLE_VALUE] = [BICst.COLUMN.STRING, BICst.COLUMN.NUMBER, BICst.COLUMN.DATE];
ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT] = [BICst.COLUMN.STRING, BICst.COLUMN.NUMBER, BICst.COLUMN.DATE];

ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS = {};
ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[BICst.COLUMN.NUMBER] = [BICst.COLUMN.STRING, BICst.COLUMN.DATE]
ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[BICst.COLUMN.STRING] = [BICst.COLUMN.NUMBER, BICst.COLUMN.DATE]
ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[BICst.COLUMN.DATE] = [BICst.COLUMN.STRING,BICst.COLUMN.NUMBER]
ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS = [{
    text: BI.i18nText("BI-Rank_In_Asc"),
    value :  'BI-Calculate_Target_Rank_ASC',
    sortType: BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC,
    groupType : BICst.TARGET_TYPE.RANK
}, {
    text: BI.i18nText("BI-Rank_In_Des"),
    value: 'BI-Calculate_Target_Rank_DESC',
    sortType: BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC,
    groupType : BICst.TARGET_TYPE.RANK
},{
    text: BI.i18nText("BI-Asc_Group")+ BI.i18nText('BI-Ranging'),
    value: 'BI-Calculate_Target_Rank_Group_ASC',
    sortType: BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC,
    groupType : BICst.TARGET_TYPE.RANK_IN_GROUP
}, {
    text: BI.i18nText("BI-Des_Group") + BI.i18nText('BI-Ranging'),
    value: 'BI-Calculate_Target_Rank_Group_DESC',
    sortType: BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC,
    groupType : BICst.TARGET_TYPE.RANK_IN_GROUP
}]