/**
 * BI风格
 *
 * Created by GUY on 2016/7/6.
 * @class FS.StyleSetting
 * @extends BI.Widget
 */
FS.StyleSetting = BI.inherit(FR.Widget, {

    _defaultConfig: function () {
        return BI.extend(FS.StyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fs-style-setting"
        });
    },

    _init: function () {
        FS.StyleSetting.superclass._init.apply(this, arguments);

        var mainBackground = this._createMainBackground();
        var widgetBackground = this._createWidgetBackground();
        var titleColour = this._createWidgetTitleColor();
        var titleWordStyle = this._createTitleWordStyle();
        var style = this._createStyle();
        var color = this._createColor();
        var chartWordStyle = this._createChartWordStyle();
        var controlTheme = this._createControlTheme();
        var preview = this._createPreview();

        BI.createWidget({
            type: "bi.vertical",
            element: this.options.renderEl,
            items: [
                mainBackground,
                widgetBackground,
                titleColour,
                titleWordStyle,
                style,
                color,
                chartWordStyle,
                controlTheme,
                preview]
        });
    },

    _createComboWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
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
                cls: "global-style-item-label",
                text: name + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, widget],
            vgap: 10
        }
    },

    _createControlTheme: function(){
        var self = this;
        this.controlTheme = BI.createWidget({
            type: "bi.color_chooser",
            height: 30,
            width: 160
        });
        this.controlTheme.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });
        return BI.createWidget({
            type: "bi.left",
            cls: "global-style-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText("BI-Control_Theme") + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, this.controlTheme],
            vgap: 10
        });
    },

    _createChartWordStyle: function(){
        var self = this;
        this.chartWordStyle = BI.createWidget({
            type: "bi.global_style_index_chart_tool_bar",
            cls: "global-style-border"
        });
        this.chartWordStyle.on(BI.GlobalStyleIndexChartToolBar.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });
        return BI.createWidget({
            type: "bi.left",
            cls: "global-style-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText("BI-Chart_Word_Style") + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, this.chartWordStyle],
            vgap: 10
        });
    },

    _createTitleWordStyle: function(){
        var self = this;
        this.titleWordStyle = BI.createWidget({
            type: "bi.global_style_index_title_tool_bar",
            cls: "global-style-border"
        });
        this.titleWordStyle.on(BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });
        return this._createWrapper(BI.i18nText("BI-Title_Word_Style"), this.titleWordStyle);
    },

    _createWidgetTitleColor: function(){
        var self = this;
        this.titleColour = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.titleColour.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });
        return this._createComboWrapper(BI.i18nText("BI-Title_Colour"), this.titleColour);
    },

    _createWidgetBackground: function(){
        var self = this;
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });
        return this._createComboWrapper(BI.i18nText("BI-Widget_Background_Colour"), this.widgetBackground);
    },

    _createMainBackground: function(){
        var self = this;
        this.mainBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.mainBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });

        return BI.createWidget({
            type: "bi.left",
            cls: "global-style-wrapper-bottom",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText("BI-Background_Colour") + ":",
                textAlign: "left",
                height: 30,
                width: 105
            }, self.mainBackground],
            vgap: 10
        });
    },

    _createStyle: function () {
        var self = this;

        this.style = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems([{
                cls: "axis-chart-style-normal-icon style-setting-style-button",
                title: BI.i18nText("BI-Common"),
                value: BICst.CHART_STYLE.STYLE_NORMAL
            }, {
                cls: "axis-chart-style-gradual-icon style-setting-style-button",
                title: BI.i18nText("BI-Top_Down_Shade"),
                value: BICst.CHART_STYLE.STYLE_GRADUAL
            }], {
                type: "bi.icon_button",
                width: 40,
                height: 30,
                iconWidth: 24,
                iconHeight: 24
            }),
            layouts: [{
                type: "bi.vertical_adapt"
            }]
        });
        this.style.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });
        //this.style = BI.createWidget({
        //    type: "bi.text_value_combo",
        //    cls: "style-setting-combo",
        //    height: 25,
        //    text: BI.i18nText('BI-Common'),
        //    items: [{
        //        text: BI.i18nText('BI-Common'), value: BICst.CHART_STYLE.STYLE_NORMAL
        //    }, {
        //        text: BI.i18nText('BI-Top_Down_Shade'), value: BICst.CHART_STYLE.STYLE_GRADUAL
        //    }]
        //});
        //this.style.on(BI.TextValueCombo.EVENT_CHANGE, function () {
        //    self._save();
        //    self._preview();
        //});
        this.style.setValue(BICst.CHART_STYLE.STYLE_NORMAL);

        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText('BI-Total_Style') + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, this.style],
            vgap: 10
        }
    },

    _createColor: function () {
        var self = this;
        var label = BI.createWidget({
            type: "bi.label",
            height: 25,
            textAlign: "left",
            hgap: 10,
            text: BI.i18nText('BI-Chart_Color')
        });

        this.color = BI.createWidget({
            type: "bi.text_value_combo",
            cls: "style-setting-combo",
            width: 200,
            height: 25
        });
        this.color.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self._save();
            self._preview();
        });

        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText('BI-Chart_Color') + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, this.color],
            vgap: 10
        }
    },

    _createPreview: function () {
        this.preview = BI.createWidget({
            type: "fs.chart_preview",
            width: 600,
            height: 400
        });

        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                cls: "global-style-item-label",
                text: BI.i18nText('BI-Pre_View') + ":",
                textAlign: "left",
                height: 30,
                width: 110
            }, this.preview],
            vgap: 10
        }
    },

    _preview: function () {
        this.preview.populate(this.data);
    },

    _save: function () {
        var chartStyle = this.style.getValue()[0] || BICst.CHART_STYLE.STYLE_NORMAL;
        var defaultColor = this.color.getValue()[0];
        var mainBackground = this.mainBackground.getValue();
        var widgetBackground = this.widgetBackground.getValue();
        var titleBackground = this.titleColour.getValue();
        var titleFont = this.titleWordStyle.getValue();
        var chartFont = this.chartWordStyle.getValue();
        var controlTheme = this.controlTheme.getValue();

        this.data.chartStyle = chartStyle;
        this.data.defaultColor = defaultColor;
        this.data.mainBackground = mainBackground;
        this.data.widgetBackground = widgetBackground;
        this.data.titleBackground = titleBackground;
        this.data.titleFont = titleFont;
        this.data.chartFont = chartFont;
        this.data.controlTheme = controlTheme;

        BI.requestAsync('fr_bi_base', 'set_config_setting', {
                chartStyle: chartStyle,
                defaultColor: defaultColor,
                mainBackground: mainBackground,
                widgetBackground: widgetBackground,
                titleBackground: titleBackground,
                titleFont: titleFont,
                chartFont: chartFont,
                controlTheme: controlTheme
            },
            function (res) {
                FR.Msg.toast(BI.i18nText("FS-Generic-Simple_Successfully"));
            }
        );
    },

    populate: function () {
        this.data = Data.BufferPool.getDefaultChartConfig();
        this.color.populate(this.data.styleList);
        this.style.setValue(this.data.chartStyle || 0);
        this.mainBackground.setValue(this.data.mainBackground);
        this.widgetBackground.setValue(this.data.widgetBackground);
        this.titleColour.setValue(this.data.titleBackground);
        this.titleWordStyle.setValue(this.data.titleFont);
        this.chartWordStyle.setValue(this.data.chartFont);
        this.controlTheme.setValue(this.data.controlTheme);
        if (BI.isKey(this.data.defaultColor)) {
            this.color.setValue(this.data.defaultColor);
        } else if (this.data.styleList.length > 0) {
            this.color.setValue(this.data.styleList[0].value);
        }
        this._preview();
    }
});
$.shortcut('fs.style_setting', FS.StyleSetting);