//放置用户可以配置的常量

//full week names
BICst.FULL_WEEK_NAMES = [BI.i18nText("BI-Monday"),
    BI.i18nText("BI-Tuesday"),
    BI.i18nText("BI-Wednesday"),
    BI.i18nText("BI-Thursday"),
    BI.i18nText("BI-Friday"),
    BI.i18nText("BI-Saturday"),
    BI.i18nText("BI-Sunday")];

//full month names
BICst.FULL_MONTH_NAMES = [
    BI.i18nText("BI-January"),
    BI.i18nText("BI-February"),
    BI.i18nText("BI-March"),
    BI.i18nText("BI-April"),
    BI.i18nText("BI-May"),
    BI.i18nText("BI-June"),
    BI.i18nText("BI-July"),
    BI.i18nText("BI-August"),
    BI.i18nText("BI-September"),
    BI.i18nText("BI-October"),
    BI.i18nText("BI-November"),
    BI.i18nText("BI-December")];

//full quarter names
BICst.FULL_QUARTER_NAMES = [BI.i18nText("BI-Quarter_1"),
    BI.i18nText("BI-Quarter_2"),
    BI.i18nText("BI-Quarter_3"),
    BI.i18nText("BI-Quarter_4")];

BICst.CONFIG = {
    SHOW_DASHBOARD_TITLE: true
};

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


