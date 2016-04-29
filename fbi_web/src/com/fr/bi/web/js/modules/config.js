//放置用户可以配置的常量

//dashboard toolbar
BICst.DASHBOARD_TOOLBAR = [{
    text: BI.i18nText('BI-Save_As'),
    value: BICst.DASHBOARD_TOOLBAR_SAVEAS,
    title: BI.i18nText('BI-Save_As'),
    cls: "toolbar-save-font"
}, {
    text: BI.i18nText('BI-Undo'),
    value: BICst.DASHBOARD_TOOLBAR_UNDO,
    title: BI.i18nText('BI-Undo'),
    cls: "toolbar-undo-font"
}, {
    text: BI.i18nText('BI-Redo'),
    value: BICst.DASHBOARD_TOOLBAR_REDO,
    title: BI.i18nText('BI-Redo'),
    cls: "toolbar-redo-font"
}];


BICst.DASHBOARD_WIDGET_ICON = [[{
    text: BI.i18nText("BI-Summary_Table"),
    value: BICst.Widget.TABLE,
    cls: "chart-table-font"
}, {
    text: BI.i18nText("BI-Cor_Chart"),
    value: BICst.Widget.AXIS,
    cls: "chart-axis-font",
    children: [{
        text: BI.i18nText("BI-Cor_Chart"),
        value: BICst.Widget.AXIS,
        cls: "chart-axis-font"
    }, {
        text: BI.i18nText("BI-Bar_Chart"),
        value: BICst.Widget.BAR,
        cls: "chart-bar-font"
    }, {
        text: BI.i18nText("BI-Stacked_Bar_Chart"),
        value: BICst.Widget.ACCUMULATE_BAR,
        cls: "chart-accumulate-bar-font"
    }]
}, {
    text: BI.i18nText("BI-Pie_Chart"),
    value: BICst.Widget.PIE,
    cls: "chart-pie-font"
}, {
    text: BI.i18nText("BI-Map"),
    value: BICst.Widget.MAP,
    cls: "chart-map-font"
}, {
    text: BI.i18nText("BI-Dashboard_Chart"),
    value: BICst.Widget.DASHBOARD,
    cls: "chart-dashboard-font"
}, {
    text: BI.i18nText("BI-Donut_Chart"),
    value: BICst.Widget.DOUGHNUT,
    cls: "chart-doughnut-font"
}, {
    text: BI.i18nText("BI-Detail_Table"),
    value: BICst.Widget.DETAIL,
    cls: "chart-detail-font"
}, {
    text: BI.i18nText("BI-More"),
    value: BICst.Widget.Content,
    cls: "chart-more-font",
    children: [{
        text: BI.i18nText("BI-Text_Widget"),
        value: BICst.Widget.Content,
        cls: "chart-content-font"
    }, {
        text: BI.i18nText("BI-Image_Widget"),
        value: BICst.Widget.IMAGE,
        cls: "chart-image-font"
    }, {
        text: BI.i18nText("BI-Web_Widget"),
        value: BICst.Widget.WEB,
        cls: "chart-web-font"
    }]
}, {
    text: BI.i18nText("BI-Bubble_Chart"),
    value: BICst.Widget.BUBBLE,
    cls: "chart-bubble-font"
}, {
    text: BI.i18nText("BI-Scatter"),
    value: BICst.Widget.SCATTER,
    cls: "chart-scatter-font"
}, {
    text: BI.i18nText("BI-Radar"),
    value: BICst.Widget.RADAR,
    cls: "chart-radar-font"
}
], [{//以下是控件
    text: BI.i18nText("BI-Text_Control"),
    value: BICst.Widget.STRING,
    cls: "chart-string-font"
}, {
    text: BI.i18nText("BI-Numeric_Control"),
    value: BICst.Widget.NUMBER,
    cls: "chart-number-font"
}, {
    text: BI.i18nText("BI-Tree_Control"),
    value: BICst.Widget.TREE,
    cls: "chart-tree-font"
}, {
    text: BI.i18nText("BI-Date_Control"),
    value: BICst.Widget.DATE,
    cls: "chart-date-font",
    children: [
        {
            text: BI.i18nText("BI-Year_Control"),
            value: BICst.Widget.YEAR,
            cls: "chart-year-font"
        }, {
            text: BI.i18nText("BI-Year_Month_Con"),
            value: BICst.Widget.MONTH,
            cls: "chart-month-font"
        }, {
            text: BI.i18nText("BI-Year_Quarter_Con"),
            value: BICst.Widget.QUARTER,
            cls: "chart-quarter-font"
        }, {
            text: BI.i18nText("BI-Date_Control"),
            value: BICst.Widget.YMD,
            cls: "chart-ymd-font"
        }, {
            text: BI.i18nText("BI-Date_Range_Control"),
            value: BICst.Widget.DATE,
            cls: "chart-date-range-font"
        }]
}, {
    text: BI.i18nText("BI-Query_Button"),
    value: BICst.Widget.QUERY,
    cls: "chart-query-font"
}, {
    text: BI.i18nText("BI-Reset_Button"),
    value: BICst.Widget.RESET,
    cls: "chart-reset-font"
}], [{
    text: BI.i18nText("BI-Reuse"),
    value: -1,
    cls: "chart-reuse-font"
}]];

//三种表格类型
BICst.TABLE_TYPE = [{
    title: BI.i18nText("BI-Group_Table"),
    value: BICst.Widget.TABLE,
    text: BI.i18nText("BI-Group_Table")
}, {
    title: BI.i18nText("BI-Cross_Table"),
    value: BICst.Widget.CROSS_TABLE,
    text: BI.i18nText("BI-Cross_Table")
}, {
    title: BI.i18nText("BI-Complex_Table"),
    value: BICst.Widget.COMPLEX_TABLE,
    text: BI.i18nText("BI-Complex_Table")
}];

