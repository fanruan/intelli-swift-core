/**
 * Created by fay on 2016/9/18.
 */
BI.TreeLabelView = BI.inherit(BI.Widget, {
    _constant: {
        LIST_LABEL_HEIGHT: 40,
        DEFAULT_LEFT_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.TreeLabelView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-tree-label-view",
            titleWidth: 55
        })
    },

    _init: function () {
        BI.TreeLabelView.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.container = BI.createWidget();
        this.items = [];
        this.title = BI.createWidget();
        this._initView();

    },

    _initView: function () {
        var self = this, o = this.options;
        o.itemsCreator({}, function (v) {
            var items = v.items || [], titles = BI.isEmptyArray(o.titles) ? v.titles || [] : o.titles;
            BI.each(items, function (idx, item) {
                var temp = BI.createWidget({
                    type: "bi.list_label",
                    items: item,
                    showTitle: false
                });
                temp.on(BI.ListLabel.EVENT_CHANGE, function (value, id) {
                    self._changeView({
                        floor: idx,
                        value: value,
                        id: id,
                        type: 1
                    });
                    self.fireEvent(BI.TreeLabelView.EVENT_CHANGE, arguments);
                });
                self.items.push(temp);
            });
            self.title = BI.createWidget({
                type: "bi.button_group",
                items: BI.createItems(titles, {
                    type: "bi.label",
                    height: self._constant.LIST_LABEL_HEIGHT,
                    width: o.titleWidth
                }),
                height: self._constant.LIST_LABEL_HEIGHT * titles.length,
                layouts: [{
                    type: "bi.vertical"
                }]
            });
            BI.createWidget({
                type: "bi.vertical",
                element: self.container,
                items: self.items
            });
            self.right = BI.createWidget({
                type: "bi.button_group",
                cls: "list-label-group",
                items: [self.container],
                height: self._constant.LIST_LABEL_HEIGHT * self.items.length,
                layouts: [{
                    type: "bi.horizontal"
                }]
            });
            BI.createWidget({
                type: "bi.absolute",
                items: [{
                    el: self.title,
                    left:0,
                    right:0,
                    top:0,
                    bottom:0,
                    width: 55
                }, {
                    el: self.right,
                    left: 60,
                    right:0,
                    top:0,
                    bottom:0
                }],
                element: self.element
            })
        })
    },

    _changeView: function (op) {
        if (op.floor === this.items.length - 1) {
            return;
        }
        var options = {};
        options.id = op.id;
        options.type = op.type;
        options.floor = op.floor;
        options.value = op.value;
        options.selectedValues = this.getValue();
        this.options.itemsCreator(options, BI.bind(this._updateView, this));
    },

    _updateView: function (items, floor) {
        var self = this;
        var updateList = this.items.slice(floor + 1);
        var values = items.slice(floor + 1);
        BI.each(updateList, function (idx, listLabel) {
            if (BI.isNull(values[idx])) {
                return;
            }
            var value = listLabel.getValue();
            listLabel.populate({
                items: values[idx]
            });
            listLabel.setValue(value);
        });
    },

    refreshView: function (data) {
        if(data.titles) {
            this.setTitles(data.titles);
        }
        if(data.items) {
            this.setItems(data.items);
        }
    },

    getSelectedButtons: function () {
        var result = [];
        BI.each(this.items, function (idx, item) {
            result.push(item.getSelectedButtons());
        });
        return result;
    },

    getAllButtons: function () {
        var result = [];
        BI.each(this.items, function (idx, item) {
            result.push(item.getAllButtons().slice(1))
        });
        return result;
    },

    getValue: function () {
        var result = [];
        BI.each(this.items, function (idx, item) {
            result.push(item.getValue());
        });
        return result;
    },

    setItems: function (items) {
        var self =this;
        var length = this.right.getAllButtons().length;
        var deletes = [];
        for(var i=0;i < length;i++) {
            deletes.push(i);
        }
        this.right.removeItemAt(deletes);
        self.items = [];
        BI.each(items, function (idx, values) {
            var temp = BI.createWidget({
                type: "bi.list_label",
                items: values,
                showTitle: false
            });
            temp.on(BI.ListLabel.EVENT_CHANGE, function (value, id) {
                self._changeView({
                    floor: idx,
                    value: value,
                    id: id,
                    type: 1
                });
                self.fireEvent(BI.TreeLabelView.EVENT_CHANGE, arguments);
            });
            self.items.push(temp);
        });
        var temp = BI.createWidget({
            type: "bi.vertical",
            items: self.items
        });
        this.right.addItems([temp]);
        this.right.setHeight(self.items.length * this._constant.LIST_LABEL_HEIGHT);
    },

    setTitles: function (titles) {
        var length = this.title.getAllButtons().length;
        var deletes = [];
        for(var i=0;i < length;i++) {
            deletes.push(i);
        }
        this.title.removeItemAt(deletes);
        this.title.addItems(BI.createItems(titles, {
            type: "bi.label",
            height: this._constant.LIST_LABEL_HEIGHT,
            width: this.options.titleWidth
        }));
        this.title.setHeight(titles.length * this._constant.LIST_LABEL_HEIGHT);
    },

    changeValue: function (v) {
        BI.each(this.items, function (idx, item) {
            item.changeValue(v[idx] || []);
        });
    },

    setValue: function (v) {
        var value = [];
        BI.each(this.items, function (idx, item) {
            if (BI.isNotEmptyArray(v[idx])) {
                item.setValue(v[idx] || []);
            }
            if (BI.isEmptyArray(v[idx]) || BI.isNull(v[idx])) {
                value.push(["_*_"]);
            } else {
                var temp = [];
                // 排除错误的设置的值
                BI.each(item.items, function (idx, itemValue) {
                    temp.push(itemValue.value)
                });
                var valueTemp = BI.intersection(v[idx], temp);
                if (BI.isEmptyArray(valueTemp)) {
                    valueTemp = ["_*_"];
                }
                value.push(valueTemp);
            }
        });
    }
});
BI.TreeLabelView.EVENT_CHANGE = "BI.TreeLabelView.EVENT_CHANGE";
$.shortcut('bi.tree_label_view', BI.TreeLabelView);