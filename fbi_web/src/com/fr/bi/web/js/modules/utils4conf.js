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
    getAllGroupedPackagesTreeJSON4PrermissionManage: function (tree) {
        Data.Req.reqPakageAndGroup(function (data) {
                var self = this;
                var groups = data.groups, packages = data.packages;
                var packStructure = [], groupedPacks = [];
                BI.each(groups, function (id, group) {
                packStructure.push({
                    id: id,
                    text: group.name,
                    value: group.name,
                    pId: 0,
                    open: true
                });
                    BI.each(group.children, function (i, item) {
                        packStructure.push({
                            id: item.id,
                            text: packages[item.id].name,
                            value: item.id,
                            pId: id,
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
                    value: BI.i18nText('BI-Ungrouped'),
                    id: 1,
                    pId: 0,
                    open: true
                });
            }
            Data.SharingPool.put("packStructure", packStructure);
                tree.populate(packStructure);
            }
        );
    },
    getAuthorityBypackageId: function (packageId) {
        Data.SharingPool.put("authorityByPackageName", Data.Req.reqAuthorityByPackageId(packageId));
        Data.SharingPool.put("authorityByPackageName", []);

    },
    getAllAuthority: function () {
        Data.SharingPool.put("allAuthority", Data.Req.reqAllAuthority());
    },
    getConfDataByField: function (table, fieldName, filterConfig, callback) {
        Data.Req.reqFieldsDataByData({
            table: table,
            field: fieldName,
            filterConfig: filterConfig
        }, function (data) {
            callback(data.value, data.hasNext);
        });
    },
    getAllPackageIDs4Conf: function () {
        return BI.keys(Data.SharingPool.cat("packages"));
    },

    getPackageNameByID4Conf: function (packageId) {
        return Data.SharingPool.get("packages")[packageId].name;
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

    getConfPackageTablesByID: function (pid) {
        return Data.SharingPool.get("packages", pid, "tables");
    },

    getConfAllPackageIDs: function () {
        return BI.keys(Data.SharingPool.get("packages"));
    },

    getConfPackageNameByID: function (pid) {
        return Data.SharingPool.get("packages", pid, "name");
    },

    getUpdateSettingByID: function(id) {
        return Data.SharingPool.get("update_settings", id);
    },

    //fuck you
    getConfDataByField: function (table, fieldName, filterConfig, callback) {
        Data.Req.reqFieldsDataByData({
            table: table,
            field: fieldName,
            filterConfig: filterConfig
        }, function (data) {
            callback(data.value, data.hasNext);
        });
    },

    //fuck you
    getCircleLayerLevelInfo: function (table, layerInfo, callback) {
        Data.Req.reqCircleLayerLevelInfoByTableAndCondition(table, layerInfo,
            function (res) {
                callback(res);
            });
    },

    //fuck you
    getConfNumberFieldMaxMinValue: function (table, fieldName, fieldType, callback) {
        Data.Req.reqNumberFieldMaxMinValue(table, fieldName, fieldType,
            function (res) {
                callback(res);
            })
    },

    getTablesOfOnePackage: function (pId, callback) {
        Data.Req.reqTablesOfOnePackage(pId, function (res) {
            callback(res);
        })
    },

    updateTablesOfOnePackage: function (data, callback) {
        Data.Req.reqUpdateTablesOfOnePackage(data, function (res) {
            callback(res);
        })
    },

    saveFileGetExcelData: function (fileId, callback) {
        Data.Req.reqSaveFileGetExcelData({fileId: fileId}, function (res) {
            callback(res);
        })
    },

    getExcelDataByFileName: function (fullFileName, callback) {
        Data.Req.reqExcelDataByFileName({fileName: fullFileName}, function (res) {
            callback(res);
        })
    },

    saveDataLink: function (data, callback) {
        Data.Req.reqSaveDataLink(data, function () {
            callback();
        });
    },

    getCubePath: function (callback) {
        Data.Req.reqCubePath(function (path) {
            callback(path);
        });
    },

    checkCubePath: function (path, callback) {
        Data.Req.reqCheckCubePath(path, function (res) {
            callback(res);
        });
    },

    getConnectionNames: function (callback) {
        Data.Req.reqConnectionName(function (res) {
            callback(res);
        })
    },

    getServerSetPreviewBySql: function (data, callback) {
        Data.Req.reqServerSetPreviewBySql(data, function (res) {
            callback(res);
        });
    },

    getTablesDetailInfoByTables: function (tables, callback) {
        Data.Req.reqTablesDetailInfoByTables(tables, function (res) {
            callback(res);
        });
    },

    getRelationAndTransByTables: function (data, callback) {
        Data.Req.reqRelationAndTransByTables(data, function (rt) {
            callback(rt);
        })
    },

    checkCubeStatusByTable: function (table, callback) {
        Data.Req.reqCubeStatusByTable(table, function (data) {
            callback(data);
        })
    },

    //fields 传[]表示获取全部字段
    getPreviewDataByTableAndFields: function (table, fields, callback) {
        Data.Req.reqPreviewDataByTableAndFields(table, fields, function (data) {
            callback(data);
        })
    },

    getTestConnectionByLink: function (link, callback) {
        Data.Req.reqTestConnectionByLink(link, function (data) {
            callback(data);
        })
    },

    getTestConnectionByLinkName: function (name, callback) {
        Data.Req.reqTestConnectionByLinkName(name, function (data) {
            callback(data);
        })
    },

    getSchemasByLink: function (link, callback) {
        Data.Req.reqSchemasByLink(link, function (data) {
            callback(data);
        })
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

    getTranslationsRelationsFields: function (callback) {
        Data.Req.reqTranslationsRelationsFields(function (data) {
            callback(data);
        });
    },

    getUpdatePreviewSqlResult: function(data, callback){
        Data.Req.reqUpdatePreviewSqlResult(data, function (res) {
            callback(res);
        })
    },

    modifyGlobalUpdateSetting: function(data, callback){
        Data.Req.reqModifyGlobalUpdateSetting(data, function(res) {
            callback(res);
        })
    },

    getCubeLog: function(callback) {
        Data.Req.reqCubeLog(function(res){
            callback(res);
        });
    },
    updatePackageAuthority: function (data, callback) {
        Data.Req.reqUpdatePackageAuthority(data, function (res) {
            callback(res);
        })
    },

    getAllPackages: function(callback) {
        Data.Req.reqAllBusinessPackages(function(res) {
            callback(res);
        })
    }

});