//图表类型
BICst.CHART_TYPE = [{
    title: BI.i18nText("BI-Cor_Chart"),
    value: BICst.Widget.AXIS,
    cls: "detail-chart-axis-font"
}, {
    title: BI.i18nText("BI-Bar_Chart"),
    value: BICst.Widget.BAR,
    cls: "detail-chart-bar-font"
}, {
    title: BI.i18nText("BI-Stacked_Bar_Chart"),
    value: BICst.Widget.ACCUMULATE_BAR,
    cls: "detail-char-accumulate-bar-font"
}, {
    title: BI.i18nText("BI-Pie_Chart"),
    value: BICst.Widget.PIE,
    cls: "detail-chart-pie-font"
}, {
    title: BI.i18nText("BI-Map"),
    value: BICst.Widget.MAP,
    cls: "detail-chart-map-font"
}, {
    title: BI.i18nText("BI-Dashboard_Chart"),
    value: BICst.Widget.DASHBOARD,
    cls: "detail-chart-dashboard-font"
}, {
    title: BI.i18nText("BI-Donut_Chart"),
    value: BICst.Widget.DOUGHNUT,
    cls: "detail-chart-doughnut-font"
}, {
    title: BI.i18nText("BI-Bubble_Chart"),
    value: BICst.Widget.BUBBLE,
    cls: "detail-chart-bubble-font"
}, {
    title: BI.i18nText("BI-Scatter"),
    value: BICst.Widget.SCATTER,
    cls: "detail-chart-scatter-font"
}, {
    title: BI.i18nText("BI-Radar"),
    value: BICst.Widget.RADAR,
    cls: "detail-chart-radar-font"
}];

//四种汇总
BICst.SUMMARY_TYPE_ITEMS = [{
    value: BICst.SUMMARY_TYPE.SUM, text: BI.i18nText("BI-Qiu_Sum"), text_class: "fr-bi-target-summary-sum"
}, {
    value: BICst.SUMMARY_TYPE.AVG, text: BI.i18nText("BI-Qiu_Avg"), text_class: "fr-bi-target-summary-avg"
}, {
    value: BICst.SUMMARY_TYPE.MAX, text: BI.i18nText("BI-Qiu_Max"), text_class: "fr-bi-target-summary-max"
}, {
    value: BICst.SUMMARY_TYPE.MIN, text: BI.i18nText("BI-Qiu_Min"), text_class: "fr-bi-target-summary-min"
}];

//过滤所用四种汇总
BICst.FILTER_SUMMARY_TYPE_ITEMS = [{
    value: BICst.FILTER_TYPE.NUMBER_SUM, text: BI.i18nText("BI-Qiu_Sum"), text_class: "fr-bi-target-summary-sum"
}, {
    value: BICst.FILTER_TYPE.NUMBER_AVG, text: BI.i18nText("BI-Qiu_Avg"), text_class: "fr-bi-target-summary-avg"
}, {
    value: BICst.FILTER_TYPE.NUMBER_MAX, text: BI.i18nText("BI-Qiu_Max"), text_class: "fr-bi-target-summary-max"
}, {
    value: BICst.FILTER_TYPE.NUMBER_MIN, text: BI.i18nText("BI-Qiu_Min"), text_class: "fr-bi-target-summary-min"
}];

//指标的四种类型
BICst.TARGET_CHART_STYLE = [{
    text: BI.i18nText("BI-Column_Chart"),
    value: (BICst.CHART_VIEW_STYLE_BAR = 0),
    text_class: "fr-bi-chart-view-setter-style-of-chart-vertical-bar"
}, {
    text: BI.i18nText("BI-Stacked_Chart"),
    value: (BICst.CHART_VIEW_STYLE_ACCUMULATED_BAR = 1),
    text_class: "fr-bi-chart-view-setter-style-of-chart-vertical-accumulated-bar"
}, {
    text: BI.i18nText("BI-Line_Chart"),
    value: (BICst.CHART_VIEW_STYLE_LINE = 2),
    text_class: "fr-bi-chart-view-setter-style-of-chart-line"
}, {
    text: BI.i18nText("BI-Area_Chart"),
    value: (BICst.CHART_VIEW_STYLE_SQUARE = 3),
    text_class: "fr-bi-chart-view-setter-style-of-chart-square"
}];
//组件的设置的下拉选项
BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS = [{
    value: BICst.DASHBOARD_WIDGET_DRILL,
    text: BI.i18nText("BI-Linked_To_Components"),
    extraCls: "dashboard-widget-combo-linkage-font"
}, {
    value: BICst.DASHBOARD_DETAIL_WIDGET_DRILL,
    text: BI.i18nText("BI-Linked_To_Detail"),
    extraCls: "dashboard-widget-combo-linkage-font"
}, {
    value: BICst.DASHBOARD_WIDGET_EXPAND,
    text: BI.i18nText("BI-Detailed_Setting"),
    extraCls: "dashboard-widget-combo-detail-set-font"
}, {
    value: BICst.DASHBOARD_WIDGET_EXCEL,
    text: BI.i18nText("BI-Export_As_Excel"),
    extraCls: "dashboard-widget-combo-export-excel-font"
}, {
    value: BICst.DASHBOARD_WIDGET_COPY,
    text: BI.i18nText("BI-Copy"),
    extraCls: "copy-h-font"
}, {
    value: BICst.DASHBOARD_WIDGET_DELETE,
    text: BI.i18nText("BI-Delete_Component"),
    extraCls: "delete-h-font"
}];
//组件的设置的下拉选项(预览)
BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS_SHOW = [{
    value: BICst.DASHBOARD_WIDGET_EXPAND,
    text: BI.i18nText("BI-Detailed_Setting"),
    extraCls: "dashboard-widget-combo-detail-set-font"
}, {
    value: BICst.DASHBOARD_WIDGET_EXCEL,
    text: BI.i18nText("BI-Export_As_Excel"),
    extraCls: "dashboard-widget-combo-export-excel-font"
}];

BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS.VIEW = [{
    value: BICst.DASHBOARD_WIDGET_EXCEL,
    text: BI.i18nText("BI-Export_As_Excel"),
    extraCls: "widget-combo-menu-excel-export-icon"
}, {
    value: BICst.DASHBOARD_WIDGET_EASY_SETTING,
    text: BI.i18nText("BI-Quick_Setting"),
    extraCls: "widget-combo-menu-detail-icon"
}];

//明细表的设置的下拉选项
BICst.DETIAL_WIDGET_SETCOMBO_ITEMS = [{
    value: BICst.DASHBOARD_WIDGET_EXPAND,
    text: BI.i18nText("BI-Detailed_Setting"),
    extraCls: "dashboard-widget-combo-detail-set-font"
}, {
    value: BICst.DASHBOARD_WIDGET_EXCEL,
    text: BI.i18nText("BI-Export_As_Excel"),
    extraCls: "dashboard-widget-combo-export-excel-font"
}, {
    value: BICst.DASHBOARD_WIDGET_COPY,
    text: BI.i18nText("BI-Copy"),
    extraCls: "copy-h-font"
}, {
    value: BICst.DASHBOARD_WIDGET_DELETE,
    text: BI.i18nText("BI-Delete_Detail"),
    extraCls: "delete-h-font"
}];

BICst.DETIAL_WIDGET_SETCOMBO_ITEMS.VIEW = [{
    value: BICst.DASHBOARD_WIDGET_EXCEL,
    text: BI.i18nText("BI-Export_As_Excel"),
    extraCls: "widget-combo-menu-excel-export-icon"
}, {
    value: BICst.DASHBOARD_WIDGET_EASY_SETTING,
    text: BI.i18nText("BI-Quick_Setting"),
    extraCls: "widget-combo-menu-detail-icon"
}];

//复选框和下拉树控件的下拉选项
BICst.STRING_TREE_CONTROL_SETCOMBO_ITEMS = [{
    value: BICst.DASHBOARD_WIDGET_EXPAND,
    text: BI.i18nText("BI-Detailed_Setting"),
    extraCls: "dashboard-widget-combo-detail-set-font"
}, {
    value: BICst.DASHBOARD_CONTROL_RANG_ASC,
    text: BI.i18nText("BI-Ascend"),
    list_border_not_show: true,
    extraCls: "sort-asc-small-icon"
}, {
    value: BICst.DASHBOARD_CONTROL_RANG_DESC,
    text: BI.i18nText("BI-Descend"),
    extraCls: "sort-desc-small-icon"
}, {
    value: BICst.DASHBOARD_CONTROL_CLEAR,
    text: BI.i18nText("BI-Clear_Selected_Value"),
    extraCls: "fr-bi-erase-button-small"
}, {
    value: BICst.DASHBOARD_WIDGET_COPY,
    text: BI.i18nText("BI-Copy"),
    extraCls: "widget-combo-menu-copy-icon"
}, {
    value: BICst.DASHBOARD_WIDGET_DELETE,
    text: BI.i18nText("BI-Delete_Control"),
    extraCls: "widget-combo-menu-delete-icon"
}];

BICst.STRING_TREE_CONTROL_SETCOMBO_ITEMS.VIEW = [{
    value: BICst.DASHBOARD_CONTROL_RANG_ASC,
    text: BI.i18nText("BI-Ascend"),
    list_border_not_show: true,
    extraCls: "sort-asc-small-icon"
}, {
    value: BICst.DASHBOARD_CONTROL_RANG_DESC, text: BI.i18nText("BI-Descend"), extraCls: "sort-desc-small-icon"
}];


//时间控件的下拉选项
BICst.TIME_CONTROL_SETCOMBO_ITEMS = [{
    value: BICst.DASHBOARD_WIDGET_EXPAND,
    text: BI.i18nText("BI-Detailed_Setting"),
    extraCls: "dashboard-widget-combo-detail-set-font"
}, {
    value: BICst.DASHBOARD_CONTROL_CLEAR,
    text: BI.i18nText("BI-Clear_Selected_Value"),
    extraCls: "dashboard-widget-combo-clear"
}, {
    value: BICst.DASHBOARD_WIDGET_COPY,
    text: BI.i18nText("BI-Copy"),
    extraCls: "copy-h-font"
}, {
    value: BICst.DASHBOARD_WIDGET_DELETE,
    text: BI.i18nText("BI-Delete_Control"),
    extraCls: "delete-h-font"
}];

BICst.TIME_CONTROL_SETCOMBO_ITEMS.VIEW = [];

//数值控件的下拉选项
BICst.NUMBER_CONTROL_SETCOMBO_ITEMS = [{
    value: BICst.DASHBOARD_WIDGET_EXPAND,
    text: BI.i18nText("BI-Detailed_Setting"),
    extraCls: "widget-combo-menu-detail-icon"
}, {
    value: BICst.DASHBOARD_CONTROL_CLEAR,
    text: BI.i18nText("BI-Clear_Selected_Value"),
    extraCls: "fr-bi-erase-button-small"
}, {
    value: BICst.DASHBOARD_WIDGET_COPY, text: BI.i18nText("BI-Copy"), extraCls: "widget-combo-menu-copy-icon"
}, {
    value: BICst.DASHBOARD_WIDGET_DELETE,
    text: BI.i18nText("BI-Delete_Control"),
    extraCls: "widget-combo-menu-delete-icon"
}];

