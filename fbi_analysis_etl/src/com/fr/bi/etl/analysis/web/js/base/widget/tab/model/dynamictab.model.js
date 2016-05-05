BI.DynamictabModel = BI.inherit(BI.MVCModel, {

    _constant : {
        baseName:"sheet"
    },

    
    _init : function () {
        BI.DynamictabModel.superclass._init.apply(this, arguments);
        var self = this, items = this.get(ETLCst.ITEMS);
        this.set(ETLCst.ITEMS,[]);
        items = items || [];
        BI.each(items, function (i, item) {
           self.addItem(item)
        })
        if(items.length === 0) {
            this.addItem()
        }
    },

    addItem : function (v) {
        var newItem = BI.extend({}, v);
        newItem.table_name = this.createNewName(newItem.table_name);
        return this._createItemModel(newItem)
    },

    _createItemModel : function (newItem) {
        newItem.value = BI.UUID();
        var items = this.get(ETLCst.ITEMS);
        items.push(newItem.value);
        this.set(ETLCst.ITEMS, items);
        this.set(newItem.value, new BI.HistoryTabModel(newItem));
        return  newItem.value;
    },

    copyItem : function (id) {
        var newItem = this.get(id).update()
        newItem.table_name = this.createNewName(this.getName(id));
        return this._createItemModel(newItem);
    },

    getSheetData : function (v) {
        return this.get(v).update();
    },

    removeItem : function (id) {
        var items = this.get(ETLCst.ITEMS);
        var newItem = [];
        var deletePos = -1;
        BI.each(items, function(idx, item){
            if(item !== id){
                newItem.push(item)
            } else {
                deletePos = idx;
            }
        })
        this.set(ETLCst.ITEMS, newItem)
        this.unset(id)
        return deletePos;
    },

    isNameExists : function (name, id){
        var hasSameName = false;
        var items = this.get(ETLCst.ITEMS)
        var self = this;
        BI.some(items, function(idx, item){
            if(item !== id){
                if(self.getName(item) === name){
                    hasSameName = true;
                    return true;
                }
            }
        })
        return hasSameName;
    },

    reName : function (id, newName) {
        this.get(id).set("table_name", newName)
    },

    getName : function (id) {
        var childModel = this.get(id);
        return BI.isNotNull(childModel) ? childModel.get("table_name") : void 0
    },

    createNewName : function(baseName) {
        var base = baseName;
        var startIndex = 0;
        while(BI.isNull(baseName) || this.isNameExists(baseName)){
            var defaultName = base || this._constant.baseName;
            baseName =  defaultName + ++ startIndex;
        }
        return baseName;
    },

    _createName : function(baseName){
        var defaultName = baseName || this._constant.baseName;
        return defaultName + ++this.startIndex;
    },
    
    update : function () {
        var self = this,tables = [];
        BI.each(this.get(ETLCst.ITEMS), function (i, item) {
            tables.push(self.get(item).update())
        })
        var res = {};
        res[ETLCst.ITEMS]= tables;
        return res;
    }
    
})