BICst.DASHBOARD_WIDGETS = [[{
    text: BI.i18nText("BI-Group_Table"),
    title: BI.i18nText("BI-Group_Table"),
    value: BICst.WIDGET.TABLE,
    cls: "drag-group-icon",
    children: [{
        text: BI.i18nText("BI-Group_Table"),
        title: BI.i18nText("BI-Group_Table"),
        value: BICst.WIDGET.TABLE,
        cls: "drag-group-icon"
    }, {
        text: BI.i18nText("BI-Cross_Table"),
        title: BI.i18nText("BI-Cross_Table"),
        value: BICst.WIDGET.CROSS_TABLE,
        cls: "drag-cross-icon"
    }]
}, {
    text: BI.i18nText("BI-Column_Chart"),
    title: BI.i18nText("BI-Column_Chart"),
    value: BICst.WIDGET.AXIS,
    cls: "drag-axis-icon",
    children: [{
        text: BI.i18nText("BI-Column_Chart"),
        title: BI.i18nText("BI-Column_Chart"),
        value: BICst.WIDGET.AXIS,
        cls: "drag-axis-icon"
    }, {
        text: BI.i18nText("BI-Stacked_Chart"),
        title: BI.i18nText("BI-Stacked_Chart"),
        value: BICst.WIDGET.ACCUMULATE_AXIS,
        cls: "drag-axis-accu-icon"
    }, {
        text: BI.i18nText("BI-Percent_Accumulate_Axis"),
        title: BI.i18nText("BI-Percent_Accumulate_Axis"),
        value: BICst.WIDGET.PERCENT_ACCUMULATE_AXIS,
        cls: "drag-axis-percent-accu-icon"
    }, {
        text: BI.i18nText("BI-Compare_Axis"),
        title: BI.i18nText("BI-Compare_Axis"),
        value: BICst.WIDGET.COMPARE_AXIS,
        cls: "drag-axis-compare-icon"
    }, {
        text: BI.i18nText("BI-Fall_Axis"),
        title: BI.i18nText("BI-Fall_Axis"),
        value: BICst.WIDGET.FALL_AXIS,
        cls: "drag-axis-fall-icon"
    }]
}, {
    text: BI.i18nText("BI-Bar_Chart"),
    title: BI.i18nText("BI-Bar_Chart"),
    value: BICst.WIDGET.BAR,
    cls: "drag-bar-icon",
    children: [{
        text: BI.i18nText("BI-Bar_Chart"),
        title: BI.i18nText("BI-Bar_Chart"),
        value: BICst.WIDGET.BAR,
        cls: "drag-bar-icon"
    }, {
        text: BI.i18nText("BI-Stacked_Bar_Chart"),
        title: BI.i18nText("BI-Stacked_Bar_Chart"),
        value: BICst.WIDGET.ACCUMULATE_BAR,
        cls: "drag-bar-accu-icon"
    }, {
        text: BI.i18nText("BI-Compare_Bar"),
        title: BI.i18nText("BI-Compare_Bar"),
        value: BICst.WIDGET.COMPARE_BAR,
        cls: "drag-bar-compare-icon"
    }]
}, {
    text: BI.i18nText("BI-Line_Chart"),
    title: BI.i18nText("BI-Line_Chart"),
    value: BICst.WIDGET.LINE,
    cls: "drag-line-icon"
}, {
    text: BI.i18nText("BI-Area_Chart"),
    title: BI.i18nText("BI-Area_Chart"),
    value: BICst.WIDGET.AREA,
    cls: "drag-area-icon",
    children: [{
        text: BI.i18nText("BI-Area_Chart"),
        title: BI.i18nText("BI-Area_Chart"),
        value: BICst.WIDGET.AREA,
        cls: "drag-area-icon"
    }, {
        text: BI.i18nText("BI-Accumulate_Area"),
        title: BI.i18nText("BI-Accumulate_Area"),
        value: BICst.WIDGET.ACCUMULATE_AREA,
        cls: "drag-area-accu-icon"
    }, {
        text: BI.i18nText("BI-Percent_Accumulate_Area"),
        title: BI.i18nText("BI-Percent_Accumulate_Area"),
        value: BICst.WIDGET.PERCENT_ACCUMULATE_AREA,
        cls: "drag-area-percent-accu-icon"
    }, {
        text: BI.i18nText("BI-Compare_Area"),
        title: BI.i18nText("BI-Compare_Area"),
        value: BICst.WIDGET.COMPARE_AREA,
        cls: "drag-area-compare-icon"
    }, {
        text: BI.i18nText("BI-Compare_Range_Area"),
        title: BI.i18nText("BI-Compare_Range_Area"),
        value: BICst.WIDGET.RANGE_AREA,
        cls: "drag-area-range-icon"
    }]
}, {
    text: BI.i18nText("BI-Combine_Chart"),
    title: BI.i18nText("BI-Combine_Chart"),
    value: BICst.WIDGET.COMBINE_CHART,
    cls: "drag-combine-icon",
    children: [{
        text: BI.i18nText("BI-Combine_Chart"),
        title: BI.i18nText("BI-Combine_Chart"),
        value: BICst.WIDGET.COMBINE_CHART,
        cls: "drag-combine-icon"
    }, {
        text: BI.i18nText("BI-Multi_Axis_Combine_Chart"),
        title: BI.i18nText("BI-Multi_Axis_Combine_Chart"),
        value: BICst.WIDGET.MULTI_AXIS_COMBINE_CHART,
        cls: "drag-combine-mult-icon"
    }]
}, {
    text: BI.i18nText("BI-Pie_Chart"),
    title: BI.i18nText("BI-Pie_Chart"),
    value: BICst.WIDGET.PIE,
    cls: "drag-pie-icon"
}, {
    text: BI.i18nText("BI-Donut_Chart"),
    title: BI.i18nText("BI-Donut_Chart"),
    value: BICst.WIDGET.DONUT,
    cls: "drag-donut-icon"
}, {
    text: BI.i18nText("BI-Map"),
    title: BI.i18nText("BI-Map"),
    value: BICst.WIDGET.MAP,
    cls: "drag-map-china-icon"
}, {
    text: BI.i18nText("BI-GIS_Map"),
    title: BI.i18nText("BI-GIS_Map"),
    value: BICst.WIDGET.GIS_MAP,
    cls: "drag-map-gis-icon"
}, {
    text: BI.i18nText("BI-Dashboard_Chart"),
    title: BI.i18nText("BI-Dashboard_Chart"),
    value: BICst.WIDGET.DASHBOARD,
    cls: "drag-dashboard-icon"
}, {
    text: BI.i18nText("BI-Radar"),
    title: BI.i18nText("BI-Radar"),
    value: BICst.WIDGET.RADAR,
    cls: "drag-radar-icon",
    children: [{
        text: BI.i18nText("BI-Radar"),
        title: BI.i18nText("BI-Radar"),
        value: BICst.WIDGET.RADAR,
        cls: "drag-radar-icon"
    }, {
        text: BI.i18nText("BI-Accumulate_Radar"),
        title: BI.i18nText("BI-Accumulate_Radar"),
        value: BICst.WIDGET.ACCUMULATE_RADAR,
        cls: "drag-radar-accu-icon"
    }]
}, {
    text: BI.i18nText("BI-Bubble_Chart"),
    title: BI.i18nText("BI-Bubble_Chart"),
    value: BICst.WIDGET.BUBBLE,
    cls: "drag-bubble-icon",
    children: [{
        text: BI.i18nText("BI-Bubble_Chart"),
        title: BI.i18nText("BI-Bubble_Chart"),
        value: BICst.WIDGET.BUBBLE,
        cls: "drag-bubble-icon"
    }, {
        text: BI.i18nText("BI-Force_Bubble"),
        title: BI.i18nText("BI-Force_Bubble"),
        value: BICst.WIDGET.FORCE_BUBBLE,
        cls: "drag-bubble-force-icon"
    }]
}, {
    text: BI.i18nText("BI-Scatter"),
    title: BI.i18nText("BI-Scatter"),
    value: BICst.WIDGET.SCATTER,
    cls: "drag-scatter-icon"
}], [{
    text: BI.i18nText("BI-Detail_Table"),
    title: BI.i18nText("BI-Detail_Table"),
    value: BICst.WIDGET.DETAIL,
    cls: "drag-detail-icon"
}, {
    text: BI.i18nText("BI-Text_Widget"),
    title: BI.i18nText("BI-Text_Widget"),
    value: BICst.WIDGET.CONTENT,
    cls: "drag-input-icon"
}, {
    text: BI.i18nText("BI-Image_Widget"),
    title: BI.i18nText("BI-Image_Widget"),
    value: BICst.WIDGET.IMAGE,
    cls: "drag-image-icon"
}, {
    text: BI.i18nText("BI-Web_Widget"),
    title: BI.i18nText("BI-Web_Widget"),
    value: BICst.WIDGET.WEB,
    cls: "drag-web-icon"
}], [{//以下是控件
    text: BI.i18nText("BI-Text_Control"),
    title: BI.i18nText("BI-Text_Control"),
    value: BICst.WIDGET.STRING,
    cls: "drag-string-icon"
}, {
    text: BI.i18nText("BI-Numeric_Control"),
    title: BI.i18nText("BI-Numeric_Control"),
    value: BICst.WIDGET.NUMBER,
    cls: "drag-number-icon"
}, {
    text: BI.i18nText("BI-Tree_Control"),
    title: BI.i18nText("BI-Tree_Control"),
    value: BICst.WIDGET.TREE,
    cls: "drag-tree-icon"
}, {
    text: BI.i18nText("BI-Date_Control"),
    title: BI.i18nText("BI-Date_Control"),
    value: BICst.WIDGET.DATE,
    cls: "drag-date-icon",
    children: [
        {
            text: BI.i18nText("BI-Year_Control"),
            title: BI.i18nText("BI-Year_Control"),
            value: BICst.WIDGET.YEAR,
            cls: "drag-year-icon"
        }, {
            text: BI.i18nText("BI-Year_Month_Con"),
            title: BI.i18nText("BI-Year_Month_Con"),
            value: BICst.WIDGET.MONTH,
            cls: "drag-year-month-icon"
        }, {
            text: BI.i18nText("BI-Year_Quarter_Con"),
            title: BI.i18nText("BI-Year_Quarter_Con"),
            value: BICst.WIDGET.QUARTER,
            cls: "drag-year-season-icon"
        }, {
            text: BI.i18nText("BI-Date_Control"),
            title: BI.i18nText("BI-Date_Control"),
            value: BICst.WIDGET.YMD,
            cls: "drag-ymd-icon"
        }, {
            text: BI.i18nText("BI-Date_Range_Control"),
            title: BI.i18nText("BI-Date_Range_Control"),
            value: BICst.WIDGET.DATE,
            cls: "drag-date-icon"
        }]
}, {
    text: BI.i18nText("BI-General_Query"),
    title: BI.i18nText("BI-General_Query"),
    value: BICst.WIDGET.GENERAL_QUERY,
    cls: "drag-general-query-icon"
}, {
    text: BI.i18nText("BI-Query_Button"),
    title: BI.i18nText("BI-Query_Button"),
    value: BICst.WIDGET.QUERY,
    cls: "drag-query-icon"
}, {
    text: BI.i18nText("BI-Reset_Button"),
    title: BI.i18nText("BI-Reset_Button"),
    value: BICst.WIDGET.RESET,
    cls: "drag-reset-icon"
}]];

