/**
 * @class BI.CompareAreaChartsSetting
 * @extends BI.Widget
 * 对比面积样式
 */
BI.CompareAreaChartsSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.CompareAreaChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-compare-area-chart-setting"
        })
    },

    _init: function () {
        BI.CompareAreaChartsSetting.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //组件标题
        this.title = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.title.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //详细设置
        this.titleDetailSettting = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.titleDetailSettting.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //风格——1、2
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.chartTypeGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AREA_CHART_STYLE_GROUP, {
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
        this.chartTypeGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Chart"),
                lgap: constant.SIMPLE_H_LGAP,
                textHeight: constant.SINGLE_LINE_HEIGHT,
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
                    items: [this.colorSelect],
                },/* {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names",
                    lgap: constant.SIMPLE_H_GAP
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartStyleGroup],
                    lgap: constant.SIMPLE_H_GAP
                },*/ {
                    type: "bi.label",
                    text: BI.i18nText("BI-Type"),
                    cls: "attr-names",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartTypeGroup],
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.lYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.lYAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //千分符
        this.YSeparators = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.YSeparators.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function () {
            self.editTitleLY.setVisible(this.isSelected());
            self.LYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.editTitleLY = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.editTitleLY.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.LYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.LYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function() {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //左轴标签
        this.showLeftLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showLeftLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.leftLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.leftLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //左轴线颜色
        this.leftLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.leftLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.customYScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customYScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                text: BI.i18nText("BI-Positive_Value_Axis"),
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
                    items: [this.YSeparators]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleLY, this.editTitleLY]
                },  {
                    type: "bi.vertical_adapt",
                    items: [this.LYTitleStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showLeftLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftLineColor]
                }, {
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

        this.numberLevelrY = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevelrY.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.RYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.RYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.rYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rYAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.XSeparators = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.XSeparators.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleRY.on(BI.Controller.EVENT_CHANGE, function () {
            self.editTitleRY.setVisible(this.isSelected());
            self.RYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.editTitleRY = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleRY.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.RYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.RYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //右轴标签
        this.showRightLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showRightLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.rightLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.rightLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //逆轴刻度自定义
        this.showXCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.showXCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.customXScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.customXScale.setValue({})
            }
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.customXScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customXScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //右轴线颜色
        this.rightLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.rYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                text: BI.i18nText("BI-Negative_Value_Axis"),
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
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
                    items: [this.numberLevelrY]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.RYUnit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rYAxisStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.XSeparators]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleRY, this.editTitleRY]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.RYTitleStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showRightLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showXCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customXScale]
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
            self.editTitleX.setVisible(this.isSelected());
            self.catTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        this.catTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.catTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //显示分类轴标签
        this.showCatLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showCatLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.catLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //分类轴标签
        this.catLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.catLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //分类轴线颜色
        this.catLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.catLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.xAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Category_Axis"),
                lgap: constant.SIMPLE_H_LGAP,
                textHeight: constant.SINGLE_LINE_HEIGHT,
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
                    items: [this.catLabelStyle]
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

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: constant.LEGEND_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //图例详细设置
        this.legendSetting = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendSetting.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //显示横向网格线
        this.showHGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 60
        });

        this.showHGridLine.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //显示纵向网格线
        this.showVGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 60
        });

        this.showVGridLine.on(BI.Controller.EVENT_CHANGE, function() {
            self.vGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.vGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
        });

        //空值连续
        this.nullContinuity = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Null_Continue"),
            width: 90
        });

        this.nullContinuity.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        //数据点提示详细设置
        this.tooltipSetting = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipSetting.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
        });

        this.showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
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
                    items: [this.showHGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.hGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showVGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.vGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.nullContinuity]
                }/*, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Tooltip"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.tooltipSetting]
                }*/], {
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CompareAreaChartsSetting.EVENT_CHANGE)
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
            items: [widgetTitle, this.tableStyle, this.lYAxis, this.rYAxis, this.xAxis, this.showElement, this.otherAttr, modelChange],
            hgap: 10
        })
    },

    _invisible: function (v) {
        this.tableStyle.setVisible(v);
        this.lYAxis.setVisible(v);
        this.rYAxis.setVisible(v);
        this.xAxis.setVisible(v);
        this.showElement.setVisible(v);
        this.otherAttr.setVisible(v);
    },

    populate: function () {
        var wId = this.options.wId;

        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId);
        var titleX = BI.Utils.getWSXAxisTitleByID(wId);
        var titleRY = BI.Utils.getWSRightYAxisTitleByID(wId);
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
        if (titleRY === "") {
            BI.any(view[BICst.REGION.TARGET2], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleRY = BI.Utils.getDimensionNameByID(dId);
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
        this.chartTypeGroup.setValue(BI.Utils.getWSChartLineTypeByID(wId));
        this.lYAxisStyle.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.rYAxisStyle.setValue(BI.Utils.getWSRightYAxisStyleByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.numberLevelrY.setValue(BI.Utils.getWSRightYAxisNumLevelByID(wId));
        this.LYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.RYUnit.setValue(BI.Utils.getWSRightYAxisUnitByID(wId));
        this.isShowTitleLY.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.isShowTitleRY.setSelected(BI.Utils.getWSShowRightYAxisTitleByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleLY.setValue(titleLY);
        this.editTitleRY.setValue(titleRY);
        this.editTitleX.setValue(titleX);
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.showDataTable.setSelected(BI.Utils.getWSShowDataTableByID(wId));
        this.gridLine.setSelected(BI.Utils.getWSShowGridLineByID(wId));
        this.showZoom.setSelected(BI.Utils.getWSShowZoomByID(wId));
        this.minimalistModel.setSelected(BI.Utils.getWSMinimalistByID(wId));
        this._invisible(!BI.Utils.getWSMinimalistByID(wId));
        this.showYCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.customYScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.customYScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
        this.showXCustomScale.setSelected(BI.Utils.getWSShowXCustomScale(wId));
        this.customXScale.setValue(BI.Utils.getWSCustomXScale(wId));
        this.customXScale.setVisible(BI.Utils.getWSShowXCustomScale(wId));
        this.YSeparators.setSelected(BI.Utils.getWSNumberSeparatorsByID(wId));
        this.XSeparators.setSelected(BI.Utils.getWSRightNumberSeparatorsByID(wId));
        this.showLeftLabel.setSelected(BI.Utils.getWSShowLValueAxisLabelByID(wId));
        this.leftLabelStyle.setValue(BI.Utils.getWSLValueAxisLabelSettingByID(wId));
        this.leftLineColor.setValue(BI.Utils.getWSLValueAxisLineColorByID(wId));
        this.showRightLabel.setSelected(BI.Utils.getWSShowRValueAxisLabelByID(wId));
        this.rightLabelStyle.setValue(BI.Utils.getWSRValueAxisLabelSettingByID(wId));
        this.rightLineColor.setValue(BI.Utils.getWSRValueAxisLineColorByID(wId));
        this.showCatLabel.setSelected(BI.Utils.getWSShowCatLabelByID(wId));
        this.catLabelStyle.setValue(BI.Utils.getWSCatLabelStyleByID(wId));
        this.catLineColor.setValue(BI.Utils.getWSCatLineColorByID(wId));
        this.legendSetting.setValue(BI.Utils.getWSLegendSettingByID(wId));
        this.showHGridLine.setSelected(BI.Utils.getWSShowHGridLineByID(wId));
        this.hGridLineColor.setValue(BI.Utils.getWSHGridLineColorByID(wId));
        this.showVGridLine.setSelected(BI.Utils.getWSShowVGridLineByID(wId));
        this.vGridLineColor.setValue(BI.Utils.getWSVGridLineColorByID(wId));
        this.tooltipSetting.setValue(BI.Utils.getWSToolTipSettingByID(wId));
        this.nullContinuity.setSelected(BI.Utils.getWSNullContinueByID(wId));
        this.LYTitleStyle.setValue(BI.Utils.getWSLeftTitleStyleByID(wId));
        this.RYTitleStyle.setValue(BI.Utils.getWSRightTitleStyleByID(wId));
        this.catTitleStyle.setValue(BI.Utils.getWSCatTitleStyleByID(wId));

        this.editTitleLY.setVisible(this.isShowTitleLY.isSelected());
        this.editTitleRY.setVisible(this.isShowTitleRY.isSelected());
        this.editTitleX.setVisible(this.isShowTitleX.isSelected());
        this.LYTitleStyle.setVisible(this.isShowTitleLY.isSelected());
        this.RYTitleStyle.setVisible(this.isShowTitleRY.isSelected());
        this.catTitleStyle.setVisible(this.isShowTitleX.isSelected());
        this.leftLabelStyle.setVisible(this.showLeftLabel.isSelected());
        this.rightLabelStyle.setVisible(this.showRightLabel.isSelected());
        this.catLabelStyle.setVisible(this.showCatLabel.isSelected());
        this.hGridLineColor.setVisible(this.showHGridLine.isSelected());
        this.vGridLineColor.setVisible(this.showVGridLine.isSelected())
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
            chart_line_type: this.chartTypeGroup.getValue()[0],
            left_y_axis_style: this.lYAxisStyle.getValue()[0],
            right_y_axis_style: this.rYAxisStyle.getValue()[0],
            left_y_axis_number_level: this.numberLevellY.getValue()[0],
            right_y_axis_number_level: this.numberLevelrY.getValue()[0],
            left_y_axis_unit: this.LYUnit.getValue(),
            right_y_axis_unit: this.RYUnit.getValue(),
            show_left_y_axis_title: this.isShowTitleLY.isSelected(),
            show_right_y_axis_title: this.isShowTitleRY.isSelected(),
            show_x_axis_title: this.isShowTitleX.isSelected(),
            left_y_axis_title: this.editTitleLY.getValue(),
            right_y_axis_title: this.editTitleRY.getValue(),
            x_axis_title: this.editTitleX.getValue(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_data_table: this.showDataTable.isSelected(),
            show_grid_line: this.gridLine.isSelected(),
            show_zoom: this.showZoom.isSelected(),
            minimalist_model: this.minimalistModel.isSelected(),
            show_y_custom_scale: this.showYCustomScale.isSelected(),
            custom_y_scale: this.customYScale.getValue(),
            show_x_custom_scale: this.showXCustomScale.isSelected(),
            custom_x_scale: this.customXScale.getValue(),
            num_separators: this.YSeparators.isSelected(),
            right_num_separators: this.XSeparators.isSelected(),
            show_left_label: this.showLeftLabel.isSelected(),
            left_label_style: this.leftLabelStyle.getValue(),
            left_line_color: this.leftLineColor.getValue(),
            show_right_label: this.showRightLabel.isSelected(),
            right_label_style: this.rightLabelStyle.getValue(),
            right_line_color: this.rightLineColor.getValue(),
            show_cat_label: this.showCatLabel.isSelected(),
            cat_label_style: this.catLabelStyle.getValue(),
            cat_line_color: this.catLineColor.getValue(),
            chart_legend_setting: this.legendSetting.getValue(),
            show_h_grid_line: this.showHGridLine.isSelected(),
            h_grid_line_color: this.hGridLineColor.getValue(),
            show_v_grid_line: this.showVGridLine.isSelected(),
            v_grid_line_color: this.vGridLineColor.getValue(),
            tooltip_setting: this.tooltipSetting.getValue(),
            null_continue: this.nullContinuity.isSelected(),
            left_title_style: this.LYTitleStyle.getValue(),
            right_title_style: this.RYTitleStyle.getValue(),
            cat_title_style: this.catTitleStyle.getValue()
        }
    }
});
BI.CompareAreaChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.compare_area_chart_setting", BI.CompareAreaChartsSetting);
