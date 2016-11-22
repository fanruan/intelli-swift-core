/**
 * @class BI.DashboardChartSetting
 * @extends BI.Widget
 * 仪表盘样式
 */
BI.DashboardChartSetting = BI.inherit(BI.AbstractChartSetting, {

    _constant: {
        SIMPLE_H_GAP2: 20,
        RADIO_WIDTH: 100,
        POINTER_SEGMENT_WIDTH: 150,
        PERCENTAGE_SEGMENT_WIDTH: 160
    },

    _defaultConfig: function () {
        return BI.extend(BI.DashboardChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-dashboard-chart-setting"
        })
    },

    _init: function () {
        BI.DashboardChartSetting.superclass._init.apply(this, arguments);
        var self = this, constant = BI.AbstractChartSetting;

        //显示组件标题
        this.showName = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Chart_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showName.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.widgetName, this.widgetNameStyle],
            hgap: constant.SIMPLE_H_GAP
        });

        var widgetTitle = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.vertical_adapt",
                items: [this.showName]
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetTitle]
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            }),
            hgap: constant.SIMPLE_H_GAP
        });

        this.dashboardChartType = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.DASHBOARD_CHART_STYLE_GROUP, {
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
        this.dashboardChartType.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._showPointer(v);
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
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
                    text: BI.i18nText("BI-Type"),
                    cls: "attr-names",
                    lgap: this._constant.SIMPLE_H_GAP2
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.dashboardChartType],
                    lgap: constant.SIMPLE_H_GAP
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Widget_Background_Colour"),
                    cls: "attr-names",
                    lgap: constant.SIMPLE_H_GAP
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.widgetBG],
                    lgap: constant.SIMPLE_H_GAP
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                })
            }]
        });

        //数量级和单位
        this.leftYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.leftYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        this.leftYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.leftYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        this.leftYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.leftYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        //千分符
        this.leftYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.leftYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        //minScale
        this.minScale = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Default_Data"),
            validationChecker: function (v) {
                return self.maxScale.getValue() === '' ? true :
                    (BI.parseFloat(v) > 0 && BI.parseFloat(v) < BI.parseFloat(self.maxScale.getValue()))
            }
        });

        this.minScale.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE)
        });

        //maxScale
        this.maxScale = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Default_Data"),
            validationChecker: function (v) {
                return self.minScale.getValue() === '' ? true :
                    BI.parseFloat(v) > BI.parseFloat(self.minScale.getValue())
            }
        });

        this.maxScale.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE)
        });

        this.styleRadio = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.CHART_SCALE_SETTING, {
                type: "bi.single_select_radio_item",
                width: this._constant.RADIO_WIDTH,
                height: constant.BUTTON_HEIGHT
            }),
            layouts: [{
                type: "bi.horizontal_adapt",
                height: constant.BUTTON_HEIGHT
            }]
        });

        this.styleRadio.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._doClickButton(v);
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE)
        });

        //添加条件button
        this.addConditionButton = BI.createWidget({
            type: "bi.button",
            text: "+" + BI.i18nText("BI-Add_Condition"),
            height: constant.BUTTON_HEIGHT
        });

        this.addConditionButton.on(BI.Button.EVENT_CHANGE, function () {
            self.dashboardStyles.addItem();
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        this.dashboardStyles = BI.createWidget({
            type: "bi.chart_add_condition_group"
        });

        this.dashboardStyles.on(BI.ChartAddConditionGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        //percent
        this.showPercentage = BI.createWidget({
            type: "bi.segment",
            height: 28,
            width: this._constant.PERCENTAGE_SEGMENT_WIDTH,
            items: BICst.PERCENTAGE_SHOW
        });

        this.showPercentage.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE)
        });

        var labelPercentage = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Percentage"),
            height: constant.BUTTON_HEIGHT,
            cls: "attr-names"
        });

        this.textPercentage = BI.createWidget({
            type: "bi.left",
            items: [labelPercentage, this.showPercentage],
            lgap: constant.SIMPLE_H_GAP
        });

        //单指针，多指针
        this.dashboardPointer = BI.createWidget({
            type: "bi.segment",
            width: this._constant.POINTER_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.POINTERS
        });

        this.dashboardPointer.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent((BI.DashboardChartSetting.EVENT_CHANGE));
        });

        this.dashboardScale = BI.createWidget({
            type: "bi.left",
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Min_Scale"),
                lgap: constant.SIMPLE_H_GAP,
                cls: "attr-names"
            }, {
                type: "bi.vertical_adapt",
                items: [this.minScale]
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Max_Scale"),
                cls: "attr-names"
            }, {
                type: "bi.vertical_adapt",
                items: [this.maxScale]
            }, {
                type: "bi.vertical_adapt",
                items: [this.textPercentage]
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            }),
            lgap: constant.SIMPLE_H_GAP
        });

        var lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                text: BI.i18nText("BI-Dashboard"),
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Number_of_pointers"),
                    textAlign: "left",
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.dashboardPointer]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYNumberLevel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYUnit]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYSeparator]
                }, this.dashboardScale, {
                    type: "bi.vertical_adapt",
                    items: [this.styleRadio]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.addConditionButton]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.dashboardStyles],
                    width: "100%",
                    height: ""
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }],
        });

        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            width: 170
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
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
            items: [widgetTitle, tableStyle, lYAxis, otherAttr],
            hgap: 10
        });

    },

    _doClickButton: function (v) {
        switch (v) {
            case BICst.SCALE_SETTING.AUTO:
                this.addConditionButton.setVisible(false);
                this.dashboardStyles.setVisible(false);
                break;
            case BICst.SCALE_SETTING.CUSTOM:
                this.addConditionButton.setVisible(true);
                this.dashboardStyles.setVisible(true);
                break;
        }
    },

    _showPointer: function (pictureType) {
        switch (pictureType) {
            case BICst.CHART_SHAPE.NORMAL:
            case BICst.CHART_SHAPE.HALF_DASHBOARD:
                this.dashboardPointer.setVisible(true);
                this.textPercentage.setVisible(false);
                break;
            case BICst.CHART_SHAPE.PERCENT_DASHBOARD:
            case BICst.CHART_SHAPE.PERCENT_SCALE_SLOT:
            case BICst.CHART_SHAPE.VERTICAL_TUBE:
            case BICst.CHART_SHAPE.HORIZONTAL_TUBE:
                this.dashboardPointer.setVisible(false);
                this.textPercentage.setVisible(true);
                break;
        }
    },

    populate: function () {
        var wId = this.options.wId;
        this.showName.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setValue(BI.Utils.getWidgetNameByID(wId));
        this.widgetNameStyle.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));

        this.widgetBG.setValue(BI.Utils.getWSWidgetBGByID(wId));
        this.dashboardChartType.setValue(BI.Utils.getWSDashboardChartTypeByID(wId));
        this.dashboardPointer.setValue(BI.Utils.getWSChartDashboardPointerByID(wId));
        this._showPointer(BI.Utils.getWSDashboardChartTypeByID(wId));
        this.styleRadio.setValue(BI.Utils.getWSChartStyleRadioByID(wId));
        this._doClickButton(BI.Utils.getWSChartStyleRadioByID(wId));
        this.dashboardStyles.setValue(BI.Utils.getWSChartDashboardStylesByID(wId));
        this.minScale.setValue(BI.Utils.getWSChartMinScaleByID(wId));
        this.maxScale.setValue(BI.Utils.getWSChartMaxScaleByID(wId));
        this.showPercentage.setValue(BI.Utils.getWSChartShowPercentageByID(wId));

        this.leftYNumberLevel.setValue(BI.Utils.getWSChartLeftYNumberLevelByID(wId));
        this.leftYUnit.setValue(BI.Utils.getWSDashboardUnitByID(wId));
        this.leftYSeparator.setSelected(BI.Utils.getWSLeftYNumberSeparatorByID(wId));

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),

            widgetBG: this.widgetBG.getValue(),
            dashboardChartType: this.dashboardChartType.getValue()[0],
            dashboardPointer: this.dashboardPointer.getValue()[0],
            styleRadio: this.styleRadio.getValue()[0],
            dashboardStyles: this.dashboardStyles.getValue(),
            minScale: this.minScale.getValue(),
            maxScale: this.maxScale.getValue(),
            showPercentage: this.showPercentage.getValue()[0],

            leftYNumberLevel: this.leftYNumberLevel.getValue()[0],
            leftYUnit: this.leftYUnit.getValue(),
            leftYSeparator: this.leftYSeparator.isSelected(),

            transferFilter: this.transferFilter.isSelected(),
        }
    }
});
BI.DashboardChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dashboard_chart_setting", BI.DashboardChartSetting);