BICst.SVG_MAP_TYPE = [{
    text: BI.i18nText("BI-China"),
    title: BI.i18nText("BI-China"),
    value: BICst.MAP_TYPE.CHINA,
    cls: "drag-map-china-icon"
}, {
    text: BI.i18nText("BI-JIANGSUSHENG"),
    title: BI.i18nText("BI-JIANGSUSHENG"),
    value: BICst.MAP_TYPE.JIANGSUSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-SHANDONGSHENG"),
    title: BI.i18nText("BI-SHANDONGSHENG"),
    value: BICst.MAP_TYPE.SHANDONGSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-TAIWANSHENG"),
    title: BI.i18nText("BI-TAIWANSHENG"),
    value: BICst.MAP_TYPE.TAIWANSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-YUNNANSHENG"),
    title: BI.i18nText("BI-YUNNANSHENG"),
    value: BICst.MAP_TYPE.YUNNANSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-JILINSHENG"),
    title: BI.i18nText("BI-JILINSHENG"),
    value: BICst.MAP_TYPE.JILINSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-SICHUANSHENG"),
    title: BI.i18nText("BI-SICHUANSHENG"),
    value: BICst.MAP_TYPE.SICHUANSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-ANHUISHENG"),
    title: BI.i18nText("BI-ANHUISHENG"),
    value: BICst.MAP_TYPE.ANHUISHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-SHANXISHENG"),
    title: BI.i18nText("BI-SHANXISHENG"),
    value: BICst.MAP_TYPE.SHANXISHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-GUANGDONGSHENG"),
    title: BI.i18nText("BI-GUANGDONGSHENG"),
    value: BICst.MAP_TYPE.GUANGDONGSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-JIANGXISHENG"),
    title: BI.i18nText("BI-JIANGXISHENG"),
    value: BICst.MAP_TYPE.JIANGXISHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-HEBEISHENG"),
    title: BI.i18nText("BI-HEBEISHENG"),
    value: BICst.MAP_TYPE.HEBEISHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-HENANSHENG"),
    title: BI.i18nText("BI-HENANSHENG"),
    value: BICst.MAP_TYPE.HENANSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-ZHEJIANGSHENG"),
    title: BI.i18nText("BI-ZHEJIANGSHENG"),
    value: BICst.MAP_TYPE.ZHEJIANGSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-HAINANSHENG"),
    title: BI.i18nText("BI-HAINANSHENG"),
    value: BICst.MAP_TYPE.HAINANSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-HUBEISHENG"),
    title: BI.i18nText("BI-HUBEISHENG"),
    value: BICst.MAP_TYPE.HUBEISHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-HUNANSHENG"),
    title: BI.i18nText("BI-HUNANSHENG"),
    value: BICst.MAP_TYPE.HUNANSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-GANSUSHENG"),
    title: BI.i18nText("BI-GANSUSHENG"),
    value: BICst.MAP_TYPE.GANSUSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-FUJIANSHENG"),
    title: BI.i18nText("BI-FUJIANSHENG"),
    value: BICst.MAP_TYPE.FUJIANSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-GUIZHOUSHENG"),
    title: BI.i18nText("BI-GUIZHOUSHENG"),
    value: BICst.MAP_TYPE.GUIZHOUSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-LIAONINGSHENG"),
    title: BI.i18nText("BI-LIAONINGSHENG"),
    value: BICst.MAP_TYPE.LIAONINGSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-SHANXISHENG1"),
    title: BI.i18nText("BI-SHANXISHENG1"),
    value: BICst.MAP_TYPE.SHANXISHENG1,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-QINGHAISHENG"),
    title: BI.i18nText("BI-QINGHAISHENG"),
    value: BICst.MAP_TYPE.QINGHAISHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-HEILONGJIANGSHENG"),
    title: BI.i18nText("BI-HEILONGJIANGSHENG"),
    value: BICst.MAP_TYPE.HEILONGJIANGSHENG,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-BEIJINGSHI"),
    title: BI.i18nText("BI-BEIJINGSHI"),
    value: BICst.MAP_TYPE.BEIJINGSHI,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-TIANJINSHI"),
    title: BI.i18nText("BI-TIANJINSHI"),
    value: BICst.MAP_TYPE.TIANJINSHI,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-CHONGQINGSHI"),
    title: BI.i18nText("BI-CHONGQINGSHI"),
    value: BICst.MAP_TYPE.CHONGQINGSHI,
    cls: "drag-map-china-icon"
}, {
    text: BI.i18nText("BI-SHANGHAISHI"),
    title: BI.i18nText("BI-SHANGHAISHI"),
    value: BICst.MAP_TYPE.SHANGHAISHI,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-NEIMENGGUZIZHIQU"),
    title: BI.i18nText("BI-NEIMENGGUZIZHIQU"),
    value: BICst.MAP_TYPE.NEIMENGGUZIZHIQU,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-GUANGXIZHUANGZUZIZHIQU"),
    title: BI.i18nText("BI-GUANGXIZHUANGZUZIZHIQU"),
    value: BICst.MAP_TYPE.GUANGXIZHUANGZUZIZHIQU,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-XICANGZIZHIQU"),
    title: BI.i18nText("BI-XICANGZIZHIQU"),
    value: BICst.MAP_TYPE.XICANGZIZHIQU,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-NINGXIAHUIZUZIZHIQU"),
    title: BI.i18nText("BI-NINGXIAHUIZUZIZHIQU"),
    value: BICst.MAP_TYPE.NINGXIAHUIZUZIZHIQU,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-XIANGGANGTEBIEXINGZHENGQU"),
    title: BI.i18nText("BI-XIANGGANGTEBIEXINGZHENGQU"),
    value: BICst.MAP_TYPE.XIANGGANGTEBIEXINGZHENGQU,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-XINJIANGWEIWUERZIZHIQU"),
    title: BI.i18nText("BI-XINJIANGWEIWUERZIZHIQU"),
    value: BICst.MAP_TYPE.XINJIANGWEIWUERZIZHIQU,
    cls: "drag-map-svg-icon"
}, {
    text: BI.i18nText("BI-AOMENTEBIEXINGZHENGQU"),
    title: BI.i18nText("BI-AOMENTEBIEXINGZHENGQU"),
    value: BICst.MAP_TYPE.AOMENTEBIEXINGZHENGQU,
    cls: "drag-map-svg-icon"
}];

