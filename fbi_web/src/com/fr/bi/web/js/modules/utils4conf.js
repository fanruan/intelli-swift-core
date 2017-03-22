/**
 * 配置部分常用utils
 * 添加或修改注意加 4Conf 后缀
 * SharingPool 中维护 translations, relations, fields
 */
BI.extend(BI.Utils, {

    isAdmin4Conf: function () {
        return Data.SharingPool.get("isAdmin");
    },

    //转义 表名和字段名
    getTransNameById4Conf: function (id) {
        return Data.SharingPool.cat("translations")[id];
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

    isFieldExistById4Conf: function (fieldId) {
        return BI.isNotNull(Data.SharingPool.cat("fields")[fieldId]);
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

    getTableIdByFieldId4Conf: function (fieldId) {
        var field = Data.SharingPool.cat("fields")[fieldId];
        if (BI.isNotNull(field)) {
            return field.table_id;
        }
    },

    //获取关联字段
    getRelationFieldsByFieldId4Conf: function (relations, fieldId) {
        var primKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var currentPrimKey = primKeyMap[fieldId] || [], currentForKey = foreignKeyMap[fieldId];
        var relationFields = [], relationIds = [], rId;

        BI.each(currentPrimKey, function (i, maps) {
            var table = maps.primaryKey, relationTable = maps.foreignKey;
            //处理1:1 和 自循环
            var rId = relationTable.field_id;
            if (table.field_id === fieldId && (!relationIds.contains(rId) || table.field_id === rId)) {
                relationIds.push(rId);
                relationFields.push(relationTable);
            }
        });
        BI.each(currentForKey, function (i, maps) {
            var table = maps.foreignKey, relationTable = maps.primaryKey;
            rId = relationTable.field_id;
            if (table.field_id === fieldId && !relationIds.contains(rId)) {
                relationIds.push(rId);
                relationFields.push(relationTable);
            }
        });
        return relationFields;
    },

    getPrimaryFieldsByFieldId4Conf: function (fieldId, primaryFields) {
        var self = this;
        var relations = Data.SharingPool.cat("relations");
        var fields = Data.SharingPool.cat("fields");
        var tableId = "";
        if (BI.isNotNull(fields[fieldId])) {
            tableId = fields[fieldId].table_id;
        }
        var connectionSet = relations.connectionSet;
        BI.each(connectionSet, function (i, cs) {
            var pId = cs.primaryKey.field_id;
            if (cs.foreignKey.table_id === tableId && !primaryFields.contains(pId)) {
                primaryFields.push(pId);
                self.getPrimaryFieldsByFieldId4Conf(pId, primaryFields);
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
    getRelationTypeById4Conf: function (baseFieldId, relationId, relations) {
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

    //复制关联，用于选择业务包表时候的关联继承
    copyRelation4Conf: function (oFields, fieldIds, id) {
        var relations = Data.SharingPool.cat("relations");
        var connectionSet = relations.connectionSet, primaryKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var addedConn = [], addedPriMap = {}, addedForMap = {};
        BI.each(connectionSet, function (k, keys) {
            var copyRelation = getCopyOfRelation(keys, oFields, fieldIds, id);
            if (BI.isNotEmptyObject(copyRelation)) {
                addedConn.push(copyRelation);
            }
        });
        relations.connectionSet = connectionSet.concat(addedConn);
        BI.each(primaryKeyMap, function (pfId, maps) {
            var addedPris = [], nPKId = null;
            BI.each(maps, function (k, keys) {
                var copyRelation = getCopyOfRelation(keys, oFields, fieldIds, id);
                if (BI.isNotEmptyObject(copyRelation)) {
                    nPKId = copyRelation.primaryKey.field_id;
                    addedPris.push(copyRelation);
                }
            });
            if (addedPris.length > 0 && BI.isNotNull(nPKId)) {
                addedPriMap[nPKId] = addedPris;
            }
        });
        BI.each(addedPriMap, function (pkId, ms) {
            var pkMaps = relations.primKeyMap[pkId];
            if (BI.isNotNull(pkMaps)) {
                primaryKeyMap[pkId] = pkMaps.concat(ms);
            } else {
                primaryKeyMap[pkId] = ms;
            }
        });
        BI.each(foreignKeyMap, function (ffId, maps) {
            var addedFors = [], nFKId = null;
            BI.each(maps, function (k, keys) {
                var copyRelation = getCopyOfRelation(keys, oFields, fieldIds, id);
                if (BI.isNotEmptyObject(copyRelation)) {
                    nFKId = copyRelation.foreignKey.field_id;
                    addedFors.push(copyRelation);
                }
            });
            if (addedFors.length > 0 && BI.isNotNull(nFKId)) {
                addedForMap[nFKId] = addedFors;
            }
        });
        BI.each(addedForMap, function (fkId, ms) {
            var fkMaps = relations.foreignKeyMap[fkId];
            if (BI.isNotNull(fkMaps)) {
                foreignKeyMap[fkId] = fkMaps.concat(ms);
            } else {
                foreignKeyMap[fkId] = ms;
            }
        });
        return {
            connectionSet: addedConn,
            primKeyMap: addedPriMap,
            foreignKeyMap: addedForMap
        };

        function getCopyOfRelation(keys, oFields, fieldIds, nTableId) {
            var relation = {};
            var primKey = keys.primaryKey, foreignKey = keys.foreignKey;
            BI.each(oFields, function (i, ofs) {
                BI.each(ofs, function (j, oField) {
                    if (oField.id === primKey.field_id) {
                        var nPK = {}, nFK = BI.deepClone(foreignKey);
                        BI.each(fieldIds, function (k, fid) {
                            if (BI.Utils.getFieldNameById4Conf(fid) === BI.Utils.getFieldNameById4Conf(primKey.field_id)) {
                                nPK = {
                                    field_id: fid,
                                    table_id: nTableId
                                }
                            }
                        });
                        relation = {
                            primaryKey: nPK,
                            foreignKey: nFK
                        }
                    }
                    if (oField.id === foreignKey.field_id) {
                        var nPK = BI.deepClone(primKey), nFK = {};
                        BI.each(fieldIds, function (k, fid) {
                            if (BI.Utils.getFieldNameById4Conf(fid) === BI.Utils.getFieldNameById4Conf(foreignKey.field_id)) {
                                nFK = {
                                    field_id: fid,
                                    table_id: nTableId
                                }
                            }
                        });
                        relation = {
                            primaryKey: nPK,
                            foreignKey: nFK
                        }
                    }
                });
            });
            return relation;
        }
    },

    /**
     * 获取所有业务包分组信息树结构
     * @returns {Array}
     */
    getAllGroupedPackagesTreeJSON4Conf: function (data) {
        var groups = data.groups, packages = data.packages;
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
        var groupMap = Data.SharingPool.get("authority_settings", "groups"),
            packages = Data.SharingPool.get("authority_settings", "packages");
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

    getUpdatePreviewSqlResult: function (data, callback, complete) {
        Data.Req.reqUpdatePreviewSqlResult(data, function (res) {
            callback(res);
        }, complete)
    },

    getConfDataByField: function (table, fieldName, filterConfig, callback, complete) {
        Data.Req.reqFieldsDataByData({
            table: table,
            fieldName: fieldName,
            fieldType: BICst.COLUMN.STRING,
            filterConfig: filterConfig
        }, function (data) {
            callback(data.value, data.hasNext);
        }, complete);
    },

    getSortableConfDataByField: function (table, fieldName, fieldType, filterConfig, callback, complete) {
        Data.Req.reqFieldsDataByData({
            table: table,
            fieldName: fieldName,
            fieldType: fieldType,
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

    getAllPackageIDs4Conf: function () {
        return BI.keys(Data.SharingPool.cat("packages"));
    },

    getPackageNameByID4Conf: function (packageId) {
        var packages = Data.SharingPool.cat("packages");
        if (BI.isNotNull(packages[packageId])) {
            return packages[packageId].name;
        }
    },

    getPackageGroupIDs4Conf: function () {
        return BI.keys(Data.SharingPool.cat("groups"));
    },

    getGroupNameById4Conf: function (gid) {
        var groups = Data.SharingPool.cat("groups");
        if (BI.isNotNull(groups[gid])) {
            return groups[gid].name;
        }
        return "";
    },

    getGroupChildrenByGroupId4Conf: function (gid) {
        var groups = Data.SharingPool.get("groups");
        if (BI.isNotNull(groups[gid])) {
            return groups[gid].children;
        }
        return [];
    },

    getGroupInitTimeByGroupId4Conf: function (gid) {
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

    getConfPackagePositionByID: function (pid) {
        return Data.SharingPool.get("packages", pid, "position");
    },

    getUpdateSettingByID: function (id) {
        return Data.SharingPool.get("update_settings", id);
    },

    getAuthorityLoginField: function () {
        return Data.SharingPool.get("authority_settings", "login_field");
    },

    setLoginField: function (field) {
        var authoritySettings = Data.SharingPool.get("authority_settings");
        authoritySettings.login_field = field;
        Data.SharingPool.put("authority_settings", authoritySettings);
        BI.Utils.saveLoginField({"login_field": field.id}, BI.emptyFn);
    },

    clearLoginField: function () {
        var authoritySettings = Data.SharingPool.get("authority_settings");
        delete authoritySettings.login_field;
        Data.SharingPool.put("authority_settings", authoritySettings);
        BI.Utils.saveLoginField({}, BI.emptyFn);
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
    },

    //权限相关 目前功能逻辑大部分都是根据是否为管理员  --- start ---
    _getAuthByPage: function (page) {
        return Data.SharingPool.get("authNodes", page);
    },

    hasDataLinkPageAuth: function () {
        return this._getAuthByPage(BICst.DATA_CONFIG_AUTHORITY.DATA_CONNECTION.PAGE);
    },

    hasPackManagerPageAuth: function () {
        return this._getAuthByPage(BICst.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER.PAGE);
    },

    hasMultiPathSettingPageAuth: function () {
        return this._getAuthByPage(BICst.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING);
    },

    hasPackageAuthorityPageAuth: function () {
        return this._getAuthByPage(BICst.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY);
    },

    hasFineIndexUpdatePageAuth: function () {
        return this._getAuthByPage(BICst.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE);
    },

    hasEditPackageGroupAuth: function () {
        return this.isAdmin4Conf();
    },

    hasAddPackageGroupAuth: function () {
        return this.isAdmin4Conf();
    },

    hasBatchSetPackageGroupAuth: function () {
        return this.isAdmin4Conf();
    },

    hasBatchSetPackAuthorityAuth: function () {
        return this.isAdmin4Conf();
    },

    hasTableAuthByTableId4Conf: function (tableId) {
        var packageId = this.getPackageIdByTableId4Conf(tableId);
        return BI.isNotNull(packageId);
    },

    hasEditLoginFieldAuth: function () {
        return this.isAdmin4Conf();
    }

    //权限相关   --- end ---
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

    saveFileGetExcelViewData: function (fileId, callback, complete) {
        Data.Req.reqSaveFileGetExcelViewData({fileId: fileId}, function (res) {
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

    updatePackageName4Conf: function (data, callback, complete) {
        Data.Req.reqUpdatePackageName(data, function (res) {
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
        return Data.Req.reqTablesDetailInfoByTables4Refresh(tables, function (res) {
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

    getUpdateSettingBySourceId: function (data, callback, complete) {
        Data.Req.reqUpdateSettingById(data, function (res) {
            callback(res);
        }, complete);
    },

    modifyUpdateSetting: function (data, callback, complete) {
        Data.Req.reqModifyUpdateSetting(data, function (res) {
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
    },

    updateTablesTranOfPackage: function (data, callback, complete) {
        Data.Req.reqUpdateTablesTranOfPackage(data, function (res) {
            callback(res);
        }, complete);
    },

    getRelationsDetail4Conf: function (callback, complete) {
        Data.Req.reqGetRelationsDetail(function (res) {
            callback(res);
        }, complete);
    },

    getData4SelectField4Conf: function (callback, complete) {
        Data.Req.reqGetData4SelectField(function (res) {
            callback(res);
        }, complete);
    },

    getLinksAndPackages4Conf: function (callback, complete) {
        Data.Req.reqLinksAndPackages(function (res) {
            callback(res);
        }, complete);
    },

    getTablesInfoByIds4Conf: function (data, callback, complete) {
        Data.Req.reqTablesInfoByIds(data, function (res) {
            callback(res);
        }, complete);
    }
});
