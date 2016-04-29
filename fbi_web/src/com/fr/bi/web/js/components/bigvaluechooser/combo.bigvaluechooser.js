/**
 * 复杂的复选下拉框控件, 适用于数据量大的情况
 *
 * @class BI.BigValueChooserCombo
 * @extends BI.Widget
 */
BI.BigValueChooserCombo = BI.inherit(BI.Widget, {

    _const: {
        perPage: 10
    },
    _defaultConfig: function () {
        return BI.extend(BI.BigValueChooserCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-big-value-chooser-combo",
            height: 30,
            itemsCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.BigValueChooserCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.cache = {};
        this.combo = BI.createWidget({
            type: 'bi.multi_select_combo',
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this),
            width: o.width,
            height: o.height
        });

        this.combo.on(BI.MultiSelectCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BigValueChooserCombo.EVENT_CONFIRM);
        });
    },

    _itemsCreator: function (options, callback) {
        var self = this, o = this.options;
        var str = "";
        str += options.keyword || "";
        str += BI.isNull(options.type) ? "" : options.type;
        str += options.times || "";
        BI.each(options.selected_values, function (i, v) {
            str += v.type + v.value;
        });
        var md5 = BI.MD5.hex_md5(str);
        if (BI.isNotNull(this.cache[md5])) {
            callback(this.cache[md5]);
            return;
        }
        return o.itemsCreator(options, function (res) {
            self.cache[md5] = res;
            callback(res);
        });
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    },

    populate: function () {
        this.combo.populate();
    }
});
BI.BigValueChooserCombo.EVENT_CONFIRM = "BigValueChooserCombo.EVENT_CONFIRM";
$.shortcut('bi.big_value_chooser_combo', BI.BigValueChooserCombo);