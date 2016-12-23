/**
 * 配置部分常用utils
 * 添加或修改注意加 4Conf 后缀
 * SharingPool 中维护 translations, relations, fields
 */
BI.extend(BI.Utils, {

    getCurrentPackage4Conf: function () {
        return BI.firstObject(Data.SharingPool.get(BICst.CURRENT_EDITING_PACKAGE));
    },

    getCurrentPackageName4Conf: function () {
        var pack = BI.firstObject(Data.SharingPool.get(BICst.CURRENT_EDITING_PACKAGE));
        return pack.name;
    },

    getCurrentPackageId4Conf: function () {
        return BI.firstKey(Data.SharingPool.cat(BICst.CURRENT_EDITING_PACKAGE));
    },

    getCurrentPackageTables4Conf: function () {
        var pack = BI.firstObject(Data.SharingPool.get(BICst.CURRENT_EDITING_PACKAGE));
        return pack.tables;
    },

    //转义 表名和字段名
    getTransNameById4Conf: function (id) {
        return Data.SharingPool.cat("translations")[id];
    },

    updateTranName4Conf: function (id, tranName) {
        var translation = Data.SharingPool.cat("translations");
        translation[id] = tranName;
    },

    updateFields4Conf: function (id, field) {
        var fields = Data.SharingPool.cat("fields");
        fields[id] = field;
    },

    getFieldNameById4Conf: function (id) {
        var field = Data.SharingPool.cat("fields")[id];
        if (BI.isNotNull(field)) {
            return field.field_name;
        }
    },

    getFieldsByTableId4Conf: function (tableId) {
        var allFields = Data.SharingPool.cat("fields");
        var fields = [];
        BI.each(allFields, function (id, field) {
            if (field.table_id === tableId) {
                fields.push(BI.deepClone(field));
            }
        });
        return fields;
    },

    getFieldTypeById4Conf: function (id) {
        var field = Data.SharingPool.cat("fields")[id];
        if (BI.isNotNull(field)) {
            return field.field_type;
        }
    },

    getFieldUsableById4Conf: function (id) {
        var field = Data.SharingPool.cat("fields")[id];
        if (BI.isNotNull(field)) {
            return field.is_usable;
        }
    },

    getFieldIdByNameAndTableId4Conf: function (name, tableId) {
        var fields = Data.SharingPool.cat("fields");
        var id;
        BI.some(fields, function (i, field) {
            if (field.field_name === name && field.table_id === tableId) {
                id = field.id;
                return true;
            }
        });
        return id;
    },

    //检查表名或者字段名的合法性
    checkTranNameById4Conf: function (id, name) {
        var fields = Data.SharingPool.cat("fields");
        var translations = Data.SharingPool.cat("translations");
        var isValid = true;
        if (BI.isNotNull(fields[id])) {
            var tableId = fields[id].table_id;
            BI.some(fields, function (fId, field) {
                if (fId !== id && tableId === field.table_id &&
                    (name === field.field_name || name === translations[field.id])) {
                    isValid = false;
                    return true;
                }
            });
        } else {
            var packId = this.getPackageIdByTableId4Conf(id);
            var tables = this.getTablesIdByPackageId4Conf(packId);
            BI.some(tables, function (i, table) {
                if (table.id !== id && name === translations[table.id]) {
                    isValid = false;
                    return true;
                }
            });
        }
        return isValid;
    },

    getPackageIdByTableId4Conf: function (tableId) {
        var packages = Data.SharingPool.cat("packages");
        var packId;
        BI.some(packages, function (pId, pack) {
            var tables = pack.tables;
            return BI.some(tables, function (i, table) {
                if (table.id === tableId) {
                    packId = pId;
                    return true;
                }
            })
        });
        return packId;
    },

    getTablesIdByPackageId4Conf: function (packId) {
        var packages = Data.SharingPool.cat("packages");
        var tableIds = [];
        BI.each(packages[packId].tables, function (i, table) {
            tableIds.push(table.id);
        });
        return tableIds;
    },

    updateTableIdsInPackage4Conf: function (packId, tableIds) {
        var packages = Data.SharingPool.cat("packages");
        if (BI.isNull(packages[packId])) {
            packages[packId] = {};
        }
        var idsOB = [];
        BI.each(tableIds, function (i, tId) {
            idsOB.push({id: tId});
        });
        packages[packId].tables = idsOB;
    },

    getTableIdByFieldId4Conf: function (fieldId) {
        var field = Data.SharingPool.cat("fields")[fieldId];
        if (BI.isNotNull(field)) {
            return field.table_id;
        }
    },

    //同步读取到的关联
    saveReadRelation4Conf: function (newRelations, fieldId) {
        if (BI.isNotNull(fieldId)) {
            this.removeRelationByFieldId4Conf(fieldId);
        }
        var relations = Data.SharingPool.cat("relations"),
            connectionSet = relations.connectionSet,
            primKeyMap = relations.primKeyMap,
            foreignKeyMap = relations.foreignKeyMap;
        BI.each(newRelations, function (i, read) {
            var pk = read.primaryKey, fk = read.foreignKey;
            var isExist = false;
            BI.each(connectionSet, function (j, conn) {
                var p = conn.primaryKey, f = conn.foreignKey;
                if (p.field_id === pk.field_id && f.field_id === fk.field_id) {
                    isExist = true;
                }
            });
            //读取到的关联都是1:N的
            if (isExist === false) {
                connectionSet.push(read);
                primKeyMap[pk.field_id] || (primKeyMap[pk.field_id] = []);
                foreignKeyMap[fk.field_id] || (foreignKeyMap[fk.field_id] = []);
                primKeyMap[pk.field_id].push(read);
                foreignKeyMap[fk.field_id].push(read);
            }
        });
    },

    // 保存关联
    saveRelations4Conf: function (nreRelations, fieldId) {
        //先删除已存在的
        if (BI.isNotNull(fieldId)) {
            this.removeRelationByFieldId4Conf(fieldId);
        }
        var relations = Data.SharingPool.cat("relations");
        var nConn = nreRelations.connectionSet, nPKMap = nreRelations.primKeyMap, nFKMap = nreRelations.foreignKeyMap;
        var connectionSet = relations.connectionSet,
            primKeyMap = relations.primKeyMap,
            foreignKeyMap = relations.foreignKeyMap;
        BI.each(nConn, function (i, conn) {
            connectionSet.push((conn));
        });
        BI.extend(primKeyMap, nPKMap);
        BI.extend(foreignKeyMap, nFKMap);
    },

    //获取关联字段
    getRelationFieldsByFieldId4Conf: function (fieldId) {
        var relations = Data.SharingPool.cat("relations");
        var translations = Data.SharingPool.cat("translations");
        var primKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var currentPrimKey = primKeyMap[fieldId] || [], currentForKey = foreignKeyMap[fieldId];
        var relationIds = [];

        BI.each(currentPrimKey, function (i, maps) {
            var table = maps.primaryKey, relationTable = maps.foreignKey;
            //处理1:1 和 自循环
            if (table.field_id === fieldId && (!relationIds.contains(relationTable.field_id) || table.field_id === relationTable.field_id)) {
                relationIds.push(relationTable.field_id);
            }
        });
        BI.each(currentForKey, function (i, maps) {
            var table = maps.foreignKey, relationTable = maps.primaryKey;
            if (table.field_id === fieldId && !relationIds.contains(relationTable.field_id)) {
                relationIds.push(relationTable.field_id);
            }
        });
        return relationIds;
    },

    //是否是主键字段
    isPrimaryKeyByFieldId4Conf: function (fieldId) {
        var relations = Data.SharingPool.cat("relations");
        var primKeyMap = relations.primKeyMap;
        var currentPrimKey = primKeyMap[fieldId] || [];
        return BI.some(currentPrimKey, function (i, maps) {
            var pk = maps.primaryKey, fk = maps.foreignKey;
            return pk.field_id === fieldId && fk.field_id !== fieldId;
        });
    },

    //删除表删除相关关联 和 字段
    removeRelationByTableId4Conf: function (tableId) {
        var self = this;
        var relations = Data.SharingPool.cat("relations");
        var fields = Data.SharingPool.cat("fields");
        var connectionSet = relations.connectionSet,
            primaryKeyMap = relations.primKeyMap,
            foreignKeyMap = relations.foreignKeyMap;
        var resultConnectionSet = [];
        BI.each(connectionSet, function (i, keys) {
            var primKey = keys.primaryKey, foreignKey = keys.foreignKey;
            if (!(self.getTableIdByFieldId4Conf(primKey.field_id) === tableId || self.getTableIdByFieldId4Conf(foreignKey.field_id) === tableId)) {
                resultConnectionSet.push(connectionSet[i])
            }
        });
        relations.connectionSet = resultConnectionSet;
        BI.each(primaryKeyMap, function (kId, maps) {
            if (self.getTableIdByFieldId4Conf(kId) === tableId) {
                delete primaryKeyMap[kId];
            } else {
                BI.remove(maps, function (i, keys) {
                    return tableId === self.getTableIdByFieldId4Conf(keys.primaryKey.field_id) || tableId === self.getTableIdByFieldId4Conf(keys.foreignKey.field_id);
                });
                if (primaryKeyMap[kId].length === 0) {
                    delete primaryKeyMap[kId];
                }
            }
        });
        BI.each(foreignKeyMap, function (kId, maps) {
            if (tableId === self.getTableIdByFieldId4Conf(kId)) {
                delete foreignKeyMap[kId];
            } else {
                BI.remove(maps, function (i, keys) {
                    return tableId === self.getTableIdByFieldId4Conf(keys.primaryKey.field_id) || tableId === self.getTableIdByFieldId4Conf(keys.foreignKey.field_id);
                });
                if (foreignKeyMap[kId].length === 0) {
                    delete foreignKeyMap[kId];
                }
            }
        });
    },

    removeRelationByFieldId4Conf: function (fieldId) {
        var relations = Data.SharingPool.cat("relations");
        var fields = Data.SharingPool.cat("fields");
        var connectionSet = relations.connectionSet,
            primaryKeyMap = relations.primKeyMap,
            foreignKeyMap = relations.foreignKeyMap;
        var resultConnectionSet = [];
        BI.each(connectionSet, function (i, keys) {
            var primKey = keys.primaryKey, foreignKey = keys.foreignKey;
            if (!(primKey.field_id === fieldId || foreignKey.field_id === fieldId)) {
                resultConnectionSet.push(connectionSet[i])
            }
        });
        relations.connectionSet = resultConnectionSet;
        BI.each(primaryKeyMap, function (kId, maps) {
            if (kId === fieldId) {
                delete primaryKeyMap[kId];
            } else {
                BI.remove(maps, function (i, keys) {
                    return fieldId === keys.primaryKey.field_id || fieldId === keys.foreignKey.field_id;
                });
                if (primaryKeyMap[kId].length === 0) {
                    delete primaryKeyMap[kId];
                }
            }
        });
        BI.each(foreignKeyMap, function (kId, maps) {
            if (fieldId === kId) {
                delete foreignKeyMap[kId];
            } else {
                BI.remove(maps, function (i, keys) {
                    return fieldId === keys.primaryKey.field_id || fieldId === keys.foreignKey.field_id;
                });
                if (foreignKeyMap[kId].length === 0) {
                    delete foreignKeyMap[kId];
                }
            }
        });
    },

    //关联类型
    getRelationTypeById4Conf: function (baseFieldId, relationId) {
        var relations = Data.SharingPool.cat("relations");
        var primKeyMap = relations.primKeyMap;
        if (BI.isNotNull(primKeyMap[baseFieldId]) &&
            BI.isNotNull(primKeyMap[relationId])) {
            var isForeign1 = false, isForeign2 = false;
            BI.some(primKeyMap[baseFieldId], function (i, pf) {
                if (pf.foreignKey.field_id === relationId) {
                    return isForeign1 = true;
                }
            });
            BI.some(primKeyMap[relationId], function (i, pf) {
                if (pf.foreignKey.field_id === baseFieldId) {
                    return isForeign2 = true;
                }
            });
            if ((isForeign1 === true && isForeign2 === true)) {
                return BICst.RELATION_TYPE.ONE_TO_ONE;
            } else if (isForeign1 === true && isForeign2 === false) {
                return BICst.RELATION_TYPE.ONE_TO_N;
            } else if (isForeign1 === false && isForeign2 === true) {
                return BICst.RELATION_TYPE.N_TO_ONE;
            }
        }
        if (BI.isNotNull(primKeyMap[baseFieldId]) && BI.isNull(primKeyMap[relationId])) {
            var isForeign = false;
            BI.some(primKeyMap[baseFieldId], function (i, pf) {
                if (pf.foreignKey.field_id === relationId) {
                    return isForeign = true;
                }
            });
            if (isForeign === true) {
                return BICst.RELATION_TYPE.ONE_TO_N;
            }
        }
        if (BI.isNotNull(primKeyMap[relationId]) && BI.isNull(primKeyMap[baseFieldId])) {
            var isForeign = false;
            BI.some(primKeyMap[relationId], function (i, pf) {
                if (pf.foreignKey.field_id === baseFieldId) {
                    return isForeign = true;
                }
            });
            if (isForeign === true) {
                return BICst.RELATION_TYPE.N_TO_ONE;
            }
        }
    },


    /**
     * 获取所有业务包分组信息树结构
     * @returns {Array}
     */
    getAllGroupedPackagesTreeJSON4Conf: function () {
        var groups = Data.SharingPool.get("groups"), packages = Data.SharingPool.get("packages");
        var packStructure = [], groupedPacks = [];
        BI.each(groups, function (id, group) {
            packStructure.push({
                id: id,
                text: group.name,
                isParent: true
            });
            BI.each(group.children, function (i, item) {
                packStructure.push({
                    id: item.id,
                    text: packages[item.id].name,
                    value: item.id,
                    pId: id
                });
                groupedPacks.push(item.id);
            })
        });
        BI.each(packages, function (id, pack) {
            var isGrouped = false;
            BI.any(groupedPacks, function (i, pId) {
                if (pId === id) {
                    isGrouped = true;
                    return false;
                }
            });
            //未分组
            if (!isGrouped) {
                packStructure.push({
                    text: pack.name,
                    value: pack.id
                })
            }
        });
        return packStructure;
    },

    /**
     * 获取所有业务包分组信息树结构
     * 用于业务包权限管理功能
     * @returns {Array}
     */
    getAllGroupedPackagesTree: function () {
        var groupMap = Data.SharingPool.get("groups"), packages = Data.SharingPool.get("packages");
        var packStructure = [], groupedPacks = [];
        //排个序
        var groups = BI.sortBy(groupMap, function (id, item) {
            return item.init_time;
        });
        BI.each(groups, function (i, group) {
            packStructure.push({
                id: group.id,
                text: group.name,
                title: group.name,
                value: group.name,
                open: true,
                isParent: true
            });
            BI.each(group.children, function (i, item) {
                packStructure.push({
                    id: item.id,
                    text: packages[item.id].name,
                    title: packages[item.id].name,
                    value: item.id,
                    pId: group.id,
                    open: true
                });
                groupedPacks.push(item.id);
            })
        });

        var isGroupedExisted = false;
        BI.each(packages, function (id, pack) {
            var isGrouped = false;
            BI.any(groupedPacks, function (i, pId) {
                if (pId === id) {
                    isGrouped = true;
                    return false;
                }
            });
            //未分组
            if (!isGrouped) {
                isGroupedExisted = true;
                packStructure.push({
                    text: pack.name,
                    title: pack.name,
                    value: pack.id,
                    pId: 1,
                    id: id,
                    open: true
                })
            }
        });
        if (isGroupedExisted === true) {
            packStructure.push({
                text: BI.i18nText('BI-Ungrouped'),
                title: BI.i18nText('BI-Ungrouped'),
                value: BI.i18nText('BI-Ungrouped'),
                id: 1,
                open: true,
                isParent: true
            });
        }
        return packStructure;

    },

    getAllPackageIDs4Conf: function () {
        return BI.keys(Data.SharingPool.cat("packages"));
    },

    getPackageNameByID4Conf: function (packageId) {
        return Data.SharingPool.cat("packages")[packageId].name;
    },

    getTableIDsOfPackageID4Conf: function (packageId) {
        //这里要取最新的，从shared中
        if (packageId === this.getCurrentPackageId4Conf()) {
            return BI.keys(this.getCurrentPackageTables4Conf());
        } else {
            var packages = Data.SharingPool.cat("packages");
            return BI.pluck(packages[packageId].tables, "id");
        }
    },

    getConfPackageGroupIDs: function () {
        return BI.keys(Data.SharingPool.cat("groups"));
    },

    getGroupNameById4Conf: function (gid) {
        var groups = Data.SharingPool.cat("groups");
        if (BI.isNotNull(groups[gid])) {
            return groups[gid].name;
        }
        return "";
    },

    getConfGroupChildrenByGroupId: function (gid) {
        var groups = Data.SharingPool.get("groups");
        if (BI.isNotNull(groups[gid])) {
            return groups[gid].children;
        }
        return [];
    },

    getConfGroupInitTimeByGroupId: function (gid) {
        var groups = Data.SharingPool.get("groups");
        if (BI.isNotNull(groups[gid])) {
            return groups[gid].init_time;
        }
        return "";
    },

    getConfPackageTablesByID: function (pid) {
        return Data.SharingPool.get("packages", pid, "tables");
    },

    getConfAllPackageIDs: function () {
        return BI.keys(Data.SharingPool.get("packages"));
    },

    getConfPackageNameByID: function (pid) {
        return Data.SharingPool.get("packages", pid, "name");
    },

    getUpdateSettingByID: function (id) {
        return Data.SharingPool.get("update_settings", id);
    },

    getAuthorityLoginField: function () {
        return Data.SharingPool.get("authority_settings", "login_field");
    },

    getAuthorityRoles: function () {
        return Data.SharingPool.get("authority_settings", "all_roles");
    },

    getPackageAuthorityByID: function (pid) {
        return Data.SharingPool.get("authority_settings", "packages_auth", pid);
    },

    getFormulaStringFromFormulaValue: function (formulaValue) {
        var formulaString = "";
        var regx = /[\+\-\*\/\(\),"'\[\]&^%#@!~`:;><?.]|\w[^\$\(\)\+\-\*\/]*\w|\w[^\$\(\+\-\*\/]*\w|\$\{[^\$\(\)\+\-\*\/)\$,]*\w\}|\$\{[^\$\(\)\+\-\*\/]*\w\}|\$\{[^\$\(\)\+\-\*\/]*[\u4e00-\u9fa5]\}|\w/g;
        var result = formulaValue.match(regx);
        BI.each(result, function (i, item) {
            var fieldRegx = /\$[\{][^\}]*[\}]/;
            var str = item.match(fieldRegx);
            if (BI.isNotEmptyArray(str)) {
                formulaString = formulaString + str[0].substring(2, item.length - 1);
            } else {
                formulaString = formulaString + item;
            }
        });
        return formulaString;
    },

    getTableNameByFieldId4Conf: function (fieldId) {
        var translations = Data.SharingPool.get("translations");
        var tableId = this.getTableIdByFieldId4Conf(fieldId);
        if (BI.isNotNull(tableId)) {
            return translations[tableId]
        }
    }

});


