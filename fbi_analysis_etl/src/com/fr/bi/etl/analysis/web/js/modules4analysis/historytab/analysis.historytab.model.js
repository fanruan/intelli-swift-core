/**
 * Created by Young's on 2017/4/12.
 */
BI.AnalysisHistoryTabModel = BI.inherit(BI.OB, {
    _init: function () {
        BI.AnalysisHistoryTabModel.superclass._init.apply(this, arguments);
        this.items = [];
        this.invalidIndex = -1;
        this.allHistory = false;
    },

    _initItems: function (table) {
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[table.etlType];
        this.items = this.items.concat({
            op: operator,
            table: table
        });
        this._initId([table]);
        if (BI.isNotNull(table.parents) && table.parent.length > 0) {
            if (table.parents.length !== 2) {
                this._initItems(table.parents[0]);
            } else {
                this._initId(table.parents);
                this.allHistory = true;
            }
        }
    },

    _initId: function (tables) {
        var self = this;
        if (BI.isNotNull(tables)) {
            BI.each(tables, function (idx, item) {
                item.value = item.value || BI.UUID();
                var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[item.etlType];
                BI.extend(item, {
                    operatorType: operator.operatorType,
                    text: operator.text
                });
                if (item.etlType === ETLCst.ETL_TYPE.SELECT_DATA) {
                    item.operator = new BI.AnalysisETLOperatorSelectDataModel(item).update().operator
                }
                self._initId(item.parent);
            });
        }
    },

    getOperators: function () {
        return this.items;
    },

    getInvalidIndex: function () {
        return this.invalidIndex;
    },

    setInvalidIndex: function (invalidIndex) {
        this.invalidIndex = invalidIndex;
    },

    getInvalidTitle: function () {
        return this.invalidTitle;
    },

    setInvalidTitle: function (invalidTitle) {
        this.invalidTitle = invalidTitle;
    },

    addNewOperator: function (operator, table) {
        operator.value = BI.UUID();
        BI.extend(operator, table);
        if (this.items.length > 0) {
            operator.parents = [this.items[this.items.length - 1]];
        }
        this.items.push(operator);
        return operator;
    },

    checkBeforeSave: function (table, index) {
        index++;
        var items = this.items;
        var p = table;
        for (var i = index; i < items.length; i++) {
            var c = BI.deepClone(items[index]);
            c.parents = [p];
            var modelClass = ETLCst.OPERATOR_MODEL_CLASS[c.operatorType];
            var m = new modelClass(c);
            var res = m.check();
            if (res[0] === true) {
                return [false, BI.i18nText("BI-Basic_Step") + ":" + (i + 1) + ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[c.etlType].text + " " + res[1] + "!!!" +
                BI.i18nText("BI-Confirm_Continue")]
            }
            p = c;
        }
        return [true]
    },

    saveItem: function (table) {
        var index = this.getIndexByValue(table.value);
        var items = this.items;
        items[index] = table;
        if (BI.isNotNull(items[index - 1])) {
            items[index].parents = [items[index - 1]];
        }
        if (BI.isNotNull(items[index + 1])) {
            items[index + 1].parents = [items[index]];
        }
    },

    createItem: function (v, options) {
        return BI.extend(options || {}, {
            text: v.text,
            operatorValue: v.value,
            operatorType: v.operatorType,
            value: BI.UUID()
        });
    },

    getIndexByValue: function (v) {
        var pos = this.items.length;
        BI.some(this.items, function (idx, item) {
            if (item.value === v) {
                pos = idx;
                return true;
            }
        });
        return pos;
    },

    getOperatorByValue: function (value) {
        return BI.find(this.items, function (i, item) {
            return item.value === value;
        });
    },

    setFields: function (value, fields) {
        var item = this.getOperatorByValue(value);
        item.etlFields = fields;
    },

    getOperatorTypeByValue: function (value) {
        var operator = this.getOperatorByValue(value);
        return operator.operatorType;
    },

    removeItemFromValue: function (v) {
        var pos = this.getIndexByValue(v);
        var items = this.items;
        items = items.slice(0, pos);
        this.items = items;
        return pos;
    },

    isModelValid: function () {
        return this.invalidIndex === -1 || this.invalidIndex >= this.items.length
    },

    createHistoryModel: function () {
        var items = this.items;
        return {
            items: this._toArray(items[0])
        }
    },

    _toArray: function (model) {
        var items = [];
        var item = this._createItem(model, null);
        items.push(item);
        var parents = model.parents;
        var self = this;
        BI.each(parents, function (idx, item) {
            self._iteratorTree(item, items, model.value)
        });
        return items;
    },

    _iteratorTree: function (model, items, pid) {
        items.push(this._createItem(model, pid));
        if (BI.isNotNull(model.parents) && model.parents.length !== 2) {
            this._iteratorTree(model.parents[0], items, model.value)
        }
    },

    _createItem: function (table, pid) {
        return BI.extend(BI.deepClone(table), {
            id: table.value,
            pId: pid
        });
    },

    populate: function (table) {
        this.value = table.value;
        this.tableName = table.tableName;
        this.invalidIndex = table.invalidIndex || this.invalidIndex;
        this.items = [];
        if (BI.isNull(table.etlType)) {
            this.items.push(BI.extend(ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_FIELD, {
                value: BI.UUID()
            }));
        } else {
            this._initItems(table);
        }
    },

    getValue: function () {
        var items = this.items;
        var res = BI.extend(BI.extend({}, items[items.length - 1]), {
            value: this.value,
            tableName: this.tableName,
            invalidIndex: this.invalidIndex
        });
        if (!this.isModelValid()) {
            res.etlFields = []
        }
        return res;
    }
});