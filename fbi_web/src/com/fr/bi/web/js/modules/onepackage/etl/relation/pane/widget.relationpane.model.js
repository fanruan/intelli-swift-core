/**
 * Created by Young's on 2016/4/12.
 */
BI.RelationPaneModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.RelationPaneModel.superclass._init.apply(this, arguments);
    },

    getFieldId: function () {
        return this.options.field.id;
    },

    getFieldNameByField: function (field) {
        var tranName = field[BICst.JSON_KEYS.FIELD_TRAN_NAME];
        var fieldName = field.field_name;
        return BI.isNotNull(tranName) ? (tranName + "(" + fieldName + ")") : fieldName;
    },

    getRelationFields: function () {
        var o = this.options;
        return BI.Utils.getRelationFieldsByFieldId4Conf(o.relations, o.field.id);
    },

    hasTableAuthByTableId: function (tableId) {
        return BI.Utils.hasTableAuthByTableId4Conf(tableId);
    },

    getRelationType: function (relationId) {
        var o = this.options;
        var baseId = o.field.id, relations = o.relations;
        var primKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var type;
        if (BI.isNotNull(primKeyMap[baseId])) {
            BI.some(primKeyMap[baseId], function(i, key) {
                if (key.foreignKey.field_id === relationId) {
                    type = BICst.RELATION_TYPE.ONE_TO_N;
                    return true;
                }
            });
        }
        if (BI.isNotNull(foreignKeyMap[baseId])) {
            BI.some(foreignKeyMap[baseId], function(i, key) {
                if (key.primaryKey.field_id === relationId) {
                    if (type === BICst.RELATION_TYPE.ONE_TO_N) {
                        type = BICst.RELATION_TYPE.ONE_TO_ONE;
                    } else {
                        type = BICst.RELATION_TYPE.N_TO_ONE;
                    }
                    return true;
                }
            });
        }
        return type;
    },

    getCacheData: function () {
        return this.cacheData;
    },

    getFieldById: function (fieldId) {
        var fields = this.cacheData.fields;
        var translations = this.cacheData.translations;
        var field = fields[fieldId];
        field[BICst.JSON_KEYS.TABLE_TRAN_NAME] = translations[field.table_id];
        field[BICst.JSON_KEYS.FIELD_TRAN_NAME] = translations[field.id];
        return field;
    },

    assertCacheData: function (callback) {
        var self = this;
        if (BI.isNull(this.cacheData)) {
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: BICst.BODY_ELEMENT,
                text: BI.i18nText("BI-Basic_Loading")
            });
            BI.Utils.getData4SelectField4Conf(function (data) {
                self.cacheData = data;
                callback();
            }, function () {
                mask.destroy();
            });
        } else {
            callback();
        }

    },

    //找到所有的关联表，删掉所有原来的关联，添加上现在的
    getParsedRelation: function (currentValue) {
        var field = this.options.field,
            relations = {},
            primKeyMap = {},
            foreignKeyMap = {},
            connectionSet = [];
        field.field_id = field.id;
        //添加现在的关联
        BI.each(currentValue, function (i, r) {
            var rField = r.field, relationType = r.relationType;
            if (BI.isNull(relationType)) {
                return;
            }
            rField.field_id = rField.id;
            switch (relationType) {
                case BICst.RELATION_TYPE.ONE_TO_ONE:
                    //1:1，connectionSet里加2个，primKeyMap里加2个
                    connectionSet.push({
                        primaryKey: field,
                        foreignKey: rField
                    });
                    connectionSet.push({
                        primaryKey: rField,
                        foreignKey: field
                    });
                    primKeyMap[field.id] || (primKeyMap[field.id] = []);
                    primKeyMap[field.id].push({
                        primaryKey: field,
                        foreignKey: rField
                    });
                    primKeyMap[rField.id] || (primKeyMap[rField.id] = []);
                    primKeyMap[rField.id].push({
                        primaryKey: rField,
                        foreignKey: field
                    });
                    break;
                case BICst.RELATION_TYPE.ONE_TO_N:
                    //1:N，connectionSet里加1个，primKeyMap里加一个，foreignKeyMap里加一个
                    connectionSet.push({
                        primaryKey: field,
                        foreignKey: rField
                    });
                    primKeyMap[field.id] || (primKeyMap[field.id] = []);
                    primKeyMap[field.id].push({
                        primaryKey: field,
                        foreignKey: rField
                    });
                    foreignKeyMap[rField.id] || (foreignKeyMap[rField.id] = []);
                    foreignKeyMap[rField.id].push({
                        primaryKey: field,
                        foreignKey: rField
                    });
                    break;
                case BICst.RELATION_TYPE.N_TO_ONE:
                    //同上
                    connectionSet.push({
                        primaryKey: rField,
                        foreignKey: field
                    });
                    primKeyMap[rField.id] || (primKeyMap[rField.id] = []);
                    primKeyMap[rField.id].push({
                        primaryKey: rField,
                        foreignKey: field
                    });
                    foreignKeyMap[field.id] || (foreignKeyMap[field.id] = []);
                    foreignKeyMap[field.id].push({
                        primaryKey: rField,
                        foreignKey: field
                    });
                    break;
            }
        });
        relations.connectionSet = connectionSet;
        relations.primKeyMap = primKeyMap;
        relations.foreignKeyMap = foreignKeyMap;
        return relations;
    }
});