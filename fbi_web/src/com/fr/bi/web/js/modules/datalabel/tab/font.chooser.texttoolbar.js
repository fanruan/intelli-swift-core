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

    _items: [{
        value: "Microsoft YaHei",
        text: "微软雅黑"
    }, {
        value: "SimHei",
        text: "黑体"
    }],

    _init: function () {
        BI.TextToolbarFontChooser.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.editor_trigger",
            height: o.height,
            triggerWidth: 12,
            value: "Microsoft YaHei"
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
                    items: BI.createItems(this._items, {
                        type: "bi.single_select_item"
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });
        this.combo.on(BI.Combo.EVENT_CHANGE, function () {
            console.log(this)
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