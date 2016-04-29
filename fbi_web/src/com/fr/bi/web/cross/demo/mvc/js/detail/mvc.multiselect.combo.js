MultiSelectComboView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(MultiSelectComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multi-select-combo bi-mvc-layout"
        })
    },

    _init: function () {
        MultiSelectComboView.superclass._init.apply(this, arguments);
    },

    _createMultiSelectCombo: function () {

        var self = this;
        var widget = BI.createWidget({
            type: 'bi.multi_select_combo',
            itemsCreator: BI.bind(this._itemsCreator, this),
            width: 200
        });

        widget.on(BI.MultiSelectCombo.EVENT_CONFIRM, function () {
            BI.Msg.toast(JSON.stringify(this.getValue()));
        });

        return widget;
    },

    _getItemsByTimes: function (items, times) {
        var res = [];
        for (var i = (times - 1) * 3; items[i] && i < times * 3; i++) {
            res.push(items[i]);
        }
        return res;
    },

    _hasNextByTimes: function (items, times) {
        return times * 3 < items.length;
    },

    _itemsCreator: function (options, callback) {
        var self = this;
        var items = ITEMS;
        if (options.keyword) {
            var search = BI.Func.getSearchResult(items, options.keyword);
            items = search.matched.concat(search.finded);
        }
        if (options.selected_values) {//过滤
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
        BI.delay(function () {
            callback({
                items: self._getItemsByTimes(items, options.times),
                hasNext: self._hasNextByTimes(items, options.times)
            });
        }, 1000);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: 'bi.absolute',
            scrolly: false,
            element: vessel,
            items: [{
                el: this._createMultiSelectCombo(),
                right: 10,
                top: 10
            }]
        })
    }
});

MultiSelectComboModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(MultiSelectComboModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        MultiSelectComboModel.superclass._init.apply(this, arguments);
    }
});