/**
 * Created by Young's on 2017/4/12.
 */
BI.AnalysisHistoryTabModel = BI.inherit(BI.OB, {
    _init: function () {
        BI.AnalysisHistoryTabModel.superclass._init.apply(this, arguments);
        var self = this;
        this.options.items = [];
        if (BI.isNull(this.options.invalidIndex)) {
            this.options.invalidIndex = Number.MAX_VALUE;
        }
        this.options.allHistory = false;
        if (BI.isNull(this.options.etlType)) {
            this.addItemAfter(ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_FIELD, -1, new BI.AnalysisETLOperatorSelectDataModel().update())
        } else {
            BI.each(self._initItems(BI.deepClone(this.options), []), function (i, item) {
                self.addItemAfter(item.op, i - 1, item.table)
            })
        }
    },

    _initItems: function (table, items) {
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[table.etlType];
        var self = this;
        items = BI.concat([{
            op: operator,
            table: table
        }], items);
        self._initId([table]);
        if (BI.isNotNull(table.parents)) {
            if (table.parents.length !== 2) {
                items = this._initItems(table.parents[0], items);
            } else {
                self._initId(table.parents);
                this.options.allHistory = true;
            }
        }
        return items;
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
                self._initId(item[ETLCst.PARENTS])
            })
        }
    },

    get: function(key) {
        return this.options[key];
    },

    set: function(key, value) {
        this.options[key] = value;
    },

    addItemAfter: function (v, index, options) {
        index++;
        var newItem = this.createItem(v, options);
        var items = this.options.items;
        if (items.length !== 0) {
            newItem.parents = [items[index - 1]];
        }
        if (BI.isNotNull(items[index])) {
            items[index].parents = [newItem];
        }
        items = BI.concat(BI.concat(items.slice(0, index), newItem), items.slice(index));
        this.options.items = items;
        return newItem;
    },

    checkBeforeSave: function (table, index) {
        index++;
        var items = this.options.items;
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
        var items = this.options.items;
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
        var pos = this.options.items.length - 1;
        BI.some(this.options.items, function (idx, item) {
            if (item.value === v) {
                pos = idx;
                return true;
            }
        });
        return pos;
    },

    findItem: function (v) {
        var items = this.options.items;
        return BI.find(items, function (idx, item) {
            return item.value === v;
        })
    },

    setFields: function (v, fields) {
        var item = this.findItem(v);
        item[ETLCst.FIELDS] = fields;
    },

    getOperatorType: function (v) {
        var item = this.findItem(v);
        return item.operatorType
    },

    removeItemFromValue: function (v) {
        var pos = this.getIndexByValue(v);
        var items = this.options.items;
        items = items.slice(0, pos);
        this.options.items = items;
        return pos;
    },

    isModelValid: function () {
        return this.options.invalidIndex >= this.options.items.length
    },

    createHistoryModel: function () {
        var items = this.options.items;
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

    getValue: function () {
        var items = this.options.items;
        var res = BI.extend(BI.extend({}, items[items.length - 1]), {
            value: BI.deepClone(this.options.value),
            tableName: BI.deepClone(this.options.tableName),
            invalidIndex: BI.deepClone(this.options.invalidIndex)
        });
        if (!this.isModelValid()) {
            res[ETLCst.FIELDS] = []
        }
        return res;
    }
});