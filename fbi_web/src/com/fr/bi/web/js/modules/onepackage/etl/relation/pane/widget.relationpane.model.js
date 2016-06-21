/**
 * Created by Young's on 2016/4/12.
 */
BI.RelationPaneModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.RelationPaneModel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.fieldId = o.field_id;
        this.relations = o.relations;
        this.translations = o.translations;
        this.allFields = o.all_fields;
    },

    getFieldId: function(){
        return this.fieldId;
    },

    getRelations: function() {
        return BI.deepClone(this.relations);
    },

    setRelations: function(relations) {
        this.relations = relations;
    },

    getTranslations: function(){
        return this.translations;
    },

    getAllFields: function(){
        return this.allFields;
    },

    setOldRelationValue: function(oldRelationValue){
        this.oldRelationValue = oldRelationValue;
    },

    getTableIdByFieldId: function(fieldId) {
        var field = this.allFields[fieldId];
        if(BI.isNotNull(field)) {
            return field.table_id;
        }
    },

    getTableNameByFieldId: function(fieldId) {
        var field = this.allFields[fieldId];
        if(BI.isNotNull(field)) {
            return this.translations[field.table_id];
        }
    },

    getFieldNameByFieldId: function(fieldId) {
        var field = this.allFields[fieldId];
        if(BI.isNotNull(field)) {
            return this.translations[field.field_id] || field.field_name;
        }
    },

    getFieldTypeByFieldId: function(fieldId) {
        var field = this.allFields[fieldId];
        if(BI.isNotNull(field)) {
            return field.field_type;
        }
    },

    getRelationIds: function(){
        var self = this;
        var primKeyMap = this.relations.primKeyMap, foreignKeyMap = this.relations.foreignKeyMap;
        var currentPrimKey = primKeyMap[this.fieldId] || [], currentForKey = foreignKeyMap[this.fieldId];
        var relationIds = [];
        BI.each(currentPrimKey, function(i, maps){
            var table = maps.primaryKey, relationTable = maps.foreignKey;
            //处理1:1 和 自循环
            if(table.field_id === self.fieldId && (!relationIds.contains(relationTable.field_id) || table.field_id === relationTable.field_id)){
                relationIds.push(relationTable.field_id);
            }
        });
        BI.each(currentForKey, function(i, maps){
            var table = maps.foreignKey, relationTable = maps.primaryKey;
            if(table.field_id === self.fieldId && !relationIds.contains(relationTable.field_id)){
                relationIds.push(relationTable.field_id);
            }
        });
        return relationIds;
    },

    getRelationType: function(rId){
        var self = this;
        var primKeyMap = this.relations.primKeyMap;
        if(BI.isNotNull(primKeyMap[this.fieldId]) &&
            BI.isNotNull(primKeyMap[rId])){
            var isForeign1 = false, isForeign2 = false;
            BI.some(primKeyMap[this.fieldId], function(i, pf){
                if(pf.foreignKey.field_id === rId){
                    return isForeign1 = true;
                }
            });
            BI.some(primKeyMap[rId], function(i, pf){
                if(pf.foreignKey.field_id === self.fieldId){
                    return isForeign2 = true;
                }
            });
            if((isForeign1 === true && isForeign2 === true)){
                return BICst.RELATION_TYPE.ONE_TO_ONE;
            } else if(isForeign1 === true && isForeign2 === false){
                return BICst.RELATION_TYPE.ONE_TO_N;
            } else if(isForeign1 === false && isForeign2 === true){
                return BICst.RELATION_TYPE.N_TO_ONE;
            }
        }
        if(BI.isNotNull(primKeyMap[this.fieldId]) && BI.isNull(primKeyMap[rId])){
            var isForeign = false;
            BI.some(primKeyMap[this.fieldId], function(i, pf){
                if(pf.foreignKey.field_id === rId ){
                    return isForeign = true;
                }
            });
            if(isForeign === true){
                return BICst.RELATION_TYPE.ONE_TO_N;
            }
        }
        if(BI.isNotNull(primKeyMap[rId]) && BI.isNull(primKeyMap[this.fieldId])){
            var isForeign = false;
            BI.some(primKeyMap[rId], function(i, pf){
                if(pf.foreignKey.field_id === self.fieldId ){
                    return isForeign = true;
                }
            });
            if(isForeign === true){
                return BICst.RELATION_TYPE.N_TO_ONE;
            }
        }
    },

    //找到所有的关联表，删掉所有原来的关联，添加上现在的
    getParsedRelation: function(currentValue){
        var self = this;
        var relations = this.getRelations();
        var primKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var connectionSet = relations.connectionSet;

        //删掉所有当前表的关联
        BI.each(connectionSet, function(i, c) {
            if(BI.isNotNull(c) && (c.primaryKey.field_id === self.fieldId || c.foreignKey.field_id === self.fieldId)) {
                connectionSet.splice(i, 1);
            }
        });
        delete primKeyMap[this.fieldId];
        delete foreignKeyMap[this.fieldId];
        BI.each(primKeyMap, function(id, mapArray) {
            BI.each(mapArray, function(i, map){
                if(BI.isNotNull(map) && (map.primaryKey.field_id === self.fieldId || map.foreignKey.field_id === self.fieldId)) {
                    mapArray.splice(i, 1);
                }
            });
            mapArray.length === 0 && (delete primKeyMap[id]);
        });
        BI.each(foreignKeyMap, function(id, mapArray) {
            BI.each(mapArray, function(i, map){
                if(BI.isNotNull(map) && (map.primaryKey.field_id === self.fieldId || map.foreignKey.field_id === self.fieldId)) {
                    mapArray.splice(i, 1);
                }
            });
            mapArray.length === 0 && (delete foreignKeyMap[id]);
        });

        //添加现在的关联
        BI.each(currentValue, function(i, r){
            var rFieldId = r.fieldId, relationType = r.relationType;
            if(BI.isNull(relationType)){
                return;
            }
            switch (relationType){
                case BICst.RELATION_TYPE.ONE_TO_ONE:
                    //1:1，connectionSet里加2个，primKeyMap里加2个
                    connectionSet.push({
                        primaryKey: {
                            field_id: self.fieldId
                        },
                        foreignKey: {
                            field_id: rFieldId
                        }
                    });
                    connectionSet.push({
                        primaryKey: {
                            field_id: rFieldId
                        },
                        foreignKey: {
                            field_id: self.fieldId
                        }
                    });
                    primKeyMap[self.fieldId] || (primKeyMap[self.fieldId] = []);
                    primKeyMap[self.fieldId].push({
                        primaryKey: {
                            field_id: self.fieldId
                        },
                        foreignKey: {
                            field_id: rFieldId
                        }
                    });
                    primKeyMap[rFieldId] || (primKeyMap[rFieldId] = []);
                    primKeyMap[rFieldId].push({
                        primaryKey: {
                            field_id: rFieldId
                        },
                        foreignKey: {
                            field_id: self.fieldId
                        }
                    });
                    break;
                case BICst.RELATION_TYPE.ONE_TO_N:
                    //1:N，connectionSet里加1个，primKeyMap里加一个，foreignKeyMap里加一个
                    connectionSet.push({
                        primaryKey: {
                            field_id: self.fieldId
                        },
                        foreignKey: {
                            field_id: rFieldId
                        }
                    });
                    primKeyMap[self.fieldId] || (primKeyMap[self.fieldId] = []);
                    primKeyMap[self.fieldId].push({
                        primaryKey: {
                            field_id: self.fieldId
                        },
                        foreignKey: {
                            field_id: rFieldId
                        }
                    });
                    foreignKeyMap[rFieldId] || (foreignKeyMap[rFieldId] = []);
                    foreignKeyMap[rFieldId].push({
                        primaryKey: {
                            field_id: self.fieldId
                        },
                        foreignKey: {
                            field_id: rFieldId
                        }
                    });
                    break;
                case BICst.RELATION_TYPE.N_TO_ONE:
                    //同上
                    connectionSet.push({
                        primaryKey: {
                            field_id: rFieldId
                        },
                        foreignKey: {
                            field_id: self.fieldId
                        }
                    });
                    primKeyMap[rFieldId] || (primKeyMap[rFieldId] = []);
                    primKeyMap[rFieldId].push({
                        primaryKey: {
                            field_id: rFieldId
                        },
                        foreignKey: {
                            field_id: self.fieldId
                        }
                    });
                    foreignKeyMap[self.fieldId] || (foreignKeyMap[self.fieldId] = []);
                    foreignKeyMap[self.fieldId].push({
                        primaryKey: {
                            field_id: rFieldId
                        },
                        foreignKey: {
                            field_id: self.fieldId
                        }
                    });
                    break;
            }
        });
        return relations;
    }
});