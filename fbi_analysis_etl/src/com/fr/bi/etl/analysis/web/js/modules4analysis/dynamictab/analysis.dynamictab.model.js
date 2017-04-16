/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisDynamicTabModel = BI.inherit(FR.OB, {

    _constant: {
        baseName: "sheet"
    },

    _init: function () {
        BI.AnalysisDynamicTabModel.superclass._init.apply(this, arguments);
        this.sheets = [];
    },

    getSheets: function () {
        return this.sheets;
    },

    getSheetByValue: function (value) {
        return BI.find(this.sheets, function (i, sheet) {
            return sheet.value === value;
        });
    },

    getSheetByIndex: function (index) {
        return this.sheets[index];
    },

    addSheet: function () {
        var newSheet = {};
        newSheet.value = BI.UUID();
        newSheet.tableName = this.createNewName();
        this.sheets.push(newSheet);
        return newSheet.value;
    },

    copyItem: function (value) {
        var newItem = this.getSheetByValue(value);
        newItem.tableName = this.createNewName(this.getSheetNameByValue(value));
        return this._createItemModel(newItem);
    },

    _createItemModel: function (newItem) {
        newItem.value = BI.UUID();
        this.sheets.push(newItem);
        return newItem.value;
    },

    removeSheet: function (id) {
        var items = this.sheets;
        var newItem = [];
        var deletePos = -1;
        BI.each(items, function (idx, item) {
            if (item.value !== id) {
                newItem.push(item);
            } else {
                deletePos = idx;
            }
        });
        this.sheets = newItem;
        return deletePos;
    },

    isNameExists: function (name, id) {
        var self = this;
        var hasSameName = false;
        BI.some(this.sheets, function (idx, item) {
            if (item.value !== id) {
                if (self.getSheetByValue(item.value) === name) {
                    hasSameName = true;
                    return true;
                }
            }
            var parents = self._getChildParents(item.value);
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

    _getChildParents: function (value) {
        var sheet = this.getSheetByValue(value);
        return sheet.parents || [];
    },

    reName: function (value, newName) {
        var sheet = this.getSheetByValue(value);
        sheet.tableName = newName;
    },

    getSheetNameByValue: function (value) {
        var sheet = this.getSheetByValue(value);
        return sheet.tableName || void 0;
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

    populate: function (items) {
        this.sheets = items;
        if (this.sheets.length === 0) {
            this.addSheet();
        }
    },

    getValue: function () {
        return {
            items: this.sheets
        };
    }
});