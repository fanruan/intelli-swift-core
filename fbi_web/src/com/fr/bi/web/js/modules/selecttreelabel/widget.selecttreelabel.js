/**
 * Created by fay on 2016/10/11.
 */
BI.SelectTreeLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectTreeLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-tree-label",
            wId: ""
        })
    },

    _init: function () {
        BI.SelectTreeLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.treeLabel = BI.createWidget({
            type: "bi.tree_label",
            element: this.element,
            itemsCreator: function (op, callback) {
                var data = {};
                data.floors = op.floor;
                data.parentValues = op.parentValues;
                data.selectedValues = self.selectedValues || [];
                if (BI.isEmptyObject(op) || BI.isEmptyArray(op.parentValues)) {
                    callback({});
                } else {
                    BI.Utils.getWidgetDataByID(o.wId, {
                        success: function (jsonData) {
                            callback(jsonData);
                        }
                    }, {tree_options: data})
                }
            }
        });

        this.treeLabel.on(BI.TreeLabel.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectTreeLabel.EVENT_CONFIRM, arguments);
        });
    },

    setValue: function (v) {
        var self = this, o = this.options;
        var selectedValues = [];
        convertToArray(v, selectedValues, 0);
        this.selectedValues = selectedValues;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId),
            titles = [],
            data = {
                floors: 0,
                selectedValues: selectedValues,
                parentValues: [{
                    id: "",
                    value: []
                }]
            };
        if (BI.isEmptyArray(dimensions)) {
            self.treeLabel.populate({
                items: [],
                titles: []
            });
            self.treeLabel.setValue(v);
            return;
        }
        BI.each(dimensions, function (idx, dId) {
            var temp = BI.Utils.getDimensionNameByID(dId);
            titles.push({
                text: temp + BI.i18nText("BI-Colon"),
                title: temp
            })
        });
        BI.Utils.getWidgetDataByID(o.wId, {
            success: function (jsonData) {
                self.treeLabel.populate({
                    items: jsonData.items,
                    titles: titles
                });
                self.treeLabel.setValue(v);
            }
        }, {tree_options: data});

        function convertToArray(obj, result, i) {
            if (BI.isEmptyObject(obj)) {
                return [];
            }
            var keys = Object.keys(obj);
            result[i] = BI.uniq(BI.concat(result[i] || [], keys));
            BI.each(keys, function (idx, key) {
                convertToArray(obj[key], result, i + 1)
            });
            return result;
        }
    },

    getValue: function () {
        return this.treeLabel.getValue();
    }
});
BI.SelectTreeLabel.EVENT_CONFIRM = "SelectTreeLabel.EVENT_CONFIRM";
$.shortcut('bi.select_tree_label', BI.SelectTreeLabel);