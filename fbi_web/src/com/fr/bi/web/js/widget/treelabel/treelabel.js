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
        //     items.sort(function (a, b) {
        //         var flagA = BI.contains(values[idx], a.value);
        //         var flagB = BI.contains(values[idx], b.value);
        //         return flagB - flagA;
        //     })
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

    // _getSelectedValues: function () {
    //     var values = this._getValue().reverse(),
    //         temp = [];
    //
    //     for(var i = values.length-2; i >= 0; i--) {
    //         BI.each(values[i], function (idx, value) {
    //             if (value.value === "*" || values[i+1][0].value === "*") {
    //                 value.children = values[i+1];
    //             } else {
    //                 temp = [];
    //                 BI.each(values[i+1], function (index, node) {
    //                     if(values[i+1][index].pId === value.id) {
    //                         temp.push(node);
    //                     }
    //                 });
    //                 value.children = temp;
    //             }
    //         })
    //     }
    //     return convertToObject(values[0],{});
    //
    //     function convertToObject(children, map) {
    //         if(BI.isEmptyArray(children)) {
    //             return {};
    //         }
    //         BI.each(children, function (idx, child) {
    //             var temp = {};
    //             if(BI.isNotEmptyArray(child.children)) {
    //                 convertToObject(child.children, temp);
    //             }
    //             map[child.value] = temp;
    //         });
    //         return map;
    //     }
    // },

    // _getValue: function () {
    //     var selectedButtons = this.view.getSelectedButtons(),
    //         allButtons = this.view.getAllButtons(),
    //         result = [],
    //         selectedValues = [],
    //         temp = [];
    //     BI.each(selectedButtons, function (idx, buttons) {
    //         temp = [];
    //         for(var i =0;i<buttons.length;i++) {
    //             temp.push({
    //                 id: buttons[i].options.id,
    //                 pId: buttons[i].options.pId,
    //                 value: buttons[i].options.value
    //             })
    //         }
    //         selectedValues.push(temp);
    //     });
    //
    //     BI.each(selectedValues, function (idx, values) {
    //         temp = [];
    //         for(var i =0;i<values.length;i++) {
    //             if(BI.isArray(values[i].id)) {
    //                 temp = BI.concat(temp, convertToItems(values[i]));
    //                 values.splice(i, 1);
    //                 i--;
    //             }
    //         }
    //         selectedValues[idx] = BI.concat(selectedValues[idx], temp);
    //     });
    //
    //     var preValues, preIds=[], preValueTemp=[];
    //     temp = [];
    //     BI.each(selectedValues[selectedValues.length-1], function (idx, value) {
    //         temp.push(value);
    //     });
    //     result.push(temp);
    //     for (var i = selectedValues.length - 2; i >= 0; i--) {
    //         temp = [];
    //         preValues = selectedValues[i + 1];
    //         if(BI.isEmptyArray(preValues)) {
    //             result.push([{value: "*"}]);
    //             continue;
    //         }
    //         if(preValues[0].value === "*") {
    //             preValues = [];
    //             if(BI.isEmptyArray(allButtons[i+1])){
    //                 result.push(selectedValues[i]);
    //                 continue;
    //             }
    //             BI.each(allButtons[i+1], function (idx, button) {
    //                 var valueTemp = [];
    //                 if(BI.isArray(button.options.id)){
    //                     BI.each(button.options.id, function (index, bId) {
    //                         valueTemp.push({
    //                             id: bId,
    //                             pId: button.options.pId[index],
    //                             value: button.options.value
    //                         })
    //                     })
    //                 } else {
    //                     valueTemp.push({
    //                         id: button.options.id,
    //                         pId: button.options.pId,
    //                         value: button.options.value
    //                     })
    //                 }
    //                 BI.each(valueTemp, function (j, v) {
    //                     if(containsValue(preValueTemp, v) || i === selectedValues.length - 2) {
    //                         preValues.push(v)
    //                     }
    //                 })
    //             })
    //         }
    //         BI.each(preValues, function (idx, value) {
    //             preIds = BI.concat(preIds, value.pId);
    //         });
    //         for (var j = 0; j < selectedValues[i].length; j++) {
    //             if (BI.contains(preIds, selectedValues[i][j].id) || selectedValues[i][j].value === "*") {
    //                 temp.push(selectedValues[i][j])
    //             } else {
    //                 selectedValues[i].splice(j, 1);
    //                 j--;
    //             }
    //         }
    //         preValueTemp = preValues;
    //         result.push(temp);
    //         function containsValue(array, v) {
    //             for(var i=0;i<array.length;i++){
    //                 if(array[i].pId === v.id) {
    //                     return true;
    //                 }
    //             }
    //             return false;
    //         }
    //     }
    //     return result;
    //
    //     function convertToItems(item) {
    //         var result = [];
    //         if (BI.isArray(item.id)) {
    //             BI.each(item.id, function (index, id) {
    //                 result.push(BI.extend(BI.clone(item), {
    //                     id: id,
    //                     pId: item.pId[index]
    //                 }));
    //             })
    //         } else {
    //             result.push(item);
    //         }
    //         return result;
    //     }
    // },

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
        var selectedButtons = this.view.getSelectedButtons();
        var selectedValues = [];
        var self = this;
        var result = {};
        var temp = [];
        var floors = [];
        BI.each(selectedButtons, function (idx, buttons) {
            temp = [];
            BI.each(buttons, function (index, button) {
                temp = BI.concat(temp, convertToItems({
                    id: button.options.id,
                    pId: button.options.pId,
                    value: button.options.value
                }))
            });
            selectedValues.push(temp);
        });

        BI.each(selectedValues[0], function (idx, select) {
            var temp = 0;
            var ids = [select.id];
            while(hasChildren(ids)) {
                temp ++;
                var idTemp = [];
                BI.each(ids, function (idx ,id) {
                    BI.each(self.map[id], function (index, item) {
                        idTemp = BI.concat(idTemp, item.id);
                    });
                });
                ids = idTemp;
            }
            floors.push(temp);
        });

        for(var i = selectedValues.length-2; i >= 0; i--) {
            BI.each(selectedValues[i], function (idx, value) {
                if(selectedValues[i+1][0].value === "*" && value.value !== "*"){
                    if(!BI.isEmptyArray(self.map[value.id])) {
                        value.children = selectedValues[i+1];
                    }
                } else if(selectedValues[i+1][0].value === "*" && value.value === "*") {
                    value.children = selectedValues[i+1];
                }
                else if(value.value === "*"){
                    value.children = selectedValues[i+1];
                } else {
                    temp = [];
                    BI.each(selectedValues[i+1], function (index, node) {
                        if(selectedValues[i+1][index].pId === value.id) {
                            temp.push(node);
                        }
                    });
                    value.children = temp;
                }
            })
        }


        convertToObject(selectedValues[0], result);
        
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

        function convertToObject(children, map) {
            if(BI.isEmptyArray(children)) {
                return {};
            }
            BI.each(children, function (idx, child) {
                var temp = {};
                if(BI.isNotEmptyArray(child.children)) {
                    convertToObject(child.children, temp);
                }
                map[child.value] = temp;
            });
            return map;
        }

        function hasChildren(ids) {
            for (var i=0;i<ids.length;i++) {
                if(BI.isNotEmptyArray(self.map[ids[i]])) {
                    return true;
                }
            }
            return false;
        }
        function getChildren(origin, floor) {
            var keys = Object.keys(origin);
            for(var i = 0;i<=keys.length;i++){

            }
        }
    }
});

$.shortcut('bi.tree_label', BI.TreeLabel);