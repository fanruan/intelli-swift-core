/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisDynamicTabModel = BI.inherit(FR.OB, {

    _constant: {
        baseName: "sheet"
    },

    _init: function () {
        BI.AnalysisDynamicTabModel.superclass._init.apply(this, arguments);
        var self = this, items = this.options.items;
        BI.each(items, function (i, item) {
            self.addItem(item)
        });
        if (items.length === 0) {
            this.addItem()
        }
    },

    get: function (key) {
        return this.options[key];
    },

    set: function (key, value) {
        this.options[key] = value;
    },

    addItem: function (v) {
        var newItem = BI.extend({}, v);
        newItem.tableName = this.createNewName(newItem.tableName);
        return this._createItemModel(newItem)
    },

    _createItemModel: function (newItem) {
        newItem.value = BI.UUID();
        var items = this.options.items;
        items.push(newItem.value);
        this.options.items = items;
        this.options[newItem.value] = new BI.AnalysisHistoryTabModel(newItem);
        return newItem.value;
    },

    copyItem: function (id) {
        var newItem = this.options[id].getValue();
        newItem.tableName = this.createNewName(this.getName(id));
        return this._createItemModel(newItem);
    },

    getSheetData: function (v) {
        return this.get(v).getValue();
    },

    hasMergeHistory: function (v) {
        return this.get(v).allHistory;
    },

    removeItem: function (id) {
        var items = this.options.items;
        var newItem = [];
        var deletePos = -1;
        BI.each(items, function (idx, item) {
            if (item !== id) {
                newItem.push(item)
            } else {
                deletePos = idx;
            }
        });
        this.options.items = newItem;
        delete this.options[id];
        return deletePos;
    },

    isNameExists: function (name, id) {
        var self = this;
        var hasSameName = false;
        var items = this.options.items;
        BI.some(items, function (idx, item) {
            if (item !== id) {
                if (self.getName(item) === name) {
                    hasSameName = true;
                    return true;
                }
            }
            var parents = self._getChildParents(item);
            if (parents.length === 2) {
                BI.some(parents, function (idx, item) {
                    if (item.tableName === name) {
                        hasSameName = true;
                        return true;
                    }
                })
            }
        });
        return hasSameName;
    },

    _getChildParents: function (id) {
        var childModel = this.options[id];
        return BI.isNotNull(childModel) ? (childModel.get("parents") || [] ) : []
    },

    reName: function (id, newName) {
        this.options[id].options.tableName = newName;
    },

    getName: function (id) {
        var childModel = this.options[id];
        return BI.isNotNull(childModel) ? childModel.get("tableName") : void 0
    },

    createNewName: function (baseName) {
        var base = baseName;
        var startIndex = 0;
        while (BI.isNull(baseName) || this.isNameExists(baseName)) {
            var defaultName = base || this._constant.baseName;
            baseName = defaultName + ++startIndex;
        }
        return baseName;
    },

    _createName: function (baseName) {
        var defaultName = baseName || this._constant.baseName;
        return defaultName + ++this.startIndex;
    },

    getValue: function () {
        var self = this, tables = [];
        BI.each(this.options.items, function (i, item) {
            tables.push(self.options[item].getValue());
        });
        var res = {};
        res[ETLCst.ITEMS] = tables;
        return res;
    }
});