//地图使用区域悬浮可选择的图表类型
BICst.SUSPENSION_MAP_TYPE = [{
    text: BI.i18nText("BI-Suspension_Bubble_Chart"),
    value: BICst.WIDGET.BUBBLE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Suspension_Pie_Chart"),
    value: BICst.WIDGET.PIE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Suspension_Column_Chart"),
    value: BICst.WIDGET.AXIS,
    cls: "dot-e-font"
}];

//组件的设置的下拉选项
BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS = [{
    value: BICst.DASHBOARD_WIDGET_LINKAGE,
    text: BI.i18nText("BI-Link_To_Dots"),
    extraCls: "dashboard-widget-combo-linkage-font"
// }, {
//     value: BICst.DASHBOARD_DETAIL_WIDGET_DRILL,
//     text: BI.i18nText("BI-Linked_To_Detail"),
//     extraCls: "dashboard-widget-combo-linkage-font"
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

//时间控件的下拉选项
BICst.TIME_CONTROL_SETCOMBO_ITEMS = [
    [{
        value: BICst.DASHBOARD_WIDGET_EXPAND,
        text: BI.i18nText("BI-Detailed_Setting"),
        cls: "widget-combo-expand-font"
    }],
    [{
        value: BICst.DASHBOARD_CONTROL_CLEAR,
        text: BI.i18nText("BI-Clear_Selected_Value"),
        cls: "widget-combo-clear-font"
    }],
    [{
        value: BICst.DASHBOARD_WIDGET_RENAME,
        text: BI.i18nText("BI-Rename"),
        cls: "widget-combo-rename-edit-font"
    }],
    [{
        value: BICst.DASHBOARD_WIDGET_COPY,
        text: BI.i18nText("BI-Copy"),
        cls: "widget-combo-copy"
    }],
    [{
        value: BICst.DASHBOARD_WIDGET_DELETE,
        text: BI.i18nText("BI-Delete_Control"),
        cls: "widget-combo-delete"
    }]
];

//数值控件的下拉选项
BICst.NUMBER_CONTROL_SETCOMBO_ITEMS = [
    [{
        value: BICst.DASHBOARD_WIDGET_EXPAND,
        text: BI.i18nText("BI-Detailed_Setting"),
        cls: "widget-combo-expand-font"
    }],
    [{
        value: BICst.DASHBOARD_CONTROL_CLEAR,
        text: BI.i18nText("BI-Clear_Selected_Value"),
        cls: "widget-combo-clear-font"
    }],
    [{
        value: BICst.DASHBOARD_WIDGET_RENAME,
        text: BI.i18nText("BI-Rename"),
        cls: "widget-combo-rename-edit-font"
    }],
    [{
        value: BICst.DASHBOARD_WIDGET_COPY,
        text: BI.i18nText("BI-Copy"),
        cls: "widget-combo-copy"
    }],
    [{
        value: BICst.DASHBOARD_WIDGET_DELETE,
        text: BI.i18nText("BI-Delete_Control"),
        cls: "widget-combo-delete"
    }]
];

//通用查询的下拉选项
BICst.GENERNAL_QUERY_CONTROL_SETCOMBO_ITEMS = [
    [{
        value: BICst.DASHBOARD_CONTROL_CLEAR,
        text: BI.i18nText("BI-Clear_Selected_Value"),
        cls: "widget-combo-clear-font"
    }],
    // [{
    //     value: BICst.DASHBOARD_WIDGET_RENAME,
    //     text: BI.i18nText("BI-Rename"),
    //     cls: "widget-combo-rename-edit-font"
    // }],
    // [{
    //     value: BICst.DASHBOARD_WIDGET_COPY,
    //     text: BI.i18nText("BI-Copy"),
    //     cls: "widget-combo-copy"
    // }],
    [{
        value: BICst.DASHBOARD_WIDGET_DELETE,
        text: BI.i18nText("BI-Delete_Control"),
        cls: "widget-combo-delete"
    }]
];

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

//过滤的几种类型
//target string
BICst.TARGET_FILTER_STRING_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.TARGET_FILTER_STRING.BELONG_VALUE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Contain"),
    value: BICst.TARGET_FILTER_STRING.CONTAIN,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Contain"),
    value: BICst.TARGET_FILTER_STRING.NOT_CONTAIN,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.TARGET_FILTER_STRING.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.TARGET_FILTER_STRING.NOT_NULL,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Begin_With"),
    value: BICst.TARGET_FILTER_STRING.BEGIN_WITH,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-End_With"),
    value: BICst.TARGET_FILTER_STRING.END_WITH,
    cls: "dot-e-font"
}]];

