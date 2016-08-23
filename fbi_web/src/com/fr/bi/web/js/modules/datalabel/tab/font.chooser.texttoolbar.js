/**
 * Created by Fay on 2016/7/14.
 */
BI.TextToolbarFontChooser = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TextToolbarFontChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-tab-text-toolbar-font-chooser",
            width: 50,
            height: 20
        });
    },

    _init: function () {
        BI.TextToolbarFontChooser.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.editor_trigger",
            height: o.height,
            triggerWidth: 12
        });
        this.trigger.on(BI.EditorTrigger.EVENT_CHANGE, function () {
            self.fireEvent(BI.TextToolbarFontChooser.EVENT_CHANGE, arguments);
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            el: this.trigger,
            adjustLength: 1,
            popup: {
                minWidth: 50,
                el: {
                    type: "bi.button_group",
                    items: BI.createItems(BICst.Font_Family_COMBO, {
                        type: "bi.single_select_item"
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });
        this.combo.on(BI.Combo.EVENT_CHANGE, function () {
            this.hideView();
            self.fireEvent(BI.TextToolbarFontChooser.EVENT_CHANGE, arguments);
        })
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.TextToolbarFontChooser.EVENT_CHANGE = "BI.TextToolbarFontChooser.EVENT_CHANGE";
$.shortcut('bi.text_toolbar_font_chooser', BI.TextToolbarFontChooser);