BICst.NUMBER_CONTROL_SETCOMBO_ITEMS.VIEW = [{
    value: BICst.DASHBOARD_CONTROL_CLEAR,
    text: BI.i18nText("BI-Clear_Selected_Value"),
    extraCls: "fr-bi-erase-button-small"
}];

//通用查询的下拉选项
BICst.GENERNAL_QUERY_CONTROL_SETCOMBO_ITEMS = [{
    value: BICst.DASHBOARD_CONTROL_CLEAR,
    text: BI.i18nText("BI-Clear_Selected_Value"),
    extraCls: "fr-bi-erase-button-small"
}, {
    value: BICst.DASHBOARD_WIDGET_COPY, text: BI.i18nText("BI-Copy"), extraCls: "widget-combo-menu-copy-icon"
}, {
    value: BICst.DASHBOARD_WIDGET_DELETE,
    text: BI.i18nText("BI-Delete_Control"),
    extraCls: "widget-combo-menu-delete-icon"
}];

BICst.GENERNAL_QUERY_CONTROL_SETCOMBO_ITEMS.VIEW = [{
    value: BICst.DASHBOARD_CONTROL_CLEAR,
    text: BI.i18nText("BI-Clear_Selected_Value"),
    extraCls: "fr-bi-erase-button-small"
}]

//指标下拉选项
BICst.WIDGET_DETAIL_TARGET_ITEM = [{
    value: BICst.TARGET_COMBO.SUMMERY_TYPE,
    text: BI.i18nText("BI-Summary_Style"),
    children: BICst.SUMMARY_TYPE_ITEMS,
    suffix_class: "fr-bi-list-spread",
    list_border_not_show: true
}, {
    value: BICst.TARGET_COMBO.CHART_TYPE,
    text: BI.i18nText("BI-Chart_Type"),
    children: BICst.TARGET_CHART_STYLE,
    suffix_class: "fr-bi-list-spread"
}, {
    value: BICst.TARGET_COMBO.STYLE_SETTING, text: BI.i18nText("BI-Style_Setting")
}, {
    value: BICst.TARGET_COMBO.FILTER,
    text: BI.i18nText("BI-Filter_Number_Summary"),
    tooltip: BI.i18nText("BI-Target_Summary_Filter_Title")
}, {
    value: BICst.TARGET_COMBO.COPY, text: BI.i18nText("BI-Copy")
}, {
    value: BICst.TARGET_COMBO.DELETE, text: BI.i18nText("BI-Delete_Target")
}, {
    value: BICst.TARGET_COMBO.INFO, text: BI.i18nText("BI-This_Target_From"), disable: true
}];

BICst.WIDGET_DETAIL_TARGET_ITEM.PREDICTION = [{
    value: BICst.TARGET_ELEMENT_GATHER_STYLE,
    text: "fbi_Summary_Style",
    children: BICst.SUMMARY_TYPE_ITEMS,
    suffix_class: "fr-bi-list-spread",
    list_border_not_show: true
}, {
    value: BICst.TARGET_ELEMENT_CHART_STYLE,
    text: "fbi_Chart_Type",
    children: BICst.TARGET_CHART_STYLE,
    suffix_class: "fr-bi-list-spread"
}, {
    value: BICst.TARGET_ELEMENT_STYLE_SET, text: "fbi_Style_Setting"
}, {
    value: BICst.TARGET_ELEMENT_FILTER_SET, text: "fbi_Filter_Setting"
}, {
    value: BICst.TARGET_ELEMENT_PREDICT, text: "fbi_Predict_Target"
}, {
    value: BICst.TARGET_ELEMENT_DELETE, text: "fbi_Delete_Target"
}];

BICst.WIDGET_DETAIL_TARGET_ITEM.VIEW = [{
    value: BICst.TARGET_ELEMENT_GATHER_STYLE,
    text: "fbi_Summary_Style(Sum)",
    children: BICst.SUMMARY_TYPE_ITEMS,
    suffix_class: "fr-bi-list-spread",
    list_border_not_show: true
}, {
    value: BICst.TARGET_ELEMENT_CHART_STYLE,
    text: "fbi_Chart_Type",
    children: BICst.TARGET_CHART_STYLE,
    suffix_class: "fr-bi-list-spread"
}];

