/**
 * @class BI.RangeAreaChartsSetting
 * @extends BI.Widget
 * 范围面积样式
 */
BI.RangeAreaChartsSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.RangeAreaChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-range-area-chart-setting"
        })
    },

    _init: function () {
        BI.RangeAreaChartsSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options, constant = BI.AbstractChartSetting;

        //显示组件标题
        this.showTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Chart_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //组件标题
        this.title = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.title.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //详细设置
        this.titleDetailSettting = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.titleDetailSettting.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.title, this.titleDetailSettting],
            hgap: constant.SIMPLE_H_GAP
        });

        var widgetTitle = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.vertical_adapt",
                items: [this.showTitle]
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetTitle]
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            }),
            hgap: constant.SIMPLE_H_GAP
        });


        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //风格——1、2、3
        this.chartStyleGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AXIS_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: constant.BUTTON_WIDTH,
                height: constant.BUTTON_HEIGHT,
                iconWidth: constant.ICON_WIDTH,
                iconHeight: constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.chartStyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        this.tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [100],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Chart"),
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Color_Setting"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.colorSelect]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartStyleGroup]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Widget_Background_Colour"),
                    cls: "line-title",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.widgetBackground]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        //格式和数量级
        this.numberLevellY = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevellY.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //单位
        this.LYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.LYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        this.lYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.lYAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //千分符
        this.separators = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.separators.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleLY.setVisible(true) : self.editTitleLY.setVisible(false);
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        this.editTitleLY = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.editTitleLY.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //显示标签
        this.showLValueAxisLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 90
        });

        this.showLValueAxisLabel.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //左值轴标签设置
        this.lValueAxisLabelSetting = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.lValueAxisLabelSetting.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function() {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //坐直轴线颜色
        this.lValueAxisLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.lValueAxisLineColor.on(BI.ColorChooser.EVENT_CHANGE, function() {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //左轴刻度自定义
        this.showYCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.showYCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.customYScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.customYScale.setValue({})
            }
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        this.customYScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customYScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        this.lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                text: BI.i18nText("BI-Value_Axis"),
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.numberLevellY]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.LYUnit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.lYAxisStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.separators]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleLY, this.editTitleLY]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showLValueAxisLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.lValueAxisLabelSetting]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items:[this.lValueAxisLineColor]
                },  {
                    type: "bi.vertical_adapt",
                    items: [this.showYCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customYScale]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //显示分类轴标签
        this.showCatLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showCatLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //分类轴标签
        this.catLabel = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.catLabel.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //分类轴线颜色
        this.catLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.catLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
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
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleX]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.editTitleX]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showCatLabel]
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

        //网格线设置
        this.hGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 65
        });

        this.hGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        this.vGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 65
        });

        this.vGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //空值连续
        this.continuousNullValue = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Null_Continue"),
            width: 90
        });

        this.continuousNullValue.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //数据点提示详细设置
        this.tooltipSetting = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipSetting.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        this.showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                textHeight: constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
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
                    items: [this.showZoom]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.continuousNullValue]
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
                lgap: constant.SIMPLE_H_LGAP
            }]
        });

        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            width: 170
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
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
            lhgap: 11
        });

        //极简模式
        this.minimalistModel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Minimalist_Model"),
            width: 170
        });

        this.minimalistModel.on(BI.Controller.EVENT_CHANGE, function () {
            self._invisible(!this.isSelected());
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
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
            items: [widgetTitle, this.tableStyle, this.lYAxis, this.xAxis, this.showElement, this.otherAttr, modelChange],
            hgap: 10
        })
    },

    _invisible: function (v) {
        this.tableStyle.setVisible(v);
        this.lYAxis.setVisible(v);
        this.xAxis.setVisible(v);
        this.showElement.setVisible(v);
        this.otherAttr.setVisible(v);
    },

    populate: function () {
        var wId = this.options.wId;

        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId);
        var titleX = BI.Utils.getWSXAxisTitleByID(wId);
        if (titleLY === "") {
            BI.any(view[BICst.REGION.TARGET1], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleLY = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        if (titleX === "") {
            BI.any(view[BICst.REGION.DIMENSION1], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleX = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        this.showTitle.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.title.setValue(BI.Utils.getWidgetNameByID(wId));
        this.titleDetailSettting.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));
        this.widgetBackground.setValue(BI.Utils.getWSWidgetBGByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartStyleGroup.setValue(BI.Utils.getWSChartStyleByID(wId));
        this.lYAxisStyle.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.LYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.isShowTitleLY.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleLY.setValue(titleLY);
        this.editTitleX.setValue(titleX);
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.minimalistModel.setSelected(BI.Utils.getWSMinimalistByID(wId));
        this._invisible(!BI.Utils.getWSMinimalistByID(wId));
        this.showYCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.customYScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.customYScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));

        this.separators.setSelected(BI.Utils.getWSNumberSeparatorsByID(wId));

        this.isShowTitleLY.isSelected() ? this.editTitleLY.setVisible(true) : this.editTitleLY.setVisible(false);
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
    },

    getValue: function () {
        return {
            show_name: this.showTitle.isSelected(),
            widget_title: this.title.getValue(),
            title_detail: this.titleDetailSettting.getValue(),
            widget_bg: this.widgetBackground.getValue(),
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            chart_style: this.chartStyleGroup.getValue()[0],
            left_y_axis_style: this.lYAxisStyle.getValue()[0],
            right_y_axis_style: this.lYAxisStyle.getValue()[0],
            left_y_axis_number_level: this.numberLevellY.getValue()[0],
            right_y_axis_number_level: this.numberLevellY.getValue()[0],
            left_y_axis_unit: this.LYUnit.getValue(),
            right_y_axis_unit: this.LYUnit.getValue(),
            show_left_y_axis_title: this.isShowTitleLY.isSelected(),
            show_right_y_axis_title: this.isShowTitleLY.isSelected(),
            show_x_axis_title: this.isShowTitleX.isSelected(),
            left_y_axis_title: this.editTitleLY.getValue(),
            right_y_axis_title: this.editTitleLY.getValue(),
            x_axis_title: this.editTitleX.getValue(),
            show_data_label: this.showDataLabel.isSelected(),
            minimalist_model: this.minimalistModel.isSelected(),
            show_y_custom_scale: this.showYCustomScale.isSelected(),
            custom_y_scale: this.customYScale.getValue(),
            num_separators: this.separators.isSelected()
        }
    },

    setValue: function (v) {
        this.showTitle.setSelected(v.show_name);
        this.title.setValue(v.widget_title);
        this.titleDetailSettting.setValue(v.title_detail);
        this.widgetBackground.setValue(v.widget_bg);
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.chartStyleGroup.setValue(v.chart_style);
        this.lYAxisStyle.setValue(v.left_y_axis_style);
        this.numberLevellY.setValue(v.left_y_axis_number_level);
        this.LYUnit.setValue(v.left_y_axis_unit);
        this.isShowTitleLY.setSelected(v.show_left_y_axis_title);
        this.isShowTitleX.setSelected(v.x_axis_title);
        this.editTitleLY.setValue(v.left_y_axis_title);
        this.editTitleX.setValue(v.x_axis_title);
        this.showDataLabel.setSelected(v.show_data_label);
        this.minimalistModel.setSelected(v.minimalist_model);
        this.showYCustomScale.setSelected(v.show_y_custom_scale);
        this.customYScale.setValue(v.custom_y_scale);
        this.separators.setSelected(v.num_separators);
    }
});
BI.RangeAreaChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.range_area_chart_setting", BI.RangeAreaChartsSetting);
