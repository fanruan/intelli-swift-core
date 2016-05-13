//放置用户不可配置的常量

BICst.Widget.Widths = {};
BICst.Widget.Widths[BICst.Widget.TABLE] = 450;
BICst.Widget.Widths[BICst.Widget.BAR] = 450;
BICst.Widget.Widths[BICst.Widget.ACCUMULATE_BAR] = 450;
BICst.Widget.Widths[BICst.Widget.PIE] = 450;
BICst.Widget.Widths[BICst.Widget.DASHBOARD] = 450;
BICst.Widget.Widths[BICst.Widget.AXIS] = 450;
BICst.Widget.Widths[BICst.Widget.MAP] = 450;
BICst.Widget.Widths[BICst.Widget.DETAIL] = 450;
BICst.Widget.Widths[BICst.Widget.DOUGHNUT] = 450;
BICst.Widget.Widths[BICst.Widget.BUBBLE] = 450;
BICst.Widget.Widths[BICst.Widget.SCATTER] = 450;
BICst.Widget.Widths[BICst.Widget.RADAR] = 450;
BICst.Widget.Widths[BICst.Widget.CONTENT] = 450;
BICst.Widget.Widths[BICst.Widget.IMAGE] = 450;
BICst.Widget.Widths[BICst.Widget.WEB] = 450;

BICst.Widget.Widths[BICst.Widget.STRING] = 250;
BICst.Widget.Widths[BICst.Widget.NUMBER] = 300;
BICst.Widget.Widths[BICst.Widget.DATE] = 320;
BICst.Widget.Widths[BICst.Widget.YEAR] = 250;
BICst.Widget.Widths[BICst.Widget.QUARTER] = 250;
BICst.Widget.Widths[BICst.Widget.MONTH] = 250;
BICst.Widget.Widths[BICst.Widget.YMD] = 250;
BICst.Widget.Widths[BICst.Widget.TREE] = 250;
BICst.Widget.Widths[BICst.Widget.GENERAL_QUERY] = 450;
BICst.Widget.Widths[BICst.Widget.QUERY] = 250;
BICst.Widget.Widths[BICst.Widget.RESET] = 250;

BICst.Widget.Heights = {};
BICst.Widget.Heights[BICst.Widget.TABLE] = 250;
BICst.Widget.Heights[BICst.Widget.BAR] = 250;
BICst.Widget.Heights[BICst.Widget.ACCUMULATE_BAR] = 250;
BICst.Widget.Heights[BICst.Widget.PIE] = 250;
BICst.Widget.Heights[BICst.Widget.DASHBOARD] = 250;
BICst.Widget.Heights[BICst.Widget.AXIS] = 250;
BICst.Widget.Heights[BICst.Widget.MAP] = 250;
BICst.Widget.Heights[BICst.Widget.DETAIL] = 250;
BICst.Widget.Heights[BICst.Widget.DOUGHNUT] = 250;
BICst.Widget.Heights[BICst.Widget.BUBBLE] = 250;
BICst.Widget.Heights[BICst.Widget.SCATTER] = 250;
BICst.Widget.Heights[BICst.Widget.RADAR] = 250;
BICst.Widget.Heights[BICst.Widget.CONTENT] = 250;
BICst.Widget.Heights[BICst.Widget.IMAGE] = 250;
BICst.Widget.Heights[BICst.Widget.WEB] = 250;

BICst.Widget.Heights[BICst.Widget.STRING] = 110;
BICst.Widget.Heights[BICst.Widget.NUMBER] = 110;
BICst.Widget.Heights[BICst.Widget.DATE] = 110;
BICst.Widget.Heights[BICst.Widget.YEAR] = 110;
BICst.Widget.Heights[BICst.Widget.QUARTER] = 110;
BICst.Widget.Heights[BICst.Widget.MONTH] = 110;
BICst.Widget.Heights[BICst.Widget.YMD] = 110;
BICst.Widget.Heights[BICst.Widget.TREE] = 110;
BICst.Widget.Heights[BICst.Widget.GENERAL_QUERY] = 250;
BICst.Widget.Heights[BICst.Widget.QUERY] = 60;
BICst.Widget.Heights[BICst.Widget.RESET] = 60;

//布局方式
BICst.DASHBOARD_LAYOUT_ARRAY = [{
    text: BI.i18nText("BI-Adaptive_Layout"), value: BICst.DASHBOARD_LAYOUT_ADAPT
}, {
    text: BI.i18nText("BI-Free_Layout"), value: BICst.DASHBOARD_LAYOUT_FREE
}];

//类型&数据/样式tab
BICst.DETAIL_DATA_STYLE_TAB = [{
    text: BI.i18nText("BI-Type_Data"), value: BICst.DETAIL_TAB_TYPE_DATA
}, {
    text: BI.i18nText("BI-Style"), value: BICst.DETAIL_TAB_STYLE
}];

//业务包字段/复用
BICst.DETAIL_FIELD_REUSE_TAB = [{
    text: BI.i18nText("BI-Package_Field"), value: BICst.DETAIL_PACKAGES_FIELD
}, {
    text: BI.i18nText("BI-Reuse_Field"), value: BICst.DETAIL_DIMENSION_REUSE
}];

//etl pane card names (empty tip, only one tip, pane)
BICst.CONF_ETL_DATA_SET_EMPTY_TIP = "data_set_empty_tip";
BICst.CONF_ETL_DATA_SET_ONLY_ONE_TIP = "data_set_only_one_tip";
BICst.CONF_ETL_DATA_SET_PANE = "data_set_pane";
BICst.CONF_ETL_SET_EMPTY_TIP = "etl_empty_tip";
BICst.CONF_ETL_SET_PANE = "etl_set_pane";