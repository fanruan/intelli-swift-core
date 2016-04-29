//cursor路径
BICst.cursorUrl = "url('" + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/images/cursor/cursor_drag_hand.cur" + "'),auto";

//connection
BICst.CONNECTION = {};
BICst.CONNECTION.ETL_CONNECTION = "__FR_BI_ETL__";
BICst.TABLE_TYPE_SQL = "__FR_BI_SQL__";
BICst.TABLE_TYPE_EXCEL = "__FR_BI_EXCEL__";
BICst.TABLE_TYPE_CONNECTION = "__FR_BI_CONNECTION__";
BICst.PACKAGE = "BI-Package";

BICst.CURRENT_EDITING_PACKAGE = "__current__editing__package__";
BICst.SELECT_TABLES_LAYER = "__select_tables_layer__";
BICst.SELECT_ONE_TABLE_LAYER = "__select_one_table_layer__";
BICst.BODY_ELEMENT = "body";
BICst.LINKAGE = "__linkage__";

//data link type
BICst.DATA_LINK = {
    DATA_SOURCE: "__data_link_data_source__",
    PACKAGES: "__data_link_packages__",
    ETL: "__data_link_etl__"
};

//etl operators
BICst.ETL_OPERATOR = {
    JOIN: "join",
    UNION: "union",
    CONVERT: "convert",
    PARTIAL: "partial",
    FILTER: "filter",
    GROUP: "group",
    CIRCLE: "circle",
    FORMULA: "formula",
    NEW_GROUP: "new_group"
};