BI.HistoryTabModel = BI.inherit(BI.MVCModel, {


    _init : function () {
        BI.HistoryTabModel.superclass._init.apply(this, arguments);
        var self = this;
        this.set(ETLCst.ITEMS, []);
        if (BI.isNull(this.get('invalidIndex'))){
            this.set('invalidIndex', Number.MAX_VALUE);
        }
        this.set("allHistory", false)
        if (BI.isNull(this.options.etlType)){
            this.addItemAfter(ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_FIELD, -1)
        } else {
            BI.each(self._initItems(BI.deepClone(this.options), []), function (i, item) {
                self.addItemAfter(item.op, i - 1, item.table)
            })
        }
    },

    _initItems : function (table, items) {
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[table.etlType];
        var self = this;
        items = BI.concat([{
            op : operator,
            table : table
        }], items)
        if (BI.isNotNull(table.parents)){
            if(table.parents.length !== 2) {
                items = this._initItems(table.parents[0], items);
            } else {
                self._initId(table.parents)
                this.set("allHistory", true)
            }
        };
        return items;
    },

    _initId : function (tables) {
        var self = this;
        if(BI.isNotNull(tables)) {
            BI.each(tables, function (idx, item) {
                item.value = item.value || BI.UUID();
                var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[item.etlType]
                BI.extend(item, {
                    operatorType : operator["operatorType"],
                    text:operator["text"]
                })
                self._initId(item[ETLCst.PARENTS])
            })
        }
    },


    addItemAfter : function (v, index, options) {
        index ++;
        var newItem = this.createItem(v, options);
        var items = this.get(ETLCst.ITEMS);
        if (items.length !== 0){
            newItem.parents = [items[index - 1]];
        }
        if(BI.isNotNull(items[index])) {
            items[index].parents = [newItem];
        }
        items = BI.concat(BI.concat(items.slice(0, index), newItem), items.slice(index));
        this.set(ETLCst.ITEMS, items)
        return newItem;
    },

    saveItem : function (table) {
        var index = this.getIndexByValue(table.value);
        var items = this.get(ETLCst.ITEMS);
        items[index] = table;
        if(BI.isNotNull(items[index - 1])) {
            items[index].parents = [items[index - 1]] ;
        }
        if(BI.isNotNull(items[index + 1])) {
            items[index + 1].parents = [items[index]];
        }
    },

    createItem : function (v, options) {
        var item =  BI.extend (options || {}, {
            text: v.text,
            operatorValue:v.value,
            operatorType:v.operatorType,
            value :BI.UUID()
        })
        return item;
    },
    
    
    getIndexByValue : function (v) {
        var items = this.get(ETLCst.ITEMS);
        var pos = items.length - 1
        BI.some(items, function(idx, item){
            if(item.value === v){
                pos = idx;
                return true;
            }
        })
        return pos;

    },

    findItem : function (v) {
        var items = this.get(ETLCst.ITEMS);
        return BI.find(items, function (idx, item) {
            return item.value === v;
        })
    },

    setFields : function(v, fields){
        var item = this.findItem(v);
        item[ETLCst.FIELDS] = fields;
    },

    getOperatorType : function (v) {
        var item = this.findItem(v)
        return item.operatorType
    },

    removeItemFromValue : function (v) {
        var pos = this.getIndexByValue(v);
        var items = this.get(ETLCst.ITEMS);
        items = items.slice(0, pos);
        this.set(ETLCst.ITEMS, items)
        return pos;
    },

    isModelValid : function () {
        return this.getValue('invalidIndex') >= this.get(ETLCst.ITEMS).length
    },

    createHistoryModel : function () {
        var items = this.get(ETLCst.ITEMS);
        return    {
            items: this._toArray(items[0])
        }
    },

    _toArray : function (model) {
        var items = [];
        var item = this._createItem(model, null);
        items.push(item);
        var parents = model[ETLCst.PARENTS];
        var self = this;
        BI.each(parents, function (idx, item) {
            self._iteratorTree(item, items, model.value)
        })
        return items;
    },

    _iteratorTree : function (model, items, pid) {
            items.push(this._createItem(model, pid))
            if(BI.isNotNull(model.parents) && model.parents.length !== 2){
                this._iteratorTree(model.parents[0], items, model.value)
            }
    },

    _createItem : function (table, pid) {
        return BI.extend(BI.deepClone(table), {
            id:table.value,
            pId: pid
        });
    },

    update : function () {
        var items = this.get(ETLCst.ITEMS);
        var res =  BI.extend(BI.extend({
        }, items[items.length - 1]), {
            value:this.getValue("value"),
            table_name:this.getValue("table_name"),
            invalidIndex : this.getValue('invalidIndex')
        });
        if(!this.isModelValid()){
            res[ETLCst.FIELDS] = []
        }
        return res;
    }
})