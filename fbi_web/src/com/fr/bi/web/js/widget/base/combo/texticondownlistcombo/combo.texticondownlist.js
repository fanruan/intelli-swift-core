/**
 * @class BI.TextIconDownListCombo
 * @extend BI.Widget
 */
BI.TextIconDownListCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TextIconDownListCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-text-icon-down-list-combo",
            height: 25
        })
    },

    _init: function () {
        BI.TextIconDownListCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this._createValueMap();

        this.trigger = BI.createWidget({
            type: "bi.down_list_select_text_trigger",
            height: o.height,
            items: o.items
        });

        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            chooseType: BI.Selection.Single,
            adjustLength: 2,
            height: o.height,
            el: this.trigger,
            items: BI.deepClone(o.items)
        });

        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function () {
            self.setValue(self.combo.getValue()[0].value);
            self.fireEvent(BI.TextIconDownListCombo.EVENT_CHANGE);
        });

        this.combo.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE, function () {
            self.setValue(self.combo.getValue()[0].childValue);
            self.fireEvent(BI.TextIconDownListCombo.EVENT_CHANGE);
        });
    },

    _createValueMap: function () {
        var self = this;
        this.valueMap = {};
        BI.each(BI.flatten(this.options.items), function (idx, item) {
            if (BI.has(item, "el")) {
                BI.each(item.children, function (id, it) {
                    self.valueMap[it.value] = {value: item.el.value, childValue: it.value}
                });
            } else {
                self.valueMap[item.value] = {value: item.value};
            }
        });
    },

    setValue: function (v) {
        v = this.valueMap[v];
        this.combo.setValue([v]);
        this.trigger.setValue(v.childValue || v.value);
    },

    setEnable: function (v) {
        BI.TextIconDownListCombo.superclass.setEnable.apply(this, arguments);
        this.combo.setEnable(v);
    },

    getValue: function () {
        var v = this.combo.getValue()[0];
        return [v.childValue || v.value];
    },

    populate: function (items) {
        this.options.items = BI.flatten(items);
        this.combo.populate(items);
        this._createValueMap();
    }
});
BI.TextIconDownListCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.text_icon_down_list_combo", BI.TextIconDownListCombo);