//etl group
BICst.ETL_MANAGE_ITEMS = [{
    text: BI.i18nText("BI-Preview") + "...",
    title: BI.i18nText("BI-Preview") + "...",
    value: BICst.ETL_MANAGE_TABLE_PREVIEW
}, {
    text: BI.i18nText("BI-Table_Add_Formula"),
    title: BI.i18nText("BI-Table_Add_Formula"),
    value: BICst.ETL_MANAGE_TABLE_ADD_FIELD
}, {
    text: BI.i18nText("BI-Join_With_Table"),
    title: BI.i18nText("BI-Join_With_Table"),
    value: BICst.ETL_MANAGE_TABLE_JOIN
}, {
    text: BI.i18nText("BI-Union_With_Table"),
    title: BI.i18nText("BI-Union_With_Table"),
    value: BICst.ETL_MANAGE_TABLE_UNION
}, {
    text: BI.i18nText("BI-To_Convert_Column_Row"),
    title: BI.i18nText("BI-To_Convert_Column_Row"),
    value: BICst.ETL_MANAGE_TABLE_CONVERT
}, {
    text: BI.i18nText("BI-Use_Part_Fields"),
    title: BI.i18nText("BI-Use_Part_Fields"),
    value: BICst.ETL_MANAGE_TABLE_PARTIAL
}, {
    text: BI.i18nText("BI-Table_Filter"), title: BI.i18nText("BI-Table_Filter"), value: BICst.ETL_MANAGE_TABLE_FILTER
}, {
    text: BI.i18nText("BI-Table_Grouping_Count"),
    title: BI.i18nText("BI-Table_Grouping_Count"),
    value: BICst.ETL_MANAGE_TABLE_GROUP
}, {
    text: BI.i18nText("BI-Table_Self_Cycle"),
    title: BI.i18nText("BI-Table_Self_Cycle"),
    value: BICst.ETL_MANAGE_TABLE_CIRCLE
}, {
    text: BI.i18nText("BI-Table_Add_Grouping_Column"),
    title: BI.i18nText("BI-Table_Add_Grouping_Column"),
    value: BICst.ETL_MANAGE_TABLE_NEW_GROUP
}, {
    text: BI.i18nText("BI-Remove"), title: BI.i18nText("BI-Remove"), value: BICst.ETL_MANAGE_TABLE_DELETE
}];

//field type
BICst.ETL_ADD_FORMULA_FIELD_TYPE = [{
    value: BICst.COLUMN.STRING, cls: "chart-string-font"
}, {
    value: BICst.COLUMN.NUMBER, cls: "chart-number-font"
}, {
    value: BICst.COLUMN.DATE, cls: "chart-string-font"
}];

//过滤的几种类型
//target string
BICst.TARGET_FILTER_STRING_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.TARGET_FILTER_STRING.BELONG_VALUE
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE
}], [{
    text: BI.i18nText("BI-Contain"),
    value: BICst.TARGET_FILTER_STRING.CONTAIN
}, {
    text: BI.i18nText("BI-Not_Contain"),
    value: BICst.TARGET_FILTER_STRING.NOT_CONTAIN
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.TARGET_FILTER_STRING.IS_NULL
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.TARGET_FILTER_STRING.NOT_NULL
}], [{
    text: BI.i18nText("BI-Begin_With"),
    value: BICst.TARGET_FILTER_STRING.BEGIN_WITH
}, {
    text: BI.i18nText("BI-End_With"),
    value: BICst.TARGET_FILTER_STRING.END_WITH
}]];

//target number
BICst.TARGET_FILTER_NUMBER_COMBO = [[{
    text: BI.i18nText("BI-Equal"),
    value: BICst.TARGET_FILTER_NUMBER.EQUAL_TO
}, {
    text: BI.i18nText("BI-Not_Equal_To"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO
}], [{
    text: BI.i18nText("BI-In"),
    value: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.TARGET_FILTER_NUMBER.IS_NULL
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_NULL
}]];

BICst.DATE_WEEK_COMBO = [{
    //text: BI.i18nText("BI-Monday"),
    value: 1
}, {
    //text: BI.i18nText("BI-Tuesday"),
    value: 2
}, {
    //text: BI.i18nText("BI-Wednesday"),
    value: 3
}, {
    //text: BI.i18nText("BI-Thursday"),
    value: 4
}, {
    //text: BI.i18nText("BI-Friday"),
    value: 5
}, {
    //text: BI.i18nText("BI-Saturday"),
    value: 6
}, {
    //text: BI.i18nText("BI-Sunday"),
    value: 7
}];

BICst.DATE_SEASON_COMBO = [{
    //text: BI.i18nText("BI-Quarter_1"),
    value: 1
}, {
    //text: BI.i18nText("BI-Quarter_2"),
    value: 2
}, {
    // text: BI.i18nText("BI-Quarter_3"),
    value: 3
}, {
    //text: BI.i18nText("BI-Quarter_4"),
    value: 4
}];

BICst.BEFORE_AFTER_COMBO = [{
    text: BI.i18nText("BI-Qian_First"),
    value: 0
}, {
    text: BI.i18nText("BI-Hou_Last"),
    value: 1
}];

//dimension string
BICst.DIMENSION_FILTER_STRING_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.DIMENSION_FILTER_STRING.BELONG_VALUE
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE
}], [{
    text: BI.i18nText("BI-Contain"),
    value: BICst.DIMENSION_FILTER_STRING.CONTAIN
}, {
    text: BI.i18nText("BI-Not_Contain"),
    value: BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.DIMENSION_FILTER_STRING.IS_NULL
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.DIMENSION_FILTER_STRING.NOT_NULL
}], [{
    text: BI.i18nText("BI-Begin_With"),
    value: BICst.DIMENSION_FILTER_STRING.BEGIN_WITH
}, {
    text: BI.i18nText("BI-End_With"),
    value: BICst.DIMENSION_FILTER_STRING.END_WITH
}], [{
    text: BI.i18nText("BI-Top_N"),
    value: BICst.DIMENSION_FILTER_STRING.TOP_N
}, {
    text: BI.i18nText("BI-Last_N"),
    value: BICst.DIMENSION_FILTER_STRING.BOTTOM_N
}]];

