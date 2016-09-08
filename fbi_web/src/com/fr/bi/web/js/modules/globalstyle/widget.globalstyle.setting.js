/**
 * Created by Young's on 2016/8/24.
 */
BI.GlobalStyleSetting = BI.inherit(BI.Widget, {
    _const: {

        DEFAULTSTYLE: {
            "mainBackground": {"type": 1, "value": "#f3f3f3"},
            "widgetBackground": {"type": 1, "value": "#ffffff"},
            "titleBackground": {"type": 1, "value": "#ffffff"},
            "titleFont": {"font-weight": "normal", "font-style": "normal", "text-align": "left", "color": "#ffffff"},
            "chartStyle": 1,
            "chartColor": ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"],
            "chartFont": {"font-weight": "normal", "font-style": "normal", "color": "#b2b2b2"},
            "controlTheme": "#f3f3f3"
        },

        PREDICTIONSTYLEONE: {
            "mainBackground": {"type": 1, "value": "#212338"},
            "widgetBackground": {"type": 1, "value": "#2b2d4a"},
            "titleBackground": {"type": 1, "value": "#2b2d3a"},
            "titleFont": {"font-weight": "bold", "font-style": "normal", "text-align": "left", "color": "#ffffff"},
            "chartStyle": 1,
            "chartColor": ["#79d2f4", "#55b5e5", "#25cdea", "#1ba8ed", "#537af4"],
            "chartFont": {"font-weight": "normal", "font-style": "normal", "color": "#b2b2b2"},
            "controlTheme": "#25cdea"
        },

        PREDICTIONSTYLETWO: {
            "mainBackground": {"type": 1, "value": "#dae0e0"},
            "widgetBackground": {"type": 1, "value": "#f7f7f7"},
            "titleBackground": {"type": 1, "value": "#5e6472"},
            "titleFont": {"font-weight": "bold", "font-style": "italic", "text-align": "left", "color": "#ffffff"},
            "chartStyle": 1,
            "chartColor": ["#f4ab98", "#f1c15f", "#e18169", "#af7e7e", "#6f6870"],
            "chartFont": {"font-weight": "normal", "font-style": "normal", "color": "#5e6472"},
            "controlTheme": "#af7e7e"
        }
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
            height: 30,
            width: 90
        });
        cancel.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CANCEL);
        });

        var save = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sure"),
            height: 30,
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
            if (value["currentStyle"] == 0) {
                self._setCenterValue(self._const.DEFAULTSTYLE)
            }
            if (value["currentStyle"] == 1) {
                self._setCenterValue(self._const.PREDICTIONSTYLEONE)
            }
            if (value["currentStyle"] == 2) {
                self._setCenterValue(self._const.PREDICTIONSTYLETWO)
            }
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        this.predictionStyle.on(BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE, function (direction) {
            self.predictionStyle.pageChange(direction);
        });
        this.predictionStyle.on(BI.GlobalStyleIndexPredictionStyle.CUSTOM_SELECT, function (button) {
            self._setCenterValue(button.getValue());
        });
        this.predictionStyle.on(BI.GlobalStyleIndexPredictionStyle.CUSTOM_DELETE, function (button) {
            self.predictionStyle.deleteCustomButton(button);
            if (self.predictionStyle.getCustomNumber() < 5) {
                self.textButton.setEnable(true);
            }
        });
        this._initCenter();

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            width: 420,
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Global_Style"),
                    height: 40,
                    lgap: 20,
                    textAlign: "left",
                    cls: "global-style-title"
                },
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
        this.populate();
    },

    _initCenter: function () {
        var self = this;

        //保存
        this.textButton = BI.createWidget({
            type: "bi.text_button",
            //cls:"item-save",
            text: BI.i18nText("BI-Save_As_Prediction_Style"),
            textAlign: "right",
            height: 30,
            width: 100
        });
        this.textButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self._saveButton();
        });
        var saveLabel = BI.createWidget({
            type: "bi.right",
            cls: "item-save",
            items: [
                this.textButton, {
                    type: "bi.label",
                    height: 30
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
            cls: "bi-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "item-label",
                text: BI.i18nText("BI-Background_Colour") + ":",
                textAlign: "left",
                height: 30,
                width:95
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
            cls: "border"
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
                extraCls: "chart-style-font",
                width: BI.AbstractChartSetting.BUTTON_WIDTH,
                height: BI.AbstractChartSetting.BUTTON_HEIGHT,
                iconWidth: BI.AbstractChartSetting.ICON_WIDTH,
                iconHeight: BI.AbstractChartSetting.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: 30
            }]
        });
        this.chartStyle.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var chartStyleWrapper = this._createWrapper(BI.i18nText("BI-Chart_Style"), this.chartStyle);

        //图表配色
        this.chartColour = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            cls: "border",
            height: 30
        });
        this.chartColour.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        this.chartColour.populate();
        var chartColourWrapper = this._createWrapper(BI.i18nText("BI-Chart_Colour"), this.chartColour);

        //图表文字
        this.chartWordStyle = BI.createWidget({
            type: "bi.global_style_index_chart_tool_bar",
            cls: "border"
        });
        this.chartWordStyle.on(BI.GlobalStyleIndexChartToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var chartWordWrapper = BI.createWidget({
            type: "bi.left",
            cls: "bi-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "item-label",
                text: BI.i18nText("BI-Chart_Word_Style") + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, this.chartWordStyle],
            vgap: 10
        });
        //控件主题
        this.controlTheme = BI.createWidget({
            type: "bi.color_chooser",
            height: 30,
            width: 160
        });
        this.controlTheme.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE)
        });
        var controlThemeWrapper = BI.createWidget({
            type: "bi.left",
            cls: "bi-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "item-label",
                text: BI.i18nText("BI-Control_Theme") + ":",
                textAlign: "left",
                height: 30,
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
                chartWordWrapper,
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
                cls: "item-label",
                text: name + ":",
                textAlign: "left",
                height: 30,
                width: 95
            }, widget],
            vgap: 10
        }
    },

    _createWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls: "item-label",
                text: name + ":",
                textAlign: "left",
                height: 30,
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

    getDefaultStyle:function () {
        return this._const.DEFAULTSTYLE
    },
    populate: function () {
       var v = BI.Utils.getGlobalStyle();
       if (BI.isNotNull(v.predictionValue) && BI.isNotNull(v.chartColor) && BI.isNotNull(v.controlTheme)) {
            this.setValue(BI.Utils.getGlobalStyle());
       } else {
           this._setCenterValue(this._const.DEFAULTSTYLE);
           this.predictionStyle.setValue({
               "currentStyle": 0
           });
       }
    }
});
BI.GlobalStyleSetting.EVENT_CHANGE = "EVENT_CHANGE";
BI.GlobalStyleSetting.EVENT_CANCEL = "EVENT_CANCEL";
BI.GlobalStyleSetting.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.global_style_setting", BI.GlobalStyleSetting);