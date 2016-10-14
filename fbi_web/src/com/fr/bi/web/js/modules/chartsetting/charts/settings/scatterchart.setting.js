/**
 * @class BI.ScatterChartSetting
 * @extends BI.Widget
 * 散点样式
 */
BI.ScatterChartSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function(){
        return BI.extend(BI.ScatterChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-scatter-chart-setting"
        })
    },

    _init: function(){
        BI.ScatterChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options, constant = BI.AbstractChartSetting;

        //组件设置
        this.widgetSetting = BI.createWidget({
            type: "bi.widget_block_setting",
            wId: o.wId
        });

        this.widgetSetting.on(BI.WidgetBlockSetting.EVENT_CHANGE, function() {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [100],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Table_Sheet_Style"),
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Color_Setting"),
                    cls: "attr-names"
                }, {
                    el: {
                        type: "bi.vertical_adapt",
                        items: [this.colorSelect]
                    },
                    lgap: constant.SIMPLE_H_GAP
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                })
            }]
        });

        //x轴 切换对应 rAxisValueSetting
        this.rValueAxisSetting = BI.createWidget({
            type: "bi.value_axis_block_setting",
            headText: BI.i18nText("BI-X_Axis"),
            reversed: false,
            wId: o.wId
        });

        this.rValueAxisSetting.on(BI.ValueAxisBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //y轴 切换对应lAxisValueSetting
        this.lValueAxisSetting = BI.createWidget({
            type: "bi.value_axis_block_setting",
            headText: BI.i18nText("BI-Y_Axis"),
            reversed: false,
            wId: o.wId
        });

        this.lValueAxisSetting.on(BI.ValueAxisBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: constant.LEGEND_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                text: BI.i18nText("BI-Element_Show"),
                textHeight: constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Legend_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.legend]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.gridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
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

        //大数据模式
        this.bigDataMode = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Big_Data_Mode"),
            width: 120
        });

        this.bigDataMode.on(BI.Controller.EVENT_CHANGE, function () {
            self._bigDataMode(!this.isSelected());
            if (this.isSelected()) {
                self.rulesDisplay.setValue(BICst.DISPLAY_RULES.GRADIENT);
                self._colorSettingChange(BICst.DISPLAY_RULES.GRADIENT)
            }
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        var modeChange = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Mode_Change"),
                    cls: "line-title"
                }, this.bigDataMode]
            },
            height: constant.SINGLE_LINE_HEIGHT,
            lhgap: constant.SIMPLE_H_GAP
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
            height: constant.SINGLE_LINE_HEIGHT,
            lhgap: constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.widgetSetting, tableStyle, this.rValueAxisSetting, this.lValueAxisSetting, showElement, otherAttr, modeChange],
            hgap: 10
        })
    },

    _bigDataMode: function (v) {
        this.showDataLabel.setEnable(v);
        this.transferFilter.setEnable(v)
    },

    populate: function(){
        var wId = this.options.wId;

        this.widgetSetting.populate();
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.rValueAxisSetting.setValue(BI.Utils.getWSRightValueAxisSettingByID(wId));
        this.lValueAxisSetting.setValue(BI.Utils.getWSLeftValueAxisSettingByID(wId));

        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.gridLine.setSelected(BI.Utils.getWSShowGridLineByID(wId));
        this.bigDataMode.setSelected(BI.Utils.getWSBigDataModelByID(wId));
        this._bigDataMode(!BI.Utils.getWSBigDataModelByID(wId));
    },

    getValue: function(){
        return {
            widget_setting: this.widgetSetting.getValue(),
            right_value_setting: this.rValueAxisSetting.getValue(),
            left_value_setting: this.lValueAxisSetting.getValue(),

            chart_color: this.colorSelect.getValue()[0],
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_grid_line: this.gridLine.isSelected(),
            transfer_filter: this.transferFilter.isSelected(),
            big_data_mode: this.bigDataMode.isSelected(),
        }
    },

    setValue: function(v){
        this.widgetSetting.setValue(v.widget_setting);
        this.rValueAxisSetting.setValue(v.right_value_setting);
        this.lValueAxisSetting.setValue(v.left_value_setting);

        this.colorSelect.setValue(v.chart_color);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.gridLine.setSelected(v.show_grid_line);
        this.transferFilter.setSelected(v.transfer_filter);
        this.bigDataMode.setSelected(v.big_data_mode)
    }
});
BI.ScatterChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.scatter_chart_setting", BI.ScatterChartSetting);
