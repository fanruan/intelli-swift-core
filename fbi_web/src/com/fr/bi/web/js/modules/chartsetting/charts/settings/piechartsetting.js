/**
 * @class BI.PieChartSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.PieChartSetting = BI.inherit(BI.Widget, {

    constant: {
        CHART_TYPE_SEGMENT_WIDTH: 180,
        SINGLE_LINE_HEIGHT: BICst.CHART.SINGLE_LINE_HIGHT,
        SIMPLE_H_GAP: BICst.CHART.SIMPLE_H_GAP,
        SIMPLE_L_GAP: BICst.CHART.SIMPLE_L_GAP,
        SIMPLE_H_LGAP: BICst.CHART.SIMPLE_H_LGAP,
        CHECKBOX_WIDTH: BICst.CHART.CHECKBOX_WIDTH,
        EDITOR_WIDTH: BICst.CHART.EDITOR_WIDTH,
        EDITOR_HEIGHT: BICst.CHART.EDITOR_HEIGHT,
        BUTTON_WIDTH: BICst.CHART.BUTTON_WIDTH,
        BUTTON_HEIGHT: BICst.CHART.BUTTON_HEIGHT,
        ICON_WIDTH: BICst.CHART.ICON_WIDTH,
        ICON_HEIGHT: BICst.CHART.ICON_HEIGHT,
        NUMBER_LEVEL_SEGMENT_WIDTH: BICst.CHART.NUMBER_LEVEL_SEGMENT_WIDTH,
        FORMAT_SEGMENT_WIDTH: BICst.CHART.FORMAT_SEGMENT_WIDTH,
        LEGEND_SEGMENT_WIDTH: BICst.CHART.LEGEND_SEGMENT_WIDTH
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
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.PieChartSetting.EVENT_CHANGE);
        });

        this.chartStyleGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AXIS_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: this.constant.BUTTON_WIDTH,
                height: this.constant.BUTTON_HEIGHT,
                iconWidth: this.constant.ICON_WIDTH,
                iconHeight: this.constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: this.constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.chartStyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.LineAreaChartSetting.EVENT_CHANGE);
        });

        this.chartTypeGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.PIE_CHART_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: this.constant.BUTTON_WIDTH,
                height: this.constant.BUTTON_HEIGHT,
                iconWidth: this.constant.ICON_WIDTH,
                iconHeight: this.constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: this.constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.chartTypeGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.LineAreaChartSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [100],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Table_Sheet_Style"),
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
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
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names",
                    lgap: this.constant.SIMPLE_H_GAP
                }, {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.chartStyleGroup]
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
            cls: "unit-input",
            errorText: BI.i18nText("BI-Please_Enter_Number_1_To_100"),
            validationChecker: function(v){
                if(BI.isNaturalNumber(v)){
                    return BI.parseInt(v) <= 100 && BI.parseInt(v) >= 0;
                }
                return false;
            }
        });

        this.innerRadius.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.PieChartSetting.EVENT_CHANGE);
        });

        //总角度
        this.totalAngle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.CHART_TYPE_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.PIE_TOTAL_ANGLE
        });

        this.totalAngle.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.PieChartSetting.EVENT_CHANGE);
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: this.constant.LEGEND_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.PieChartSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.PieChartSetting.EVENT_CHANGE);
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Legend_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.legend]
                }, {
                    type: "bi.center_adapt",
                    items: [this.showDataLabel]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        var lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                text: BI.i18nText("BI-Show_Param"),
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
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
            items: [tableStyle, lYAxis, showElement, otherAttr],
            hgap: 10
        })
    },

    populate: function(){
        var wId = this.options.wId;
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartStyleGroup.setValue(BI.Utils.getWSChartStyleByID(wId));
        this.chartTypeGroup.setValue(BI.Utils.getWSChartPieTypeByID(wId));
        this.totalAngle.setValue(BI.Utils.getWSChartTotalAngleByID(wId));
        this.innerRadius.setValue(BI.Utils.getWSChartInnerRadiusByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));

    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            chart_style: this.chartStyleGroup.getValue()[0],
            chart_pie_type: this.chartTypeGroup.getValue()[0],
            chart_total_angle: this.totalAngle.getValue()[0],
            chart_inner_radius: this.innerRadius.getValue(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.chartStyleGroup.setValue(v.chart_style);
        this.chartTypeGroup.setValue(v.chart_pie_type);
        this.totalAngle.setValue(v.chart_total_angle);
        this.innerRadius.setValue(v.chart_inner_radius);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
    }
});
BI.PieChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.pie_chart_setting", BI.PieChartSetting);