BICst.DIMENSION_TAR_FILTER_NUMBER_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE
}], [{
    text: BI.i18nText("BI-Above_Average"),
    value: BICst.DIMENSION_FILTER_NUMBER.MORE_THAN_AVG
}, {
    text: BI.i18nText("BI-Below_Average"),
    value: BICst.DIMENSION_FILTER_NUMBER.LESS_THAN_AVG
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.DIMENSION_FILTER_NUMBER.IS_NULL
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.DIMENSION_FILTER_NUMBER.NOT_NULL
}], [{
    text: BI.i18nText("BI-Top_N"),
    value: BICst.DIMENSION_FILTER_NUMBER.TOP_N
}, {
    text: BI.i18nText("BI-Last_N"),
    value: BICst.DIMENSION_FILTER_NUMBER.BOTTOM_N
}]];

BICst.DIMENSION_DIM_FILTER_NUMBER_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.DIMENSION_FILTER_STRING.BELONG_VALUE
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE
}], [{
    text: BI.i18nText("BI-Above_Average"),
    value: BICst.DIMENSION_FILTER_NUMBER.MORE_THAN_AVG
}, {
    text: BI.i18nText("BI-Below_Average"),
    value: BICst.DIMENSION_FILTER_NUMBER.LESS_THAN_AVG
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.DIMENSION_FILTER_NUMBER.IS_NULL
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.DIMENSION_FILTER_NUMBER.NOT_NULL
}], [{
    text: BI.i18nText("BI-Top_N"),
    value: BICst.DIMENSION_FILTER_NUMBER.TOP_N
}, {
    text: BI.i18nText("BI-Last_N"),
    value: BICst.DIMENSION_FILTER_NUMBER.BOTTOM_N
}]];

//date
BICst.FILTER_DATE_COMBO = [[{
    el: {
        text: BI.i18nText("BI-In"),
        value: BI.i18nText("BI-In")
    },
    children: [{
        text: BI.i18nText("BI-Periods_Time"),
        value: BICst.FILTER_DATE.BELONG_DATE_RANGE
    }, {
        text: BI.i18nText("BI-Control_Value"),
        value: BICst.FILTER_DATE.BELONG_WIDGET_VALUE
    }]
}, {
    el: {
        text: BI.i18nText("BI-Not_In"),
        value: BI.i18nText("BI-Not_In")
    },
    children: [{
        text: BI.i18nText("BI-Periods_Time"),
        value: BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE
    }, {
        text: BI.i18nText("BI-Control_Value"),
        value: BICst.FILTER_DATE.NOT_BELONG_WIDGET_VALUE
    }]
}], [{
    text: BI.i18nText("BI-More_Than"),
    value: BICst.FILTER_DATE.LATER_THAN
}, {
    text: BI.i18nText("BI-Less_Than"),
    value: BICst.FILTER_DATE.EARLY_THAN
}], [{
    text: BI.i18nText("BI-Equal"),
    value: BICst.FILTER_DATE.EQUAL_TO
}, {
    text: BI.i18nText("BI-Not_Equal_To"),
    value: BICst.FILTER_DATE.NOT_EQUAL_TO
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.FILTER_DATE.IS_NULL
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.FILTER_DATE.NOT_NULL
}]];

BICst.DATA_SETTING_FILTER_DATE_COMBO = [[{
    text: BI.i18nText("BI-In"), value: BICst.FILTER_DATE.BELONG_DATE_RANGE
}, {
    text: BI.i18nText("BI-Not_In"), value: BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE
}], [{
    text: BI.i18nText("BI-Equal"), value: BICst.FILTER_DATE.EQUAL_TO
}, {
    text: BI.i18nText("BI-Not_Equal_To"), value: BICst.FILTER_DATE.NOT_EQUAL_TO
}], [{
    text: BI.i18nText("BI-Is_Null"), value: BICst.FILTER_DATE.IS_NULL
}, {
    text: BI.i18nText("BI-Not_Null"), value: BICst.FILTER_DATE.NOT_NULL
}]];

BICst.Date_Range_FILTER_COMBO = [{
    text: BI.i18nText("BI-Last_Section_Of_Same_Interval"),
    value: BICst.LAST_SAME_PERIOD
}, {
    text: BI.i18nText("BI-The_Same_Time_Period"),
    value: BICst.SAME_PERIOD
}];

BICst.FILTER_CONDITION_TYPE = [{
    text: BI.i18nText("BI-And"), value: BICst.FILTER_TYPE.AND
}, {
    text: BI.i18nText("BI-Or"), value: BICst.FILTER_TYPE.OR
}];

//添加公式combo
BICst.FILTER_ADD_FORMULA_COMBO = [{
    text: BI.i18nText("BI-Condition_Expression_And"), value: BICst.FILTER_OPERATION_FORMULA_AND
}, {
    text: BI.i18nText("BI-Condition_Expression_Or"), value: BICst.FILTER_OPERATION_FORMULA_OR
}];

//添加条件combo
BICst.FILTER_ADD_CONDITION_COMBO = [{
    text: BI.i18nText("BI-Condition_And"), value: BICst.FILTER_OPERATION_CONDITION_AND
}, {
    text: BI.i18nText("BI-Condition_Or"), value: BICst.FILTER_OPERATION_CONDITION_OR
}];

//自定义分组，自动分组
BICst.NUMBER_INTERVAL_CUSTOM_GROUP = [{
    text: BI.i18nText("BI-Custom"),
    value: BICst.NUMBER_INTERVAL_CUSTOM_GROUP_CUSTOM
}, {
    text: BI.i18nText("BI-Auto"),
    value: BICst.NUMBER_INTERVAL_CUSTOM_GROUP_AUTO
}];

//配置中分组统计用到的下拉框内容
BICst.CONF_GROUP_STRING = [{
    text: BI.i18nText("BI-Same_Value_A_Group"),
    value: BICst.GROUP.ID_GROUP
}, {
    text: BI.i18nText("BI-Custom_Grouping_Dot"),
    value: BICst.GROUP.CUSTOM_GROUP
}];

