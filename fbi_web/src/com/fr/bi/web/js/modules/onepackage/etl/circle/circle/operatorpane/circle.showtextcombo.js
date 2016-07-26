/**
 * 自循环列显示值复选Combo
 *
 * @class BI.CircleShowTextCombo
 * @extends BI.Widget
 */
BI.CircleShowTextCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CircleShowTextCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-circle-show-text-combo",
            items: []
        });
    },

    _init: function () {
        BI.CircleShowTextCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: 'bi.all_value_chooser_combo',
            element: this.element,
            itemsCreator: function (op, callback) {
                callback(o.items);
            }
        });

        this.combo.on(BI.AllValueChooserCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.CircleShowTextCombo.EVENT_CONFIRM);
        });
    },

    //_itemsCreator: function (options, callback) {
    //    var self = this;
    //    var items = this.options.items;
    //    if (BI.isNotNull(options.keyword)) {
    //        var search = BI.Func.getSearchResult(items, options.keyword);
    //        items = search.matched.concat(search.finded);
    //    }
    //    if (BI.isNotNull(options.selected_values)) {//过滤
    //        var filter = BI.makeObject(options.selected_values, true);
    //        items = BI.filter(items, function (i, ob) {
    //            return !filter[ob.value];
    //        });
    //    }
    //    if (options.type == BI.MultiSelectCombo.REQ_GET_DATA_LENGTH) {
    //        callback({count: items.length});
    //        return;
    //    }
    //    callback({
    //        items: items
    //    });
    //},

    populate: function (items) {
        this.options.items = items;
    },

    setValue: function (v) {
        if (BI.isNull(v)) {
            this.combo.setValue();
            return;
        }
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.CircleShowTextCombo.EVENT_CONFIRM = "CircleShowTextCombo.EVENT_CONFIRM";
$.shortcut('bi.circle_show_text_combo', BI.CircleShowTextCombo);