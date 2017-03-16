/**
 * Created by zcf on 2016/8/29.
 */
BI.GlobalStyleIndexAlignChooser = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyleIndexAlignChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-index-align-chooser",
            width: 40,
            height: 20
        });
    },

    _init: function () {
        BI.GlobalStyleIndexAlignChooser.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button_group = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: BI.createItems([{
                cls: "align-chooser-button text-align-left-font",
                title: BI.i18nText("BI-Position_Left"),
                selected: true,
                value: "left"
            }, {
                cls: "align-chooser-button text-align-center-font",
                title: BI.i18nText("BI-Position_Center"),
                value: "center"
            }], {
                type: "bi.icon_button",
                height: o.height
            }),
            layouts: [{
                type: "bi.center"
            }]
        });
        this.button_group.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.button_group.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexAlignChooser.EVENT_CHANGE, arguments);
        });
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    },

    getValue: function () {
        return this.button_group.getValue()[0];
    }
});
BI.GlobalStyleIndexAlignChooser.EVENT_CHANGE = "BI.GlobalStyleIndexAlignChooser.EVENT_CHANGE";
$.shortcut('bi.global_style_index_align_chooser', BI.GlobalStyleIndexAlignChooser);