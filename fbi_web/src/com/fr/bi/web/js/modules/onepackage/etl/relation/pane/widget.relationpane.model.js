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

    //一个比较长的方法，添加或修改关联关系的
    getParsedRelation: function(currentValue){
        var self = this;
        var oldValue = this.oldRelationValue;
        var relations = this.getRelations();
        var primKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var connectionSet = relations.connectionSet;
        var added = [], removed = [];
        BI.each(oldValue, function(i, v){
            var isRemoved = true;
            BI.each(currentValue, function(j, c){
                if(c.fieldId === v.fieldId &&
                    c.relationType === v.relationType){
                    isRemoved = false;
                }
            });
            isRemoved && removed.push(v);
        });
        BI.each(currentValue, function(i, v){
            var isAdded = true;
            BI.each(oldValue, function(j, c){
                if(c.fieldId === v.fieldId &&
                    c.relationType === v.relationType){
                    isAdded = false;
                }
            });
            isAdded && added.push(v);
        });
        //删除
        BI.each(removed, function(i, r){
            var rFieldId = r.fieldId, relationType = r.relationType;
            switch (relationType){
                case BICst.RELATION_TYPE.ONE_TO_ONE:
                    //1:1，connectionSet里删除2个，primKeyMap里删除2个，foreignKeyMap里也删除两个
                    BI.each(connectionSet, function(j, c){
                        if(BI.isNull(c)){
                            return;
                        }
                        if(c.primaryKey.field_id === self.fieldId || c.primaryKey.field_id === rFieldId){
                            connectionSet.splice(j, 1);
                        }
                    });
                    BI.each(primKeyMap[self.fieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.primaryKey.field_id === self.fieldId){
                            primKeyMap[self.fieldId].splice(j, 1);
                            BI.isEmptyArray(primKeyMap[self.fieldId]) && (delete primKeyMap[self.fieldId]);
                        }
                    });
                    BI.each(primKeyMap[rFieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.primaryKey.field_id === rFieldId ){
                            primKeyMap[rFieldId].splice(j, 1);
                            BI.isEmptyArray(primKeyMap[rFieldId]) && (delete primKeyMap[rFieldId]);
                        }
                    });
                    BI.each(foreignKeyMap[self.fieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.foreignKey.field_id === self.fieldId){
                            foreignKeyMap[self.fieldId].splice(j, 1);
                            BI.isEmptyArray(foreignKeyMap[self.fieldId]) && (delete foreignKeyMap[self.fieldId]);
                        }
                    });
                    BI.each(foreignKeyMap[rFieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.foreignKey.field_id === rFieldId ){
                            foreignKeyMap[rFieldId].splice(j, 1);
                            BI.isEmptyArray(foreignKeyMap[rFieldId]) && (delete foreignKeyMap[rFieldId]);
                        }
                    });
                    break;
                case BICst.RELATION_TYPE.ONE_TO_N:
                    //1:N，connectionSet里删除1个，primKeyMap删除1个，foreignKeyMap删除1个
                    BI.each(connectionSet, function(j, c){
                        if(BI.isNull(c)){
                            return;
                        }
                        if(c.primaryKey.field_id === self.fieldId && c.foreignKey.field_id === rFieldId){
                            connectionSet.splice(j, 1);
                        }
                    });
                    BI.each(primKeyMap[self.fieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.primaryKey.field_id === self.fieldId && map.foreignKey.field_id === rFieldId ){
                            primKeyMap[self.fieldId].splice(j, 1);
                            BI.isEmptyArray(primKeyMap[self.fieldId]) && (delete primKeyMap[self.fieldId]);
                        }
                    });
                    BI.each(foreignKeyMap[rFieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.foreignKey.field_id === rFieldId && map.primaryKey.field_id === self.fieldId){
                            foreignKeyMap[rFieldId].splice(j, 1);
                            BI.isEmptyArray(foreignKeyMap[self.fieldId]) && (delete foreignKeyMap[self.fieldId]);
                        }
                    });
                    break;
                case BICst.RELATION_TYPE.N_TO_ONE:
                    //同上
                    BI.each(connectionSet, function(j, c){
                        if(BI.isNull(c)){
                            return;
                        }
                        if(c.primaryKey.field_id === rFieldId && c.foreignKey.field_id === self.fieldId){
                            connectionSet.splice(j, 1);
                        }
                    });
                    BI.each(primKeyMap[rFieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.primaryKey.field_id === rFieldId && map.foreignKey.field_id === self.fieldId ){
                            primKeyMap[rFieldId].splice(j, 1);
                            BI.isEmptyArray(primKeyMap[self.fieldId]) && (delete primKeyMap[self.fieldId]);
                        }
                    });
                    BI.each(foreignKeyMap[self.fieldId], function(j, map){
                        if(BI.isNull(map)){
                            return;
                        }
                        if(map.primaryKey.field_id === rFieldId && map.foreignKey.field_id === self.fieldId ){
                            foreignKeyMap[self.fieldId].splice(j, 1);
                            BI.isEmptyArray(foreignKeyMap[self.fieldId]) && (delete foreignKeyMap[self.fieldId]);
                        }
                    });
                    break;
            }
        });
        //添加
        BI.each(added, function(i, r){
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