/**
 * config utils by requests
 */
BI.extend(BI.Utils, {

    getConfDataByField: function (table, fieldName, filterConfig, callback, complete) {
        Data.Req.reqFieldsDataByData({
            table: table,
            field: fieldName,
            filterConfig: filterConfig
        }, function (data) {
            callback(data.value, data.hasNext);
        }, complete);
    },

    getConfDataByFieldId: function (fieldId, filterConfig, callback, complete) {
        Data.Req.reqFieldsDataByFieldId({
            field_id: fieldId,
            filterConfig: filterConfig
        }, function (data) {
            callback(data.value, data.hasNext);
        }, complete);
    },

    savePackageAuthority: function (data, callback, complete) {
        Data.Req.reqSavePackageAuthority(data, function (res) {
            callback(res);
        }, complete);
    },

    getCircleLayerLevelInfo: function (table, layerInfo, callback, complete) {
        Data.Req.reqCircleLayerLevelInfoByTableAndCondition(table, layerInfo,
            function (res) {
                callback(res);
            }, complete);
    },

    getConfNumberFieldMaxMinValue: function (table, fieldName, callback, complete) {
        Data.Req.reqNumberFieldMaxMinValue(table, fieldName,
            function (res) {
                callback(res);
            }, complete)
    },

    updateTablesOfOnePackage: function (data, callback, complete) {
        Data.Req.reqUpdateTablesOfOnePackage(data, function (res) {
            callback(res);
        }, complete)
    },

    updateRelation4Conf: function (data, callback, complete) {
        Data.Req.reqUpdateRelation(data, function (res) {
            callback(res);
        }, complete)
    },

    getExcelHTMLView: function (fileId, callback, complete) {
        Data.Req.reqGetExcelHTMLView({fileId: fileId}, function (res) {
            callback(res);
        }, complete)
    },

    saveFileGetExcelData: function (fileId, callback, complete) {
        Data.Req.reqSaveFileGetExcelData({fileId: fileId}, function (res) {
            callback(res);
        }, complete)
    },

    getExcelDataByFileName: function (fullFileName, callback, complete) {
        Data.Req.reqExcelDataByFileName({fileName: fullFileName}, function (res) {
            callback(res);
        }, complete)
    },

    saveDataLink: function (data, callback, complete) {
        Data.Req.reqSaveDataLink(data, function () {
            callback();
        }, complete);
    },

    addNewTables4Conf: function (data, callback, complete) {
        Data.Req.reqAddNewTables(data, function (res) {
            callback(res);
        }, complete);
    },

    removeTableById4Conf: function (data, callback, complete) {
        Data.Req.reqRemoveTable(data, function (res) {
            callback(res);
        }, complete);
    },

    updateOneTable4Conf: function (data, callback, complete) {
        Data.Req.reqUpdateOneTable(data, function (res) {
            callback(res);
        }, complete);
    },

    getCubePath: function (callback, complete) {
        Data.Req.reqCubePath(function (path) {
            callback(path);
        }, complete);
    },

    checkCubePath: function (path, callback, complete) {
        Data.Req.reqCheckCubePath(path, function (res) {
            callback(res);
        }, complete);
    },

    saveCubePath: function (path, callback, complete) {
        Data.Req.reqSaveCubePath(path, function (res) {
            callback(res);
        }, complete)
    },

    saveLoginField: function (data, callback, complete) {
        Data.Req.reqSaveLoginField(data, function (res) {
            callback(res);
        }, complete)
    },

    getConnectionNames: function (callback, complete) {
        Data.Req.reqConnectionName(function (res) {
            callback(res);
        }, complete)
    },

    getTablesByConnectionName: function (connectionName, callback, complete) {
        BIReq.reqTablesByConnectionName(connectionName, function (res) {
            callback(res);
        }, complete);
    },

    getTablesByPackId: function (packId, callback, complete) {
        BIReq.reqTablesByPackId(packId, function (res) {
            callback(res);
        }, complete)
    },

    getSimpleTablesByPackId: function (data, callback, complete) {
        BIReq.reqSimpleTablesByPackId(data, function (res) {
            callback(res);
        }, complete);
    },

    getTableInfoByTableId4Conf: function (data, callback, complete) {
        BIReq.reqTableInfoByTableId(data, function (res) {
            callback(res);
        }, complete);
    },

    releaseTableLock4Conf: function (data) {
        BIReq.reqReleaseTableLock(data);
    },

    getServerSetPreviewBySql: function (data, callback, complete) {
        Data.Req.reqServerSetPreviewBySql(data, function (res) {
            callback(res);
        }, complete);
    },

    getTablesDetailInfoByTables: function (tables, callback, complete) {
        Data.Req.reqTablesDetailInfoByTables(tables, function (res) {
            callback(res);
        }, complete);
    },

    getTablesDetailInfoByTables4Refresh: function (tables, callback, complete) {
        Data.Req.reqTablesDetailInfoByTables4Refresh(tables, function (res) {
            callback(res);
        }, complete);
    },

    reqCubeStatusCheck: function (table, callback, complete) {
        Data.Req.reqCubeStatusCheck(table, function (data) {
            callback(data);
        }, complete)
    },
    checkTableExist: function (table, callback, complete) {
        Data.Req.reqIsTableExist(table, function (data) {
            callback(data);
        }, complete)
    },

    //fields 传[]表示获取全部字段
    getPreviewDataByTableAndFields: function (table, fields, callback, complete) {
        Data.Req.reqPreviewDataByTableAndFields(table, fields, function (data) {
            callback(data);
        }, complete)
    },

    getTestConnectionByLink: function (link, callback, complete) {
        Data.Req.reqTestConnectionByLink(link, function (data) {
            callback(data);
        }, complete)
    },

    getTestConnectionByLinkName: function (name, callback, complete) {
        Data.Req.reqTestConnectionByLinkName(name, function (data) {
            callback(data);
        }, complete)
    },

    getSchemasByLink: function (link, callback, complete) {
        Data.Req.reqSchemasByLink(link, function (data) {
            callback(data);
        }, complete)
    },

    getTranslationsRelationsFields: function (callback, complete) {
        Data.Req.reqTranslationsRelationsFields(function (data) {
            callback(data);
        }, complete);
    },

    getUpdatePreviewSqlResult: function (data, callback, complete) {
        Data.Req.reqUpdatePreviewSqlResult(data, function (res) {
            callback(res);
        }, complete)
    },

    modifyGlobalUpdateSetting: function (data, callback, complete) {
        Data.Req.reqModifyGlobalUpdateSetting(data, function (res) {
            callback(res);
        }, complete)
    },

    getCubeLog: function (callback, complete) {
        Data.Req.reqCubeLog(function (res) {
            callback(res);
        }, complete);
    },

    getAllPackages: function (callback, complete) {
        Data.Req.reqAllBusinessPackages(function (res) {
            callback(res);
        }, complete)
    },

    getTableNamesOfAllPackages: function (callback, complete) {
        Data.Req.getTableNamesOfAllPackages(function (res) {
            callback(res);
        }, complete)
    },

    generateCubeByTable: function (data, callback, complete) {
        Data.Req.reqGenerateCubeByTable(data, function () {
            callback();
        }, complete);
    },

    generateCube: function (callback, complete) {
        Data.Req.reqGenerateCube(function (res) {
            callback(res);
        }, complete);
    },

    checkTableInUse: function (data, callback, complete) {
        Data.Req.reqCheckTableInUse(data, function (res) {
            callback(res);
        }, complete);
    }
});
