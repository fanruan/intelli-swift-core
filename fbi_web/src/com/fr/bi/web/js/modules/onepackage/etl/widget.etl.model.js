/**
 * Created by Young's on 2016/3/11.
 */
BI.ETLModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.ETLModel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.id = o.id;
        this.relations = o.relations;
        this.translations = o.translations;
        this.updateSettings = o.update_settings;
        this.usedFields = o.used_fields;
        this.allFields = o.all_fields;
        this.excelView = o.excel_view;
        this.tablesMap = {};
        if(BI.isNull(this.translations[this.id])){
            this.translations[this.id] = BI.isNotNull(o.tableData) ?
                this._getDistinctTableName(o.tableData.table_name) :  this._getDistinctTableName("ETL");
        }
        if(BI.isNull(this.usedFields)){
            this.allTableIds = [];
            this.allTables = [];
            this.isNew = true;
            return;
        }
        this.tableData = o.table_data;
        this.fields = this.tableData.fields;
        var finalTable = [this.tableData];
        this._addId2Tables(finalTable, this.tablesMap);
        this.allTableIds = this._getTablesId(finalTable, []);
        this.allTables = [finalTable];
    },

    getId: function(){
        return this.id;
    },

    isNewTable: function(){
        return this.isNew;
    },

    getTableData: function(){
        return BI.deepClone(this.tableData);
    },

    getFields: function(){
        return BI.deepClone(this.fields);
    },

    setFields: function(fields){
        var self = this;
        this.fields = fields;
        this.usedFields = [];
        BI.each(fields, function(i, fs){
            BI.each(fs, function(j, field){
                self.usedFields.push(field.field_name);
                self.allFields[field.id] = field;
            })
        });
    },

    getAllTables: function(){
        return BI.deepClone(this.allTables);
    },

    getAllTableIds: function(){
        return BI.deepClone(this.allTableIds);
    },

    getTablesMap: function(){
        return BI.deepClone(this.tablesMap);
    },

    getTableById: function(id){
        return BI.deepClone(this.tablesMap[id]);
    },

    getRelations: function(){
        return BI.deepClone(this.relations);
    },

    setRelations: function(relations) {
        this.relations = relations;
    },

    getTranslations: function(){
        return BI.deepClone(this.translations);
    },

    setTranslations: function(translations){
        this.translations = translations;
    },

    getUpdateSettings: function() {
        return BI.deepClone(this.updateSettings);
    },

    setUpdateSettings: function(updateSettings) {
        this.updateSettings = updateSettings;
    },

    getUsedFields: function(){
        return BI.deepClone(this.usedFields);
    },

    setUsedFields: function(usedFields){
        this.usedFields = usedFields;
    },

    getAllFields: function(){
        return BI.deepClone(this.allFields);
    },

    getExcelView: function(){
        return BI.deepClone(this.excelView);
    },

    isValidTableTranName: function (name) {
        var self = this;
        var currentPackTables = BI.Utils.getCurrentPackageTables4Conf();
        var isValid = true;
        BI.some(currentPackTables, function (tId, table) {
            if ( tId !== self.getId() && self.translations[tId] === name && isValid === true) {
                isValid = false;
                return true;
            }
        });
        return isValid;
    },

    addNewTables: function(tables){
        var self = this;
        BI.each(tables, function(i, table){
            var newIdsMap = {};
            self._addUUID2Tables([table], newIdsMap);
            BI.extend(self.tablesMap, newIdsMap);
            self.allTables.push([table]);
            self.allTableIds = self.getAllTableIds().concat(self._getTablesId([table], []));
        });
    },

    setExcelView: function(excelView) {
        this.excelView = excelView;
    },

    removeOneTable: function(tId){
        var self = this;
        BI.some(this.allTables, function(i, tables){
            //需要看是否含有子节点，1、没有直接删除 2、有的话还要把直接子节点拿出来放到allTables里面
            if(tables[0].id === tId){
                self.allTables.splice(i, 1);
                var childTables = tables[0].tables;
                BI.each(childTables, function(j, cTable){
                    self.allTables.push([cTable]);
                });
                BI.some(self.allTableIds, function(k, id){
                    if(id === tId){
                        self.allTableIds.splice(k, 1);
                        return true;
                    }
                });
                return true;
            }
        });
    },

    getTranName: function(){
        return this.getTranslations()[this.getId()];
    },

    setTranName: function(name){
        this.translations[this.getId()] = name;
    },

    saveTableById: function(tId, data){
        var self = this;
        if(BI.isNotNull(data.translations)){
            this.setTranslations(data.translations);
        }
        if(BI.isNotNull(data.relations)){
            this.setRelations(data.relations);
        }

        if(this.getAllTables().length > 1 &&
            (data.etl_type === BICst.ETL_OPERATOR.UNION || data.etl_type === BICst.ETL_OPERATOR.JOIN)){
            var sAllTables = [];
            BI.each(this.getAllTables(), function(i, tables){
                if(BI.isNotNull(tables) && tables[0].id === tId ||
                    !self._getTablesId(data.tables, []).contains(tables[0].id)){
                    sAllTables.push(tables);
                }
            });
            this.allTables = sAllTables;
        }

        //changed的子节点需要全部替换为UUID，避免出现重复的
        self._addUUID2Tables(data.tables, {});

        //遍历一下这个etl树，找到修改的节点，替换掉
        self._replaceNodeInAllTables(BI.extend(data, {id: tId}));
        this.tablesMap = {};
        this.allTableIds = [];
        BI.each(this.getAllTables(), function(i, tables){
            self._addId2Tables(tables, self.tablesMap);
            self.allTableIds = self.getAllTableIds().concat(self._getTablesId(tables, []));
        });
    },

    _getDistinctTableName: function(name){
        var self = this;
        var allTableNameTrans = [];
        var currentPackTables = BI.Utils.getCurrentPackageTables4Conf();
        var translations = this.getTranslations();
        BI.each(currentPackTables, function(tid, table){
            if(tid !== self.getId()){
                allTableNameTrans.push({name: translations[tid]});
            }
        });
        return BI.Func.createDistinctName(allTableNameTrans, name);
    },

    //自己有id的table使用原来的
    _addId2Tables: function(tables, ids){
        var self = this;
        BI.each(tables, function(i, table){
            var id = table.id || BI.UUID();
            if(BI.isNotNull(table.tables)){
                self._addId2Tables(tables[i].tables, ids);
                tables[i] = BI.extend(table, {
                    id: id,
                    tables: tables[i].tables,
                    translations: self.getTranslations(),
                    relations: self.getRelations()
                });
            } else {
                tables[i] = BI.extend(table, {
                    id: id,
                    translations: self.getTranslations(),
                    relations: self.getRelations()
                });
            }
            ids[id] = tables[i];
        });
    },

    //给tables添加新的uuid
    _addUUID2Tables: function(tables, ids){
        var self = this;
        BI.each(tables, function(i, table){
            var id = BI.UUID();
            if(BI.isNotNull(table.tables)){
                self._addUUID2Tables(tables[i].tables, ids);
                tables[i] = BI.extend(table, {
                    id: id,
                    tables: tables[i].tables,
                    translations: self.getTranslations(),
                    relations: self.getRelations()
                });
            } else {
                tables[i] = BI.extend(table, {
                    id: id,
                    translations: self.getTranslations(),
                    relations: self.getRelations()
                });
            }
            ids[id] = tables[i];
        });
    },

    _replaceNode: function(tables, newNode){
        var self = this;
        BI.some(tables, function(i, table){
            if(table.id === newNode.id){
                tables[i] = newNode;
                return true;
            }
            if(BI.isNotNull(table.tables)){
                tables[i].tables = self._replaceNode(table.tables, newNode);
            }

        });
        return tables;
    },

    _replaceNodeInAllTables: function(newNode){
        var self = this;
        var allTables = BI.deepClone(this.getAllTables());
        BI.each(allTables, function(i, tables){
            allTables[i] = self._replaceNode(tables, newNode);
        });
        this.allTables = allTables;
    },

    _getTablesId: function(tables, tableIds){
        var self = this;
        BI.each(tables, function(i, table){
            if(BI.isNotNull(table.tables)){
                tableIds = self._getTablesId(table.tables, tableIds);
            }
            tableIds.push(table.id);
        });
        return tableIds;
    },

    getValue: function(){
        var finalTable = this.getAllTables()[0][0];
        var data = {
            id: this.getId(),
            translations: this.getTranslations(),
            relations: this.getRelations(),
            all_fields: this.getAllFields(),
            usedFields: this.getUsedFields(),
            fields: this.getFields(),
            excel_view: this.getExcelView(),
            update_settings: this.getUpdateSettings()
        };
        if (BI.isNotNull(finalTable.etl_type)) {
            data.etl_type = finalTable.etl_type;
            data.etl_value = finalTable.etl_value;
            data.tables = finalTable.tables;
            data.connection_name = finalTable.connection_name;
        } else {
            data = BI.extend(finalTable, data);
        }
        return data;
    }
});
