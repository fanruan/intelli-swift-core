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
        var layer = BI.Layers.create(BICst.GLOBAL_STYLE_LAYER);
        this.globalStyleSetting = BI.createWidget({
            type: "bi.global_style_setting"
        });
        BI.Layers.show(BICst.GLOBAL_STYLE_LAYER);
        this.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CANCEL, function () {
            BI.Layers.hide(BICst.GLOBAL_STYLE_LAYER);
            self.fireEvent(BI.GlobalStyle.EVENT_CANCEL);
        });
        this.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyle.EVENT_PREVIEW);
        });
        this.globalStyleSetting.on(BI.GlobalStyleSetting.EVENT_SAVE, function () {
            BI.Layers.hide(BICst.GLOBAL_STYLE_LAYER);
            self.fireEvent(BI.GlobalStyle.EVENT_SAVE);
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
                el: this.globalStyleSetting,
                top: 40,
                left: 140,
                bottom: 10
            }]
        })
    },

    getDefaultStyle:function () {
        return this.globalStyleSetting.getDefaultStyle();
    },

    getValue: function () {
        return this.globalStyleSetting.getValue();
    },

    populate: function () {
        BI.Layers.show(BICst.GLOBAL_STYLE_LAYER);
        this.globalStyleSetting.populate();
    }
});
BI.GlobalStyle.EVENT_PREVIEW = "EVENT_PREVIEW";
BI.GlobalStyle.EVENT_SAVE = "EVENT_SAVE";
BI.GlobalStyle.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.global_style", BI.GlobalStyle);
