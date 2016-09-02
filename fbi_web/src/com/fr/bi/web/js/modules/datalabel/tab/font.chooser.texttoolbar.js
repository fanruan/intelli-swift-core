/**
 * Created by Fay on 2016/7/14.
 */
BI.TextToolbarFontChooser = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TextToolbarFontChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-tab-text-toolbar-font-chooser",
        });
    },

    _init: function () {
        BI.TextToolbarFontChooser.superclass._init.apply(this, arguments);
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.text_value_combo",
            element: this.element,
            items: BICst.FONT_FAMILY_COMBO,
            height: 24
        });
        this.combo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.TextToolbarFontChooser.EVENT_CHANGE);
        })
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue()[0];
    }
});
BI.TextToolbarFontChooser.EVENT_CHANGE = "BI.TextToolbarFontChooser.EVENT_CHANGE";
$.shortcut('bi.text_toolbar_font_chooser', BI.TextToolbarFontChooser);