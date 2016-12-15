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

    /**
     * 获取所有业务包分组信息树结构
     * 注意当前业务包可能为未保存
     * @returns {Array}
     */
    getAllGroupedPackagesTreeJSON4Conf: function () {
        var groups = Data.SharingPool.get("groups"), packages = Data.SharingPool.get("packages");
        var packStructure = [], groupedPacks = [];
        var currentPack = this.getCurrentPackage4Conf();
        BI.each(groups, function (id, group) {
            packStructure.push({
                id: id,
                text: group.name,
                isParent: true
            });
            BI.each(group.children, function (i, item) {
                if (item.id !== currentPack.id) {
                    packStructure.push({
                        id: item.id,
                        text: packages[item.id].name,
                        value: item.id,
                        pId: id
                    });
                    groupedPacks.push(item.id);
                }
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
            if (!isGrouped && currentPack.id !== pack.id) {
                packStructure.push({
                    text: pack.name,
                    value: pack.id
                })
            }
        });
        //添加当前业务包
        if (BI.isNotEmptyString(currentPack.groupId)) {
            packStructure.push({
                id: currentPack.id,
                text: currentPack.name,
                value: currentPack.id,
                pId: currentPack.groupId
            });
        } else {
            packStructure.push({
                text: currentPack.name,
                value: currentPack.id
            })
        }
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

    getUpdatePreviewSqlResult: function (data, callback, complete) {
        Data.Req.reqUpdatePreviewSqlResult(data, function (res) {
            callback(res);
        }, complete)
    },

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
    }

});

BI.extend(BI.Utils, {
    getConfPackageGroupIDs: function () {
        return BI.keys(Data.SharingPool.cat("groups"));
    },

    getConfGroupNameByGroupId: function (gid) {
        var groups = Data.SharingPool.get("groups");
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

    savePackageAuthority: function (data, callback, complete) {
        Data.Req.reqSavePackageAuthority(data, function (res) {
            callback(res);
        }, complete);
    },

    //fuck you
    getCircleLayerLevelInfo: function (table, layerInfo, callback, complete) {
        Data.Req.reqCircleLayerLevelInfoByTableAndCondition(table, layerInfo,
            function (res) {
                callback(res);
            }, complete);
    },

    //fuck you
    getConfNumberFieldMaxMinValue: function (table, fieldName, callback, complete) {
        Data.Req.reqNumberFieldMaxMinValue(table, fieldName,
            function (res) {
                callback(res);
            }, complete)
    },

    getTablesOfOnePackage: function (pId, callback, complete) {
        Data.Req.reqTablesOfOnePackage(pId, function (res) {
            callback(res);
        }, complete)
    },

    refreshFieldsOfOneTable: function (tableId, callback, complete) {
        Data.Req.reqFieldsOfOneTable(tableId, function (res) {
            callback(res);
        }, complete)
    },

    updateTablesOfOnePackage: function (data, callback, complete) {
        Data.Req.reqUpdateTablesOfOnePackage(data, function (res) {
            callback(res);
        }, complete)
    },

    getExcelHTMLView: function (fileId, callback, complete) {
        Data.Req.reqGetExcelHTMLView({fileId: fileId}, function (res) {
            callback(res);
        }, complete)
    },

    saveFileGetExcelViewData: function (fileId, callback, complete) {
        Data.Req.reqSaveFileGetExcelViewData({fileId: fileId}, function (res) {
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

    getRelationAndTransByTables: function (data, callback, complete) {
        Data.Req.reqRelationAndTransByTables(data, function (rt) {
            callback(rt);
        }, complete)
    },

    reqCubeStatusCheck: function (table, callback, complete) {
        Data.Req.reqCubeStatusCheck(table,function (data) {
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

    getTableIdByFieldId4Conf: function (fieldId) {
        var field = Data.SharingPool.get("fields", fieldId);
        if (BI.isNotNull(field)) {
            return field.table_id;
        }
    },

    getTableNameByFieldId4Conf: function (fieldId) {
        var translations = Data.SharingPool.get("translations");
        var tableId = this.getTableIdByFieldId4Conf(fieldId);
        if (BI.isNotNull(tableId)) {
            return translations[tableId]
        }
    },

    getFieldNameByFieldId4Conf: function (fieldId) {
        var field = Data.SharingPool.get("fields", fieldId);
        var translations = Data.SharingPool.get("translations");
        if (BI.isNotNull(field)) {
            return translations[fieldId] || field.field_name
        }
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

    getPrimaryTablesByTable4Conf: function (table, callback, complete) {
        Data.Req.reqPrimaryTablesByTable(table, function (res) {
            callback(res);
        }, complete);
    },

    checkTableInUse: function (data, callback, complete) {
        Data.Req.reqCheckTableInUse(data, function (res) {
            callback(res);
        }, complete);
    }
});