//target number
BICst.TARGET_FILTER_NUMBER_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Equal"),
    value: BICst.TARGET_FILTER_NUMBER.EQUAL_TO,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Equal_To"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.TARGET_FILTER_NUMBER.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_NULL,
    cls: "dot-e-font"
}]];

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
    value: BICst.DIMENSION_FILTER_STRING.BELONG_VALUE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Contain"),
    value: BICst.DIMENSION_FILTER_STRING.CONTAIN,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Contain"),
    value: BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.DIMENSION_FILTER_STRING.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.DIMENSION_FILTER_STRING.NOT_NULL,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Begin_With"),
    value: BICst.DIMENSION_FILTER_STRING.BEGIN_WITH,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-End_With"),
    value: BICst.DIMENSION_FILTER_STRING.END_WITH,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Top_N"),
    value: BICst.DIMENSION_FILTER_STRING.TOP_N,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Last_N"),
    value: BICst.DIMENSION_FILTER_STRING.BOTTOM_N,
    cls: "dot-e-font"
}]];

BICst.DIMENSION_TAR_FILTER_NUMBER_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Above_Average"),
    value: BICst.DIMENSION_FILTER_NUMBER.MORE_THAN_AVG,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Below_Average"),
    value: BICst.DIMENSION_FILTER_NUMBER.LESS_THAN_AVG,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.DIMENSION_FILTER_NUMBER.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.DIMENSION_FILTER_NUMBER.NOT_NULL,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Top_N"),
    value: BICst.DIMENSION_FILTER_NUMBER.TOP_N,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Last_N"),
    value: BICst.DIMENSION_FILTER_NUMBER.BOTTOM_N,
    cls: "dot-e-font"
}]];

//dimension date
BICst.DIMENSION_FILTER_DATE_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.DIMENSION_FILTER_DATE.BELONG_VALUE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.DIMENSION_FILTER_DATE.NOT_BELONG_VALUE,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Contain"),
    value: BICst.DIMENSION_FILTER_DATE.CONTAIN,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Contain"),
    value: BICst.DIMENSION_FILTER_DATE.NOT_CONTAIN,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.DIMENSION_FILTER_DATE.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.DIMENSION_FILTER_DATE.NOT_NULL,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Begin_With"),
    value: BICst.DIMENSION_FILTER_DATE.BEGIN_WITH,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-End_With"),
    value: BICst.DIMENSION_FILTER_DATE.END_WITH,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Top_N"),
    value: BICst.DIMENSION_FILTER_DATE.TOP_N,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Last_N"),
    value: BICst.DIMENSION_FILTER_DATE.BOTTOM_N,
    cls: "dot-e-font"
}]];

//date
BICst.FILTER_DATE_COMBO = [[{
    el: {
        text: BI.i18nText("BI-In"),
        value: BI.i18nText("BI-In")
    },
    children: [{
        text: BI.i18nText("BI-Periods_Time"),
        value: BICst.FILTER_DATE.BELONG_DATE_RANGE,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Control_Value"),
        value: BICst.FILTER_DATE.BELONG_WIDGET_VALUE,
        cls: "dot-e-font"
    }]
}, {
    el: {
        text: BI.i18nText("BI-Not_In"),
        value: BI.i18nText("BI-Not_In")
    },
    children: [{
        text: BI.i18nText("BI-Periods_Time"),
        value: BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Control_Value"),
        value: BICst.FILTER_DATE.NOT_BELONG_WIDGET_VALUE,
        cls: "dot-e-font"
    }]
}], [{
    text: BI.i18nText("BI-Sooner_Than"),
    value: BICst.FILTER_DATE.EARLY_THAN,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Later_Than"),
    value: BICst.FILTER_DATE.LATER_THAN,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Equal"),
    value: BICst.FILTER_DATE.EQUAL_TO,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Equal_To"),
    value: BICst.FILTER_DATE.NOT_EQUAL_TO,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.FILTER_DATE.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.FILTER_DATE.NOT_NULL,
    cls: "dot-e-font"
}]];

