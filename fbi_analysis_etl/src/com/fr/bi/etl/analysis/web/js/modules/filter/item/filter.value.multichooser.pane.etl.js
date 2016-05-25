BI.ETLMultiValueChooserPane = BI.inherit(BI.Single, {
    _constants :{
        GAP : 5,
        SEARCHER_HEIGHT : 30,
        SELECTOR_HEIGHT : 100
    },

    _defaultConfig: function () {
        return BI.extend(BI.ETLMultiValueChooserPane.superclass._defaultConfig.apply(this, arguments), {
            itemsCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.ETLMultiValueChooserPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.storeValue = {};
        this.pane = BI.createWidget({
            type: 'bi.multi_select_loader',
            itemsCreator: BI.bind(this._itemsCreator, this),
            height : self._constants.SELECTOR_HEIGHT
        });
        this.pane.on(BI.MultiSelectLoader.EVENT_CHANGE, function () {
            self.setValue(self.pane.getValue());
            self.fireEvent(BI.ETLMultiValueChooserPane.EVENT_CONFIRM);
        })
        this.searcher = BI.createWidget({
            type: "bi.search_editor",
            height : self._constants.SEARCHER_HEIGHT
        });
        this.searcher.on(BI.SearchEditor.EVENT_CHANGE, function () {
            self.pane.populate();
        })
        this.searcher.on(BI.SearchEditor.EVENT_REMOVE, function () {
            self.pane.populate();
        })
        this.searcher.on(BI.SearchEditor.EVENT_EMPTY, function () {
            self.pane.populate();
        })
        this.searcher.on(BI.SearchEditor.EVENT_CLEAR, function () {
            self.pane.populate();
        })
        return BI.createWidget({
            type: "bi.vertical",
            scrolly : false,
            element : this.element,
            bgap : self._constants.GAP,
            items:[self.searcher, self.pane]
        })
    },

    _itemsCreator: function (opts, callback) {
        var self = this, o = this.options;
        if (!this.items) {
            o.fieldValuesCreator(function (items) {
                self.items = BI.map(items.value, function (i, v) {
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
            var keyword = self.searcher.getValue();
            if (BI.isNotNull(keyword)) {
                var search = BI.Func.getSearchResult(items, keyword);
                items = search.matched.concat(search.finded);
            }
            var values =  self.storeValue.value;
            if (BI.isNotNull(values)) {
                var filter = BI.makeObject(values, true);
                items = BI.filter(items, function (i, ob) {
                    return !filter[ob.value];
                });
            }
            callback({
                items : items
            })
        }
    },
    
    populate : function () {
        this.pane.populate();
    },

    getValue: function () {
        return this.storeValue;
    },

    setValue : function (v) {
        this.storeValue = v;
        this.pane.setValue(v)
    }
});


BI.ETLMultiValueChooserPane.EVENT_CONFIRM = "EVENT_CONFIRM";
$.shortcut('bi.multi_value_chooser_pane_etl', BI.ETLMultiValueChooserPane);