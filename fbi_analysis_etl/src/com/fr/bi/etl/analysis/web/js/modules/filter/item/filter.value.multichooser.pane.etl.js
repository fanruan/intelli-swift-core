BI.ETLMultiValueChooserPane = BI.inherit(BI.Single, {
    _constants: {
        GAP: 5,
        SEARCHER_HEIGHT: 30,
        SELECTOR_HEIGHT: 100
    },

    _defaultConfig: function () {
        return BI.extend(BI.ETLMultiValueChooserPane.superclass._defaultConfig.apply(this, arguments), {
            itemsCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.ETLMultiValueChooserPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.storeValue = {type : BI.Selection.Multi};
        this.adapter = BI.createWidget({
            type: "bi.select_list",
            el: {
                type: "bi.list_pane",
                el: {
                    type: "bi.button_group",
                    chooseType: 1,
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            },
            itemsCreator: BI.bind(this._itemsCreator, this)
        });

        this.searcher = BI.createWidget({
            type: "bi.searcher",
            element: this.element,
            isAutoSearch: false,
            isAutoSync: false,
            isDefaultInit: true,
            onSearch: function (op, callback) {
                var keyword = op.keyword;
                var res = BI.Func.getSearchResult(self.items, keyword);
                callback(res.finded, res.matched, keyword);
            },
            masker: {
                offset:{
                    top: 30
                }
            }
        });

        this.searcher.on(BI.Searcher.EVENT_CHANGE, function(value, obj){
            var values = self.adapter.getValue().value;
            if (!obj.isSelected()) {
                self.adapter.setValue(BI.deepWithout(values, obj.getValue()));
            } else {
                values.push(obj.getValue());
                self.adapter.setValue(values);
            }
        });

        this.searcher.on(BI.Searcher.EVENT_SEARCHING, function(){
            this.getView().setValue(self.adapter.getValue())
        });

        BI.createWidget({
            type: "bi.vertical",
            bgap: this._constants.GAP,
            element: this.searcher,
            items: [this.adapter]
        });
    },

    _itemsCreator: function (opts, callback) {
        var self = this, o = this.options;
        if (!this.items) {
            o.fieldValuesCreator(function (items) {
                self.items = BI.map(items.value, function (i, v) {
                    return {
                        type: "bi.multi_select_item",
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
            var keyword = self.adapter.getValue();
            if (BI.isNotNull(keyword)) {
                var search = BI.Func.getSearchResult(items, keyword);
                items = search.matched.concat(search.finded);
            }
            var values = self.storeValue.value;
            if (BI.isNotNull(values)) {
                var filter = BI.makeObject(values, true);
                items = BI.filter(items, function (i, ob) {
                    return !filter[ob.value];
                });
            }
            callback(items);
        }
    },

    populate: function () {
        this.adapter.populate();
        this.searcher.getView() && this.searcher.getView().setValue(this.adapter.getValue());
    },

    getValue: function () {
        return this.storeValue;
    },

    _adjustValue : function (v) {
        var self = this;
        var map = this._makeMap(this.storeValue.value);
        var value, assist;
        if (v.type === self.storeValue.type){
            value = v.value;
            assist = v.assist;
        } else {
            value = v.assist;
            assist = v.value;
        }
        BI.each(value, function (i, v) {
            if (!map[v]) {
                map[v] = v;
            }
        });
        BI.each(assist, function (i, v) {
            delete map[v];
        });
        self.storeValue.value = BI.values(map);

    },

    _makeMap: function (values) {
        return BI.makeObject(values || []);
    },

    setValue: function (value) {
        this._adjustValue(value);
        this.searcher.setValue(this.storeValue)
    }
});


BI.ETLMultiValueChooserPane.EVENT_CONFIRM = "EVENT_CONFIRM";
$.shortcut('bi.multi_value_chooser_pane_etl', BI.ETLMultiValueChooserPane);