/**
 * 选择字段， 用于过滤等面板
 *
 * Created by GUY on 2015/10/29.
 * @class BI.ConfFilterValueChooserCombo
 * @extends BI.Widget
 */
BI.ConfFilterValueChooserCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ConfFilterValueChooserCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-field-chooser-combo",
            fieldInfo: {}
        });
    },

    _init: function () {
        BI.ConfFilterValueChooserCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: 'bi.multi_select_combo',
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this)
        });

        this.combo.on(BI.MultiSelectCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.ConfFilterValueChooserCombo.EVENT_CONFIRM);
        });
    },

    _getItemsByTimes: function (items, times) {
        var res = [];
        BI.each(BI.makeArray(100, null), function(idx, item){
            var i = (times - 1) * 100 + idx;
            if(BI.isNotNull(items[i])){
                res.push(items[i]);
            }
        });
        return res;
    },

    _hasNextByTimes: function (items, times) {
        return times * 100 < items.length;
    },

    _itemsCreator: function (options, callback) {
        var self = this, o = this.options;
        if (!this.items) {
            this.items = [];
            BI.Utils.getConfDataByField(o.table, o.fieldName, {
                type: BICst.REQ_DATA_TYPE.REQ_GET_ALL_DATA
            }, function (items) {
                BI.each(items, function (i, v) {
                    if(BI.isNotNull(v)){
                        self.items.push({
                            text: v,
                            value: v,
                            title: v
                        });
                    }
                });
                call();
            });
        } else {
            call();
        }
        function call() {
            var items = self.items;
            if (BI.isNotNull(options.keyword)) {
                var search = BI.Func.getSearchResult(items, options.keyword);
                items = search.matched.concat(search.finded);
            }
            if (BI.isNotNull(options.selected_values)) {//过滤
                var filter = BI.makeObject(options.selected_values, true);
                items = BI.filter(items, function (i, ob) {
                    return !filter[ob.value];
                });
            }
            if (options.type == BI.MultiSelectCombo.REQ_GET_ALL_DATA) {
                callback({
                    items: items
                });
                return;
            }
            if (options.type == BI.MultiSelectCombo.REQ_GET_DATA_LENGTH) {
                callback({count: items.length});
                return;
            }
            callback({
                items: self._getItemsByTimes(items, options.times),
                hasNext: self._hasNextByTimes(items, options.times)
            });
        }
    },

    setValue: function (v) {
        if(BI.isNull(v)){
            this.combo.setValue();
            return;
        }
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.ConfFilterValueChooserCombo.EVENT_CONFIRM = "ConfFilterValueChooserCombo.EVENT_CONFIRM";
$.shortcut('bi.conf_filter_value_chooser_combo', BI.ConfFilterValueChooserCombo);