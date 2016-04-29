ETLCst.ANALYSIS_TABLE_SET = {
    EDIT : 1001,
    RENAME : 1002,
    COPY : 1003,
    DELETE : 1004
};


ETLCst.ANALYSIS_ETL_PAGES = {
    SELECT_DATA :"bi.analysis_etl_operator_select_data",
    SELECT_NONE_DATA : "bi.analysis_etl_operator_select_none_data",
    FILTER:"bi.analysis_etl_operator_filter",
    GROUP_SUMMARY:"bi.analysis_etl_operator_summary",
    ADD_COLUMN:"bi.analysis_etl_operator_add_column",
    USE_PART_FIELDS:"bi.analysis_etl_operator_use_part_field",
    MERGE_SHEET:"bi.analysis_etl_operator_merge_sheet"
}

BI.ANALYSIS_ETL_HEADER = {
    DELETE:"delete",
    FILTER:"filter",
    NORMAL:"normal"
}

BI.ANALYSIS_ETL_ADD_COLUMN_TYPE = {
    FORMULA:"formula",
    DATE_DIFF:"date_diff",
    DATE_YEAR:"date_year",
    DATE_SEASON:"date_season",
    DATE_MONTH:"date_month",
    EXPR_CPP:"expr_same_period",
    EXPR_LP:"expr_last_period",
    EXPR_CPP_PERCENT:"expr_same_period_percent",
    EXPR_LP_PERCENT:"expr_last_period_percent",
    EXPR_SUM:"expr_sum",
    EXPR_ACC:"expr_acc",
    EXPR_RANK:"expr_rank",
    GROUP:"group_value",
    SINGLE_VALUE:"single_value",
    VALUE_CONVERT:"value_convert"
}