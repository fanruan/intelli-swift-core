/**
 * @class BI.AxisChartsSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.AxisChartsSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.AxisChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-axis-chart-setting"
        })
    },

    _init: function () {
        BI.AxisChartsSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options, constant = BI.AbstractChartSetting;

        //组件设置
        this.widgetSetting = BI.createWidget({
            type: "bi.widget_block_setting",
            wId: o.wId
        });

        this.widgetSetting.on(BI.WidgetBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //图表样式
        this.chartStyleSetting = BI.createWidget({
            type: "bi.chart_style_block_setting"
        });

        this.chartStyleSetting.on(BI.ChartStyleBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //左值轴
        this.lValueAxisSetting = BI.createWidget({
            type: "bi.value_axis_block_setting",
            wId: o.wId
        });

        this.lValueAxisSetting.on(BI.ValueAxisBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //右值轴
        this.rValueAxisSetting = BI.createWidget({
            type: "bi.value_axis_block_setting",
            wId: o.wId
        });

        this.rValueAxisSetting.on(BI.ValueAxisBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //分类轴标签
        this.showCatLable = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showCatLable.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.catLabel = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.catLabel.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //分类轴线颜色
        this.catLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.catLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //横轴文本方向
        this.text_direction = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            allowBlank: false,
            value: "0",
            errorText: BI.i18nText("BI-Please_Enter_Number_From_To_To", -90, 90),
            validationChecker: function (v) {
                return BI.isInteger(v) && v >= -90 && v <= 90;
            }
        });
        this.text_direction.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: constant.LEGEND_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //图例详细设置
        this.legendSetting = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendSetting.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //网格线设置
        this.hGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 65
        });

        this.hGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.vGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 65
        });

        this.vGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //数据表格
        this.showDataTable = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Table"),
            width: 115
        });

        this.showDataTable.on(BI.Controller.EVENT_CHANGE, function () {
            if (this.isSelected()) {
                self.showZoom.setSelected(false);
            }
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //图表缩放滚轮
        this.showZoom = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Zoom"),
            width: 140
        });

        this.showZoom.on(BI.Controller.EVENT_CHANGE, function () {
            if (this.isSelected()) {
                self.showDataTable.setSelected(false);
            }
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //数据点提示详细设置
        this.tooltipSetting = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipSetting.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
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
                    items: [this.legendSetting]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Grid_Line"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.hGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.hGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.vGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.vGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataTable]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showZoom]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Tooltip"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.tooltipSetting]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.xAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Category_Axis"),
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Text_Direction"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.text_direction]
                }, {
                    type: "bi.label",
                    text: "。",
                    textHeight: 30,
                    height: constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleX]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.editTitleX]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showCatLable]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.catLabel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.catLineColor]
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
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.otherAttr = BI.createWidget({
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

        //极简模式
        this.minimalistModel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Minimalist_Model"),
            width: 170
        });

        this.minimalistModel.on(BI.Controller.EVENT_CHANGE, function () {
            self._invisible(!this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        var modelChange = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Mode_Change"),
                    cls: "line-title"
                }, this.minimalistModel]
            },
            height: constant.SINGLE_LINE_HEIGHT,
            lhgap: constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.widgetSetting, this.chartStyleSetting, this.lValueAxisSetting, this.rValueAxisSetting, this.xAxis, this.showElement, this.otherAttr, modelChange],
            hgap: 10
        })
    },

    _invisible: function (v) {
        this.chartStyleSetting.setVisible(v);
        this.lValueAxisSetting.setVisible(v);
        this.xAxis.setVisible(v);
        this.rValueAxisSetting.setVisible(v);
        this.showElement.setVisible(v);
        this.otherAttr.setVisible(v);
    },

    populate: function () {
        var wId = this.options.wId;
        var view = BI.Utils.getWidgetViewByID(wId);
        var titleX = BI.Utils.getWSXAxisTitleByID(wId);
        if (titleX === "") {
            BI.any(view[BICst.REGION.DIMENSION1], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleX = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }

        this.widgetSetting.populate();
        this.chartStyleSetting.setValue(BI.Utils.getWSChartStyleSettingByID(wId));
        this.lValueAxisSetting.setValue(BI.Utils.getWSLeftValueAxisSettingByID(wId));
        this.rValueAxisSetting.setValue(BI.Utils.getWSRightValueAxisSettingByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleX.setValue(titleX);
        this.text_direction.setValue(BI.Utils.getWSTextDirectionByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.showDataTable.setSelected(BI.Utils.getWSShowDataTableByID(wId));
        this.showZoom.setSelected(BI.Utils.getWSShowZoomByID(wId));
        this.minimalistModel.setSelected(BI.Utils.getWSMinimalistByID(wId));
        this._invisible(!BI.Utils.getWSMinimalistByID(wId));
        // this.isShowTitleLY.isSelected() ? this.editTitleLY.setVisible(true) : this.editTitleLY.setVisible(false);
        // this.isShowTitleRY.isSelected() ? this.editTitleRY.setVisible(true) : this.editTitleRY.setVisible(false);
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
        // this.customYScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
        // this.customXScale.setVisible(BI.Utils.getWSShowXCustomScale(wId));
    },

    getValue: function () {
        return {
            widget_setting: this.widgetSetting.getValue(),
            chart_style_setting: this.chartStyleSetting.getValue(),
            transfer_filter: this.transferFilter.isSelected(),
            show_x_axis_title: this.isShowTitleX.isSelected(),
            x_axis_title: this.editTitleX.getValue(),
            text_direction: this.text_direction.getValue(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_data_table: this.showDataTable.isSelected(),
            show_zoom: this.showZoom.isSelected(),
            minimalist_model: this.minimalistModel.isSelected(),
        }
    },

    setValue: function (v) {
        this.widgetSetting.setValue(v.widget_setting);
        this.chartStyleSetting.setValue(v.chart_style_setting);
        this.transferFilter.setSelected(v.transfer_filter);
        this.isShowTitleX.setSelected(v.x_axis_title);
        this.editTitleX.setValue(v.x_axis_title);
        this.text_direction.setValue(v.text_direction);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.showDataTable.setSelected(v.show_data_table);
        this.showZoom.setSelected(v.show_zoom);
        this.minimalistModel.setSelected(v.minimalist_model);

    }
});
BI.AxisChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.axis_charts_setting", BI.AxisChartsSetting);