//auth date
BICst.AUTH_FILTER_DATE_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.FILTER_DATE.BELONG_DATE_RANGE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Equal"),
    value: BICst.FILTER_DATE.EQUAL_TO,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Equal_To"),
    value: BICst.FILTER_DATE.NOT_EQUAL_TO,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.FILTER_DATE.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.FILTER_DATE.NOT_NULL,
    cls: "dot-e-font"
}]];

BICst.DATA_SETTING_FILTER_DATE_COMBO = [[{
    text: BI.i18nText("BI-In"),
    value: BICst.FILTER_DATE.BELONG_DATE_RANGE,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_In"),
    value: BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Equal"),
    value: BICst.FILTER_DATE.EQUAL_TO,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Equal_To"),
    value: BICst.FILTER_DATE.NOT_EQUAL_TO,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.FILTER_DATE.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.FILTER_DATE.NOT_NULL,
    cls: "dot-e-font"
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

//auth string
BICst.AUTHORITY_FILTER_STRING_COMBO = [[{
    el: {
        text: BI.i18nText("BI-In"),
        value: BICst.TARGET_FILTER_STRING.BELONG_VALUE
    },
    children: [{
        text: BI.i18nText("BI-Field_Value"),
        value: BICst.TARGET_FILTER_STRING.BELONG_VALUE,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Login_User_Info"),
        value: BICst.TARGET_FILTER_STRING.BELONG_USER,
        cls: "dot-e-font"
    }]
}, {
    el: {
        text: BI.i18nText("BI-Not_In"),
        value: BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE
    },
    children: [{
        text: BI.i18nText("BI-Field_Value"),
        value: BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Login_User_Info"),
        value: BICst.TARGET_FILTER_STRING.NOT_BELONG_USER,
        cls: "dot-e-font"
    }]
}], [{
    text: BI.i18nText("BI-Contain"),
    value: BICst.TARGET_FILTER_STRING.CONTAIN,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Contain"),
    value: BICst.TARGET_FILTER_STRING.NOT_CONTAIN,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.TARGET_FILTER_STRING.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.TARGET_FILTER_STRING.NOT_NULL,
    cls: "dot-e-font"
}], [{
    text: BI.i18nText("BI-Begin_With"),
    value: BICst.TARGET_FILTER_STRING.BEGIN_WITH,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-End_With"),
    value: BICst.TARGET_FILTER_STRING.END_WITH,
    cls: "dot-e-font"
}]];

//auth number
BICst.AUTHORITY_FILTER_NUMBER_COMBO = [[{
    text: BI.i18nText("BI-Equal"),
    value: BICst.TARGET_FILTER_NUMBER.EQUAL_TO,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Equal_To"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO,
    cls: "dot-e-font"
}], [{
    el: {
        text: BI.i18nText("BI-In"),
        value: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE
    },
    children: [{
        text: BI.i18nText("BI-Interval_Value"),
        value: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Login_User_Info"),
        value: BICst.TARGET_FILTER_NUMBER.BELONG_USER,
        cls: "dot-e-font"
    }]
}, {
    el: {
        text: BI.i18nText("BI-Not_In"),
        value: BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE
    },
    children: [{
        text: BI.i18nText("BI-Interval_Value"),
        value: BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Login_User_Info"),
        value: BICst.TARGET_FILTER_NUMBER.NOT_BELONG_USER,
        cls: "dot-e-font"
    }]
}], [{
    text: BI.i18nText("BI-Is_Null"),
    value: BICst.TARGET_FILTER_NUMBER.IS_NULL,
    cls: "dot-e-font"
}, {
    text: BI.i18nText("BI-Not_Null"),
    value: BICst.TARGET_FILTER_NUMBER.NOT_NULL,
    cls: "dot-e-font"
}]];

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
}, {
    text: BI.i18nText("BI-Record_Count"),
    value: BICst.SUMMARY_TYPE.RECORD_COUNT
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
}, {
    text: BI.i18nText("BI-Record_Count"),
    value: BICst.SUMMARY_TYPE.RECORD_COUNT
}];

BICst.CONF_STATISTIC_DATE = [{
    text: BI.i18nText("BI-No_Repeat_Count"),
    value: BICst.SUMMARY_TYPE.COUNT
}, {
    text: BI.i18nText("BI-Record_Count"),
    value: BICst.SUMMARY_TYPE.RECORD_COUNT
}];
//选色
BICst.CHART_COLORS = [{
    header: BI.i18nText("BI-Default"),
    text: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"],
    value: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]
}, {
    header: BI.i18nText("BI-Bright"),
    text: ["#9193ac", "#779ae3", "#e6a469", "#eddc80", "#9889d0"],
    value: ["#9193ac", "#779ae3", "#e6a469", "#eddc80", "#9889d0"]
}, {
    header: BI.i18nText("BI-Elegant"),
    text: ["#f07d0a", "#009de3", "#58cc7d", "#e85050", "#9889d0"],
    value: ["#f07d0a", "#009de3", "#58cc7d", "#e85050", "#9889d0"]
}];

//图值轴格式
BICst.CHART_VALUE_AXIS_STYLE = [{
    text: BI.i18nText("BI-Normal"),
    value: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]
}, {
    text: "0",
    value: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]
}, {
    text: "0.0",
    value: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]
}, {
    text: "0.00",
    value: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]
}];

