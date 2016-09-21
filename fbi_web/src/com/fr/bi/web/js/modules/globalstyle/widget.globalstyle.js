/**
 * 全局样式
 * Created by Young's on 2016/8/24.
 */
BI.GlobalStyle = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style"
        })
    },

    _init: function () {
        BI.GlobalStyle.superclass._init.apply(this, arguments);
        var self = this;
        var globalStyleButton = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-global-style-font",
            element: this.element,
            height: 30,
            text: BI.i18nText("BI-Global_Style"),
            width: 90
        });
        globalStyleButton.on(BI.Button.EVENT_CHANGE, function () {
            var cacheGS = {};
            if (BI.isNull(self.globalStyleSetting)) {
                var layer = BI.Layers.create(BICst.GLOBAL_STYLE_LAYER);
                self.globalStyleSetting = BI.createWidget({
                    type: "bi.global_style_setting"
                });
                BI.Layers.show(BICst.GLOBAL_STYLE_LAYER);
                var v = BI.Utils.getGlobalStyle();
                if (BI.isNull(v.predictionValue)) {
                    self.fireEvent(BI.GlobalStyle.EVENT_SET, BICst.GLOBALPREDICTIONSTYLE.DEFAULT)
                }
                cacheGS = BI.Utils.getGlobalStyle();

                self.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CANCEL, function () {
                    BI.Layers.hide(BICst.GLOBAL_STYLE_LAYER);
                    self.fireEvent(BI.GlobalStyle.EVENT_SET, cacheGS);
                });
                self.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CHANGE, function () {
                    var gs = self.getValue();
                    self.fireEvent(BI.GlobalStyle.EVENT_SET, gs);
                });
                self.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_SAVE, function () {
                    BI.Layers.hide(BICst.GLOBAL_STYLE_LAYER);
                    var gs = this.getValue();
                    cacheGS = gs;
                    self.fireEvent(BI.GlobalStyle.EVENT_SET, gs);
                });
                self.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CHART, function () {
                    self.fireEvent(BI.GlobalStyle.EVENT_CHART_CHANGE);
                });
                BI.createWidget({
                    type: "bi.absolute",
                    element: layer,
                    cls: "bi-global-style",
                    items: [{
                        el: {
                            type: "bi.default",
                            cls: "global-style-mask"
                        },
                        top: 0,
                        right: 0,
                        bottom: 0,
                        left: 0
                    }, {
                        el: self.globalStyleSetting,
                        top: 40,
                        left: 140,
                        bottom: 10
                    }]
                });
            } else {
                BI.Layers.show(BICst.GLOBAL_STYLE_LAYER);
                self.globalStyleSetting.populate();
            }
        });
    },

    getValue: function () {
        return this.globalStyleSetting.getValue();
    },

    populate: function (gs) {
        var globalStyle = gs || BI.Utils.getGlobalStyle();
        var manager = new BI.StyleSetManager;
        manager.setThemeStyle(globalStyle);
        manager.setGlobalStyle("globalstyle", globalStyle);
    }
});
BI.GlobalStyle.EVENT_SET = "EVENT_SET";
BI.GlobalStyle.EVENT_CHART_CHANGE = "EVENT_CHART";
// BI.GlobalStyle.EVENT_PREVIEW = "EVENT_PREVIEW";
// BI.GlobalStyle.EVENT_SAVE = "EVENT_SAVE";
// BI.GlobalStyle.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.global_style", BI.GlobalStyle);
