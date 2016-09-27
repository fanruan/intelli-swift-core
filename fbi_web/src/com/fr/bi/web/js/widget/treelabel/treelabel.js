/**
 * Created by fay on 2016/9/14.
 */
BI.TreeLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TreeLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-tree-label",
            itemsCreator: BI.emptyFn,
            titles: []
        })
    },

    _init: function () {
        BI.TreeLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        if (BI.isNotNull(o.items)) {
            this._initData(o.items);
        }
        var titles = [];
        BI.each(o.titles, function (idx, title) {
            titles.push({
                text: title + ":"
            })
        });
        this.view = BI.createWidget({
            type: "bi.tree_label_view",
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this),
            titles: titles
        })
    },

    _initData: function (items) {
        var self = this, result = [];
        this.map = {};
        this.itemsMap = {};
        BI.each(items, function (idx, item) {
            var temp = [];
            BI.each(item, function (i, data) {
                var node = BI.clone(data);
                self.itemsMap[node.id] = node;
                var has = contains(temp, node);
                if (has) {
                    has.id = BI.isArray(has.id) ? has.id.push(node.id) : [has.id, node.id];
                } else {
                    temp.push(node)
                }
            });
            result.push(temp);
        });
        this.items = result;
        function contains(array, node) {
            for (var i = 0; i < array.length; i++) {
                if (array[i].value === node.value) {
                    return array[i];
                }
            }
            return false;
        }
    },

    _updateData: function (items) {
        var self = this;
        // 更新map
        BI.each(items, function (idx, item) {
            if (!self.itemsMap[item.id]) {
                self.itemsMap[item.id] = item;
            }

            if (!self.map[item.id]) {
                self.map[item.id] = [];
            }

            if (BI.isNotNull(item.pId)) {
                if (!containsId(self.map[item.pId], item)) {
                    self.map[item.pId] = BI.concat(self.map[item.pId] || [], item)
                }
            }
        });

        function containsId(array, item) {
            if (BI.isNull(array)) {
                return false;
            }
            for (var i = 0; i < array.length; i++) {
                if (array[i].id === item.id) {
                    return true;
                }
            }
            return false;
        }
    },

    _updateItems: function (floor) {
        floor = floor || 0;
        var self = this;
        var result = [];
        var values = this.view.getValue();
        for (var i = floor + 1; i <= this.items.length - 1; i++) {
            var temp = [];
            var preItems = this.items[i - 1];
            var preValues = values[i - 1];
            var preSelectedItems = [];

            if (i === floor + 1) {
                if (BI.contains(preValues, "*")) {
                    BI.each(preItems, function (idx, item) {
                        preSelectedItems = BI.concat(preSelectedItems, convertToItems(item));
                    });
                } else {
                    BI.each(preItems, function (idx, item) {
                        if (BI.contains(preValues, item.value)) {
                            preSelectedItems = BI.concat(preSelectedItems, convertToItems(item));
                        }
                    });
                }
            } else {
                if (BI.contains(preValues, "*")) {
                    BI.each(result[i - floor - 2], function (idx, item) {
                        preSelectedItems = BI.concat(preSelectedItems, convertToItems(item));
                    });
                } else {
                    BI.each(result[i - floor - 2], function (idx, item) {
                        if (BI.contains(preValues, item.value)) {
                            preSelectedItems = BI.concat(preSelectedItems, convertToItems(item));
                        }
                    });
                }
            }
            BI.each(preSelectedItems, function (idx, item) {
                BI.each(self.map[item.id], function (index, data) {
                    var node = BI.clone(data);
                    var has = contains(temp, node);
                    if (has) {
                        has.id = BI.isArray(has.id) ? has.id.push(node.id) : [has.id, node.id];
                    } else {
                        temp.push(node);
                    }
                });
            });
            result.push(temp);
        }
        this.items = BI.concat(this.items.slice(0, floor + 1), result);
        return result;

        function contains(array, item) {
            for (var i = 0; i < array.length; i++) {
                if (array[i].value === item.value) {
                    return array[i]
                }
            }
            return false;
        }

        function convertToItems(item) {
            var result = [];
            if (BI.isArray(item.id)) {
                BI.each(item.id, function (index, id) {
                    result.push(BI.extend(BI.clone(item), {
                        id: id
                    }));
                })
            } else {
                result.push(item);
            }
            return result;
        }
    },

    _itemsCreator: function (options, callback) {
        var self = this, o = this.options;
        if (!this.items) {
            o.itemsCreator({}, function (value) {
                self._initData(value.items);
                call();
            })
        } else {
            call();
        }
        function call() {
            switch (options.type) {
                case 1:
                    self._updateLabelView(options, callback);
                    break;
                default:
                    self._initLabelView(options, callback)
            }
        }
    },

    _initLabelView: function (op, callback) {
        callback(this.items);
    },

    _updateLabelView: function (op, callback) {
        var self = this, o = this.options;
        var floor = op.floor;
        var ids = op.id;
        var resultId = [];
        if (BI.isArray(ids)) {
            BI.each(ids, function (idx, id) {
                if (self.itemsMap[id] && self.itemsMap[id].clicked !== true) {
                    resultId.push(id);
                    self.itemsMap[id].clicked = true;   //表示已获取某ID下的数据
                }
            });
        } else {
            if (self.itemsMap[ids] && self.itemsMap[ids].clicked !== true) {
                resultId.push(ids);
                self.itemsMap[ids].clicked = true;
            }
        }

        if (BI.isNotEmptyArray(resultId) || BI.contains(op.value, "*")) {
            o.itemsCreator(op, function (value) {
                self._updateData(value.items);
                self._updateItems(floor);
            });
        } else {
            this._updateItems(floor);
        }

        callback(this.items, floor);
    },

    _getSelectedValues: function (map, values) {
        var preValues = [];
        for (var i = 0; i < values.length; i++) {
            if(BI.isEmptyObject(map)) {
                BI.each(values[i], function (idx, value) {
                    var temp = {};
                    preValues.push(temp);
                    map[value] = temp;
                })
            } else {
                var tempArray = [];
                BI.each(values[i], function (idx, value) {
                    BI.each(preValues, function (index, preTemp) {
                        var temp = {};
                        tempArray.push(temp);
                        preTemp[value] = temp;
                    });
                });
                preValues = tempArray;
            }
        }
        return map;
    },

    setValue: function (v) {
        var self = this, op = {
            selected_values: v
        };
        o.itemsCreator(op, function (value) {
            self._updateData(value.items);
            self._updateItems();
            this.view.setValue(v);
        });
    },

    getValue: function () {
        var resultMap = {};
        var values = this.view.getValue();
        return this._getSelectedValues(resultMap, values);
    }
});

$.shortcut('bi.tree_label', BI.TreeLabel);