//表格（分组表、交叉表和复杂表）类型
BICst.TABLE_FORM_GROUP = [{
    cls: "table-open-row-style-font",
    value: BICst.TABLE_FORM.OPEN_ROW
}, {
    cls: "table-open-col-style-font",
    value: BICst.TABLE_FORM.OPEN_COL
}];

//表格（分组表、交叉表和复杂表）风格
BICst.TABLE_STYLE_GROUP = [{
    cls: "table-style1-icon",
    value: BICst.TABLE_STYLE.STYLE1
}, {
    cls: "table-style2-icon",
    value: BICst.TABLE_STYLE.STYLE2
}, {
    cls: "table-style3-icon",
    value: BICst.TABLE_STYLE.STYLE3
}];

//坐标轴图风格
BICst.AXIS_STYLE_GROUP = [{
    cls: "axis-chart-style-normal-icon",
    value: BICst.CHART_STYLE.STYLE_NORMAL
}, {
    cls: "axis-chart-style-gradual-icon",
    value: BICst.CHART_STYLE.STYLE_GRADUAL
}];

//折线图和面积图线形
BICst.LINE_CHART_STYLE_GROUP = [{
    cls: "line-chart-style-broken-icon",
    value: BICst.CHART_SHAPE.NORMAL
}, {
    cls: "line-chart-style-curve-icon",
    value: BICst.CHART_SHAPE.CURVE
}, {
    cls: "line-chart-style-vertical-icon",
    value: BICst.CHART_SHAPE.RIGHT_ANGLE
}];

BICst.AREA_CHART_STYLE_GROUP = [{
    cls: "area-chart-style-broken-icon",
    value: BICst.CHART_SHAPE.NORMAL
}, {
    cls: "area-chart-style-curve-icon",
    value: BICst.CHART_SHAPE.CURVE
}, {
    cls: "area-chart-style-vertical-icon",
    value: BICst.CHART_SHAPE.RIGHT_ANGLE
}];

//饼图类型
BICst.PIE_CHART_STYLE_GROUP = [{
    cls: "pie-chart-style-normal-icon",
    value: BICst.CHART_SHAPE.NORMAL
}, {
    cls: "pie-chart-style-equal-arc-rose-icon",
    value: BICst.CHART_SHAPE.EQUAL_ARC_ROSE
}, {
    cls: "pie-chart-style-not-equal-arc-rose-icon",
    value: BICst.CHART_SHAPE.NOT_EQUAL_ARC_ROSE
}];

//雷达图类性
BICst.RADAR_CHART_STYLE_GROUP = [{
    cls: "radar-chart-style-polygon-icon",
    value: BICst.CHART_SHAPE.POLYGON
}, {
    cls: "radar-chart-style-circle-icon",
    value: BICst.CHART_SHAPE.CIRCLE
}];

//堆积雷达图类型
BICst.ACC_RADAR_CHART_STYLE_GROUP = [{
    cls: "acc_radar-chart-style-polygon-icon",
    value: BICst.CHART_SHAPE.POLYGON
}, {
    cls: "acc_radar-chart-style-circle-icon",
    value: BICst.CHART_SHAPE.CIRCLE
}];

//仪表盘类型
BICst.DASHBOARD_CHART_STYLE_GROUP = [{
    cls: "dashboard-chart-style-360-icon",
    title: BI.i18nText("BI-Multi_Pointer_Dashboard_360"),
    value: BICst.CHART_SHAPE.NORMAL
}, {
    cls: "dashboard-chart-style-180-icon",
    title: BI.i18nText("BI-Multi_Pointer_Dashboard_180"),
    value: BICst.CHART_SHAPE.HALF_DASHBOARD
}, {
    cls: "dashboard-chart-style-percent-icon",
    title: BI.i18nText("BI-Percent_Scale_Slot_Dashboard"),
    value: BICst.CHART_SHAPE.PERCENT_SCALE_SLOT
}, {
    cls: "dashboard-chart-style-percent-scale-slot-icon",
    title: BI.i18nText("BI-Percent_Donut_Dashboard"),
    value: BICst.CHART_SHAPE.PERCENT_DASHBOARD
}, {
    cls: "dashboard-chart-style-vertical-tube-icon",
    title: BI.i18nText("BI-Vertical_Tube"),
    value: BICst.CHART_SHAPE.VERTICAL_TUBE
}, {
    cls: "dashboard-chart-style-horizontal-tube-icon",
    title: BI.i18nText("BI-Horizontal_Tube"),
    value: BICst.CHART_SHAPE.HORIZONTAL_TUBE
}];

BICst.CHART_SCALE_SETTING = [{
    text: BI.i18nText("BI-Auto"),
    value: BICst.SCALE_SETTING.AUTO
}, {
    text: BI.i18nText("BI-Custom"),
    value: BICst.SCALE_SETTING.CUSTOM
}];

BICst.BUBBLE_CHART_STYLE_GROUP = [{
    text: BI.i18nText("BI-Bubble_Without_Shadow"),
    cls: "bubble-style-button bubble-no-projector",
    value: BICst.CHART_SHAPE.NO_PROJECTOR
}, {
    text: BI.i18nText("BI-Bubble_With_Shadow"),
    cls: "bubble-style-button bubble-with-projector",
    value: BICst.CHART_SHAPE.PROJECTOR
}];

