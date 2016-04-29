MultiSelectCheckPaneView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(MultiSelectCheckPaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multi-select-combo bi-mvc-layout"
        })
    },

    _init: function () {
        MultiSelectCheckPaneView.superclass._init.apply(this, arguments);
    },

    _createMultiSelectCheckPane: function () {

        var self = this;

        var checkPane = BI.createWidget({
            type: 'bi.multi_select_check_pane',
            itemsCreator: BI.bind(self._itemsCreator, self),
            height: 203,
            width: 200
        });

        return checkPane;

    },

    _getItemsByTimes: function (times) {
        var res = [];
        for (var i = (times - 1) * 3; i < times * 3; i++) {
            res.push(ITEMS[i]);
        }
        return res;
    },

    _hasNextByTimes: function (times) {
        return times * 3 < ITEMS.length;
    },

    _itemsCreator: function (options, callback) {
        var self = this;
        callback({
            items: this._getItemsByTimes(options.times),
            hasNext: this._hasNextByTimes(options.times)
        });
    },

    _render: function (vessel) {
        var self = this;
        var display = this._createMultiSelectCheckPane();
        BI.createWidget({
            type: 'bi.vertical',
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [display, {
                type: "bi.button",
                text: "populate",
                height: 30,
                handler: function () {
                    display.populate();
                }
            }]
        })
    }
});

MultiSelectCheckPaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(MultiSelectCheckPaneModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        MultiSelectCheckPaneModel.superclass._init.apply(this, arguments);
    }
});