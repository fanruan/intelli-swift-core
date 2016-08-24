/**
 * Created by Young's on 2016/8/24.
 */
BI.GlobalStyleSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.GlobalStyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-setting"
        })
    },

    _init: function() {
        BI.GlobalStyleSetting.superclass._init.apply(this, arguments);

        var self = this;
        var cancel = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: 30,
            width: 90
        });
        cancel.on(BI.Button.EVENT_CHANGE, function() {
             self.fireEvent(BI.GlobalStyleSetting.EVENT_CANCEL);
        });
        var preview = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Preview"),
            height: 30,
            width: 90
        });
        preview.on(BI.Button.EVENT_CHANGE, function() {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_PREVIEW);
        });
        var save = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sure"),
            height: 30,
            width: 90
        });
        save.on(BI.Button.EVENT_CHANGE, function() {
            self.fireEvent(BI.GlobalStyleSetting.EVENT_SAVE);
        });


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
                el: {
                    type: "bi.vertical"
                }
            }, {
                el: {
                    type: "bi.left_right_vertical_adapt",
                    items: {
                        left: [cancel],
                        right: [preview, save]
                    },
                    lhgap: 10,
                    rhgap: 10,
                    height: 60
                },
                height: 60
            }]
        });
    },
    
    populate: function() {
        
    }
});
BI.GlobalStyleSetting.EVENT_CANCEL = "EVENT_CANCEL";
BI.GlobalStyleSetting.EVENT_SAVE = "EVENT_SAVE";
BI.GlobalStyleSetting.EVENT_PREVIEW = "EVENT_PREVIEW";
$.shortcut("bi.global_style_setting", BI.GlobalStyleSetting);