BICst.CONF_GROUP_NUMBER = [{
    text: BI.i18nText("BI-Same_Value_A_Group"),
    value: BICst.GROUP.ID_GROUP
}, {
    text: BI.i18nText("BI-Grouping_Setting"),
    value: BICst.GROUP.CUSTOM_NUMBER_GROUP
}];

BICst.CONF_GROUP_DATE = [{
    text: BI.i18nText("BI-Date"),
    value: BICst.GROUP.YMD
}, {
    text: BI.i18nText("BI-Year_Fen"),
    value: BICst.GROUP.Y
}, {
    text: BI.i18nText("BI-Quarter"),
    value: BICst.GROUP.S
}, {
    text: BI.i18nText("BI-Month_Fen"),
    value: BICst.GROUP.M
}, {
    text: BI.i18nText("BI-Week_XingQi"),
    value: BICst.GROUP.W
}];

BICst.CONF_STATISTIC_STRING = [{
    text: BI.i18nText("BI-No_Repeat_Count"),
    value: BICst.SUMMARY_TYPE.COUNT
}, {
    text: BI.i18nText("BI-String_Summary_By_Connection"),
    value: BICst.SUMMARY_TYPE.APPEND
}];

BICst.CONF_STATISTIC_NUMBER = [{
    text: BI.i18nText("BI-Qiu_Sum"),
    value: BICst.SUMMARY_TYPE.SUM
}, {
    text: BI.i18nText("BI-Qiu_Avg"),
    value: BICst.SUMMARY_TYPE.AVG
}, {
    text: BI.i18nText("BI-Qiu_Max"),
    value: BICst.SUMMARY_TYPE.MAX
}, {
    text: BI.i18nText("BI-Qiu_Min"),
    value: BICst.SUMMARY_TYPE.MIN
}, {
    text: BI.i18nText("BI-No_Repeat_Count"),
    value: BICst.SUMMARY_TYPE.COUNT
}];

//表格（分组表、交叉表和复杂表）类型
BICst.TABLE_FORM_GROUP = [{
    cls: "table-open-col-style-font",
    value: BICst.TABLE_FORM.OPEN_COL
}, {
    cls: "table-open-row-style-font",
    value: BICst.TABLE_FORM.OPEN_ROW
}];

//表格（分组表、交叉表和复杂表）风格
BICst.TABLE_STYLE_GROUP = [{
    cls: "table-style1-icon",
    value: BICst.TABLE_STYLE.NORMAL
}, {
    cls: "table-style2-icon",
    value: BICst.TABLE_STYLE.BLUE
}, {
    cls: "table-style3-icon",
    value: BICst.TABLE_STYLE.INTERVAL
}];

//表设置所有默认属性（应该是包含分组表、交叉表、复杂表和其他所有图表）
BICst.DEFAULT_CHART_SETTING = {
    table_form: BICst.TABLE_FORM.OPEN_COL,
    theme_color: "#65bce7",
    table_style: BICst.TABLE_STYLE.NORMAL,
    show_number: false,
    show_row_total: true,
    show_col_total: true,
    open_row_node: false,
    open_col_node: false,
    max_row: BICst.TABLE_MAX_ROW,
    max_col: BICst.TABLE_MAX_COL,
    freeze_dim: true,
    transfer_filter: true
};


BICst.CAL_TARGET_TYPE = [{
    text: BI.i18nText("BI-Formula"),
    value: BICst.TARGET_TYPE.FORMULA
}, {
    text: BI.i18nText("BI-Ranging"),
    value: BICst.TARGET_TYPE.RANK
}, {
    text: BI.i18nText("BI-Group_Ranking"),
    value: BICst.TARGET_TYPE.RANK_IN_GROUP
}, {
    text: BI.i18nText("BI-All_Values"),
    value: BICst.TARGET_TYPE.SUM_OF_ALL
}, {
    text: BI.i18nText("BI-All_Values_In_Group"),
    value: BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP
}, {
    text: BI.i18nText("BI-Cumulative_Value"),
    value: BICst.TARGET_TYPE.SUM_OF_ABOVE
}, {
    text: BI.i18nText("BI-Cumulative_Value_In_Group"),
    value: BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP
}, {
    text: BI.i18nText("BI-Month_On_Month_Rate"),
    value: BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE
}, {
    text: BI.i18nText("BI-Month_On_Value"),
    value: BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE
}, {
    text: BI.i18nText("BI-Year_On_Year_Rate"),
    value: BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE
}, {
    text: BI.i18nText("BI-Year_On_Value"),
    value: BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE
}];


BICst.CAL_TARGET_RANK_TYPE = [{
    text: BI.i18nText("BI-Rank_In_Asc"),
    value: BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC
}, {
    text: BI.i18nText("BI-Rank_In_Des"),
    value: BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC
}];

BICst.CAL_TARGET_SUM_TYPE = [{
    text: BI.i18nText("BI-Qiu_Sum"),
    value: BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM
}, {
    text: BI.i18nText("BI-Qiu_Avg"),
    value: BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.AVG
}, {
    text: BI.i18nText("BI-Qiu_Max"),
    value: BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MAX
}, {
    text: BI.i18nText("BI-Qiu_Min"),
    value: BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MIN
}];

