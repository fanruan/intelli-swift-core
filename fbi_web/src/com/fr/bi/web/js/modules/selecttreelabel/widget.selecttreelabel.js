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
                if (BI.isEmptyObject(op)) {
                    callback({});
                } else {
                    BI.Utils.getWidgetDataByID(o.wId, {
                        success: function (jsonData) {
                            callback(jsonData);
                        }
                    }, {tree_options: op})
                }
            }
        });

        this.treeLabel.on(BI.TreeLabel.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectTreeLabel.EVENT_CONFIRM, arguments);
        });
    },

    setValue: function (v) {
        v = v || [];
        var self = this, o = this.options;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId),
            titles = [];
        v = v.slice(0, dimensions.length);
        var data = {
            floors: -1,
            selectedValues: v
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
            titles.push(BI.Utils.getDimensionNameByID(dId))
        });
        BI.Utils.getWidgetDataByID(o.wId, {
            success: function (jsonData) {
                self.treeLabel.populate({
                    items: jsonData.items,
                    values: jsonData.values,
                    titles: titles
                });
                self.treeLabel.setValue(v);
            }
        }, {tree_options: data});

    },

    getValue: function () {
        return this.treeLabel.getValue();
    }
});
BI.SelectTreeLabel.EVENT_CONFIRM = "SelectTreeLabel.EVENT_CONFIRM";
$.shortcut('bi.select_tree_label', BI.SelectTreeLabel);