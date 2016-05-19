/**
 * @class BI.CircleModel
 * @extends BI.Widget
 * @author windy
 */
BI.CircleModel = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.CircleModel.superclass._defaultConfig.apply(this, arguments), {
            info: {}
        })
    },

    _init: function(){
        BI.CircleModel.superclass._init.apply(this, arguments);
        this.populate(this.options.info);
    },

    getDefaultTableName: function(){
        var self = this;
        if(BI.isNotNull(this.old_tables.table_name)){
            return this.old_tables.table_name + "_circle";
        }
        var tables = this.tables;
        var tableName = [];
        function getDefaultName(tables){
            //只取tables[0]
            if(BI.isNotNull(tables[0].etl_type)){
                tableName.push("_" + tables[0].etl_type);
                getDefaultName(tables[0].tables);
            } else {
                tableName.push(tables[0].table_name);
            }
        }
        getDefaultName(tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function(i, name){
            tableNameString += name;
        });
        if(self.reopen === true){
            tableNameString += "_circle";
        } else {
            tableNameString = tableNameString + "_" + self.old_tables.etl_type + "_circle";
        }
        return tableNameString;
    },

    /**
     * 预览时所需数据参数
     */
    getPreTableStructure: function(){
        return this.old_tables;
    },

    getId: function(){
        return this.id;
    },

    getTableStructure: function(){
        var tables = {};
        if(this.reopen === true){
            tables = this.tables[0];
        } else {
            tables = this.old_tables;
        }
        return tables;
    },

    getTablesDetailInfoByTables: function(callback){
        BI.Utils.getTablesDetailInfoByTables([this.getTableStructure()], function(data){
            callback(data);
        });
    },

    getValue: function () {
        var self = this;
        var etlValue = this.getOperatorValue();
        var relations = this.getRelations(), tId = this.getId();
        var primKeyMap = relations["primKeyMap"], foreignKeyMap = relations["foreignKeyMap"];
        var connectionSet = relations["connectionSet"];
        //先刪除上一次的关联
        if (this.isReopen() === true) {
            var table = this.getPreTableStructure();
            var oldFloorNames = BI.pluck(table.etl_value.floors, "name");
            var oldIdFieldName = table.etl_value.id_field_name;
            var tmpConnectionSet = [], tmpPrimKey = [], tmpForeKey = [];
            var oldRelationForeignKeyIds = BI.map(oldFloorNames, function(idx, name){
                return tId + name;
            });
            var oldRelationPrimaryKeyId = tId + oldIdFieldName;
            BI.each(connectionSet, function (j, c) {
                if (BI.isNull(c)) {
                    return;
                }
                if(c["primaryKey"].field_id === tId + oldIdFieldName && BI.contains(oldRelationForeignKeyIds, c["foreignKey"].field_id)){
                    tmpConnectionSet.push(c);
                }
            });
            BI.each(primKeyMap[oldRelationPrimaryKeyId], function (j, map) {
                if (BI.isNull(map)) {
                    return;
                }
                if(map["primaryKey"].field_id === tId + oldIdFieldName && BI.contains(oldRelationForeignKeyIds, map["foreignKey"].field_id)){
                    tmpPrimKey.push(map);
                }
            });
            BI.each(oldRelationForeignKeyIds, function(idx, id){
                BI.each(foreignKeyMap[id], function (j, map) {
                    if (BI.isNull(map)) {
                        return;
                    }
                    if(map["primaryKey"].field_id === oldRelationPrimaryKeyId && id === map["foreignKey"].field_id){
                        tmpForeKey.push(map);
                    }
                });
            });
            BI.remove(connectionSet, tmpConnectionSet);
            BI.remove(primKeyMap[tId + oldIdFieldName], tmpPrimKey);
            BI.each(oldRelationForeignKeyIds, function(idx, id){
                BI.remove(foreignKeyMap[id], tmpForeKey);
            });
        }
        //设置1:N的关联
        BI.each(etlValue.floors, function (idx, floor) {
            var primaryId = tId + etlValue.id_field_name;
            var foreignId = tId + floor.name;
            connectionSet.push({
                primaryKey: {
                    field_id: primaryId
                },
                foreignKey: {
                    field_id: foreignId
                }
            });
            if(!primKeyMap[primaryId]){
                primKeyMap[primaryId] = [];
            }
            primKeyMap[primaryId].push({
                primaryKey: {
                    field_id: primaryId
                },
                foreignKey: {
                    field_id: foreignId
                }
            });
            if(!foreignKeyMap[foreignId]){
                foreignKeyMap[foreignId] = [];
            }
            foreignKeyMap[foreignId].push({
                primaryKey: {
                    field_id: primaryId
                },
                foreignKey: {
                    field_id: foreignId
                }
            });
        });

        return {
            relations: relations,
            etl_type: "circle",
            etl_value: this.getOperatorValue(),
            connection_name: BICst.CONNECTION.ETL_CONNECTION,
            tables: [this.getTableStructure()]
        }
    },

    getRelations: function(){
        return BI.deepClone(this.relations);
    },

    setOperatorValue: function(value){
        this.operator = value;
    },

    getOperatorValue: function(){
        return BI.deepClone(this.operator);
    },

    isCubeGenerated: function(){
        return this.isGenerated;
    },

    isReopen: function(){
        return this.reopen;
    },

    populate: function (info) {
        this.id = info.id;
        this.reopen = info.reopen;
        this.relations = info.relations;
        this.tables = info.tableInfo.tables;
        this.old_tables = BI.extend(info.tableInfo, {id: BI.UUID()});
        this.isGenerated = info.isGenerated;
    }
});