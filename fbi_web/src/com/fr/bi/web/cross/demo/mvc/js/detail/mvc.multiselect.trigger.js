MultiSelectTriggerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultiSelectTriggerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multi-select-trigger bi-mvc-layout"
        })
    },

    _init: function () {
        MultiSelectTriggerView.superclass._init.apply(this, arguments);
    },

    _createTrigger: function(){
        var trigger = BI.createWidget({
            type: "bi.multi_select_trigger",
            itemsCreator: BI.bind(this._itemsCreator, this),
            adapter: this.adapter,
            width: 200
        });
        trigger.setValue({
            state: BI.Selection.All,
            counter: 4
        })
        return trigger;
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
        if(options.keyword){
            var search = BI.Func.getSearchResult(items, options.keyword);
            items = search.matched.concat(search.finded);
        }
        if (options.selected_values) {//过滤
            var filter = BI.makeObject(options.selected_values, true);
            items = BI.filter(items, function (i, ob) {
                return !filter[ob.value];
            });
        }
        if(options.type == BI.MultiSelectCombo.REQ_GET_ALL_DATA){
            callback({
                items: items
            });
            return;
        }
        callback({
            items: this._getItemsByTimes(items, options.times),
            hasNext: this._hasNextByTimes(items, options.times)
        });
    },

    _render: function (vessel) {
        this.adapter = BI.createWidget({
            type: "bi.label",
            cls: "layout-bg-gray",
            text: "测试面板"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.adapter,
                left: 20,
                top: 80,
                width: 200,
                height: 300
            }]
        })
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.multi_select_check_selected_button"
            }, {
                type: "bi.multi_select_check_selected_switcher",
                adapter: this.adapter,
                popup: {
                    items: BI.createItems(ITEMS, {
                        type: "bi.multi_select_item",
                        heigth: 25
                    })
                }
            }, {
                type: "bi.right",
                items: [{
                    type: "bi.multi_select_editor",
                    cls: "mvc-border",
                    width: 200
                }]
            }, {
                type: "bi.right",
                items: [this._createTrigger()]
            }],
            hgap: 50,
            vgap: 20
        })
    }
});

MultiSelectTriggerModel = BI.inherit(BI.Model, {});