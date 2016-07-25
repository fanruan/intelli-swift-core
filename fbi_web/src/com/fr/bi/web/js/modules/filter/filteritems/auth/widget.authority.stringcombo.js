/**
 * Created by Young's on 2016/5/19.
 */
BI.AuthoritySelectFieldDataCombo = BI.inherit(BI.Widget, {

    _const: {
        perPage: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectFieldDataCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-select-field-data-combo",
            height: 30,
            field_id: ""
        });
    },

    _init: function () {
        BI.AuthoritySelectFieldDataCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: 'bi.multi_select_combo',
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this)
        });

        this.combo.on(BI.MultiSelectCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AuthoritySelectFieldDataCombo.EVENT_CONFIRM);
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
            BI.Utils.getConfDataByFieldId(o.field_id, {
                type: BICst.REQ_DATA_TYPE.REQ_GET_ALL_DATA
            }, function (items) {
                self.items = BI.map(items, function (i, v) {
                    return {
                        text: v,
                        value: v,
                        title: v
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
        var val = this.combo.getValue() || {};
        return {
            type: val.type,
            value: val.value
        }
    }
});
BI.AuthoritySelectFieldDataCombo.EVENT_CONFIRM = "AuthoritySelectFieldDataCombo.EVENT_CONFIRM";
$.shortcut('bi.authority_select_field_data_combo', BI.AuthoritySelectFieldDataCombo);