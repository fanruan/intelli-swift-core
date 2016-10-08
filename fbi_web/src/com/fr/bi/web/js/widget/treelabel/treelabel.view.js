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
            width: 330,
            titleWidth: 55
        })
    },

    _init: function () {
        BI.TreeLabelView.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.container = BI.createWidget();
        this.items = [];
        this._initView();
        this.titile = BI.createWidget({
            type: "bi.vertical",
            items: BI.createItems(o.titles, {
                type: "bi.label",
                height: this._constant.LIST_LABEL_HEIGHT,
                width: o.titleWidth
            }),
            height: this._constant.LIST_LABEL_HEIGHT * o.titles.length
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.container,
            items: this.items
        });
        this.right = BI.createWidget({
            type: "bi.horizontal",
            cls: "list-label-group",
            items: [this.container],
            height: this._constant.LIST_LABEL_HEIGHT * this.items.length,
            width: o.width - o.titleWidth - this._constant.DEFAULT_LEFT_GAP
        });
        BI.createWidget({
            type: "bi.horizontal",
            items: [this.titile, {
                el: this.right,
                lgap: this._constant.DEFAULT_LEFT_GAP
            }],
            element: this.element,
            width: o.width
        })
    },

    _initView: function () {
        var self = this, o = this.options;
        var op = {};
        o.itemsCreator(op, function (items) {
            BI.each(items, function (idx, item) {
                var temp = BI.createWidget({
                    type: "bi.list_label",
                    items: item,
                    showTitle: false
                });
                temp.on(BI.ListLabel.EVENT_CHANGE, function (value, id) {
                    self._changeView(idx, value, id)
                });
                self.items.push(temp);
            })
        })
    },

    _changeView: function (floor, value, id) {
        if (floor === this.items.length - 1) {
            return;
        }
        var options = {};
        options.id = id;
        options.type = 1;
        options.floor = floor;
        options.value = value;
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
            listLabel.removeAllItems();
            listLabel.addItems(values[idx]);
            listLabel.setValue(value);
        });
    },

    setValue: function (v) {
        var value = [];
        BI.each(this.items, function (idx, item) {
            if (BI.isNotEmptyArray(v[idx])) {
                item.setValue(v[idx] || []);
            }
            if (BI.isEmptyArray(v[idx]) || BI.isNull(v[idx])) {
                value.push(["*"]);
            } else {
                var temp = [];
                // 排除错误的设置的值
                BI.each(item.items, function (idx, itemValue) {
                    temp.push(itemValue.value)
                });
                var valueTemp = BI.intersection(v[idx], temp);
                if (BI.isEmptyArray(valueTemp)) {
                    valueTemp = ["*"];
                }
                value.push(valueTemp);
            }
        });
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
    }
});

$.shortcut('bi.tree_label_view', BI.TreeLabelView);