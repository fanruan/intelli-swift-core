MultiSelectSearchPaneView = BI.inherit(BI.View, {

    _defaultConfig: function(){
        return BI.extend(MultiSelectSearchPaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multi-select-combo bi-mvc-layout"
        })
    },

    _init: function(){
        MultiSelectSearchPaneView.superclass._init.apply(this, arguments);
    },

    _createMultiSelectSearchPane:function(){

        return BI.createWidget({
            type: 'bi.multi_select_search_pane',
            cls: 'bi-search-pane',
            itemsCreator: BI.bind(this._itemsCreator, this),
            width: 200,
            height: 300
        });
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

    _render: function(vessel){
        var widget = this._createMultiSelectSearchPane();
        BI.createWidget({
            type: 'bi.vertical',
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [widget, {
                type: "bi.button",
                text: "populate",
                height: 30,
                handler: function () {
                    widget.populate();
                }
            }, {
                type: "bi.button",
                text: "setValue({type: 1, value: ['楼上偷懒', '我是短短的才怪']})",
                height: 30,
                handler: function () {
                    widget.setValue({type: 1, value: ['楼上偷懒', '我是短短的才怪']});
                }
            }, {
                type: "bi.button",
                text: "setValue({type: 2, value: ['楼上偷懒', '我是短短的才怪']})",
                height: 30,
                handler: function () {
                    widget.setValue({type: 2, value: ['楼上偷懒', '我是短短的才怪']});
                }
            }, {
                type: "bi.button",
                text: "setKeyword('w')",
                height: 30,
                handler: function () {
                    widget.setKeyword('w')
                }
            }, {
                type: "bi.button",
                text: "getValue",
                height: 30,
                handler: function () {
                    BI.Msg.alert("", JSON.stringify(widget.getValue()));
                }
            }]
        })
    }
});

MultiSelectSearchPaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(MultiSelectSearchPaneModel.superclass._defaultConfig.apply(this, arguments), {
        })
    },
    _init: function(){
        MultiSelectSearchPaneModel.superclass._init.apply(this, arguments);
    }
});