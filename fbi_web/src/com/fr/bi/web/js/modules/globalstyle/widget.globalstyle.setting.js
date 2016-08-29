/**
 * Created by Young's on 2016/8/24.
 */
BI.GlobalStyleSetting = BI.inherit(BI.Widget, {
    _const: {},
    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-setting"
        })
    },

    _init: function () {
        BI.GlobalStyleSetting.superclass._init.apply(this, arguments);

        var self = this;
        this.chartConstant = BI.AbstractChartSetting;
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
        var preview = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Preview"),
            height: 30,
            width: 90
        });
        preview.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_PREVIEW);
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
            type: "bi.button",
            text: "wahaha",
            level: "common",
            height: 190
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
            },{
                el:{
                    type:"bi.vertical",
                    items:[this.predictionStyle]
                },
                height:190
            },{
                el: this.centerItems
            }, {
                el: {
                    type: "bi.left_right_vertical_adapt",
                    items: {
                        left: [cancel],
                        right: [preview, save]
                    },
                    lhgap: 20,
                    rhgap: 20,
                    height: 60
                },
                height: 60
            }]
        });
    },

    _initCenter: function () {
        var self = this;

        //保存
        var textButton = BI.createWidget({
            type: "bi.text_button",
            //cls:"item-save",
            text: BI.i18nText("BI-Save_As_Prediction_Style"),
            textAlign: "right",
            height: 30,
            width: 100
        });
        textButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self._saveTextButton();
        });
        var saveLabel = BI.createWidget({
            type: "bi.right",
            cls:"item-save",
            items: [
                textButton, {
                    type: "bi.label",
                    height: 30
                }]
        });

        //界面背景
        this.backgroundColour = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.backgroundColour.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var backgroundColourWrapper=BI.createWidget({
            type: "bi.left",
            cls: "bi-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls:"item-label",
                text: BI.i18nText("BI-Background_Colour") + ":",
                textAlign: "left",
                height: 30,
                width: 105
            }, self.backgroundColour],
            vgap: 10
        });
        //组件背景
        this.widgetBackgroundColour = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackgroundColour.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var widgetBackgroundColourWrapper = this._createComboWrapper(BI.i18nText("BI-Widget_Background_Colour"), this.widgetBackgroundColour);

        //标题栏
        this.titleColour = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.titleColour.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var titleColourWrapper = this._createComboWrapper(BI.i18nText("BI-Title_Colour"), this.titleColour);

        //标题文字
        this.titleWordStyle = BI.createWidget({
            //type: "bi.text_toolbar"
            type:"bi.global_style_index_title_tool_bar",
            cls:"border"
        });
        this.titleWordStyle.on(BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE, function () {
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var titleWordStyleWrapper = this._createWrapper(BI.i18nText("BI-Title_Word_Style"), this.titleWordStyle);

        //图表风格
        this.chartStyle = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AXIS_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: this.chartConstant.BUTTON_WIDTH,
                height: this.chartConstant.BUTTON_HEIGHT,
                iconWidth: this.chartConstant.ICON_WIDTH,
                iconHeight: this.chartConstant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: 30
            }]
        });
        this.chartStyle.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var chartStyleWrapper = this._createWrapper(BI.i18nText("BI-Chart_Style"), this.chartStyle);

        //图表配色
        this.chartColour = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            cls:"border",
            height: 30
        });
        this.chartColour.populate();
        this.chartColour.setValue(BICst.CHART_COLORS[0]["value"]);
        this.chartColour.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var chartColourWrapper = this._createWrapper(BI.i18nText("BI-Chart_Colour"), this.chartColour);

        //图表文字
        this.chartWordStyle = BI.createWidget({
            //type: "bi.text_toolbar"
            type:"bi.global_style_index_chart_tool_bar",
            cls:"border"
        });
        this.chartWordStyle.on(BI.GlobalStyleIndexChartToolBar.EVENT_CHANGE, function () {
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var chartWordWrapper=BI.createWidget({
            type: "bi.left",
            cls: "bi-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls:"item-label",
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
            self._backgroundColour(this.getValue());
            self.fireEvent(BI.GlobalStyleSetting.EVENT_CHANGE);
        });
        var controlThemeWrapper=BI.createWidget({
            type: "bi.left",
            cls: "bi-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls:"item-label",
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
            hgap:20
        });
    },

    _createComboWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls:"item-label",
                text: name + ":",
                textAlign: "left",
                height: 30,
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
                cls:"item-label",
                text: name + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, widget],
            vgap: 10
        }
    },

    _backgroundColour: function (value) {
        // console.log(value);
        // alert(value);
        // console.log(JSON.stringify(value));
        // alert(JSON.stringify(value));
    },

    _saveTextButton: function () {
        // console.log("123");
        // alert("123");
    },

    getValue: function () {
        var self = this;
        return {
            "backgroundColour": self.backgroundColour.getValue(),
            "widgetBackgroundColour": self.widgetBackgroundColour.getValue(),
            "titleColour": self.titleColour.getValue(),
            "titleWordStyle": self.titleWordStyle.getValue(),
            "chartStyle": self.chartStyle.getValue(),
            "chartColour": self.chartColour.getValue(),
            "chartWordStyle": self.chartWordStyle.getValue(),
            "controlTheme": self.controlTheme.getValue()
        }
    },

    setValue: function (v) {

    },

    populate: function (v) {

    }
});
BI.GlobalStyleSetting.EVENT_CHANGE = "EVENT_CHANGE";
BI.GlobalStyleSetting.EVENT_CANCEL = "EVENT_CANCEL";
BI.GlobalStyleSetting.EVENT_SAVE = "EVENT_SAVE";
BI.GlobalStyleSetting.EVENT_PREVIEW = "EVENT_PREVIEW";
$.shortcut("bi.global_style_setting", BI.GlobalStyleSetting);