//数据连接配置
BICst.DATABASE = {
    ORACLE: "Oracle",
    DB2: "DB2",
    SQL_SERVER: "SQL Server",
    MYSQL: "MySQL",
    SYBASE: "Sybase",
    DERBY: "Derby",
    POSTGRE: "Postgre",
    OTHERS: BI.i18nText("BI-Others")
};
BICst.DATA_LINK_MANAGE = {
    CODES: [
        {text: "", value: ""},
        {text: "BIG5", value: "BIG5"},
        {text: "EUC_JP", value: "EUC_JP"},
        {text: "EUC_KR", value: "EUC_KR"},
        {text: "GBK", value: "GBK"},
        {text: "ISO-8859-1", value: "ISO-8859-1"},
        {text: "UTF-8", value: "UTF-8"},
        {text: "UTF-16", value: "UTF-16"},
        {text: "CP850", value: "CP850"}
    ],
    DATABASES: [{
        text: "Oracle",
        value: BICst.DATABASE.ORACLE
    }, {
        text: "DB2",
        value: BICst.DATABASE.DB2
    }, {
        text: "SQL Server",
        value: BICst.DATABASE.SQL_SERVER
    }, {
        text: "MySQL",
        value: BICst.DATABASE.MYSQL
    }, {
        text: "Sybase",
        value: BICst.DATABASE.SYBASE
    }, {
        text: "Derby",
        value: BICst.DATABASE.DERBY
    }, {
        text: "Postgre",
        value: BICst.DATABASE.POSTGRE
    }, {
        text: BI.i18nText("BI-Others"),
        value: BICst.DATABASE.OTHERS
    }],
    URLS: {
        "oracle.jdbc.driver.OracleDriver": "jdbc:oracle:thin:@localhost:1521:databaseName",
        "com.ibm.db2.jcc.DB2Driver": "jdbc:db2://localhost:50000/",
        "com.microsoft.sqlserver.jdbc.SQLServerDriver": "jdbc:sqlserver://localhost:1433;databaseName=",
        "com.mysql.jdbc.Driver": "jdbc:mysql://localhost/",
        "org.gjt.mm.mysql.Driver": "jdbc:mysql://localhost/",
        "com.sybase.jdbc2.jdbc.SybDriver": "jdbc:sybase:Tds:localhost:5000/",
        "org.apache.derby.jdbc.ClientDriver": "jdbc:derby://localhost:1527/",
        "org.postgresql.Driver": "jdbc:postgresql://localhost:5432/",
        "sun.jdbc.odbc.JdbcOdbcDriver": "jdbc:odbc:",
        "org.hsqldb.jdbcDriver": "jdbc:hsqldb:file:[PATH_TO_DB_FILES]",
        "com.inet.tds.TdsDriver": "jdbc:inetdae7:localhost:1433/",
        "COM.cloudscape.JDBCDriver": "jdbc:cloudscape:/cloudscape/",
        "com.internetcds.jdbc.tds.Driver": "jdbc:freetds:sqlserver://localhost/"
    },
    DRIVERS: {
        ORACLE: [{
            text: "oracle.jdbc.driver.OracleDriver",
            value: "oracle.jdbc.driver.OracleDriver"
        }],
        DB2: [{
            text: "com.ibm.db2.jcc.DB2Driver",
            value: "com.ibm.db2.jcc.DB2Driver"
        }],
        SQL_SERVER: [{
            text: "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            value: "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        }],
        MYSQL: [{
            text: "com.mysql.jdbc.Driver",
            value: "com.mysql.jdbc.Driver"
        }, {
            text: "org.gjt.mm.mysql.Driver",
            value: "org.gjt.mm.mysql.Driver"
        }],
        SYBASE: [{
            text: "com.sybase.jdbc2.jdbc.SybDriver",
            value: "com.sybase.jdbc2.jdbc.SybDriver"
        }],
        DERBY: [{
            text: "org.apache.derby.jdbc.ClientDriver",
            value: "org.apache.derby.jdbc.ClientDriver"
        }],
        POSTGRE: [{
            text: "org.postgresql.Driver",
            value: "org.postgresql.Driver"
        }],
        OTHERS: [{
            text: "sun.jdbc.odbc.JdbcOdbcDriver",
            value: "sun.jdbc.odbc.JdbcOdbcDriver"
        }, {
            text: "org.hsqldb.jdbcDriver",
            value: "org.hsqldb.jdbcDriver"
        }, {
            text: "com.inet.tds.TdsDriver",
            value: "com.inet.tds.TdsDriver"
        }, {
            text: "COM.cloudscape.JDBCDriver",
            value: "COM.cloudscape.JDBCDriver"
        }, {
            text: "com.internetcds.jdbc.tds.Driver",
            value: "com.internetcds.jdbc.tds.Driver"
        }]
    }
};

BICst.TARGET_STYLE_FORMAT = [{
    text: BI.i18nText("BI-Normal"),
    value: BICst.TARGET_STYLE.FORMAT.NORMAL
}, {
    text: "0",
    value: BICst.TARGET_STYLE.FORMAT.ZERO2POINT
}, {
    text: "0.0",
    value: BICst.TARGET_STYLE.FORMAT.ONE2POINT
}, {
    text: "0.00",
    value: BICst.TARGET_STYLE.FORMAT.TWO2POINT
}];

BICst.TARGET_STYLE_LEVEL = [{
    text: BI.i18nText("BI-Count"),
    value: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL
}, {
    text: BI.i18nText("BI-Wan"),
    value: BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND
}, {
    text: BI.i18nText("BI-Million"),
    value: BICst.TARGET_STYLE.NUM_LEVEL.MILLION
}, {
    text: BI.i18nText("BI-Yi"),
    value: BICst.TARGET_STYLE.NUM_LEVEL.YI
}, {
    text: "%",
    value: BICst.TARGET_STYLE.NUM_LEVEL.PERCENT
}];

