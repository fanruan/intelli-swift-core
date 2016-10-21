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
        this.titles = [];
        BI.each(o.titles, function (idx, title) {
            self.titles.push({
                text: title + ":"
            })
        });
        this.view = BI.createWidget({
            type: "bi.tree_label_view",
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this),
            titles: this.titles
        });
        this.view.on(BI.TreeLabelView.EVENT_CHANGE, function () {
            self.fireEvent(BI.TreeLabel.EVENT_CHANGE, arguments);
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
                    if (!containsId(has.id, node.id)) {
                        BI.isArray(has.id) ? has.id.push(node.id) : has.id = [has.id, node.id];
                        BI.isArray(has.pId) ? has.pId.push(node.pId) : has.pId = [has.pId, node.pId];
                    }
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

        function containsId(ids, id) {
            if (BI.isArray(ids)) {
                return BI.contains(ids, id);
            } else {
                return ids === id;
            }
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
                    self.map[item.pId] = BI.concat(self.map[item.pId] || [], item);
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

    _updateItems: function (floor, v) {
        floor = floor || 0;
        var self = this;
        var result = [];
        var values = v || this.view.getValue();
        for (var i = floor + 1; i <= this.items.length - 1; i++) {
            var temp = [];
            var preItems = this.items[i - 1];
            var preValues = values[i - 1] || [BICst.LIST_LABEL_TYPE.ALL];
            var preSelectedItems = [];

            if (i === floor + 1) {
                if (BI.contains(preValues, BICst.LIST_LABEL_TYPE.ALL)) {
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
                if (BI.contains(preValues, BICst.LIST_LABEL_TYPE.ALL)) {
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
                        if (!containsId(has.id, node.id)) {
                            BI.isArray(has.id) ? has.id.push(node.id) : has.id = [has.id, node.id];
                            BI.isArray(has.pId) ? has.pId.push(node.pId) : has.pId = [has.pId, node.pId];
                        }
                    } else {
                        temp.push(node);
                    }
                });
            });
            result.push(temp);
        }
        this.items = BI.concat(this.items.slice(0, floor + 1), result);
        // BI.each(this.items, function (idx, items) {
        //     // var selected = [], unselected = [];
        //     // BI.each(items, function (i, item) {
        //     //     if(BI.contains(values[idx], item.value)) {
        //     //         selected.push(item);
        //     //     } else {
        //     //         unselected.push(item);
        //     //     }
        //     // });
        //     // self.items[idx] = BI.concat(selected, unselected);
        //     if(items.length >= 40) {
        //         items.sort(function (a, b) {
        //             var flagA = BI.contains(values[idx], a.value);
        //             var flagB = BI.contains(values[idx], b.value);
        //             return flagB - flagA;
        //         })
        //     }
        // });
        return result;

        function contains(array, item) {
            for (var i = 0; i < array.length; i++) {
                if (array[i].value === item.value) {
                    return array[i]
                }
            }
            return false;
        }

        function containsId(ids, id) {
            if (BI.isArray(ids)) {
                return BI.contains(ids, id);
            } else {
                return ids === id;
            }
        }

        function convertToItems(item) {
            var result = [];
            if (BI.isArray(item.id)) {
                BI.each(item.id, function (index, id) {
                    result.push(BI.extend(BI.clone(item), {
                        id: id,
                        pId: item.pId[index]
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
                self.titles = value.titles;
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
        callback({items: this.items, titles: this.titles});
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

        //if (BI.isNotEmptyArray(resultId) || BI.contains(op.value, BICst.LIST_LABEL_TYPE.ALL)) {
        o.itemsCreator(op, function (value) {
            self._updateData(value.items);
            self._updateItems(floor);
            callback(self.items, floor);
        });
        // } else {
        //     this._updateItems(floor);
        //     callback(this.items, floor);
        // }
    },

    populate: function (v) {
        this._initData(v.items);
        var items = [];
        BI.each(v.items, function (idx, array) {
            BI.each(array, function (i, item) {
                items.push(item);
            })
        });
        this._updateData(items);

        var result = [];
        convertToArray(v.selectedValue, result, 0);
        this._updateItems(0, result);
        for (var i = this.items.length; i < v.titles.length; i++) {
            this.items.push([]);
        }
        this.view.refreshView({
            items: this.items,
            titles: v.titles
        });

        this.view.setValue(result);

        function convertToArray(obj, result, i) {
            if (BI.isEmptyObject(obj)) {
                return;
            }
            var keys = Object.keys(obj);
            result[i] = BI.uniq(BI.concat(result[i] || [], keys));
            BI.each(keys, function (idx, key) {
                convertToArray(obj[key], result, i + 1)
            });
            return result;
        }
    },

    // setValue: function (v) {
    //     v = v || {};
    //     var result = [];
    //     convertToArray(v,result,0);
    //     this._updateItems(0, result);
    //     this.view.refreshView({
    //         items: this.items
    //     });
    //
    //     this.view.setValue(result);
    //
    //     function convertToArray(obj, result, i) {
    //         if(BI.isEmptyObject(obj)) {
    //             return ;
    //         }
    //         var keys = Object.keys(obj);
    //         result[i] = BI.uniq(BI.concat(result[i]||[],keys));
    //         BI.each(keys, function (idx, key) {
    //             convertToArray(obj[key], result, i+1)
    //         })
    //
    //     }
    // },

    getValue: function () {
        var selectedButtons = this.view.getSelectedButtons();
        var selectedValues = [];
        var result = {};
        var temp = [];
        BI.each(selectedButtons, function (idx, buttons) {
            temp = [];
            BI.each(buttons, function (index, button) {
                temp = BI.concat(temp, {
                    value: button.options.value
                })
            });
            selectedValues.push(temp);
        });

        for (var i = selectedValues.length - 2; i >= 0; i--) {
            BI.each(selectedValues[i], function (idx, value) {
                temp = [];
                BI.each(selectedValues[i + 1], function (index, node) {
                    temp.push(node);
                });
                value.children = temp;
            })
        }
        return convertToObject(selectedValues[0], result);

        function convertToObject(children, map) {
            if (BI.isEmptyArray(children)) {
                return {};
            }
            BI.each(children, function (idx, child) {
                var temp = {};
                if (BI.isNotEmptyArray(child.children)) {
                    convertToObject(child.children, temp);
                }
                map[child.value] = BI.extend(map[child.value] || {}, temp);
            });
            return map;
        }
    }
});
BI.TreeLabel.EVENT_CHANGE = "BI.TreeLabel.EVENT_CHANGE";
$.shortcut('bi.tree_label', BI.TreeLabel);