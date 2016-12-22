/**
 * Created by Young's on 2016/4/12.
 */
BI.RelationPaneModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.RelationPaneModel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.fieldId = o.field.id;
    },

    getFieldId: function () {
        return this.fieldId;
    },

    getRelations: function () {
        return BI.deepClone(this.relations);
    },

    setRelations: function (relations) {
        this.relations = relations;
    },

    getTranslations: function () {
        return this.translations;
    },

    getAllFields: function () {
        return this.allFields;
    },

    setOldRelationValue: function (oldRelationValue) {
        this.oldRelationValue = oldRelationValue;
    },

    getTableIdByFieldId: function (fieldId) {
        return BI.Utils.getTableIdByFieldId4Conf(fieldId);
    },

    getTableNameByFieldId: function (fieldId) {
        var tableId = BI.Utils.getTableIdByFieldId4Conf(fieldId);
        return BI.Utils.getTransNameById4Conf(tableId);
    },

    getFieldNameByFieldId: function (fieldId) {
        var tranName = BI.Utils.getTransNameById4Conf(fieldId);
        var fieldName = BI.Utils.getFieldNameById4Conf(fieldId);
        return BI.isNotNull(tranName) ? (tranName + "(" + fieldName + ")") : fieldName;
    },

    getFieldTypeByFieldId: function (fieldId) {
       return BI.Utils.getFieldTypeById4Conf(fieldId);
    },

    getRelationIds: function () {
        return BI.Utils.getRelationFieldsByFieldId4Conf(this.fieldId);
    },

    getRelationType: function(fieldId) {
        return BI.Utils.getRelationTypeById4Conf(this.fieldId, fieldId);
    },

    //找到所有的关联表，删掉所有原来的关联，添加上现在的
    getParsedRelation: function (currentValue) {
        var self = this;
        var relations = this.getRelations();
        var primKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var connectionSet = relations.connectionSet;

        //删掉所有当前表的关联
        BI.remove(connectionSet, function (i, c) {
            return BI.isNotNull(c) && (c.primaryKey.field_id === self.fieldId || c.foreignKey.field_id === self.fieldId);
        }, self);
        delete primKeyMap[this.fieldId];
        delete foreignKeyMap[this.fieldId];
        BI.each(primKeyMap, function (id, mapArray) {
            BI.remove(mapArray, function (i, map) {
                return BI.isNotNull(map) && (map.primaryKey.field_id === self.fieldId || map.foreignKey.field_id === self.fieldId);
            }, self);
            mapArray.length === 0 && (delete primKeyMap[id]);
        });
        BI.each(foreignKeyMap, function (id, mapArray) {
            BI.remove(mapArray, function (i, map) {
                return BI.isNotNull(map) && (map.primaryKey.field_id === self.fieldId || map.foreignKey.field_id === self.fieldId);
            }, self);
            mapArray.length === 0 && (delete foreignKeyMap[id]);
        });

        //添加现在的关联
        BI.each(currentValue, function (i, r) {
            var rFieldId = r.fieldId, relationType = r.relationType;
            if (BI.isNull(relationType)) {
                return;
            }
            switch (relationType) {
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