//表设置所有默认属性（应该是包含分组表、交叉表、复杂表和其他所有图表）
BICst.DEFAULT_CHART_SETTING = {
    table_form: BICst.TABLE_FORM.OPEN_ROW,
    theme_color: "#65bce7",
    table_style: BICst.TABLE_STYLE.STYLE1,
    show_number: false,
    show_row_total: true,
    show_col_total: true,
    open_row_node: false,
    open_col_node: false,
    max_row: BICst.TABLE_MAX_ROW,
    max_col: BICst.TABLE_MAX_COL,
    freeze_dim: true,
    freeze_first_column: false,
    transfer_filter: true,
    show_name: true,
    name_pos: BICst.DASHBOARD_WIDGET_NAME_POS_LEFT,

    //图
    chart_color: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"],
    chart_style: BICst.CHART_STYLE.STYLE_NORMAL,
    chart_line_type: BICst.CHART_SHAPE.NORMAL,
    chart_pie_type: BICst.CHART_SHAPE.NORMAL,
    chart_radar_type: BICst.CHART_SHAPE.POLYGON,
    chart_dashboard_type: BICst.CHART_SHAPE.NORMAL,
    chart_inner_radius: 0,
    chart_total_angle: BICst.PIE_ANGLES.TOTAL,
    left_y_axis_style: BICst.TARGET_STYLE.FORMAT.NORMAL,
    x_axis_style: BICst.TARGET_STYLE.FORMAT.NORMAL,
    right_y_axis_style: BICst.TARGET_STYLE.FORMAT.NORMAL,
    right_y_axis_second_style: BICst.TARGET_STYLE.FORMAT.NORMAL,
    left_y_axis_number_level: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
    number_of_pointer: BICst.POINTER.ONE,
    dashboard_number_level: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
    x_axis_number_level: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
    right_y_axis_number_level: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
    right_y_axis_second_number_level: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
    left_y_axis_unit: "",
    dashboard_unit: "",
    x_axis_unit: "",
    right_y_axis_unit: "",
    right_y_axis_second_unit: "",
    show_left_y_axis_title: false,
    show_right_y_axis_title: false,
    show_right_y_axis_second_title: false,
    left_y_axis_title: "",
    right_y_axis_title: "",
    right_y_axis_second_title: "",
    left_y_axis_reversed: false,
    right_y_axis_reversed: false,
    right_y_axis_second_reversed: false,
    show_x_axis_title: false,
    x_axis_title: "",
    text_direction: "0",
    chart_legend: BICst.CHART_LEGENDS.BOTTOM,
    show_data_label: false,
    show_data_table: false,
    show_grid_line: true,
    show_zoom: false,
    null_continue: true,
    bubble_display: BICst.DISPLAY_RULES.DIMENSION,
    bubble_style: BICst.CHART_SHAPE.NO_PROJECTOR
};

BICst.DASHBOARD_STYLE_CONDITIONS = [{
    range: {
        min: 0,
        max: 100,
        closemin: true,
        closemax: false
    },
    color: "#65B3EE"
}, {
    range: {
        min: 100,
        max: 200,
        closemin: true,
        closemax: false
    },
    color: "#95E1AA"
}, {
    range: {
        min: 200,
        max: 300,
        closemin: true,
        closemax: true
    },
    color: "#F8D08E"
}];

BICst.MAP_STYLE_CONDITIONS = [{
    range: {
        min: 0,
        max: 100,
        closemin: true,
        closemax: false
    },
    color: "#65B3EE"
}, {
    range: {
        min: 100,
        max: 200,
        closemin: true,
        closemax: false
    },
    color: "#95E1AA"
}, {
    range: {
        min: 200,
        max: 300,
        closemin: false,
        closemax: true
    },
    color: "#F8D08E"
}, {
    range: {
        min: 300,
        max: 400,
        closemin: false,
        closemax: true
    },
    color: "#e697c8"
}, {
    range: {
        min: 400,
        max: 500,
        closemin: true,
        closemax: true
    },
    color: "#a484b9"
}];

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

BICst.BUBBLE_DISPLAY_RULES = [{
    text: BI.i18nText("BI-Dimension_Color_Setting"),
    value: BICst.DISPLAY_RULES.DIMENSION
}, {
    text: BI.i18nText("BI-Bubble_Fixed_Color"),
    value: BICst.DISPLAY_RULES.FIXED
}, {
    text: BI.i18nText("BI-Bubble_Gradient"),
    value: BICst.DISPLAY_RULES.GRADIENT
}];

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

BICst.TARGET_STYLE_LEVEL_SHORT = [{
    text: BI.i18nText("BI-Normal"),
    value: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL
}, {
    text: "%",
    value: BICst.TARGET_STYLE.NUM_LEVEL.PERCENT
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

BICst.POINTERS = [{
    text: BI.i18nText("BI-One_pointer"),
    value: BICst.POINTER.ONE
}, {
    text: BI.i18nText("BI-Some_pointers"),
    value: BICst.POINTER.SOME
}];

BICst.PIE_TOTAL_ANGLE = [{
    text: "180'",
    value: BICst.PIE_ANGLES.HALF
}, {
    text: "270'",
    value: BICst.PIE_ANGLES.THREE_FOURTHS
}, {
    text: "360'",
    value: BICst.PIE_ANGLES.TOTAL
}];

BICst.CHART_LEGEND = [{
    text: BI.i18nText("BI-Hidden"),
    value: BICst.CHART_LEGENDS.NOT_SHOW
}, {
    text: BI.i18nText("BI-Ju_xia"),
    value: BICst.CHART_LEGENDS.BOTTOM
}, {
    text: BI.i18nText("BI-Ju_Right"),
    value: BICst.CHART_LEGENDS.RIGHT
}];


