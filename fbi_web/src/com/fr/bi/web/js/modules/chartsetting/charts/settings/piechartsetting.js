/**
 * @class BI.PieChartSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.PieChartSetting = BI.inherit(BI.Widget, {

    constant: {
        SINGLE_LINE_HEIGHT: 60,
        SIMPLE_H_GAP: 10,
        SIMPLE_L_GAP: 2,
        CHECKBOX_WIDTH: 16,
        EDITOR_WIDTH: 80,
        EDITOR_HEIGHT: 26,
        BUTTON_WIDTH: 40,
        BUTTON_HEIGHT: 30,
        ICON_WIDTH: 24,
        ICON_HEIGHT: 24,
        NUMBER_LEVEL_SEGMENT_WIDTH: 300,
        FORMAT_SEGMENT_WIDTH: 240,
        CHART_TYPE_SEGMENT_WIDTH: 180
    },

    _defaultConfig: function(){
        return BI.extend(BI.PieChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.PieChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate(BICst.CHART_COLORS);

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.PieChartSetting.EVENT_CHANGE);
        });

        this.chartTypeGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.PIE_CHART_STYLE_GROUP, {
                type: "bi.text_button",
                extraCls: "table-style-font",
                width: this.constant.BUTTON_WIDTH,
                height: this.constant.BUTTON_HEIGHT
            }),
            layouts: [{
                type: "bi.left"
            }]
        });
        this.chartTypeGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal",
            cls: "single-line-settings",
            lgap: this.constant.SIMPLE_H_GAP,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Table_Sheet_Style"),
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Chart_Color"),
                    cls: "attr-names"
                }, {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.colorSelect]
                    },
                    lgap: this.constant.SIMPLE_H_GAP
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Type"),
                    cls: "attr-names",
                    lgap: this.constant.SIMPLE_H_GAP2
                }, {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.chartTypeGroup]
                    },
                    lgap: this.constant.SIMPLE_H_GAP
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                })
            }]
        });

        //内径大小
        this.innerRadius = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        //总角度
        this.totalAngle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.CHART_TYPE_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.PIE_TOTAL_ANGLE
        });

        var lYAxis = BI.createWidget({
            type: "bi.horizontal",
            cls: "single-line-settings",
            lgap: this.constant.SIMPLE_H_GAP,
            items: [{
                type: "bi.label",
                height: "100%",
                textHeight: 60,
                text: BI.i18nText("BI-Show_Param"),
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Inner_Radius_Size"),
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.innerRadius]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Total_Angle"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.totalAngle]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            width: 170
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        var otherAttr = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Interactive_Attr"),
                    cls: "line-title"
                }, this.transferFilter]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [tableStyle, lYAxis, otherAttr],
            hgap: 10
        })
    },

    populate: function(){
        var wId = this.options.wId;
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartTypeGroup.setValue(BI.Utils.getWSChartPieTypeByID(wId));
        this.totalAngle.setValue(BI.Utils.getWSChartTotalAngleByID(wId));
        this.innerRadius.setValue(BI.Utils.getWSChartInnerRadiusByID(wId));
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            chart_pie_type: this.chartTypeGroup.getValue()[0],
            chart_total_angle: this.totalAngle.getValue()[0],
            chart_inner_radius: this.innerRadius.getValue()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.chartTypeGroup.setValue(v.chart_pie_type);
        this.totalAngle.setValue(v.chart_total_angle);
        this.innerRadius.setValue(v.chart_inner_radius);
    }
});
BI.PieChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.pie_chart_setting", BI.PieChartSetting);