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
        this.manager = new BI.StyleSetManager;
        var globalStyleButton = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-global-style-font",
            element: this.element,
            height: 30,
            text: BI.i18nText("BI-Global_Style"),
            width: 90
        });
        globalStyleButton.on(BI.Button.EVENT_CHANGE, function () {
            self._createGlobalStylePane();
        });
    },

    _createGlobalStylePane: function () {
        var self = this;
        var cacheGS = {};
        if (BI.isNull(this.globalStyleSetting)) {
            var layer = BI.Layers.create(BICst.GLOBAL_STYLE_LAYER);
            this.globalStyleSetting = BI.createWidget({
                type: "bi.global_style_setting"
            });
            this.globalStyleSetting.populate();
            BI.Layers.show(BICst.GLOBAL_STYLE_LAYER);
            cacheGS = BI.Utils.getGlobalStyle();

            this.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CANCEL, function () {
                BI.Layers.hide(BICst.GLOBAL_STYLE_LAYER);
                self.fireEvent(BI.GlobalStyle.EVENT_SET, cacheGS);
                self.populate();
            });
            this.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CHANGE, function () {
                var gs = self.getValue();
                self.fireEvent(BI.GlobalStyle.EVENT_SET, gs);
                self.populate();
            });
            this.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_SAVE, function () {
                BI.Layers.hide(BICst.GLOBAL_STYLE_LAYER);
                var gs = this.getValue();
                cacheGS = gs;
                self.fireEvent(BI.GlobalStyle.EVENT_SET, gs);
            });
            this.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CHART, function () {
                BI.Utils.broadcastAllWidgets2Refresh(true);
            });
            BI.createWidget({
                type: "bi.absolute",
                element: layer,
                items: [{
                    el: this.globalStyleSetting,
                    top: 40,
                    left: 140,
                    bottom: 10
                }]
            });
        } else {
            BI.Layers.show(BICst.GLOBAL_STYLE_LAYER);
            this.globalStyleSetting.populate();
        }
    },

    getValue: function () {
        return this.globalStyleSetting.getValue();
    },

    populate: function () {
        var globalStyle = BI.Utils.getGlobalStyle();
        this.manager.setThemeStyle(globalStyle);
        this.manager.setGlobalStyle(globalStyle);
    }
});
BI.GlobalStyle.EVENT_SET = "EVENT_SET";
BI.GlobalStyle.EVENT_CHART_CHANGE = "EVENT_CHART";
// BI.GlobalStyle.EVENT_PREVIEW = "EVENT_PREVIEW";
// BI.GlobalStyle.EVENT_SAVE = "EVENT_SAVE";
// BI.GlobalStyle.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.global_style", BI.GlobalStyle);
