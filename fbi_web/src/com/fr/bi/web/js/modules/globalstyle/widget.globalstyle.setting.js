/**
 * Created by Young's on 2016/8/24.
 */
BI.GlobalStyleSetting = BI.inherit(BI.Widget, {
    _const: {
        HEIGHT: 30
    },

    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-setting"
        })
    },

    _init: function () {
        BI.GlobalStyleSetting.superclass._init.apply(this, arguments);

        var self = this;
        var cancel = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: this._const.HEIGHT,
            width: 90
        });
        cancel.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CANCEL);
        });

        var save = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sure"),
            height: this._const.HEIGHT,
            width: 90
        });
        save.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_SAVE);
        });

        //预设样式
        this.predictionStyle = BI.createWidget({
            type: "bi.global_style_index_prediction_style"
        });
        this.predictionStyle.on(BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE, function () {
            var value = this.getValue();
            self._setCenterValue(value["currentStyle"]);
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHART);
        });
        this.predictionStyle.on(BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE, function (direction) {
            self.predictionStyle.pageChange(direction);
        });
        this.predictionStyle.on(BI.GlobalStyleIndexPredictionStyle.CUSTOM_SELECT, function (button) {
            self._setCenterValue(button.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHART);
        });
        this.predictionStyle.on(BI.GlobalStyleIndexPredictionStyle.CUSTOM_DELETE, function (button) {
            self.predictionStyle.deleteCustomButton(button);
            if (self.predictionStyle.getCustomNumber() < 5) {
                self.textButton.setEnable(true);
            }
        });
        this._initCenter();

        var header = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Global_Style"),
            height: 40,
            lgap: 20,
            textAlign: "left",
            cls: "global-style-title"
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            width: 420,
            items: [{
                el: header,
                height: 40
            }, {
                el: this.predictionStyle,
                height: 190
            }, {
                el: this.centerItems
            }, {
                el: {
                    type: "bi.left_right_vertical_adapt",
                    items: {
                        left: [cancel],
                        right: [save]
                    },
                    llgap: 20,
                    rrgap: 20,
                    height: 60
                },
                height: 60
            }]
        });
        this.element.draggable({
            handle: header.element,
            axis: "x"
        });

        this.populate();
    },

    _initCenter: function () {
        var self = this;

        //保存
        this.textButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Save_As_Prediction_Style"),
            warningTitle: BI.i18nText("BI-Number_Limit"),
            textAlign: "right",
            height: this._const.HEIGHT,
            width: 100
        });
        this.textButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self._saveButton();
        });
        var saveLabel = BI.createWidget({
            type: "bi.right",
            cls: "global-style-item-save",
            items: [
                this.textButton, {
                    type: "bi.label",
                    height: this._const.HEIGHT
                }]
        });

        //界面背景
        this.mainBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.mainBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var backgroundColourWrapper = BI.createWidget({
            type: "bi.left",
            cls: "global-style-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText("BI-Background_Colour") + ":",
                textAlign: "left",
                height: this._const.HEIGHT,
                width: 105
            }, self.mainBackground],
            vgap: 10
        });

        //组件背景
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var widgetBackgroundColourWrapper = this._createComboWrapper(BI.i18nText("BI-Widget_Background_Colour"), this.widgetBackground);

        //标题栏
        this.titleColour = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.titleColour.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var titleColourWrapper = this._createComboWrapper(BI.i18nText("BI-Title_Colour"), this.titleColour);

        //标题文字
        this.titleWordStyle = BI.createWidget({
            type: "bi.global_style_index_title_tool_bar",
            cls: "global-style-border"
        });
        this.titleWordStyle.on(BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var titleWordStyleWrapper = this._createWrapper(BI.i18nText("BI-Title_Word_Style"), this.titleWordStyle);

        //图表风格
        this.chartStyle = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AXIS_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "global-style-chart-style-font",
                width: BI.AbstractChartSetting.BUTTON_WIDTH,
                height: BI.AbstractChartSetting.BUTTON_HEIGHT,
                iconWidth: BI.AbstractChartSetting.ICON_WIDTH,
                iconHeight: BI.AbstractChartSetting.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: this._const.HEIGHT
            }]
        });
        this.chartStyle.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHART);
        });
        var chartStyleWrapper = this._createWrapper(BI.i18nText("BI-Chart_Style"), this.chartStyle);

        //图表配色
        this.chartColour = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            height: this._const.HEIGHT
        });
        this.chartColour.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHART);
        });
        this.chartColour.populate();
        //var chartColourWrapper = this._createWrapper(BI.i18nText("BI-Chart_Colour"), this.chartColour);//取消屏蔽后还要改回来
        var chartColourWrapper=BI.createWidget({
            type:"bi.left",
            cls: "global-style-wrapper-bottom",
            items:[{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText("BI-Chart_Colour") + ":",
                textAlign: "left",
                height: this._const.HEIGHT,
                width: 110
            },this.chartColour],
            vgap: 10
        });

        //图表文字
        this.chartWordStyle = BI.createWidget({
            type: "bi.global_style_index_chart_tool_bar",
            cls: "global-style-border"
        });
        this.chartWordStyle.on(BI.GlobalStyleIndexChartToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var chartWordWrapper = BI.createWidget({
            type: "bi.left",
            cls: "global-style-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText("BI-Chart_Word_Style") + ":",
                textAlign: "left",
                height: this._const.HEIGHT,
                width: 110
            }, this.chartWordStyle],
            vgap: 10
        });
        //控件主题
        this.controlTheme = BI.createWidget({
            type: "bi.color_chooser",
            height: this._const.HEIGHT,
            width: 160
        });
        this.controlTheme.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE)
        });
        var controlThemeWrapper = BI.createWidget({
            type: "bi.left",
            cls: "global-style-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText("BI-Control_Theme") + ":",
                textAlign: "left",
                height: this._const.HEIGHT,
                width: 110
            }, this.controlTheme],
            vgap: 10
        });
        //中间所有元素
        this.centerItems = BI.createWidget({
            type: "bi.vertical",
            items: [
                saveLabel,
                backgroundColourWrapper,
                widgetBackgroundColourWrapper,
                titleColourWrapper,
                titleWordStyleWrapper,
                chartStyleWrapper,
                chartColourWrapper,
                //chartWordWrapper,//不能用暂时屏蔽掉
                controlThemeWrapper
            ],
            hgap: 20
        });
    },

    _saveButton: function () {
        this.predictionStyle.addUserCustomButton(this._getCenterValue());
        if (this.predictionStyle.getCustomNumber() == 5) {
            this.textButton.setEnable(false);
        }
    },

    _createComboWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: name + ":",
                textAlign: "left",
                height: this._const.HEIGHT,
                width: 105
            }, widget],
            vgap: 10
        }
    },

    _createWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: name + ":",
                textAlign: "left",
                height: this._const.HEIGHT,
                width: 110
            }, widget],
            vgap: 10
        }
    },

    _getCenterValue: function () {
        return {
            "mainBackground": this.mainBackground.getValue(),
            "widgetBackground": this.widgetBackground.getValue(),
            "titleBackground": this.titleColour.getValue(),
            "titleFont": this.titleWordStyle.getValue(),
            "chartStyle": this.chartStyle.getValue()[0],
            "chartColor": this.chartColour.getValue()[0],
            "chartFont": this.chartWordStyle.getValue(),
            "controlTheme": this.controlTheme.getValue()
        }
    },

    getValue: function () {
        var result = this._getCenterValue();
        result.predictionValue = this.predictionStyle.getValue();
        return result;
    },

    _setCenterValue: function (v) {
        this.mainBackground.setValue(v.mainBackground);
        this.widgetBackground.setValue(v.widgetBackground);
        this.titleColour.setValue(v.titleBackground);
        this.titleWordStyle.setValue(v.titleFont);
        this.chartStyle.setValue(v.chartStyle);
        this.chartColour.setValue(v.chartColor);
        this.chartWordStyle.setValue(v.chartFont);
        this.controlTheme.setValue(v.controlTheme);
    },

    setValue: function (v) {
        this.predictionStyle.setValue(v.predictionValue);
        this._setCenterValue(v);
    },

    populate: function () {
        var v = BI.Utils.getGlobalStyle();
        if (BI.isNotNull(v.predictionValue) && BI.isNotNull(v.chartColor) && BI.isNotNull(v.controlTheme)) {
            this.setValue(BI.Utils.getGlobalStyle());
        } else {
            this._setCenterValue(BICst.GLOBALPREDICTIONSTYLE.DEFAULT);
            this.predictionStyle.setValue({
                "currentStyle": BICst.GLOBALPREDICTIONSTYLE.DEFAULT
            });
        }
    }
});
BI.GlobalStyleSetting.EVENT_CHART = "EVENT_CHART";
BI.GlobalStyleSetting.EVENT_CHANGE = "EVENT_CHANGE";
BI.GlobalStyleSetting.EVENT_CANCEL = "EVENT_CANCEL";
BI.GlobalStyleSetting.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.global_style_setting", BI.GlobalStyleSetting);